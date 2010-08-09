/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package gaudi
import org.json.simple.{JSONValue,JSONObject,JSONArray}
import scala.util.matching.Regex
import java.io._

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	// Format command for execution 
	private def formatCommand(cmdParam: String): Array[String] = {
		val cP = cmdParam.split(":")
		cP
	}
	
	// Print executed command
	private def printCommand(cmd: String, param: String): Unit = {
		if(beVerbose) {
			println(String.format("\t%s %s", cmd, param))
		}
	}
	
	// Execute a command in the action
	private def doCommand(cmd: String, param: String): Boolean = {
		cmd match {
			case "exec" => {
				printCommand(cmd, param)
				val rt = Runtime.getRuntime()
				rt.exec(param)
				true
			}
			case "mkdir" => {
				printCommand(cmd, param)
				val aDir: Boolean = new File(param).mkdir()
				aDir
			}
			case _ => {
				printCommand("Error: " + cmd, "is an invalid command.")
				false
			}
		}
	}
	// Execute an action 
	def doAction(action: JSONArray): Unit = {
		val actionArray = action.toArray()
		for(cmdParam <- actionArray) {
			val cP: Array[String] = formatCommand(cmdParam.toString())
			doCommand(cP(0), cP(1))
		}
	}
}