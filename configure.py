#!/usr/bin/env python

"""
Python-based configuration script to amend Ant build file (build.xml)
as necessary depending on target platform.

Usage: chmod +x configure.py
\t./configure.py
"""
import sys
import optparse
import re
import os
import subprocess

def configureBuild(args):

	env_SCALA_HOME = '/path/to/scala/dir'
	system_family = 'an.operating.system'

	# Detect operating system
	try:
		uname = subprocess.check_output(['uname', '-s'])
		if(re.match('.*n[i|u]x|.*BSD|.*CYGWIN', uname)):
			print('\nDetected system: Linux/Unix-like (not Mac OS X).\n')
			system_family = '*nix'

		elif(re.match('.*Darwin', uname)):
			print('\nDetected system: Darwin/Mac OS X.\n')
			system_family = 'darwin'
	except:
		print('\nDetected system: Windows.\n')
		system_family = 'windows'

	# Detect windowing system on Unix-likes, not Mac OS X.
	if(system_family == '*nix'):
		pass

	# Find required Scala distribution and associated tools
	# necessary to build Gaudi on the system.
	# ...

def removeEnvVars():
	os.unsetenv(SCALA_HOME)

def showCLIoptions():
	print(__doc__)

configureBuild(sys.argv)
