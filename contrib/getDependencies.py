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
# python /contrib/getDependencies.py local|travis
#

import sys
import urllib
from subprocess import call

def getDependencies(client):

	deps = ["Scala 2.9.3", "txtrevise utility", "JSON.simple 1.1", "Apache Commons IO 2.2", "JNR POSIX 3.0.7"] #, "Google Guava 18.0"]

	urls = [
	"http://www.scala-lang.org/files/archive/scala-2.9.3.tgz",
	"https://raw.githubusercontent.com/stpettersens/txtrevise/master/python/txtrevise.py",
	"http://json-simple.googlecode.com/files/json_simple-1.1.jar",
	"http://mirror.gopotato.co.uk/apache//commons/io/binaries/commons-io-2.4-bin.zip",
	"https://dl.dropboxusercontent.com/u/34600/jars/jnr-posix-3.0.7.jar"] #,
	 #"http://search.maven.org/remotecontent?filepath=com/google/guava/guava/18.0/guava-18.0.jar"]

	dests = ["scala-2.9.3.tgz", "txtrevise.py", "json_simple-1.1.jar", "commons-2.4.bin.zip", "jnr-posix-3.0.7.jar"] #, "guava-18.0.jar"]

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
		#call(["wget", "-O", current, urls.pop(0)])

		# Extract TAR files.
		if current.find(".tgz") != -1 or current.find("tar.gz") != -1:
			call(["tar", "xfz", current])
			call(["mv", "scala-2.9.3", "scala"])
			call(["sudo", "cp", "-r", "scala", "/opt"])
			call(["rm", "-f", "-r", "scala", current])

		# Extract ZIP files.
		if current.find(".zip") != -1:
			call(["unzip", "-qq", current])
			call(["cp", "commons-io-2.4/commons-io-2.4.jar", "lib/commons-io-2.2.jar"])
			call(["rm", "-f", "-r", "commons-io-2.4", current])

		# Copy Python files.
		if current.find(".py") != -1:
			call(["chmod", "+x", current])
			call(["mv", current, current[:9]])
			call(["sudo", "cp", current[:9], "/usr/bin"])
			call(["rm", current[:9]])

		# Copy JARs.
		if current.find(".jar") != -1:
			call(["cp", current, "lib"])
			call(["rm", current])

if __name__ == '__main__':
	getDependencies(sys.argv[1])
