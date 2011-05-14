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
import java.util.Date
import java.text.{DateFormat,SimpleDateFormat}
import java.io.{PrintWriter,FileOutputStream,IOException}

class GaudiBase {

	var logging: Boolean = false
	var beVerbose: Boolean = true
	val ErrCode: Int = -2
	val LogFile: String = "gaudi.log"
	
	protected def logDump(message: String, isLogging: Boolean): Unit = {
		val timestamp: DateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
		if(isLogging) {
			writeToFile(LogFile, 
			String.format("[{0}]\n{1}", timestamp, message), true)
		}
	}

	// File writing operations
	protected def writeToFile(file: String, message: String, append: Boolean): Unit = {
		var out: PrintWriter = null
		try {
			out = new PrintWriter(new FileOutputStream(file, append))
			out.println(message)
		}
		catch {
			case ioe: IOException =>  println(ioe.getMessage) // ! //printError(ioe.getMessage)
		}
		finally {
			out.close()
		}
	}

	// Execute a process
	protected def executeProcess(process: String, params: String, quiet: Boolean): Unit = {
		
		var p: Process = Runtime.getRuntime().exec(
		String.format("%s %s", process, params));
	}
}

object GaudiBase {
	
	protected def logDump(message: String, isLogging: Boolean): Unit = {
		val gb = new GaudiBase()
		gb.logDump(message, isLogging)
	}
}
