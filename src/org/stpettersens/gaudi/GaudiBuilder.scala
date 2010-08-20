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

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	// Substitute variables for values
	private def substituteVars(action: Array[Object]): Unit = {
		println(preamble)
	}
	// Handle wild cards in parameters such as *.scala, *.cpp,
	// to compile all Scala or C++ files in the specified dir
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
		val cpPattn: Regex = 
		"""\{\"(\w+)\"\:\"([\\\/\"\w\d\s\$\.\*\,\_\+\-\>\_]+)\"\}""".r
		var cpPattn(cmd: String, param: String) = cmdParam
		(cmd, param)
	}
	// Print an error related to action or command and exit
	private def printError(error: String): Unit = {
		println(String.format("\tAborting: %s.", error))
		System.exit(-1)
	}
	// Print executed command
	private def printCommand(cmd: String, param: String): Unit = {
		if(beVerbose) {
			println(String.format("\t:%s %s", cmd, param))
		}
	}
	// Execute a command in the action
	def doCommand(cmd: String, param: String): Unit = {
	
		// Handle any potential wildcards in parameters
	    val wcc_param: String = handleWildcards(param)
	    println("wcc_param -> " + wcc_param)

		// Do not print "echo" commands, but do others
		if(cmd != "echo") printCommand(cmd, param)
		cmd match {
			case "exec" => {
				val exe: (String, String) = GaudiHabitat.getExeWithExt(param)
				if(exe._1 != null) {
					var p: Process = Runtime.getRuntime().exec(
					String.format("%s %s", exe._1, wcc_param))
					val reader = new BufferedReader(
					new InputStreamReader(p.getErrorStream())
					)
					var line: String = reader.readLine()
					if(line != null) println(String.format("\t~ %s", line))
				}
			}
			case "mkdir" => {
				val aDir: Boolean = new File(param).mkdir()
				if(!aDir) {
					printError(
					String.format("Problem making dir -> %s", param)
					)
				}
			}
			case "list" => println("\t-> " + handleWildcards(param))
			case "echo" => println(String.format("\t# %s", param))
			case "erase" => new File(wcc_param).delete() // Add support for wildcard
			case "copy" => {
				val srcDest: Array[String] = param.split("->")
				copyFile(new File(srcDest(0)), new File(srcDest(1)))
			}
			case "rcopy" => "Recur. copy" // TODO
			case "move" => {
				val srcDest = param.split("->") 
				moveFile(new File(srcDest(0)), new File(srcDest(1)))
			}
			case _ => {
				printError(String.format("%s is an invalid command", cmd))
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