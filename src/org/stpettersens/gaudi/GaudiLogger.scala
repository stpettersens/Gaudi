/*
 * Gaudi platform agnostic build tool
 * Copyright (c) 2010 Sam Saint-Pettersen
 * 
 * Released under the MIT License.
 * 
*/
package org.stpettersens.gaudi
import java.io._
import scala.actors.Actor

object GaudiLogger extends Actor {
	
	private val logFile: String = "gaudiDebug.log"
	
	private def dump(message: String) {
		
		var out: PrintWriter = null
		try {
			out = new PrintWriter(
			new FileOutputStream(logFile, true))
			// TODO Log date and time
			out.println(message)
		}
		catch {
			case ioe: IOException => {
				println(String.format("[Logging error: %s]", ioe.getMessage))
			}
		}
		finally {
			out.close()
		}
	}
	def act() {
		loop {
			react {
				case strMsg: String => dump(strMsg)
				case tstrMsg: (String, String) => {
					dump(String.format("%s %s", tstrMsg._1, tstrMsg._2))
				}
				case exMsg: Exception => dump(exMsg.getMessage)
				case _ => dump("Unimplemented type for logging.")
			}
		}
	}
}