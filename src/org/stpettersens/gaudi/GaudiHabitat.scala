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
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.List

object GaudiHabitat {

	private def getPaths(): List[String] = {
		val pathVar: String = System.getProperty("java.library.path")
		val pathPattn: Regex = """([\:\w\d\s\.\-\_\\\/]+)""".r
		val paths = new ListBuffer[String]()
		for(p <- pathPattn.findAllIn(pathVar)) {
			paths += p
		}
		paths.toList
	}	
	def getEnvAndOS(): (String, String) = {
		(
		String.format(
		"%s %s", 
		System.getProperty("java.vm.name"), 
		System.getProperty("java.version")
		),
		System.getProperty("os.name")
		)
	}
	def getExeWithExt(command: String): (Boolean, String, String) = {
		var doExec: Boolean = false
		val exts = new Array[String](5)
		exts(0) = ""
		exts(1) = ".exe"
		exts(2) = ".bat"
		exts(3) = ".sh"
		exts(4) = ".cmd"
		val exePattn: Regex = """(\w+)(.+)""".r
		var exePattn(exe, param) = command
		var ext: String = ".bat"
		val paths: List[String] = getPaths()
		var path: String = ""
		for(p <- paths) {
			path = p+"\\"
			if(new File(path+exe+ext).exists()) {
				doExec = true
				return (doExec, path+exe+ext, " "+param)
			}	
		}
		(doExec, path+exe+ext, " "+param)
	}
}