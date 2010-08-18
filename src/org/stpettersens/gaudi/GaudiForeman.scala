/*
 * Gauldi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import org.json.simple.{JSONValue,JSONObject,JSONArray}
import scala.util.matching.Regex

class GaudiForeman(buildConf: String) {
	
	// Parse build config into build JSON object on initialization
	val buildJson: JSONObject = parseBuildJSON()
	
	private def parseBuildJSON(): JSONObject = { 
		val buildObj: Object = JSONValue.parse(buildConf)
		buildObj.asInstanceOf[JSONObject]
	}
	// Get sub-object 'shard' from build JSON object 
	private def getShard(objectName: String): Object = {
		var shardStr = ""
		try {
			val objPattn = new Regex(objectName)
			shardStr = JSONValue.toJSONString(buildJson.get(objectName))
		}
		catch {
			case ex: Exception => {
				GaudiApp.displayError("Instructions (Bad JSON)")
				GaudiLogger ! ex
			}
		}
		JSONValue.parse(shardStr)
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
	// Get an execution action
	def getAction(action: String): JSONArray = {
		getShard(action).asInstanceOf[JSONArray]
	}
}