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
#! Encode URLs to save line space? Will this save line space?
#import base64?
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
	notify_lib = 'notification.library'
	use_gnu = False

	# Detect operating system
	uname = subprocess.check_output(['uname', '-s'])
	if re.match('.*n[i|u]x|.*BSD|.*CYGWIN', uname):
		print('\nDetected system:\n\tLinux/Unix-like (not Mac OS X).\n')
		system_family = '*nix'

	elif re.match('.*Darwin', uname):
		print('\nDetected system:\n\tDarwin/Mac OS X.\n')
		system_family = 'darwin'

	else:
		print('\nDetected system:\nWindows.\n')
		system_family = 'windows'

	# Detect desktop environment on Unix-likes (not Mac OS X).
	if system_family == '*nix':
		system_desktop = os.environ.get('DESKTOP_SESSION')

		if re.match('x.*', system_desktop):
			system_desktop = 'Xfce'

		elif system_desktop == 'default':
			system_desktop = 'KDE'

		elif system_desktop == 'gnome':
			system_desktop = system_desktop.upper()

		print('Detected desktop:\n\t{0}\n'.format(system_desktop))

	# Check for txtrevise utility,
	# if not found, prompt to download from code.google.com/p/sams-py 
	# Subversion repository over HTTP - in checkDependency(-,-,-).
	try:
		# On Unix-likes, detect using `find`. On Windows, use `where`.
		if re.match('\*nix|darwin', system_family):
			txtrevise = subprocess.check_output(['find', 'txtrevise.py'])
		else:
			txtrevise = subprocess.check_output(['where', 'txtrevise.py'])

	except:
		txtrevise = '\W'

	checkDependency('txtrevise utility', txtrevise, system_family)

	# Find required JRE, JDK (look for a Java compiler),
	# Scala distribution and associated tools necessary to build Gaudi on this system.
	t_names = [ 'JRE (Java Runtime Environment)', 'JDK (Java Development Kit)',
	'Scala distribution', 'Ant' ]

	t_commands = [ 'java', 'javac', 'scala', 'ant' ]

	# If user specified to use GNU Foundation software
	# where possible, substitute `java` and `javac` for `gij` and `gcj`.
	if use_gnu: 
		t_names[0] = 'JRE (GNU GIJ)'
		t_names[1] = 'JDK (GNU GCJ)'
		t_commands[0] = 'gij'
		t_commands[1] = 'gcj'

	# On *nix, detect using `whereis`. On Windows use `where`.
	i = 0
	for c in t_commands:
		if re.match('\*nix|darwin', system_family):
			o = subprocess.check_output(['whereis', c])
		else:
			o = subprocess.check_output(['where', c])		
		checkDependency(t_names[i], o, system_family)
		i += 1

	# Write environment variable to a build file.
	writeEnvVar('SCALA_HOME', 'abc', system_family)

	# Find required JAR libraries necessary to build Gaudi on this system.
	l_names = [ 'json.simple', 'commons-io']
	l_jars = [ 'json_simple-1.1.jar', 'commons-io-2.0.1.jar']

	i = 0
	for l in l_jars:
		if re.match('\*nix|darwin', system_family):
			o = ''
			#o = subprocess.check_output(['find', 'lib/{0}'.format(l)])
		else:
			pass
			#o = subprocess.check_output(['find', '/C lib\{0}'.format(l)])
		checkDependency(l_names[i], o, system_family)

	# Done; now prompt user to run build script.
	print('\nDependencies met. Now run:\n')

	if re.match('\*nix|darwin', system_family):
		print('./build.sh')
		print('./build.sh clean')
		print('./build.sh install')

	else:
		print('build.bat')
		print('build.bat clean')
		print('build.bat install')
	print('\n')
	# FIN!

def checkDependency(text, dep, osys):
	"""
	Check for a dependency.
	"""
	try:
		print('{0}:'.format(text))

		if text[0:9] == 'txtrevise' and re.match('\w+', dep):
			print('\tFOUND.\n')

		elif re.search('\s.+', dep):
			print('\tFOUND.\n')

		elif re.match('\W', dep):
			print('\tNOT FOUND.')
			raise RequirementNotFound(text)
		else:
			print('\tNOT FOUND.')
			raise RequirementNotFound(text)

	except RequirementNotFound as e:
		print("\nA requirement was not found. Please install it:")
		print("{0}.\n".format(e.value))

		if text[0:9] == 'txtrevise':
			y = re.compile('y', re.IGNORECASE)
			n = re.compile('n', re.IGNORECASE)
			choice = 'x'
			while not y.match(choice) or not n.match(choice):
				choice = raw_input('Download and install it now? (y/n): ')
				if(y.match(choice)):
					urllib.urlretrieve('http://sams-py.googlecode.com/svn/trunk/txtrevise/txtrevise.py',
					'txtrevise.py')

					# Mark txtrevise utility as executable.
					if re.match('\*nix|darwin', osys):
						os.system('chmod +x txtrevise.py')
					
					print('\nNow rerun this script.')
					break

				elif n.match(choice):
					break
		else:
			a = text.split(' ')
			b = a[0].lower()
			del a
			webbrowser.open_new_tab('http://stpettersens.github.com/Gaudi/dependencies.html#{0}'.format(b))

		sys.exit(1)

def writeEnvVar(var, value, osys):
	"""
	Write enviroment variables to build shell script
	or batch file.
	"""
	# Generate shell script on Unix-likes / Mac OS X.
	if re.match('\*nix|darwin', osys):
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
	# Copy _build.xml -> build.xml
	#...
	command = 'txtrevise.py -q -f build.xml -l {0} -m "<\!---->"' #! Change to execute like a Python script?
	+ ' -r "{1}"' .format(line_num, new_line)
	if re.match('\*nix|darwin', osys):
		os.system('./' + command)
	else:
		os.system(command)

def amendManifest(new_lib):
	"""
	Amend Manifest.mf file,
	by adding new library for CLASSPATH.
	"""
	# Copy _Manifest.mf -> Manifest.mf
	#...
	command = 'txtrevise.py -q -f Manifest.mf -l 2 -m "#"' #! Change to execute like a Python script?
	+ ' -r "{0}"'.format(new_lib)
	if re.match('\*nix|darwin', osys):
		os.system('./' + command)
	else:
		os.system(command)

def showCLIoptions():
	"""
	Show command line options for configuration script.
	"""
	print(__doc__)

if __name__ == '__main__':
	configureBuild(sys.argv)
