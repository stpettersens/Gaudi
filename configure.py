#!/usr/bin/env python

"""
Python-based configuration script to amend Ant build file (build.xml)
as necessary depending on target platform.

This requires Python 2.7+.

Usage: chmod +x configure.py
\t./configure.py
"""
import sys
import optparse
import re
import os
import subprocess

class RequirementNotFound(Exception):
	"""
	RequirementNotFound exception class.
	"""
	def __init__(self, value):
		self.value = value
	def __str__(self):
		return repr(self.value)

def configureBuild(args):
	"""
	Configure build; entry method.
	"""
	env_SCALA_HOME = '/path/to/scala/dir'
	system_family = 'an.operating.system'
	system_desktop = 'desktop.environment'
	scala = 'scala.distribution'
	notify_lib = 'notification.library'

	# Detect operating system
	try:
		uname = subprocess.check_output(['uname', '-s'])
		if(re.match('.*n[i|u]x|.*BSD|.*CYGWIN', uname)):
			print('\nDetected system:\n\tLinux/Unix-like (not Mac OS X).\n')
			system_family = '*nix'

		elif(re.match('.*Darwin', uname)):
			print('\nDetected system:\n\tDarwin/Mac OS X.\n')
			system_family = 'darwin'

	except:
		print('\nDetected system:\nWindows.\n')
		system_family = 'windows'

	# Detect desktop environment on Linux/Unix-likes (not Mac OS X).
	if(system_family == '*nix'):
		system_desktop = os.environ.get('DESKTOP_SESSION')
		print('Detected desktop:\n\t{0}\n'.format(system_desktop))

	# Find required Scala distribution and associated tools
	# necessary to build Gaudi on the system.
	if(system_family == '*nix' or system_family == 'darwin'):
		scala = subprocess.check_output(['whereis', 'scala'])
		ant = subprocess.check_output(['whereis', 'ant'])
	
	else:
		pass

	checkDependency('Scala distribution', scala)
	checkDependency('Apache Ant', ant)
	writeEnvVars('SCALA_HOME', 'abc')

def checkDependency(text, dep):
	"""
	Check for a dependency.
	"""
	try:
		print('{0}:'.format(text))
		if(re.match('.*/.*/.*', dep)):
			print('\tFOUND at {0}'.format(dep))

		else:
			print('\tNOT FOUND.')
			raise RequirementNotFound(text)

	except RequirementNotFound as e:
		print("\nA requirement was not found. Please install it:")
		print("{0}.\n".format(e.value))
		sys.exit(-1)

def writeEnvVars(var, value):
	"""
	Write enviroment variables to build shellscript.
	"""
	f = open('build.sh', 'w')
	f.write('#!/bin/sh\nexport {0}="{1}"\nant'.format(var, value))
	f.close()
	os.system('chmod +x build.sh')

def removeEnvVars():
	"""
	Remove any set enviromental variables.
	"""
	os.unsetenv(SCALA_HOME)

def showCLIoptions():
	"""
	Show command line options.
	"""
	print(__doc__)

configureBuild(sys.argv)
