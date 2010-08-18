/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import scala.io.Source
import scala.util.matching.Regex
import java.io.IOException
import org.json.simple.{JSONObject,JSONArray}

object GaudiApp {
  //////////////////////////////////////////////////////
  val appVersion: String = "0.1"
  val env: (String, String) = GaudiHabitat.getEnvAndOS()
  //////////////////////////////////////////////////////
  var buildFile: String = "build.json" // Default build file
  var beVerbose: Boolean = true // Gaudi is verbose by default
	  
  def main(args: Array[String]): Unit = {
	  var fSwitch: Boolean = false
	  var action: String = "build"
	  val filePattn: Regex = """(\w+.json)""".r
	  val actPattn: Regex = """([a-z]+)""".r
	  val cmdPattn: Regex =
	  """:([a-z]+)\s{1}([\\\/A-Za-z0-9\s\.\*\_\-\>\!\,]+)""".r
	  
	  /* Default behavior is to build project following
	  build file in the current directory */
	  if(args.length == 0) loadBuild(action)
	  
	  // Handle command line arguments
	  else if(args.length > 0 && args.length < 7) {
	 	  for(arg <- args) {
	 	 	  arg match {
	 	 	 	  case "-i" => displayUsage(0)
	 	 	 	  case "-v" => displayVersion() 	 
	 	 	 	  case "-g" => generateNativeFile()
	 	 	 	  case "-m" => generateMakefile()
	 	 	 	  case "-q" => beVerbose = false
	 	 	 	  case "-f" => fSwitch = true
	 	 	 	  case filePattn(f) => if(fSwitch) buildFile = arg
	 	 	 	  case actPattn(a) => action = a
	 	 	 	  case cmdPattn(c, p) => runCommand(c, p)
	 	 	 	  case _ => {
	 	 	 	 	  displayError(
	 	 	 	 	  String.format("Argument (%s is invalid)", arg)
	 	 	 	 	  )
	 	 	 	  }
	 	 	  }
	 	  }
	 	  loadBuild(action)
	  }
	  else displayError("Arguments (requires 0-6 arguments)")
  }
  // Just peform a stdin command; really just for testing implemented commands.
  // E.g. argument ":move a->b"
  def runCommand(cmd: String, param: String): Unit = {
	  // Create a new builder and feed it a dummy JSONObject
	  val builder = new GaudiBuilder(new JSONObject, beVerbose)
	  builder.doCommand(cmd, param)
	  System.exit(0)
  }
  // Load and delegate parse and execution of build file
  def loadBuild(action: String): Unit = {
	  var buildConf: String = ""
	  try {
	 	  for(line <- Source.fromFile(buildFile).getLines()) {
	 	 	  buildConf += line
	 	  }
	 	  // Shrink string, by replacing tabs with spaces;
	 	  // Gaudi build files should be written using tabs
	 	  buildConf = buildConf.replaceAll("\t","")
	  }
	  catch { // Catch I/O & general exceptions
	 	  case ioe: IOException => displayError(ioe)
	 	  case e: Exception => displayError(e)
	  }
	  finally {   
	 	  // Delegate to the foreman and builder
	 	  val foreman = new GaudiForeman(buildConf)
	 	  val builder = new GaudiBuilder(foreman.getPreamble(), beVerbose)
	 	  if(beVerbose) {
	 	 	  println(
	 	 	  String.format("[ %s => %s ]", foreman.getTarget(), action)
	 	 	  )
	 	  }	  
	 	  builder.doAction(foreman.getAction(action))
	  }
  }
  // Generate a Gaudi build file (build.json)
  def generateNativeFile(): Unit = {
	  // TODO
  }
  // Generate a Make compatible Makefile
  def generateMakefile(): Unit = {
	  // TODO
  }
  // Display an error
  def displayError(ex: Exception): Unit = {
	  println(String.format("\nError with: %s.", ex.getMessage()))
	  displayUsage(-1)
  }
  // Overloaded for String parameter
  def displayError(ex: String): Unit = {
	  println(String.format("\nError with: %s.", ex))
	  displayUsage(-1)
  }
  // Display version information and exit
  def displayVersion(): Unit = {
	  println(
	  String.format("\nGaudi v.%s [%s (%s)]\n", appVersion, env._1, env._2)
	  )
	  System.exit(0)
  }
  // Display usage information and exit
  def displayUsage(exitCode: Int): Unit = {
	  println("\nGaudi platform agnostic build tool")
	  println("Copyright (c) 2010 Sam Saint-Pettersen")
	  println("\nReleased under the MIT License.")
	  println("\nUsage: gaudi [-i|-v|-g|-m][-q -f <build file> <operation>]")
	  println("\n-i: Display usage information and quit.")
	  println("-v: Display version information and quit.")
	  println("-g: Generate native Gaudi build file (build.json).")
	  println("-m: Generate GNU Makefile from build.json.")
	  println("-q: Mute console output, except for :echo and errors (Quiet mode).")
	  println("-f: Use <build file> instead of build.json.\n")
	  System.exit(exitCode)
  }
}