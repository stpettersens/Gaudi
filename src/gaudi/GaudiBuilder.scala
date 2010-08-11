/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package gaudi
import org.json.simple.{JSONObject,JSONArray}
import scala.util.matching.Regex
import java.io._

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	// Extract comamnd and param for execution 
	private def extractCommand(cmdParam: String): (String, String) = {
		val cpPattn: Regex = """\{\"(\w+)\"\:\"(\s*\w*)\"\}""".r
		var cpPattn(c: String, p: String) = cmdParam
		(c, p)
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
		printCommand(cmd, param)
		cmd match {
			case "exec" => {
				val rt = Runtime.getRuntime()
				rt.exec(param)
			}
			case "mkdir" => {
				val aDir: Boolean = new File(param).mkdir()
				if(!aDir) {
					printError(
					String.format("Problem creating dir -> %s", param)
					)
				}
			}
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
			for(cmdParam <- actionArray) {
				val cpPair = extractCommand(cmdParam.toString())
				doCommand(cpPair._1, cpPair._2)
			}
		}
		catch {
			case ex: Exception => {
				printError("Encounted an invalid action")
			}
		}
	}
}