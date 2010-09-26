/*
	Example plugin for Gaudi build tool
	@author Sam-Saint-Pettersen
	@usage Plug-in for Gaudi demonstration
*/
import org.stpettersens.gaudi.GaudiPluginBase
public class ExamplePlugin extends GaudiPluginBase {

	def pName = "Example plug-in"

	public boolean initialize() { // TEMPORARY, USE BINDING FOR INITABLE VARIABLE
		return true
	}
	
	public void run() {
		println "Hello from ${pName}"
	}
}
