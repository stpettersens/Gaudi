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
	
	/* 
	 * TODO: Implement commands dictionary
	 * e.g. "erase" -> call to rm/del, etc...
	*/
	
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