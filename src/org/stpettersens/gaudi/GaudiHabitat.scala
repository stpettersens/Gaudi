/*
Gaudi platform agnostic build tool
Copyright 2010 Sam Saint-Pettersen.

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
import java.io.File
import scala.util.matching.Regex
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.List

object GaudiHabitat {

	private def getPaths(): List[String] = {
		val pathVar: String = System.getenv("PATH")
		val winPathPattn: Regex = """([\:\w\d\s\.\-\_\\]+)""".r
		val nixPathPattn: Regex = """([\w\d\s\.\-\_\/]+)""".r
		var pathPattn: Regex = null
		
		if(getOSFamily() == 0) {
			pathPattn = winPathPattn
		}
		else if(getOSFamily() == 1) {
			pathPattn = nixPathPattn
		}
		val paths = new ListBuffer[String]()
		for(p <- pathPattn.findAllIn(pathVar)) {
			paths += p
		}
		paths.toList
	}
	private def getOSFamily(): Int = {
		var osFamily: Int = -1
		val env: (String, String) = getEnvAndOS()
		val osFamilyPattn: Regex = """(\w+)[\s\d\w]*""".r
		var osFamilyPattn(osName) = env._2
		osName match {
			case "Windows" => osFamily = 0
			case "Linux" => osFamily = 1
			case "Macintosh" => osFamily = 1
		}
		osFamily
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
	// TODO: Possibly change this to just return the executable, no param
	def getExeWithExt(command: String): (String, String) = {
		var pathTerm: String = null
		if(getOSFamily() == 0) {
			pathTerm ="\\"
		}
		else if(getOSFamily() == 1) {
			pathTerm = "/"
		}
		val exts = new Array[String](5)
		exts(0) = ".exe"
		exts(1) = ".bat"
		exts(2) = ".cmd"
		exts(3) = ".sh"
		exts(4) = ""
		val exePattn: Regex = """([\w\d\+\-\_]+)\s*(.*)""".r
		var exePattn(exe, param) = command
		var ext: String = null
		val paths: List[String] = getPaths()
		for(path <- paths) {
			for(ext <- exts) {
				if(new File(path+pathTerm+exe+ext).exists() 
				&& new File(path+pathTerm+exe+ext).isFile()) {
					return (path+pathTerm+exe+ext, param)
				}
			}
		}
		(null, null)
	}
}