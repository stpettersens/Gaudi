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
package org.stpettersens.gaudi;
import groovy.lang.GroovyClassLoader;
import java.io.*;

// NOTE: The GaudiGroovyPlugin implementation code is written in 
// Java rather than Scala for compatibility reasons.(Specifically casting issues).
public class GaudiGroovyPlugin {

	@SuppressWarnings("unchecked")
	GaudiGroovyPlugin(String plugin, boolean logging) throws Exception {
		
		GaudiLogger logger = new GaudiLogger(logging);
		
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			Class pluginClass = gcl.parseClass(new File(plugin));
			Object aPlugin = pluginClass.newInstance();
			IGaudiPlugin gPlugin = (IGaudiPlugin) aPlugin;
			Object init = gPlugin.initialize();
			Boolean bInit = (Boolean) init; // Cast init value to Boolean *object*
		
			if(bInit) {
				// On successful initialization, display feedback and run the plug-in
				String name = gPlugin.getName().toString();
				String action = gPlugin.getAction().toString();
				System.out.println("Initialized Groovy-based plug-in.\n");
				logger.dump(String.format("Initialized Groovy-based plug-in -> %s.", name));
				System.out.println(String.format("[ %s => %s ]", name, action));
				gPlugin.run();
			
			}
			else {
				// Otherwise, display the standard did not load feedback
				System.out.println("Error with: Groovy-based Plug-in (Failed to load).");
				logger.dump("Failed to load plug-in.");
			}
		}
		catch(Exception e) {
			System.out.println("\n\tError in plug-in code:\n\t\t" + e);
			logger.dump("Error in plug-in code.");
		}
	}
}
