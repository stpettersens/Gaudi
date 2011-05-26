#!/usr/bin/env python

"""
Python-based configuration script to
detect dependencies and amend Ant build file (build.xml)
as necessary depending on target platform.

This requires Python 2.7+.

Depends on txtrevise utility - which this
script may download & install when prompted.

Usage:
\t sh mark-exec.sh
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
			txtrevise = subprocess.check_output(['find', 'txtrevise.py'])
		else:
			txtrevise = subprocess.check_output(['where', 'txtrevise.py'])

	except Exception:
		txtrevise = '\W'

	checkDependency('txtrevise utility', txtrevise, system_family)

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
	checkDependency('Scala distribution', scala, system_family)
	checkDependency('Apache Ant', ant, system_family)
	writeEnvVar('SCALA_HOME', 'abc', system_family)

	# Find required JAR libraries necessary to build Gaudi
	# on this system.


def checkDependency(text, dep, osys):
	"""
	Check for a dependency.
	"""
	try:
		print('{0}:'.format(text))

		if(text[0:9] == 'txtrevise' and re.match('\w+', dep)):
			print('\tFOUND at {0}'.format(dep))
		
		elif(re.match('\s/*.*/*.*/*.*', dep) or re.match('\s\*.*\*.*\*.*', dep)):
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
			y = re.compile('y', re.IGNORECASE)
			n = re.compile('n', re.IGNORECASE)
			choice = 'x'
			while(not y.match(choice) or not n.match(choice)):
				choice = raw_input('Download and install it now? (y/n): ')
				if(y.match(choice)):
					urllib.urlretrieve
					('http://sams-py.googlecode.com/svn/trunk/'
					+ 'txtrevise/txtrevise.py', 'txtrevise.py')

					# Mark txtrevise utility as executable.
					if(osys == "*nix" or osys == "darwin"):
						os.system('chmod +x txtrevise.py')
					
					print('\nNow rerun this script.')
					break

				elif(n.match(choice)):
					break
		else:
			webbrowser.open_new_tab
			('http://stpettersens.github.com/Gaudi/dependencies.html')

		sys.exit(-1)

def writeEnvVar(var, value, osys):
	"""
	Write enviroment variables to build shell script
	or batch file.
	"""
	# Generate shell script on Unix-likes / Mac OS X.
	if(osys == '*nix' or osys == 'darwin'):
		f = open('build.sh', 'w')
		f.write('#!/bin/sh\nexport {0}="{1}"\nant $1\n'.format(var, value))
		f.close()
		# Mark shell script as executable.
		os.system('chmod +x build.sh') 

	# Generate batch file on Windows.
	else:
		f = open('build.bat', 'w')
		f.write('@set {0}="{1}"\r\n@ant %1\r\n'.format(var, value))
		f.close()

def amendAntBld(line_num, new_line, osys):
	"""
	Amend Ant buildfile using txtrevise utility.
	"""
	command = 'txtrevise.py -q -f build.xml -l {0} -m "<>"'
	+ ' -r "{1}"' .format(line_num, new_line)
	if(osys == '*nix' or osys == 'darwin'):
		os.system('./' + command)
	else:
		os.system(command)

def showCLIoptions():
	"""
	Show command line options.
	"""
	print(__doc__)

configureBuild(sys.argv)
