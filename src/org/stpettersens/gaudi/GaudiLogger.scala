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
import java.util.Date
import java.text.{DateFormat,SimpleDateFormat}
import java.io.{PrintWriter,FileOutputStream,IOException}

object GaudiLogger {
	
	val logFile: String = "gaudiDebug.log"
	val timestamp: DateFormat = new SimpleDateFormat("[MM-dd-yyyy HH:mm:ss]")

	def dump(message: String): Unit = {
		var out: PrintWriter = null
		try {
			out = new PrintWriter(new FileOutputStream(logFile, true))
			out.println(timestamp.format(new Date()))
			out.println(message)
		}
		catch {
			case ioe: IOException => {
				println(String.format("[Logging error: %s]", ioe.getMessage))
			}
		}
		finally {
			out.close()
		}
	}
}