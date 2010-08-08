/*
 * Gauldi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package gaudi
import org.json.simple.{JSONValue,JSONObject,JSONArray}

class GaudiBuildParser(buildConf: String) {
	
	// Parse build config into build JSON object on initialization
	val buildJson: JSONObject = parseBuildJSON()
	
	private def parseBuildJSON(): JSONObject = { 
		val buildObj: Object = JSONValue.parse(buildConf)
		buildObj.asInstanceOf[JSONObject]
	}
	// Get sub-object 'shard' from build JSON object 
	private def getShard(objectName: String): Object = {
		val shardStr = JSONValue.toJSONString(buildJson.get(objectName))
		JSONValue.parse(shardStr)
	}
	// Return raw build configuration in string,
	// for debugging purposes only
	def getBuildString(): String = {
		buildConf
	}
	// Get target from parsed preamble
	def getTarget(): String  = {
		val targetStr = JSONValue.toJSONString(getPreamble().get("target"))
		targetStr.replaceAll("\"", "")
	}
	// Get the preamble from build object
	def getPreamble(): JSONObject = {
		getShard("preamble").asInstanceOf[JSONObject]
	}	
	// Get the build steps from build object
	def getBuildSteps(): JSONArray = {
		getShard("build").asInstanceOf[JSONArray]
	}
	// Get the install steps from build object
	def getInstallSteps(): JSONArray = {
		getShard("install").asInstanceOf[JSONArray]
	}
	// Get the clean steps from build object
	def getCleanSteps(): JSONArray = {
		getShard("clean").asInstanceOf[JSONArray]
	}
}