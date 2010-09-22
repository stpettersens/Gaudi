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
import scala.io.Source
import scala.util.matching.Regex
import java.io.IOException
import org.json.simple.{JSONObject,JSONArray}

object GaudiApp {
	
  //--------------------------------------------------------
  val appVersion: String = "0.1"
  val env: (String, String) = GaudiHabitat.getEnvAndOS()
  //--------------------------------------------------------
  var buildFile: String = "build.json" // Default build file
  var beVerbose: Boolean = true // Gaudi is verbose by default
  var logging: Boolean = false // Logging is disabled by default
  var logger: GaudiLogger = null
	  
  def main(args: Array[String]): Unit = {
      var pSwitch: Boolean = false
	  var fSwitch: Boolean = false
	  var action: String = "build"
	  val pluginPattn: Regex = """(\w+.groovy)""".r
	  val filePattn: Regex = """(\w+.json)""".r
	  val actPattn: Regex = """([a-z]+)""".r
	  val cmdPattn: Regex = """:([a-z]+)\s{1}([\\\/A-Za-z0-9\s\.\*\+\_\-\>\!\,]+)""".r
	  
	  /* Default behavior is to build project following
	  build file in the current directory */
	  if(args.length == 0) loadBuild(action)
	  
	  // Handle command line arguments
	  else if(args.length > 0 && args.length < 7) {
	 	  for(arg <- args) {
	 	 	  arg match {
	 	 	 	  case "-i" => displayUsage(0)
	 	 	 	  case "-v" => displayVersion()
	 	 	 	  case "-l" => logging = true
	 	 	 	  case "-n" => generateNativeFile()
	 	 	 	  case "-p" => pSwitch = true
	 	 	 	  case "-q" => beVerbose = false
	 	 	 	  case "-f" => fSwitch = true
	 	 	 	  case pluginPattn(p) => {
	 	 	 	 	  if(pSwitch) doPluginAction(arg)
	 	 	 	  }
	 	 	 	  case filePattn(f) => {
	 	 	 	 	  if(fSwitch) buildFile = arg
	 	 	 	  }
	 	 	 	  case actPattn(a) => action = a
	 	 	 	  case cmdPattn(cmd, param) => runCommand(cmd, param)
	 	 	 	  case _ => {
	 	 	 	 	  displayError(String.format("Argument (%s is invalid)", arg)) 	 	  
	 	 	 	  }
	 	 	  }
	 	  }
	 	  logger = new GaudiLogger(logging)
	 	  loadBuild(action)
	  }
	  else displayError("Arguments (requires 0-6 arguments)")
  }
  // Just peform a stdin command; really just for testing implemented commands.
  // E.g. argument ":move a->b"
  private def runCommand(cmd: String, param: String): Unit = {
	  // Create a new builder and feed it a dummy JSONObject
	  val builder = new GaudiBuilder(new JSONObject, beVerbose, logging)
	  builder.doCommand(cmd, param)
	  System.exit(0)
  }
  // Load and delegate parse and execution of build file
  private def loadBuild(action: String): Unit = {
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
	 	  val builder = new GaudiBuilder(foreman.getPreamble, beVerbose, logging)
	 	  if(beVerbose) {
	 	 	  println(String.format("[ %s => %s ]", foreman.getTarget, action))
	 	  }	  
	 	  builder.doAction(foreman.getAction(action))
	  }
  }
  // Generate a Gaudi build file (build.json)
  private def generateNativeFile(): Unit = {
	  // TODO
  }
  // Initialize and if successful run a plug-in
  private def doPluginAction(plugin: String): Unit = {
      new GaudiPluginLoader(plugin, logging)
      System.exit(0)
  }
  // Display an error
  def displayError(ex: Exception): Unit = {
	  println(String.format("\nError with: %s.", ex.getMessage))
	  logger.dump(ex.getMessage)
	  displayUsage(1)
  }
  // Overloaded for String parameter
  def displayError(ex: String): Unit = {
	  println(String.format("\nError with: %s.", ex))
	  logger.dump(ex)
	  displayUsage(1)
  }
  // Display version information and exit
  private def displayVersion(): Unit = {
	  println(String.format("Gaudi v.%s [%s (%s)]", appVersion, env._1, env._2))
	  System.exit(0)
  }
  // Display usage information and exit
  private def displayUsage(exitCode: Int): Unit = {
	  println("\nGaudi platform agnostic build tool")
	  println("Copyright (c) 2010 Sam Saint-Pettersen")
	  println("\nReleased under the Apache License v2.")
	  println("\nUsage: gaudi [-l][-i|-v|-n|-m][-q][-p <plug-in>]")
	  println("\t[-f <build file>][\"<:command>\"|<action>]")
	  println("\n-l: Enable logging of certain events.")
	  println("-i: Display usage information and quit.")
	  println("-v: Display version information and quit.")
	  println("-n: Generate native Gaudi build file (build.json).")
	  println("-p: Invoke <plug-in> action.")
	  println("-q: Mute console output, except for :echo and errors (Quiet mode).")
	  println("-f: Use <build file> instead of build.json.")
	  System.exit(exitCode)
  }
}
