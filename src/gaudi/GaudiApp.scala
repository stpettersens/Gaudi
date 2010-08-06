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
	  // Default behavior is to load project's build file
	  if(args.length == 0) doBuild(buildFile)
	  // Handle command line arguments
	  else if(args.length > 0 && args.length < 4){
	 	  for(arg <- args) {
	 	 	  arg match {
	 	 	 	  case "-i" => displayUsage()
	 	 	 	  case "-v" => displayVersion() 	 
	 	 	 	  case "-g" => generateNativeFile()
	 	 	 	  case "-m" => generateMakefile()
	 	 	 	  case "-q" => beVerbose = false
	 	 	 	  case "-f" => fOverride = true
	 	 	 	  case _ => if(fOverride) doBuild(arg)
	 	 	  }
	 	  }
	  }
	  else displayError("Arguments (requires 0-3 arguments)")
  }
  // Load and delegate parse and execution of build file
  def doBuild(buildFile: String): Unit = {
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
	  finally { // Parse configuration via gaudiBuildParser class
	 	  val bParser = new GaudiBuildParser(buildConf)
	 	  if(beVerbose) {
	 		  println(String.format("Building %s...", bParser.getTarget()))
	 	  } 	  
	  }
  }
  def generateNativeFile(): Unit = {
	  
  }
  def generateMakefile(): Unit = {
	  println("generateMakefile()")
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
  def displayInfo(): Unit = {
	  println("\nGaudi platform agnostic build tool")
	  println("Copyright (c) 2010 Sam Saint-Pettersen")
	  println("\nReleased under the MIT License.\n")
  }
  def displayUsage(): Unit = {
	  displayInfo()
	  println("Usage: gaudi [-i|-v|-g|-m][-q -f <Gaudi build file>]")
	  println("\n-i: Display usage information and quit.")
	  println("-v: Display version information and quit.")
	  println("-g: Generate native Gaudi build file (build.json).")
	  println("-m: Generate GNU Makefile from build.json.")
	  println("-q: Mute console output, except for errors (Quiet mode).")
	  println("-f: Use <build file> instead of build.json.\n")
	  System.exit(-1)
  }
}