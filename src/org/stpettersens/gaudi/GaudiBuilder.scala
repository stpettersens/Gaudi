/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import org.json.simple.{JSONObject,JSONArray}
import scala.util.matching.Regex
import java.io._
import java.nio.channels.FileChannel

class GaudiBuilder(preamble: JSONObject, beVerbose: Boolean)  {
	
	// Substitute variables for values
	private def substituteVars(action: Array[Object]): Unit = {
		println(preamble)
	}
	// Extract command and param for execution 
	private def extractCommand(cmdParam: String): (String, String) = {
		val cpPattn: Regex = 
		"""\{\"(\w+)\"\:\"([\/\\\"\w\d\s\$\.\*\,\_\+\-\>\_]+)\"\}""".r
		var cpPattn(cmd: String, param: String) = cmdParam
		(cmd, param)
	}
	// Print an error related to action or command and exit
	private def printError(error: String): Unit = {
		println(String.format("\tAborting: %s.", error))
		GaudiLogger ! error
		System.exit(-1)
	}
	// Print executed command
	private def printCommand(cmd: String, param: String): Unit = {
		if(beVerbose) {
			println(String.format("\t:%s %s", cmd, param))
		}
		GaudiLogger ! (cmd, param)
	}
	// Execute a command in the action
	def doCommand(cmd: String, param: String): Unit = {
	
		// Do not print "echo" commands, but do others
		if(cmd != "echo") printCommand(cmd, param)
		
		// Copy a file method
		def copy(): String = {
			val srcDestPair: Array[String] = param.split("->")
			val srcFile = new File(srcDestPair(0))
			val destFile = new File(srcDestPair(1))	
			var in, out: FileChannel = null
			try {
				in = new FileInputStream(srcFile).getChannel()
				out = new FileOutputStream(destFile).getChannel()
				in.transferTo(0, in.size(), out)
			}
			catch {
				case ex: Exception => {
					printError(
					String.format("Problem copying %s -> %s", 
					srcDestPair(0), srcDestPair(1)))
					GaudiLogger ! ex
				}
			}
			finally {
				if(in != null) in.close()
				if(out != null) out.close()
			}
			srcDestPair(0) // Return src filename
		}
		cmd match {
			case "exec" => {
				val exe: (String, String) = GaudiHabitat.getExeWithExt(param)
				GaudiLogger ! exe
				if(exe._1 != null) {
					var p: Process = Runtime.getRuntime().exec(exe._1 + " " + exe._2)
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
			case "copy" => copy() // Just copy file
			case "move" => new File(copy()).delete() // Copy and delete src file
			case _ => {
				printError(
				String.format("%s is an invalid command", cmd)
				)
				GaudiLogger ! ("Invalid command ->", cmd)
			}
		}
	}
	// Execute an action 
	def doAction(action: JSONArray): Unit = {
		try {
			val actionArray = action.toArray()
			//substituteVars(actionArray)
			for(cmdParam <- actionArray) {
				val cpPair = extractCommand(cmdParam.toString())
				doCommand(cpPair._1, cpPair._2)
			}
		}
		catch {
			case ex: Exception => {
				println("\t[" + ex.getMessage() + "]")
				printError("Encounted an invalid action or command")
			}
		}
	}
}