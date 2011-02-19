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

// NOTE: Interface for interoperability between GaudiPlugin
// written in Java and derived plugins written in Groovy or Jython.
public interface IGaudiPlugin {
	public boolean initialize();
	public String getName();
	public String getAction();
	public void run();
	public void doCommand(String command, String param);
}
