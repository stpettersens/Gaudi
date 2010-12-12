/*
Gaudi platform agnostic build tool
Copyright 2010 Sam Saint-Pettersen.

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
import scala.util.matching.Regex
import java.io._

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean, logging: Boolean) 
extends IGaudiBuilder {
	
	// Define global logger object
	val logger = new GaudiLogger(logging)
	
	// Substitute variables for values
	private def substituteVars(action: Array[Object]): Unit = {
		println(preamble)
	}
	// Handle wild cards in parameters such as *.scala, *.cpp,
	// for example, to compile all Scala or C++ files in the specified dir
	private def handleWildcards(param: String): String = {
		if(param.contains("*")) {
		    val rawParamPattn: Regex = """[\w\d]*\s*(.*)""".r
		    val rawParamPattn(raw_param) = param
			var dir = new File(".")
			val filePattn: Regex = """\*([\.\w\d]+)""".r
			val filePattn(ext) = raw_param
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
	// Extract command and param for execution
	private def extractCommand(cmdParam: String): (String, String) = {
		val cpPattn: Regex = """\{\"(\w+)\"\:\"([\\\/\"\w\d\s\$\.\*\,\_\+\-\>\_]+)\"\}""".r
		var cpPattn(cmd: String, param: String) = cmdParam
		(cmd, param)
	}
	// Print an error related to action or command and exit
	private def printError(error: String): Unit = {
		println(String.format("\tAborting: %s.", error))
		logger.dump(error) // Also log it
		System.exit(1) // Exit application with error code
	}
	// Print executed command
	private def printCommand(command: String, param: String): Unit = {
		if(beVerbose && command != "echo") {
			println(String.format("\t:%s %s", command, param))
		}
	}
	// File writing operations
	private def writeToFile(file: String, message: String, append: Boolean): Unit = {
		var out: PrintWriter = null
		try {
			out = new PrintWriter(new FileOutputStream(file, append))
			out.println(message)
		}
		catch {
			case ioe: IOException => printError(ioe.getMessage)
		}
		finally {
			out.close()
		}
	}
	// Execute an external program or process
	private def execExtern(param: String): Unit = {
		val exe: (String, String) = GaudiHabitat.getExeWithExt(param)
		// -----------------------------------------------------------------
		logger.dump(String.format("Executed -> %s %s\n" +
		"Wildcard matched -> %s", exe._1, exe._2, handleWildcards(exe._2)))
		// -----------------------------------------------------------------
		if(exe._1 != null) {
			var p: Process = Runtime.getRuntime().exec(String.format("%s %s", exe._1, handleWildcards(exe._2)))
			val reader = new BufferedReader(new InputStreamReader(p.getErrorStream()))
			var line: String = reader.readLine()
			if(line != null) println(String.format("\t~ %s", line))
		}
	}
	// Execute a command in the action
	def doCommand(command: String, param: String): Unit = {
		// Handle any potential wildcards in parameters
		// for all commands other than :exec and :echo
	    val wcc_param: String = handleWildcards(param)

		// Do not print "echo" commands, but do print others
		if(command != "echo") printCommand(command, param)
		command match {
			case "exec" => {
				execExtern(param)
			}
			case "mkdir" => {
				val aDir: Boolean = new File(param).mkdir()
				if(!aDir) {
					printError(String.format("Problem making dir -> %s", param))
				}
			}
			case "list" => println(String.format("\t-> %s", wcc_param))
			case "echo" => println(String.format("\t# %s", param))
			case "erase" => new File(wcc_param).delete() // Add support for wildcards
			case "copy" => {
				// Explicit the first time about this being a string array
				val srcDest: Array[String] = param.split("->")
				copyFile(new File(srcDest(0)), new File(srcDest(1))) // Via Apache Commons IO
			}
			case "rcopy" => {
				// Implicit...
				"Recur. copy" // TODO
			}
			case "move" => {
				// Et cetera...
				val srcDest = param.split("->") 
				moveFile(new File(srcDest(0)), new File(srcDest(1))) // Via Apache Commons IO
			}
			// Append message to a file
			// Usage in buildfile: { "append": file>>message" }
			// Equivalent to *nix's echo "message" >> file
			case "append" => {
				val fileMsg = param.split(">>")
				writeToFile(fileMsg(0), fileMsg(1), true)
			}
			// Invoke Scala compiler (scalac)
			case "scalac" => {
				execExtern(String.format("scalac %s", param))
			}
			// Invoke JDK jar tool to pack a jar
			case "jar" => {
				var jarSrcs = param.split("<<")
				execExtern(
				String.format("jar cfm %s Manifest.mf %s", jarSrcs(0), jarSrcs(1))
				)
			}
			// Invoke JDK jar tool to unpack a jar
			case "unjar" => {
				var jar = param
				execExtern(String.format("jar xf %s", jar))
			}
			case _ => {
				// Implement extendable commands
				printError(String.format("%s is an invalid command", command))
			}
		}
	}
	// Execute an action 
	def doAction(action: JSONArray): Unit = {
		try {
			val actionArray = action.toArray()
			//substituteVars(actionArray)
			for(cmdParam <- actionArray) {
				val cpPair = extractCommand(cmdParam.toString)
				doCommand(cpPair._1, cpPair._2)
			}
		}
		catch {
			case ex: Exception => {
				println(String.format("\t[%s]", ex.getMessage))
				printError("Encounted an invalid action or command")
			}
		}
	}
}
