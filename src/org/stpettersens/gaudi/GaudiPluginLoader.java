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
// NOTE: The GaudiPluginLoader implementation code is written in 
// Java rather than Scala for compatibility reasons.(Specifically casting issues).
package org.stpettersens.gaudi;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.io.*;

public class GaudiPluginLoader {
	
	// Define global logger object
	GaudiLogger logger = new GaudiLogger(); // Move t/f to constructor

	@SuppressWarnings("unchecked")
	GaudiPluginLoader(String plugin, boolean logging) throws Exception {
	
		// TODO: Implement plug-in access to internal commands
		// ...
		
		GroovyClassLoader loader = new GroovyClassLoader();
		loader.parseClass(new File("GaudiPlugin.groovy")); // !!!
		Class pluginClass = loader.parseClass(new File(plugin));
	
		// Cast plugin class to plugin groovy object
		GroovyObject pluginObj = (GroovyObject) pluginClass.newInstance();
		Object init = pluginObj.invokeMethod("initialize", null);
		Object name = pluginObj.invokeMethod("getName", null);
		Object action = pluginObj.invokeMethod("getAction", null);
		Boolean bInit = (Boolean) init; // Cast init to boolean *object*
		String sName = (String) name; // Cast name to string
		String sAction = (String) action; // Cast action to string
		
		if(bInit) {
			// On successful initialization, display feedback and run the plug-in
			System.out.println("Initialized plug-in.");
			System.out.println(String.format("[ %s => %s ]", sName, sAction));
			pluginObj.invokeMethod("run", null);
			logger.dump(logging, String.format("Initialized plug-in -> %s.", sName));
		}
		else {
			// Otherwise, display the standard did not load feedback.
			System.out.println("Error with: Plug-in (Failed to load).");
			logger.dump(logging, "Failed to load plug-in.");
		}
	}
}
