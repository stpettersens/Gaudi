/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package gaudi
import org.json.simple.{JSONValue,JSONObject,JSONArray}
import java.io._

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	private def printAction(cmd: String, param: String): Unit = {
		if(beVerbose) {
			println(String.format("\t\t\t^%s %s", cmd, param))
		}
	}
	
	private def doCommand(cmd: String, param: String): Boolean = {
		val padding: String = "\t\t\t^"
		cmd match {
			case "exec" => {
				printAction(cmd, param)
				val rt = Runtime.getRuntime()
				rt.exec(param)
				true
			}
			case "mkdir" => {
				printAction(cmd, param)
				val aDir: Boolean = new File(param).mkdir()
				aDir
			}
		}
	}
	def doAction(action: JSONArray): Unit = {
		if(true) doCommand("exec", "gcc") //! dummy code
		else println("Fatal problem!")
	}
}