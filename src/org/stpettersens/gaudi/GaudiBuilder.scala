/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import org.json.simple.{JSONObject,JSONArray}
import scala.util.matching.Regex
import java.io._

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	// Substitute variables for values
	private def substituteVars(action: Array[Object]): Unit = {
		println(preamble)
	}
	// Extract command and param for execution 
	private def extractCommand(cmdParam: String): (String, String) = {
		val cpPattn: Regex = """\{\"(\w+)\"\:\"([\\\"\w\s\.\+\-\_]+)\"\}""".r
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
			println(String.format("\t-> %s %s", cmd, param))
		}
	}
	// Execute a command in the action
	private def doCommand(cmd: String, param: String): Unit = {
		if(cmd != "echo") printCommand(cmd, param)
		cmd match {
			case "exec" => {
				var p: Process = Runtime.getRuntime().exec(param)
				// TODO: CAPTURE STDOUT FOR EXECUTED PROGRAM
				// ...
				val reader = new BufferedReader(
				new InputStreamReader(p.getErrorStream()))
				var line: String = reader.readLine()
				if(line != null) println(String.format("\t<- %s", line))
			}
			case "mkdir" => {
				val aDir: Boolean = new File(param).mkdir()
				if(!aDir) {
					printError(
					String.format("Problem making dir -> %s", param)
					)
				}
			}
			case "echo" => println(String.format("\t# %s", param))
			case "rm" => new File(param).delete()
			case _ => {
				printError(
				String.format("%s is an invalid command", cmd)
				)
			}
		}
	}
	// Execute an action 
	def doAction(action: JSONArray): Unit = {
		try {
			val actionArray = action.toArray()
			//substituteVars(actionArray)
			for(cmdParam <- actionArray) {
				val cpPair = extractCommand(cmdParam.toString())
				doCommand(cpPair._1, cpPair._2)
			}
		}
		catch {
			case ex: Exception => {
				println(ex) // !
				printError("Encounted an invalid action or command")
			}
		}
	}
}