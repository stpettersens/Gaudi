/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import java.io.File
import scala.util.matching.Regex

object GaudiHabitat {

	private def getPath(): String = {
		System.getProperty("java.library.path")
	}	
	
	def getEnvAndOS(): (String, String) = {
		(
		System.getProperty("java.version"), 
		System.getProperty("os.name")
		)
	}
	def getExeWithExt(command: String): String = {
		val extArray = new Array[String](4)
		extArray(0) = ""
		extArray(1) = ".sh"
		extArray(2) = ".bat"
		extArray(3) = ".cmd"
		val pathPattn: Regex = """(\w\/\\)+""".r
		val exePattn: Regex = """(\w+).+""".r
		var exePattn(exe) = command
		var ext: String = null
		var exeFile = new File()
		for(ext <- extArray) {
			for(
			println(exeFile(exe + ext).exists().toString())
		}
		exe + ext
	}
}