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
// NOTE: The GaudiPlugin implementation code is written in 
// Groovy for easy interaction with the derived Groovy-based plug-ins
public class GaudiPlugin {

	def name // String: Name of plug-in
	def action // String: Action of plug-in
	def version // String: Version of plug-in 
	def author // String: Author of plug-in
	def url // String: URL for plug-in website or associated online documentation
	def initable // Boolean: Init[ializ]able? Base class should be false, derived plug-ins true
	GaudiPlugin() {
		name = "Gaudi plug-in"
		version = "0.0"
		author = "A.B Somebody"
		url = "http://www.a.url.xyz"
		initable = false
	}
	// Initalize methods do *not* need to be redefined in derived plug-ins
	final def initialize() {
		return initable
	}
	// Method to return plug-in name and its action need not to be redefined either
	final def getNameAction() {
		return name
	}
	// But, run methods should be redefined in derived plug-ins
	def run() {
		println "\tNo run code implemented for ${name}."
	}
}
