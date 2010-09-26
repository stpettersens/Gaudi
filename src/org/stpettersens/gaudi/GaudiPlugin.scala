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

// The Gaudi plug-in base class for derived Groovy-based plug-ins
class GaudiPlugin extends GaudiPluginInterface {

	var name: String = "Gaudi plug-in"; // Plug-in name
	var action: String  = "Plug-in action"; // Plug-in action
	var version: String = "1.0"; // Plug-in version 
	var author: String = "A.B Somebody"; // Plug-in author
	var url: String = "http://www.a.url.xyz"; // Plug-in URL
	var initable: Boolean = false; // Init[ializ]able?, should be true only for derived
	
	// Initalize methods do *not* need to be redefined in derived plug-ins
	def initialize(): Boolean = {
		initable
	}
	// Non-redefinable method to return plug-in name
	def getName(): String = {
		name
	}
	// Non-redefinable method to return plug-in action
	def getAction(): String = {
		action
	}
	// But, run methods should be redefined in derived plug-ins
	def run(): Unit = {
		println(String.format("\tNo run code implemented for %s.", name))
	}
}
