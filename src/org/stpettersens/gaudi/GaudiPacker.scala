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
import java.io._
import java.util.zip._

class GaudiPacker(archFile: String) {
	
	val bufSize: Int = 1024 // Size of buffer
	val buffer: Array[Byte] = new Array(bufSize) // Define the buffer
	var entries: Enumeration = null
	
	// Extract a zip file
	def extrZipFile(): Unit = {
		try {
			val zipFile = new ZipFile(archFile)
			println(zipFile.entries)
		}
		catch {
			case ioe: IOException => GaudiApp.displayError(ioe)
		}
	}
}
