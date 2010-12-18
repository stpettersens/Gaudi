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
import java.net._
import java.io._

class GaudiMessenger(logging: Boolean) {
	
	// Define global logger object
	val logger = new GaudiLogger(logging)
	
	var port: Int = 3082
	var serverSocket: ServerSocket = null
	var clientSocket: Socket = null
	
	try {
		serverSocket = new ServerSocket(port)
		clientSocket = serverSocket.accept()
		listen()
	}
	catch {
		case ioe: IOException => {
			GaudiApp.displayError(ioe.getMessage)
		}
	}	
	
	private def listen() : Unit = {
		logger.dump("Client connected.")
	}
	
	private def reply() : Unit = {
		

	}
}