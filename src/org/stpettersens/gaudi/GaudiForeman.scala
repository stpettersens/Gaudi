/*
Gaudi platform agnostic build tool
Copyright 2010-2011 Sam Saint-Pettersen.

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
		var shardStr: String = "n"
		try {
			val objPattn = new Regex(objectName)
			shardStr = JSONValue.toJSONString(buildJson.get(objectName))
		}
		catch {
			case ex: Exception => {
				GaudiApp.displayError("Instructions (Badly formatted JSON)")
			}
			// TODO: PREVENT NULL POINTER EXCEPTIONS FROM OCCURING
		}
		JSONValue.parse(shardStr)
	}
	// Get [part] from parsed preamble
	def getPart(part: String) = {
		val partStr = JSONValue.toJSONString(getPreamble().get(part))
		partStr.replaceAll("\"", "")
	}
	// Get target from parsed preamble
	def getTarget(): String  = {
		getPart("target")
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
