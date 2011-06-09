#!/usr/bin/env python

"""
Python-based configuration script to
detect dependencies and amend Ant build file (build.xml)
and Manifest.mf as necessary depending on target platform
and chosen configuration.

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
import shutil
import urllib
import webbrowser

# Globals
use_gnu = False
use_gtk = True
use_groovy = True
use_jython = True
no_notify = False

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
	# Detect operating system
	uname = subprocess.check_output(['uname', '-s'])
	if re.match('.*n[i|u]x|.*BSD|.*CYGWIN', uname):
		print('\nDetected system:\n\tLinux/Unix-like (not Mac OS X).\n')
		system_family = '*nix'

	elif re.match('.*Darwin', uname):
		print('\nDetected system:\n\tDarwin/Mac OS X.\n')
		system_family = 'darwin'

	else:
		print('\nDetected system:\n\tWindows.\n')
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
			global no_notify
			if no_notify == False: 
				global use_gtk
				use_gtk = True

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

	checkDependency('txtrevise utility', txtrevise, system_family, False)

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
			o = subprocess.check_output(['whereis', c],
			stderr=subprocess.STDOUT)
		else:
			o = subprocess.check_output(['where', c],
			stderr=subprocess.STDOUT)
					
		checkDependency(t_names[i], o, system_family, False)
		i += 1

	# Write environment variable to a build file.
	# First, determine where lib subdirectory in Scala installation is.
	# ...
	writeEnvVar('SCALA_HOME', 'abc', system_family)

	# Find required JAR libraries necessary to build Gaudi on this system.
	l_names = [ 'json.simple', 'commons-io']
	l_jars = [ 'json_simple-1.1.jar', 'commons-io-2.0.1.jar' ]

	# When enabled, use plug-in support for Groovy and Jython
	if use_groovy:
		l_names.append('groovy')
		l_jars.append('groovy-all-1.8.0.jar')

	if use_jython:
		l_names.append('jython')
		l_jars.append('jython.jar')

	# When use GTK is enabled, add java-gnome [GTK] library to libraries list.
	if use_gtk:
		l_names.append('java-gnome')
		l_jars.append('gtk.jar')

	# On *nix, detect using `find`. On Windows use `where` again.
	i = 0
	for l in l_jars:
		try:
			if re.match('\*nix|darwin', system_family):
				o = subprocess.check_output(['find', 'lib/{0}'.format(l)],
				stderr=subprocess.STDOUT)
			else:
				o = subprocess.check_output(['where', 'lib:{0}'.format(l)], 
				stderr=subprocess.STDOUT)
				m = re.findall(l, o)
				o = m[0]

			checkDependency(l_names[i], o, system_family, True)	
		except:
			checkDependency(l_names[i], '!', system_family, True)
		i += 1

	# Copy scala-library.jar from Scala installation to Gaudi lib folder.
	#shutil.copyfile('', 'lib/scala-library.jar')

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

def checkDependency(text, dep, osys, is_lib):
	"""
	Check for a dependency.
	"""
	try:
		print('{0}:'.format(text))

		if text[0:9] == 'txtrevise' and re.match('\w+', dep):
			print('\tFOUND.\n')

		elif re.search('\s.+', dep):
			print('\tFOUND.\n')

		elif is_lib and dep == '!':
			print('\tNOT FOUND.\n')
			raise RequirementNotFound(text)
		
		elif is_lib and re.search('{0}'.format(dep), dep):
			print('\tFOUND.\n')

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
	shutil.copyfile('_build.xml', 'build.xml')
	command = 'txtrevise.py -q -f build.xml -l {0} -m "<\!---->"'
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
	shutil.copyfile('_Manifest.mf', 'Manifest.mf')
	command = 'txtrevise.py -q -f Manifest.mf -l 2 -m "#"'
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
