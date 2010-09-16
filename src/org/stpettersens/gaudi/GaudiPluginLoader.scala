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
import groovy.lang.{GroovyClassLoader,GroovyObject}
import java.io._

class GaudiPluginLoader(plugin: String) {
	
	val parent: ClassLoader = getClass().getClassLoader()
	val loader = new GroovyClassLoader(parent)
	var pluginClass = loader.parseClass(new File(plugin))
	
	// Initialize plug-in (invoke its init method)
	var pluginObj = pluginClass.asInstanceOf[GroovyObject]
	val result: Any = pluginObj.invokeMethod("initialize", null)
	if(result == 0) println("Plug-in loaded.")
	else GaudiApp.displayError("Plug-in (Failed to load).")
}
