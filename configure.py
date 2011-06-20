#!/usr/bin/env python

"""
Gaudi build configuration script.
---------------------------------

Python-based configuration script to
detect dependencies and amend Ant build file (build.xml)
and Manifest.mf as necessary and to generate script
wrappers depending on target platform and chosen
configuration.

This script requires Python 2.7+.

This script depends on the txtrevise utility - which it
may download & install when prompted.

Usage:
\t sh mark-exec.sh
\t./configure.py [arguments]
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
use_gtk = False
use_growl = False
use_groovy = True
use_jython = True
no_notify = False
log_conf = False
logger = None
use_deppage = True

class Logger:
	"""
	Logger class.
	"""
	def __init__(self):
		self.content = []
	def write(self, string):
		self.content.append(string)

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
	# Handle any command line arguments
	doc = usegnu = nojython = nogroovy = nonotify = usegrowl = None
	noplugins = minbuild = log = nodeppage = None
	parser = argparse.ArgumentParser(description='Configuration script for building Gaudi.')
	parser.add_argument('--usegnu', action='store_true', dest=usegnu, 
	help='Use GNU software - GCJ and GIJ')
	parser.add_argument('--nojython', action='store_false', dest=nojython,
	help='Disable Jython plug-in support')
	parser.add_argument('--nogroovy', action='store_false', dest=nogroovy,
	help='Disable Groovy plug-in support')
	parser.add_argument('--nonotify', action='store_true', dest=nonotify,
	help='Disable notification support')
	parser.add_argument('--noplugins', action='store_true', dest=noplugins,
	help='Disable all plug-in support')
	parser.add_argument('--usegrowl', action='store_true', dest=usegrowl,
	help='Use Growl as notification system over libnotify (GTK)')
	parser.add_argument('--minbuild', action='store_true', dest=minbuild,
	help='Use only core functionality; disable plug-ins, disable notifications')
	parser.add_argument('--log', action='store_true', dest=log, 
	help='Log output of script to file instead of terminal')
	parser.add_argument('--nodeppage', action='store_false', dest=nodeppage,
	help='Do not open dependencies web page for missing dependencies')
	parser.add_argument('--doc', action='store_true', dest=doc,
	help='Show documentation for script and exit')
	results = parser.parse_args()

	# Set and print configuration
	global use_gnu, use_groovy, use_jython, no_notify, use_growl
	global log_conf, logger, use_deppage
	use_gnu = results.usegnu
	use_jython = results.nojython
	use_groovy = results.nogroovy
	no_notify = results.nonotify
	use_growl = results.usegrowl
	use_deppage = results.nodeppage
	log_conf = results.log

	if results.doc:
		print(__doc__)
		sys.exit(0)

	logger = Logger()
	if results.noplugins or results.minbuild:
		use_jython = False
		use_groovy = False

	if results.minbuild:
		no_notify = True

	if log_conf:
		# Do not show dep page for any missing dependencies when logging.
		use_deppage = False
		sys.stdout = logger

	print('--------------------------------------')
	print('Build configuration for Gaudi')
	print('--------------------------------------')
	print('Use GNU GCJ & GIJ: {0}'.format(use_gnu))
	print('Jython plug-in support enabled: {0}'.format(use_jython))
	print('Groovy plug-in support enabled: {0}'.format(use_groovy))
	print('Notification support disabled: {0}'.format(no_notify))
	print('--------------------------------------')

	# Detect operating system.
	try:
		uname = subprocess.check_output(['uname', '-s'])
		if re.match('.*n[i|u]x|.*BSD', uname):
			print('\nDetected system:\n\tLinux/Unix-like (not Mac OS X).\n')
			system_family = '*nix'

		elif re.match('.*Darwin', uname):
			print('\nDetected system:\n\tDarwin/Mac OS X.\n')
			system_family = 'darwin'
			use_growl = True

		elif re.match('.*CYGWIN', uname):
			print('\nDetected system:\n\tCygwin.\n')
			print('Cygwin is currently unsupported. Sorry.\n')
			sys.exit(1)

		else:
			# In the event that Windows user has `uname` available:
			raise Exception

	except Exception:
			print('\nDetected system:\n\tWindows.\n')
			system_family = 'windows'

	# Detect desktop environment on Unix-likes (not Mac OS X).
	global use_gtk
	if system_family == '*nix':
		system_desktop = os.environ.get('DESKTOP_SESSION')

		if re.match('x.*', system_desktop):
			system_desktop = 'Xfce'
			if not use_growl:
				use_gtk = True

		elif system_desktop == 'default':
			system_desktop = 'KDE'
			use_growl = True

		elif system_desktop == 'gnome':
			system_desktop = system_desktop.upper() 
			if not use_growl:
				use_gtk = True

		print('Detected desktop:\n\t{0}\n'.format(system_desktop))

	# Check for txtrevise utility,
	# if not found, prompt to download from code.google.com/p/sams-py
	# Subversion repository over HTTP - in checkDependency(-,-,-).
	tool = None
	try:
		# On Unix-likes, detect using `find`. On Windows, use `where`.
		if re.match('\*nix|darwin', system_family):
			txtrevise = subprocess.check_output(['find', 'txtrevise.py'],
			stderr=subprocess.STDOUT)
			tool = 'find'
		else:
			txtrevise = subprocess.check_output(['where', 'txtrevise.py'],
			stderr=subprocess.STDOUT)
			tool = 'where'

	except:
		txtrevise = '\W'

	checkDependency('txtrevise utility', txtrevise, 'txtrevise', system_family, tool)

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

	# On *nix, detect using `whereis`. On Windows, use `where`.
	i = 0
	scala_dir = tool = None
	for c in t_commands:
		try:
			if re.match('\*nix|darwin', system_family):
				o = subprocess.check_output(['whereis', c],
				stderr=subprocess.STDOUT)
				tool = 'whereis'
			else:
				o = subprocess.check_output(['where', c],
				stderr=subprocess.STDOUT)
				tool = 'where'
					
			# Find location of Scala distribution.
			if re.search('scala', o): 
				if re.match('\*nix|darwin', system_family):
					p = re.findall('/+\w+/+scala\-*\d*\.*\d*\.*\d*\.*\d*', o)
					scala_dir = p[0]
				else:
					p = re.findall('[\w\:]+[^/]+scala\-*\d*\.*\d*\.*\d*\.*\d*', o)
					fp = p[0].split(r'\bin')
					scala_dir = fp[0]

			checkDependency(t_names[i], o, c, system_family, tool)

		except:
			checkDependency(t_names[i], o, c, system_family, tool)
		i += 1

	# Write environment variable to a build file.
	writeEnvVar('SCALA_HOME', scala_dir, system_family)

	# Write exectuable wrapper
	writeExecutable(t_commands[0], system_family)

	# Find required JAR libraries necessary to build Gaudi on this system.
	l_names = [ 'JSON.simple', 'Commons-IO' ]
	l_jars = [ 'json_simple-1.1.jar', 'commons-io-2.0.1.jar' ]

	# When enabled, use plug-in support for Groovy and Jython.
	if use_groovy:
		l_names.append('Groovy')
		l_jars.append('groovy-all-1.8.0.jar')

	if use_jython:
		l_names.append('Jython')
		l_jars.append('jython.jar')

	# When use GTK and use notifications are enabled, 
	# add java-gnome [GTK] library to libraries list.
	if use_gtk and not no_notify:
		l_names.append('java-gnome')
		l_jars.append('gtk.jar')

	# When Growl is selected as notification system,
	# add libgrowl to libraries list.
	if use_growl and not no_notify:
		l_names.append('libgrowl')
		l_jars.append('libgrowl.jar')

	# On *nix, detect using `find`. On Windows, use `where` again.
	i = 0
	for l in l_jars:
		try:
			if re.match('\*nix|darwin', system_family):
				o = subprocess.check_output(['find', 'lib/{0}'.format(l)],
				stderr=subprocess.STDOUT)
				tool = 'find'
			else:
				o = subprocess.check_output(['where', 'lib:{0}'.format(l)], 
				stderr=subprocess.STDOUT)
				tool = 'where'

			checkDependency(l_names[i], o, l, system_family, tool)	
		except:
			checkDependency(l_names[i], o, l, system_family, tool)	

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
	saveLog()
	# FIN!

def checkDependency(text, required, tomatch, osys, tool):
	"""
	Check for a dependency.
	"""
	try:
		print('{0}:'.format(text))
		if tool == 'find':
			if re.search('{0}'.format(tomatch), required):
				print('\tFOUND.\n')
			else:
				raise RequirementNotFound(text)

		elif tool == 'whereis' or tool == 'where':
			if re.search('\/', required) or re.search(tomatch, required):
				print('\tFOUND.\n')
			else:
				raise RequirementNotFound(text)
		else:
			raise RequirementNotFound(text)
			
	except RequirementNotFound as e:
		print('\tNOT FOUND.\n')
		print("A requirement was not found. Please install it:")
		print("{0}.\n".format(e.value))

		if text[0:9] == 'txtrevise':
			y = re.compile('y', re.IGNORECASE)
			n = re.compile('n', re.IGNORECASE)
			choice = 'x'
			while not y.match(choice) or not n.match(choice):
				choice = raw_input('Download and install it now? (y/n): ')
				if(y.match(choice)):
					url = 'http://sams-py.googlecode.com/svn/trunk/txtrevise/txtrevise.py'
					util = 'txtrevise.py'
					urllib.urlretrieve(url, util)

					# Mark txtrevise utility as executable.
					if re.match('\*nix|darwin', osys):
						os.system('chmod +x {0}'.format(util))
					
					print('\nNow rerun this script.')
					break

				elif n.match(choice):
					break
		else:
			global use_deppage
			if use_deppage:
				a = text.split(' ')
				a = a[0].lower()
				wurl = 'http://stpettersens.github.com/Gaudi/dependencies.html#{0}'.format(a)
				webbrowser.open_new_tab(wurl)
		
		saveLog()
		sys.exit(1)

def writeEnvVar(var, value, osys):
	"""
	Write environment variables to build shell script
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
		f.write('@set {0}={1}\r\n@ant %1\r\n'.format(var, value))
		f.close()

def writeExecutable(java, osys):
	"""
	Write executable wrapper.
	"""
	exe = 'gaudi'
	f = open(exe, 'w')
	if re.match('\*nix|darwin', osys):
		f.write('#!/bin/sh\n# Run Gaudi')
		f.write('\n{0} -jar Gaudi.jar "$@"'.format(java))
		f.close()
		os.system('chmod +x {0}'.format(exe))
	else:
		f.write('@rem Run Gaudi')
		f.write('\r\n@{0} -jar Gaudi.jar "%*"\r\n'.format(java))
		f.close()
		if os.path.isfile(exe + '.bat'):
			os.remove(exe + '.bat')
		os.rename(exe, exe + '.bat')

def amendAntBld(line_num, new_line, osys):
	"""
	Amend Ant buildfile using txtrevise utility.
	"""
	# Copy _build.xml -> build.xml
	shutil.copyfile('_build.xml', 'build.xml')
	command = 'txtrevise.py -q -f build.xml -l {0} -m "<\!---->"'
	+ ' -r "{1}"'.format(line_num, new_line)
	execChange(command)

def amendManifest(new_lib, osys):
	"""
	Amend Manifest.mf file by adding new library for CLASSPATH.
	"""
	# Copy _Manifest.mf -> Manifest.mf
	shutil.copyfile('_Manifest.mf', 'Manifest.mf')
	command = 'txtrevise.py -q -f Manifest.mf -l {0} -m #'
	+ ' -r "{0}"'.format(new_lib)
	execChange(command)

def execChange(command, osys):
	if re.match('\*nix|darwin', osys):
		os.system('./' + command)
	else:
		os.system(command)

def saveLog():
	"""
	Save script output to log.
	"""
	sys.stdout = sys.__stdout__
	global logger, log_conf
	if log_conf:
		print('Saved output to log file.')
		f = open('configure.log', 'w')
		for line in logger.content:
			f.write(line)
		f.close()

if __name__ == '__main__':
	configureBuild(sys.argv)
