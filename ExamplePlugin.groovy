/*
	Example plugin for Gaudi build tool
	@author Sam-Saint-Pettersen
	@usage Plug-in for Gaudi demonstration
*/
import org.stpettersens.gaudi.GaudiPlugin
public class ExamplePlugin extends GaudiPlugin {
	
	ExamplePlugin() {
		pName = "Example plug-in"
		pAction = "Display message"
		pVersion = "1.0"
		pAuthor = "Sam Saint-Pettersen"
		pUrl = "http://github.com/stpettersens/Gaudi"
		pInitable = true
	}
	public void run() {
		println "Hello from ${pName}"
	}
}
