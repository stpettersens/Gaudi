package org.stpettersens.gaudi
import java.util.Date
import java.text.{DateFormat,SimpleDateFormat)
import java.io.{PrintWriter,FileOutputStream,IOException}

object GaudiBase {
	
	protected def logDump(logging: Boolean, message: String): Unit = {
		val timestamp: Date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
		if(logging) {
			try {
				// TODO
			}
			catch {
				case ioe: IOException => println(ioe.Message);
			}
		}
	}
}

class GaudiBase {

	var logging: Boolean = false;
	var beVerbose: Boolean = true;
	val ErrCode: Int = -2;
	val LogFile: String = "gaudi.log";
	
	protected def logDump(message: String): Unit = {
		val timestamp: Date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
		if(isLogging()) {
			writeTo(
				LogFile, String.format("[{0}]\n{1}", timestamp, message), true
			)
		}
	}
}
