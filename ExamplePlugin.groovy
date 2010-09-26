/*
	Example plugin for Gaudi build tool
	@author Sam-Saint-Pettersen
	@usage Plug-in for Gaudi demonstration
*/
import org.stpettersens.gaudi.GaudiPluginBase
public class ExamplePlugin extends GaudiPluginBase {
		
	def name = "Example plug-in"
	def version = "1.0"
	def author = "Sam Saint-Pettersen"
	def url = "http://github.com/stpettersens/Gaudi"
	def initable = true
	
	public void run() {
		println "Hello from ${name}"
	}
}
