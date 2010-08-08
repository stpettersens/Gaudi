/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package gaudi
import org.json.simple.{JSONValue,JSONObject,JSONArray}
import scala.collection.immutable.HashMap

class GaudiBuildExecutor(preamble: JSONObject) {
	
	private def processPreamble(): Unit = {
		// TODO
	}
	private def splitCmdParam(cmdParam: String): Any = {
		// TODO 
	}
	private def doCommand(cmd: String, param: String): Unit = {
		cmd match {
			case "exec" => {
				val rt = Runtime.getRuntime()
				rt.exec(param)
			}
			case "mkdir" => {
				// TODO
			}
		}
	}
	def doBuild(buildSteps: JSONArray): Unit = {
		// TODO
	}
	def doInstall(installSteps: JSONArray): Unit = {
		// TODO
	}
	def doClean(cleanSteps: JSONArray): Unit = {
		// TODO
	}
}