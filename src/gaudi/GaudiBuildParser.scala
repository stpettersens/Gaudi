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
	
	// Return raw build configuration in string,
	// for debugging purposes only
	def getBuildString(): String = {
		buildConf
	}
	def getPreamble(): JSONObject = {
		val buildInfo = parseBuildJSON()
		val preambleStr = JSONValue.toJSONString(buildInfo.get("preamble"))
		val preambleObj = JSONValue.parse(preambleStr)
		val preambleJson = preambleObj.asInstanceOf[JSONObject]
		preambleJson
	}	
	def getBuildSteps(): JSONArray = {
		val buildInfo = parseBuildJSON()
		val bStepsStr = JSONValue.toJSONString(buildInfo.get("build"))
		val bStepsObj = JSONValue.parse(bStepsStr)
		val bStepsJson = bStepsObj.asInstanceOf[JSONArray]
		bStepsJson
	}
	def getInstallSteps(): JSONArray = {
		val buildInfo = parseBuildJSON()
		val iStepsStr = JSONValue.toJSONString(buildInfo.get("install"))
		val iStepsObj = JSONValue.parse(iStepsStr)
		val iStepsJson = iStepsObj.asInstanceOf[JSONArray]
		iStepsJson
	}
	def getCleanSteps(): JSONArray = {
		parseBuildJSON()
		val buildInfo = parseBuildJSON()
		val cStepsStr = JSONValue.toJSONString(buildInfo.get("clean"))
		val cStepsObj = JSONValue.parse(cStepsStr)
		val cStepsJson = cStepsObj.asInstanceOf[JSONArray]
		cStepsJson
	}
	private def parseBuildJSON(): JSONObject = { 
		val buildObj: Object = JSONValue.parse(buildConf)
		val buildJson = buildObj.asInstanceOf[JSONObject]
		buildJson
	}
	
}