#
#	Example plugin for Gaudi build tool
#	@author Sam-Saint-Pettersen
#	@usage Plug-in for Gaudi demonstration
#	@lang Jython
#
from org.stpettersens.gaudi import GaudiPlugin
class ExamplePlugin(GaudiPlugin):
	def __init__(self):
		self.Name = "Example plug-in"
		self.Action = "Display message"
		self.Version = "1.0"
		self.Author = "Sam Saint-Pettersen"
		self.Url = "http://github.com/stpettersens/Gaudi"
		self.Initable = True
	
	def run(self):
		self.doCommand("echo", "Hello from %s" % self.Name)

plugin = ExamplePlugin()
