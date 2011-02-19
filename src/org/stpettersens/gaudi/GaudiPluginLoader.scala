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
import java.io.File;
import scala.util.matching.Regex

class GaudiPluginLoader(plugin: String, logging: Boolean) {

	// First of all, extract plugin code from the plugin archive (Zip File)
	val unpacker = new GaudiPacker(plugin, logging)
	val codeFile = unpacker.extrZipFile()
	
	// Second step is to load pass the code onto the right script handler by
	// file extension - *.groovy is Groovy code; *.py is Jython code.
	val langPattnGvy: Regex = """([\w\:\\//]+.groovy)""".r
	val langPattnJyt: Regex = """([\w\:\\//]+.py)""".r
	
	codeFile match {
		case langPattnGvy(x) => new GaudiGroovyPlugin(codeFile, logging)
		case langPattnJyt(x) => new GaudiJythonPlugin(codeFile, logging)
	}
	// Delete extracted code file when finshed with
	new File(codeFile).delete
}
