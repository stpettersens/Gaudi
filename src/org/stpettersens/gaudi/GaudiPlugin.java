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

// The Gaudi plug-in base class for derived Groovy-based plug-ins
public class GaudiPlugin implements IGaudiPlugin {

	public static String Name = "Unspecified plug-in"; // Plug-in name
	public static String Action = "Unspecifed action"; // Plug-in action
	public static String Version = "Unspecified version"; // Plug-in version 
	public static String Author = "Unspecified author"; // Plug-in author
	public static String Url = "Unspecified URL"; // Plug-in URL
	public static boolean Initiable = false;
	public GaudiBuilder builder = new GaudiBuilder(null, null, false, true, false);
	
	// Initalize method does *not* need to be redefined in derived plug-ins
	public final boolean initialize() {
		return Initiable;
	} 
	// Non-redefinable method to return plug-in name
	public final String getName() {
		return Name;
	}
	// Non-redefinable method to return plug-in action
	public final String getAction() {
		return Action;
	}
	// But, run methods should be redefined in derived plug-ins
	public void run() {
		System.out.println(String.format("\tNo run code implemented for %s.", Name));
	}
	// Execute a Gaudi command
	public void doCommand(String command, String param) {
		builder.doCommand(command, param);
	}
}
