
#!/usr/bin/env python

#
# Gaudi dependencies gathering script.
# ---------------------------------
#
# Python-based configuration script to
# download dependencies for Gaudi such 
# as JAR libraries and Scala.
#
# This script requires Python 2.7+.
#
# Usage:
# \t./contrib/getDependencies.py null|travis
#

import sys
from subprocess import call
import urllib

def getDependencies(client):

	deps = ["Scala 2.9.3", "JSON.simple 1.1", "Apache Commons IO 2.2"]

	urls = [
	"http://www.scala-lang.org/files/archive/scala-2.9.3.tgz", 
	"http://json-simple.googlecode.com/files/json_simple-1.1.jar",
	"http://mirror.gopotato.co.uk/apache//commons/io/binaries/commons-io-2.4-bin.zip"]

	dests = ["scala-2.9.3.tgz", "json_simple-1.1.jar", "commons-2.4.bin.zip"]

	if client == "travis":
		pass
	else:
		deps.pop(0)
		urls.pop(0)
		dests.pop(0)

	while len(deps) > 0:
		current =  dests.pop(0)
		print "Downloading {0}...".format(deps.pop(0))
		urllib.urlretrieve(urls.pop(0), current)

		# Extract TAR files.
		if current.find(".tgz") != -1 or current.find("tar.gz") != -1:
			call(["tar", "xfvz", current])
			call(["mv", "scala-2.9.3", "scala"])
			call(["sudo", "cp", "-r", "scala", "/opt"])
			call(["rm", "-f", "-r", "scala"])

		# Extract ZIP files.
		if current.find(".zip") != -1:
			call(["unzip", "-qq", current])
			call(["cp", "commons-io-2.4/commons-io-2.4.jar", "lib/commons-io-2.2.jar"])
			call(["rm", "-f", "-r", "commons-io-2.4", current])

		# Copy JARs.
		if current.find(".jar") != -1:
			call(["cp", current, "lib"])
			call(["rm", current])

if __name__ == '__main__':
	getDependencies(sys.argv[1])
