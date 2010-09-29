/*
	Example plugin for Gaudi build tool
	@author Sam-Saint-Pettersen
	@usage Plug-in for Gaudi demonstration
*/
import org.stpettersens.gaudi.GaudiPlugin
public class ExamplePlugin extends GaudiPlugin {
	
	ExamplePlugin() {
		Name = "Example plug-in"
		Action = "Display message"
		Version = "1.0"
		Author = "Sam Saint-Pettersen"
		Url = "http://github.com/stpettersens/Gaudi"
		Initable = true
	}
	public void run() {
		println "Hello from ${Name}"
	}
}
