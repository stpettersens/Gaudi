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
import java.util.Calendar
import java.text.{DateFormat,SimpleDateFormat}
import java.io.{PrintWriter,FileOutputStream,IOException}

class GaudiBase {

	val caesarOffset: Array[Int] = Array(0x22, 0x1C, 0xA, 0x1F, 0x8, 0x2, 0x24, 0xF, 0x13, 0x25, 0x40, 0x10)
	val LogFile: String = "gaudi.log"
	
	// Dump output to log file.
	protected def logDump(message: String, isLogging: Boolean): Unit = {
		val calendar: Calendar = Calendar.getInstance()
		val sdf: SimpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
		val timeStamp: String = sdf.format(calendar.getTime())
		if(isLogging) {
			writeToFile(LogFile, String.format("[%s]\r\n%s", timeStamp, message), true)
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
			case ioe: IOException =>  println(ioe.getMessage)
		}
		finally {
			out.close()
		}
	}

	// Encode a message in a simple Caesar cipher
	protected def encodeText(message: String) : String = {
		var emessage: String = "";
		var x: Int = 0;
		for(s <- message) {
			
			if(x == caesarOffset.length - 1) x = 0;
			var cv: Int = message.charAt(x).asInstanceOf[Int] - caesarOffset(x);
			var ca: Char = cv.asInstanceOf[Char];
			emessage += ca
			x += 1
		}
		emessage
	}

	// Decode a message in a simple Caesar cipher
	protected def decodeText(message: String) : String = {
		var umessage: String = "";
		var x: Int = 0;
		for(s <- message) {
			
			if(x == caesarOffset.length - 1) x = 0;
			var cv: Int = message.charAt(x).asInstanceOf[Int] + caesarOffset(x);
			var ca: Char = cv.asInstanceOf[Char];
			umessage += ca
			x += 1
		}
		umessage
	}
}
