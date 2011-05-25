#!/usr/bin/env python

"""
Python-based configuration script to
detect dependencies and amend Ant build file (build.xml)
as necessary depending on target platform.

This requires Python 2.7+.

Depends on txtrevise utility - which this
script may download & install when prompted.

Usage: chmod +x configure.py
\t./configure.py
"""
import sys
import re
import os
import subprocess
import argparse
import urllib
import webbrowser

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
	txtrevise = 'txtrevise utility'
	scala = 'scala distribution'
	ant = 'apache ant'
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

	# Check for txtrevise utility,
	# if not found, prompt to download from code.google.com/p/sams-py 
	# Subversion repository over HTTP (in checkDependency(-,-)).
	try:
		if(system_family == '*nix' or system_family == 'darwin'):
			txtrevise = subprocess.check_output(['whereis', 'txtrevise'])
		else:
			txtrevise = subprocess.check_output(['where', 'txtrevise'])

	except WindowsError:
		txtrevise = '\W'

	checkDependency('txtrevise utility', txtrevise)

	# Find required Scala distribution and associated tools
	# necessary to build Gaudi on this system.
	try:
		if(system_family == '*nix' or system_family == 'darwin'):
			scala = subprocess.check_output(['whereis', 'scala'])
			ant = subprocess.check_output(['whereis', 'ant'])
		else:
			scala = subprocess.check_output(['where', 'scala'])
			ant = subprocess.check_output(['where', 'ant'])

	except WindowsError:
		pass

	# Choose appropriate path in results for each 
	# `whereis` or `where` query.
	checkDependency('Scala distribution', scala)
	checkDependency('Apache Ant', ant)
	writeEnvVar('SCALA_HOME', 'abc', system_family)

	# Find required JAR libraries necessary to build Gaudi
	# on this system.

def checkDependency(text, dep):
	"""
	Check for a dependency.
	"""
	try:
		print('{0}:'.format(text))
		if(re.match('.*/.*/.*', dep) or re.match('.*\.*\.*', dep)):
			print('\tFOUND at {0}'.format(dep))

		elif(re.match('\W', dep)):
			print('\tNOT FOUND.')
			raise RequirementNotFound(text)

		else:
			print('\tNOT FOUND.')
			raise RequirementNotFound(text)

	except RequirementNotFound as e:
		print("\nA requirement was not found. Please install it:")
		print("{0}.\n".format(e.value))

		if(text[0:9] == 'txtrevise'):
			print('\ntxtrevise utility.')
			choice = raw_input('Download and install it now? (y/n): ')
			if(choice == 'y' or choice == "Y"):
				urllib.urlretrieve(
				'http://sams-py.googlecode.com/svn/trunk/txtrevise/txtrevise.py',
				'txtrevise.py')
		else:
			webbrowser.open_new_tab('http://stpettersens.github.com/Gaudi#dl')
		
		sys.exit(-1)

def writeEnvVar(var, value, osys):
	"""
	Write enviroment variables to build shellscript.
	"""
	# Generate shell script on Unix-likes / Mac OS X.
	if(osys == '*nix' or osys == 'darwin'):
		f = open('build.sh', 'w')
		f.write('#!/bin/sh\nexport {0}="{1}"\nant\n'.format(var, value))
		f.close()
		# Mark shell script as executable.
		os.system('chmod +x build.sh') 

	# Generate batch file on Windows.
	else:
		f = open('build.bat', 'w')
		f.write('@set {0}="{1}"\r\n@ant\r\n'.format(var, value))
		f.close()

def amendAntBld(line_num, new_line):
	pass

def showCLIoptions():
	"""
	Show command line options.
	"""
	print(__doc__)

configureBuild(sys.argv)
