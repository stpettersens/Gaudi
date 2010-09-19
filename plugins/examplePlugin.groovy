/*
	Example plugin for Gaudi build tool
*/
public class examplePlugin extends GaudiPlugin {

	examplePlugin() {
		name = "Example plug-in"
		action = "Display message"
		version = "0.1"
		author = "Sam Saint-Pettersen"
		url = "http://github.com/stpettersens/Gaudi"
		initable = true
	}
	def run() {
		println "\tHello from ${name}."
	}
}
