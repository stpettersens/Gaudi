/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import org.json.simple.{JSONObject,JSONArray}
import org.apache.commons.io.FileUtils._
import org.apache.commons.io.filefilter.WildcardFileFilter
import scala.util.matching.Regex
import java.io._

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	// Substitute variables for values
	private def substituteVars(action: Array[Object]): Unit = {
		println(preamble)
	}
	// Handle wild cards in parameters such as *.scala, *.cpp,
	// to compile all Scala or C++ files in the specied dir
	private def handleWildcards(param: String): String = {
		var dir = new File(".")
		val filePattn: Regex = """(\*)\.(\w\d+)""".r
		var filePattn(wc, ext) = param
		val fileFilter = new WildcardFileFilter("*." + ext)
		val files: Array[File] = dir.listFiles(fileFilter)
		val files = new Array[String](2)
		var newParam: String = null
		for(file <- files) {
			newParam += file
		}
		newParam
	}
	// Extract command and param for execution
	private def extractCommand(cmdParam: String): (String, String) = {
		val cpPattn: Regex = 
		"""\{\"(\w+)\"\:\"([\\\/\"\w\d\s\$\.\*\,\_\+\-\>\_]+)\"\}""".r
		var cpPattn(cmd: String, param: String) = cmdParam
		(cmd, param)
	}
	// Print an error related to action or command and exit
	private def printError(error: String): Unit = {
		println(String.format("\tAborting: %s.", error))
		System.exit(-1)
	}
	// Print executed command
	private def printCommand(cmd: String, param: String): Unit = {
		if(beVerbose) {
			println(String.format("\t:%s %s", cmd, param))
		}
	}
	// Execute a command in the action
	def doCommand(cmd: String, param: String): Unit = {
	
		// Do not print "echo" commands, but do others
		if(cmd != "echo") printCommand(cmd, param)
		cmd match {
			case "exec" => {
				val exe: (String, String) = GaudiHabitat.getExeWithExt(param)
				if(exe._1 != null) {
					val param: String = handleWildcards(exe._2)
					println(param)
					var p: Process = Runtime.getRuntime().exec(exe._1 + " " + param)
					val reader = new BufferedReader(
					new InputStreamReader(p.getErrorStream())
					)
					var line: String = reader.readLine()
					if(line != null) println(String.format("\t~ %s", line))
				}
			}
			case "mkdir" => {
				val aDir: Boolean = new File(param).mkdir()
				if(!aDir) {
					printError(
					String.format("Problem making dir -> %s", param)
					)
				}
			}
			case "echo" => println(String.format("\t# %s", param))
			case "rmve" => new File(param).delete()
			case "copy" => {
				val srcDest: Array[String] = param.split("->")
				copyFile(new File(srcDest(0)), new File(srcDest(1)))
			}
			case "move" => {
				val srcDest = param.split("->") 
				moveFile(new File(srcDest(0)), new File(srcDest(1)))
			}
			case _ => {
				printError(String.format("%s is an invalid command", cmd))
			}
		}
	}
	// Execute an action 
	def doAction(action: JSONArray): Unit = {
		try {
			val actionArray = action.toArray()
			//substituteVars(actionArray)
			for(cmdParam <- actionArray) {
				val cpPair = extractCommand(cmdParam.toString)
				doCommand(cpPair._1, cpPair._2)
			}
		}
		catch {
			case ex: Exception => {
				println(String.format("\t[%s]", ex.getMessage))
				printError("Encounted an invalid action or command")
			}
		}
	}
}