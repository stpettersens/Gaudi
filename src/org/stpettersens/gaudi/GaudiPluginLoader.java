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
package org.stpettersens.gaudi;
import groovy.lang.GroovyClassLoader;
import java.io.*;

// NOTE: The GaudiPluginLoader implementation code is written in 
// Java rather than Scala for compatibility reasons.(Specifically casting issues).
public class GaudiPluginLoader {

	@SuppressWarnings("unchecked")
	GaudiPluginLoader(String plugin, boolean logging) throws Exception {
		//GaudiLogger logger = new GaudiLogger(logging);
		GroovyClassLoader gcl = new GroovyClassLoader();
		Class pluginClass = gcl.parseClass(new File(plugin));
		Object aPlugin = pluginClass.newInstance();
		IGaudiPlugin gPlugin = (IGaudiPlugin) aPlugin;
		Object init = gPlugin.initialize();
		Boolean bInit = (Boolean) init; // Cast init value to Boolean *object*
		
		if(bInit) {
			// On successful initialization, display feedback and run the plug-in
			Object name = gPlugin.getName();
			Object action = gPlugin.getAction();
			String sName = (String) name; // Cast name to string
			String sAction = (String) action; // Cast action to string
			System.out.println("Initialized plug-in.\n");
			System.out.println(String.format("[ %s => %s ]", sName, sAction));
			gPlugin.run();
			//logger.dump(String.format("Initialized plug-in -> %s.", sName));
		}
		else {
			// Otherwise, display the standard did not load feedback.
			System.out.println("Error with: Plug-in (Failed to load).");
			//logger.dump("Failed to load plug-in.");
		}
	}
}
