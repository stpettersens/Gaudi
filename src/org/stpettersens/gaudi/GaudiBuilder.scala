/*
Gaudi platform agnostic build tool
Copyright 2010-2014 Sam Saint-Pettersen.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

For dependencies, please see LICENSE file.
*/
package org.stpettersens.gaudi
import org.json.simple.{JSONObject,JSONArray}
import org.apache.commons.io.FileUtils._
import org.apache.commons.io.filefilter.WildcardFileFilter
import scala.io.Source
import scala.util.matching.Regex
import java.nio.file._
import java.io._

class GaudiBuilder(buildConf: String, preamble: JSONObject, sSwitch: Boolean, beVerbose: Boolean,
logging: Boolean) extends GaudiBase {
	// Define global messenger object.
	var messenger = new GaudiMessenger(logging)	

	// Define start time and elapsed time
	var stTime: Long = 0
	var elTime: Long = 0
	var period: String = ""

	if(sSwitch) {
		messenger.start()
	}
	var foreman = new GaudiForeman(buildConf);

	// Substitute variables for values.
	private def substituteVars(action: Array[Object]): List[String] = {
		var laction: List[String] = List()
		for(command <- action) {
			val cmd = String.format("%s", command).replaceFirst("\\$[\\w\\d]+", "subed")
			laction ::= cmd
		} 
		laction.reverse
	}
	// Handle wild cards in parameters such as *.scala, *.cpp,
	// for example, to compile all Scala or C++ files in the specified dir.
	private def handleWildcards(param: String): String = {
		if(param.contains("*")) {
		    val rawParamPattn: Regex = """[\w\d]*\s*(.*)""".r
		    var rawParamPattn(raw_param) = param
			var dir = new File(".")
			val filePattn: Regex = """\*([\.\w\d]+)""".r
			var filePattn(ext) = raw_param
			val filter: FileFilter = new WildcardFileFilter("*" + ext)
			var newParam: String = ""
			val files: Array[File] = dir.listFiles(filter)
			for(file <- files) {
				newParam += file
			}
			return newParam.replace(".\\", " ")
		}
		param
	}
	// Extract command and param for execution.
	private def extractCommand(cmdParam: String): (String, String) = {
		val cpPattn: Regex = """\{\"(\w+)\"\:\"([\\\/\"\w\d\s\$\.\*\,\_\+\-\>\_\:]+)\"\}""".r
		var cpPattn(cmd: String, param: String) = cmdParam
		(cmd, param)
	}
	// Print an error related to action or command and exit
	private def printError(error: String): Unit = {
		println(error)
		logDump(error, logging) // Also log it
		System.exit(-1) // Exit application with error code
	}
	// Print an error... (overloaded for an exception)
	private def printError(error: Exception): Unit = {
		println(error.getMessage)
		logDump(error.getMessage, logging) // Also log it
		System.exit(-1) // Exit application with error code
	}

	// Print executed command
	private def printCommand(command: String, param: String): Unit = {
		if(beVerbose && command != "echo") {
			println("\t:%s %s".format(command, param))
		}
	}
	// Execute an external program or process
	private def execExtern(param: String): Unit = {
		val exe: (String, String) = GaudiHabitat.getExeWithExt(param)
		// -----------------------------------------------------------------
		logDump("Executed -> %s %s\n" +
		"Wildcard matched -> %s".format(exe._1, exe._2, handleWildcards(exe._2)),
		logging)
		// -----------------------------------------------------------------
		if(exe._1 != null) {
			var p: Process = Runtime.getRuntime()
			.exec("%s %s".format(exe._1, handleWildcards(exe._2)))
			val reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))
			var line: String = reader.readLine()
			if(line != null) {
				val msg = "\t~ %s".format(line)
				println(msg)
				messenger.report(msg)
			}
		}
	}
	// Concaternate a file.
	private def concaternateFile(file: String): Unit = {
		try { // Open file and print lines to console.
			println("\n")
			for(line <- Source.fromFile(file).getLines) {
				println(line)
			}
		}
		catch { // Catch any I/O or general exceptions.
			case ioe: IOException => printError(ioe)
			case e: Exception => printError(e)
		}
	}
	// Erase a file.
	private def eraseFile(file: String, isExe: Boolean): Unit = {
		if(isExe && GaudiHabitat.getOSFamily() == 0) {
			new File(file.concat(".exe")).delete()
		}
		else new File(file).delete()
	}
	// Start timer (m/s) for action.
	private def startTimer(): Unit = {
		stTime = System.currentTimeMillis()
	}
	// Get time elapsed in seconds for action.
	private def getTimeElapsed(): String = {
		period = "millisecond"
		elTime = System.currentTimeMillis() - stTime
		if(elTime > 999) {
			elTime = elTime / 1000
			period = "second"
		}
		period
	}
	// Execute a command in the action
	def doCommand(command: String, param: String): Unit = {
		// Handle any potential wildcards in parameters
		// for all commands other than :exec and :echo
	    val wcc_param: String = handleWildcards(param)

		// Do not print "echo" commands, but do print others
		printCommand(command, param)
		command match {
			case "exec" => {
				execExtern(param)
			}
			case "mkdir" => {
				val aDir: Boolean = new File(param).mkdir()
				if(!aDir) {
					printError("Problem making dir -> %s".format(param))
				}
			}
			case "list" => println("\t-> %s".format(wcc_param))
			case "echo" => println("\t# %s".format(param))
			case "erase" => eraseFile(wcc_param, false) // Support wildcards.
			case "erasex" => eraseFile(wcc_param, true)
			case "xstrip" => {
				var p: String = wcc_param
				if(GaudiHabitat.getOSFamily() == 0) {
					p = wcc_param.concat(".exe")
				}
				execExtern("strip %s".format(p)) // Relies on strip command.
			}
			case "cat" => concaternateFile(param)
			case "copy" => {
				// Explicit the first time about this being a string array
				val srcDest: Array[String] = param.split("->")
				copyFile(new File(srcDest(0)), new File(srcDest(1))) // Via Apache Commons IO.
			}
			case "rcopy" => {
				// Implicit...
				"Recur. copy" // TODO
			}
			case "move" => {
				// Et cetera...
				val srcDest = param.split("->") 
				moveFile(new File(srcDest(0)), new File(srcDest(1))) // Via Apache Commons IO.
			}
			// Append message to a file
			// Usage in buildfile: { "append": file>>message" }
			// Equivalent to *nix's echo "message" >> file.
			case "append" => {
				val fileMsg = param.split(">>")
				writeToFile(fileMsg(0), fileMsg(1), true)
			}
			case "clobber" => {
				val fileMsg = param.split(">>")
				writeToFile(fileMsg(0), fileMsg(1), false)
			}
			case "notify" => {
				GaudiHabitat.sendOSNotif("null")
			}
			case "encode" => {
				val fileMsg = param.split(">>")
				writeToFile(fileMsg(0), encodeText(fileMsg(1)), true)
			}
			case "decode" => {
				val fileMsg = param.split("<<")
				writeToFile(fileMsg(0), decodeText(fileMsg(1)), true)
			}
			case "help" => {
				val onCmd = param.split(" ")
				// TODO.
			}
			case _ => {
				// Implement extendable commands here via plug-ins.
				// Catch unimplement commands here.
				printError("%s is an invalid command".format(command))
			}
		}
	}
	// Execute an action.
	def doAction(action: JSONArray): Unit = {
		try {
			startTimer()
			val actionArray = action.toArray()
			//val actionList = substituteVars(actionArray)
			val actionList = actionArray
			for(cmdParam <- actionList) {
				val cpPair = extractCommand(cmdParam.toString)
				doCommand(cpPair._1, cpPair._2)
			}
			getTimeElapsed()
			println("\nSUCCESSFUL in %d %s(s).".format(elTime, period))
		}
		catch {
			case ex: Exception => {
				getTimeElapsed()
				println("\t[%s]".format(ex.getMessage))
				println("\n\tEncounted an invalid action or command")
				printError("\nFAILED in %d %s(s)".format(elTime, period))
			}
		}
	}
}
