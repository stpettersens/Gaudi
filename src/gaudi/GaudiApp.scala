/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package gaudi
import scala.io.Source
import java.io.IOException
import org.json.simple.{JSONObject,JSONArray}

object GaudiApp {
	
  var buildFile: String = "build.json" // Default build file
  var beVerbose: Boolean = true // Gaudi is verbose by default
	  
  def main(args: Array[String]): Unit = {
	  var fOverride: Boolean = false
	  /* Default behavior is to lbuild project following
	  build file in the current directory */
	  if(args.length == 0) doRun(0)
	  // Handle command line arguments
	  else if(args.length > 0 && args.length < 4){
	 	  for(arg <- args) {
	 	 	  arg match {
	 	 	 	  case "install" => doRun(1)
	 	 	 	  case "clean" => doRun(2)
	 	 	 	  case "-i" => displayUsage()
	 	 	 	  case "-v" => displayVersion() 	 
	 	 	 	  case "-g" => generateNativeFile()
	 	 	 	  case "-m" => generateMakefile()
	 	 	 	  case "-q" => beVerbose = false
	 	 	 	  case "-f" => fOverride = true
	 	 	 	  case _ => if(fOverride) buildFile = arg
	 	 	  }
	 	  }
	  }
	  else displayError("Arguments (requires 0-3 arguments)")
  }
  // Load and delegate parse and execution of build file
  def doRun(operation: Int): Unit = {
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
	 	  val bParser = new GaudiBuildParser(buildConf)
	 	  val bExecutor = new GaudiBuildExecutor(bParser.getPreamble())
	 	  var taskVerb: String = ""
	 	  val taskTarget: String = bParser.getTarget()
	 	  operation match {
	 	 	  case 0 => { // Build project
	 	 	 	  taskVerb = "Building"
	 	 	 	  bExecutor.doBuild(bParser.getBuildSteps())
	 	 	  }
	 	 	  case 1 => { // Install project
	 	 	 	  taskVerb = "Installing"
	 	 	 	  bExecutor.doInstall(bParser.getInstallSteps())
	 	 	  }
	 	 	  case 2 =>{ // Clean project
	 	 	 	  taskVerb = "Cleaning"
	 	 	 	  bExecutor.doClean(bParser.getCleanSteps())
	 	 	  }
	 	  }
	 	  if(beVerbose) {
	 	 	  println(String.format("%s %s...", taskVerb, taskTarget))
	 	  }
	  }
  }
  def generateNativeFile(): Unit = {
	  // TODO
  }
  def generateMakefile(): Unit = {
	  // TODO
  }
  def displayError(ex: Exception): Unit = {
	  println(String.format("\nError with: %s.", ex.getMessage()))
	  displayUsage()
  }
  // Overloaded for String parameter
  def displayError(ex: String): Unit = {
	  println(String.format("\nError with: %s.", ex))
	  displayUsage()
  }
  def displayVersion(): Unit = {
	  // TODO
  }
  def displayUsage(): Unit = {
	  println("\nGaudi platform agnostic build tool")
	  println("Copyright (c) 2010 Sam Saint-Pettersen")
	  println("\nReleased under the MIT License.")
	  println("\nUsage: gaudi [-i|-v|-g|-m][-q -f <build file> <operation>]")
	  println("\n-i: Display usage information and quit.")
	  println("-v: Display version information and quit.")
	  println("-g: Generate native Gaudi build file (build.json).")
	  println("-m: Generate GNU Makefile from build.json.")
	  println("-q: Mute console output, except for errors (Quiet mode).")
	  println("-f: Use <build file> instead of build.json.\n")
	  System.exit(-1)
  }
}