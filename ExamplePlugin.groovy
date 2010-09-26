/*
	Example plugin for Gaudi build tool
*/
import org.stpettersens.gaudi.GaudiPlugin
public class ExamplePlugin extends GaudiPlugin {

	ExamplePlugin() {
		/*name = "Example plug-in"
		action = "Display message"
		version = "0.1"
		author = "Sam Saint-Pettersen"
		url = "http://github.com/stpettersens/Gaudi"*/
		initable = true
	}
	public void run() {
		println "Hello from ${name}"
	}
}
