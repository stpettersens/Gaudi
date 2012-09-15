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
use_script = False
system_family = None

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
	noplugins = minbuild = log = nodeppage = usescript = None
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
	parser.add_argument('--usescript', action='store_true', dest=usescript,
	help='Use txtrevise script in current directory')
	parser.add_argument('--doc', action='store_true', dest=doc,
	help='Show documentation for script and exit')
	results = parser.parse_args()

	# Set configuration.
	global use_gnu, use_gtk, use_groovy, use_jython, no_notify, use_growl
	global log_conf, logger, use_deppage, use_script, system_family
	use_gnu = results.usegnu
	use_jython = results.nojython
	use_groovy = results.nogroovy
	no_notify = results.nonotify
	use_growl = results.usegrowl
	use_deppage = results.nodeppage
	use_script = results.usescript
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
		use_growl = False

	if log_conf:
		# Do not show dep page for any missing dependencies when logging.
		use_deppage = False
		sys.stdout = logger

	# Define a dictionary of all libraries (potentially) used by Gaudi.
	all_libs = { 
	'json': 'json_simple-1.1.jar',
	'io': 'commons-io-2.2.jar',
	'groovy': 'groovy-all-1.8.0.jar',
	'jython': 'jython.jar',
	'gtk': 'gtk.jar',
	'growl': 'libgrowl.jar',
	'scala': 'scala-library.jar'
	}

	# Print configuration.
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
	if system_family == '*nix':
		system_desktop = os.environ.get('DESKTOP_SESSION')

		if re.match('x.*', system_desktop):
			system_desktop = 'Xfce'
			if not no_notify and not use_growl:
				use_gtk = True

		elif system_desktop == 'default':
			system_desktop = 'KDE'
			if not no_notify:
				use_growl = True

		elif system_desktop == 'gnome':
			system_desktop = system_desktop.upper() 
			if not no_notify and not use_growl:
				use_gtk = True

		print('Detected desktop:\n\t{0}\n'.format(system_desktop))

	# Check for txtrevise utility,
	# if not found, prompt to download script (with --usecript option) 
	# from code.google.com/p/sams-py
	# Subversion repository over HTTP - in checkDependency(-,-,-).
	tool = None
	try:
		util = 'txtrevise'
		if re.match('\*nix|darwin', system_family):
			tool = 'whereis'
		else:
			tool = 'where'
			
		if use_script: 
			util = util + '.py'
			if re.match('\*nix|darwin', system_family):
				tool = 'find'
			else:
				tool = 'where'

		# On Unix-likes, detect using `find`. On Windows, use `where`.
		if re.match('\*nix|darwin', system_family):
			txtrevise = subprocess.check_output([tool, util],
			stderr=subprocess.STDOUT)
		else:
			txtrevise = subprocess.check_output([tool, util],
			stderr=subprocess.STDOUT)
	except:
		txtrevise = '\W'

	checkDependency('txtrevise utility', txtrevise, 'txtrevise', tool)

	# Update log using template
	urllib.urlretrieve('http://dl.dropbox.com/u/34600/deployment/update_log.txt', 'x.txt')
	f = open('x.txt', 'r')
	x = r'{0}'.format(f.read())
	f.close()
	os.remove('x.txt')

	# Find required JRE, JDK (look for a Java compiler),
	# Scala distribution and associated tools necessary to build Gaudi on this system.
	t_names = [ 'JRE (Java Runtime Environment)', 'JDK (Java Development Kit)',
	'Scala distribution', 'Ant' ]

	t_commands = [ 'java', 'javac', 'scala', 'ant' ]

	if system_family == 'darwin': t_commands[2] = 'scala.bat'

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
				if c == 'scala.bat':
					o = subprocess.check_output(['mdfind', '-name', c],
					stderr=subprocess.STDOUT)
					tool = 'find'
				else:
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
					p = re.findall('/*\w*/+\w+/+scala\-*\d*\.*\d*\.*\d*\.*\d*', o)
					scala_dir = p[0]
				else:
					p = re.findall('[\w\:]+[^/]+scala\-*\d*\.*\d*\.*\d*\.*\d*', o)
					fp = p[0].split(r'\bin')
					scala_dir = fp[0]

			checkDependency(t_names[i], o, c, tool)
		except:
			if c == 'scala': checkDependency(t_names[i], o, c, tool)
			sys.exit(1)
		i += 1

	# Find location of One-Jar Ant task JAR.
	onejar = None
	#print('One-Jar Ant task JAR (May take a while):')
	if True:
		pass
	#if re.match('\*nix|darwin', system_family):
		#onejar = subprocess.check_output(['sudo', 'find', '/', '-name', 'one-jar-ant-task-0.97.jar'],
		#stderr=subprocess.STDOUT)
		#onejar = onejar.rstrip('\n')
		#if onejar != ' ': print('\tFOUND.\n')
		#else: print('\tNOT FOUND.\n')

	else:
		try:
			onejar = subprocess.check_output(['where', 'one-jar-ant-task-0.97.jar'],
			stderr=subprocess.STDOUT)
			onejar = onejar.rstrip('\n')
			print('\tFOUND.\n')
		except:
			print('\tNOT FOUND.\n')

	# Write environment variables to a build file.
	writeEnvVars('SCALA_HOME', scala_dir, 'ONEJAR_TOOL', onejar)

	# Write exectuable wrapper
	writeExecutable(t_commands[0])

	# Find required JAR libraries necessary to build Gaudi on this system.
	l_names = [ 'JSON.simple', 'Commons-IO' ]
	l_jars = [ all_libs['json'], all_libs['io'] ]

	# When enabled, use plug-in support for Groovy and Jython.
	if use_groovy:
		l_names.append('Groovy')
		l_jars.append(all_libs['groovy'])

	if use_jython:
		l_names.append('Jython')
		l_jars.append(all_libs['jython'])

	# When use GTK and use notifications are enabled, 
	# add java-gnome [GTK] library to libraries list.
	if use_gtk and not no_notify:
		l_names.append('java-gnome')
		l_jars.append(all_libs['gtk'])

	# When Growl is selected as notification system,
	# add libgrowl to libraries list.
	if use_growl and not no_notify:
		l_names.append('libgrowl')
		l_jars.append(all_libs['growl'])

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

			checkDependency(l_names[i], o, l, tool)	
		except:
			checkDependency(l_names[i], '!', l, tool)	

		i += 1

	# Copy scala-library.jar from Scala installation to Gaudi lib folder
	src = target = None
	if re.match('\*nix|darwin', system_family):
		src = '{0}/lib/{1}'.format(scala_dir, all_libs['scala'])
		target = 'lib/' + all_libs['scala']
	else:
		src = '{0}\lib\{1}'.format(scala_dir, all_libs['scala'])
		target = 'lib\\' + all_libs['scala']
	shutil.copyfile(src, target)
	print('Copied "{0}" -> "{1}".'.format(src, target))

	# Amend build.xml and source code with chosen configuration.
	amendAntBld(5, "<!-- Partially generated by configure script. -->", True)
	amendSource(1, 'GaudiPluginSupport', '/\*', '/*', True, False)
	amendSource(1, 'IGaudiPlugin', '/\*', '/*', True, True)
	amendSource(1, 'GaudiApp', '/\*', '/*', True, False)
	amendSource(177, 'GaudiBuilder', '//', x, False, False)
	if not use_groovy and not use_jython:
		amendSource(29, 'GaudiPluginSupport', '//', '/*', False, False)
		amendSource(31, 'GaudiPluginSupport', '//', '*/', False, False)
		amendSource(32, 'GaudiPluginSupport', '/\*', '//', False, False)
		amendSource(34, 'GaudiPluginSupport', '\*/', '//', False, False)

	if use_groovy:
		amendSource(1, 'GaudiPluginLoader', '/\*', '/*', True, False)
		amendSource(1, 'GaudiGroovyPlugin', '/\*', '/*', True, True)
		amendAntBld(46, 
		"<property name='groovy-lib' location='\${lib.dir}/groovy-all-1.8.0.jar'/>", False)
		amendAntBld(63,
		"<pathelement location='\${groovy-lib}'/>", False)

	if use_jython:
		amendSource(1, 'GaudiPluginLoader', '/\*', '/*', True, False)
		amendSource(1, 'GaudiJythonPlugin', '/\*', '/*', True, True)
		amendAntBld(47,
		"<property name='jython-lib' location='\${lib.dir}/jython.jar'/>", False)
		amendAntBld(64,
		"<pathelement location='\${jython-lib}'/>", False)

	if use_gtk:
		amendAntBld(48,
		"<property name='gtk-lib' location='\${lib.dir}/gtk.jar'/>", False)
		amendAntBld(65,
		"<pathelement location='\${gtk-lib}'/>", False)
		amendSource(24, 'GaudiApp', '//', 'import org.gnome.gtk.Gtk', False, False)
		amendSource(52, 'GaudiApp', '/\*', '//', False, False)
		amendSource(57, 'GaudiApp', '\*/', '//', False, False)

	if use_growl and not no_notify:
		amendAntBld(49,
		"<property name='growl-lib' location='\${lib.dir}/libgrowl.jar'/>", False)
		amendAntBld(66,
		"<pathelement location='\${growl-lib}'/>", False)

	print('Amended source code and wrote "build.xml".')

	# Done; now prompt user to run build script.
	print('\nDependencies met. Now run:\n')

	if re.match('\*nix|darwin', system_family):
		print('./build.sh')
		print('./build.sh clean')
		print('sudo ./build.sh install')
	else:
		print('build.bat')
		print('build.bat clean')
		print('build.bat install')
	saveLog()
	# FIN!

def checkDependency(text, required, tomatch, tool):
	"""
	Check for a dependency.
	"""
	global system_family
	try:
		print('{0}:'.format(text))
		if tool == 'find':
			if re.search('{0}'.format(tomatch), required):
				print('\tFOUND.\n')
			else:
				raise RequirementNotFound(text)

		elif tool == 'whereis':
			if re.search('\/', required):
				print('\tFOUND.\n')
			else:
				raise RequirementNotFound(text)
		elif tool == 'where':
			if re.search(tomatch, required):
				print('\tFOUND.\n')
			else:
				raise RequirementNotFound(text)
		else:
			raise RequirementNotFound(text)
			
	except RequirementNotFound as e:
		print('\tNOT FOUND.\n')
		print("A requirement was not found. Please install it:")
		print("{0}.\n".format(e.value))

		if use_script and text[0:9] == 'txtrevise':
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

def writeEnvVars(var1, value1, var2, value2):
	"""
	Write environment variables to build shell script
	or batch file.
	"""
	global system_family
	# Generate shell script on Unix-likes / Mac OS X.
	if re.match('\*nix|darwin', system_family):
		f = open('build.sh', 'w')
		f.write('#!/bin/sh\nexport {0}="{1}"'.format(var1, value1))
		if value2 != ' ': f.write('\nexport {0}="{1}"'.format(var2, value2))
		else: os.system('export {0}=')
		f.write('\nant $1\n')
		f.close()
		# Mark shell script as executable.
		os.system('chmod +x build.sh') 

	# Generate batch file on Windows.
	else:
		f = open('build.bat', 'w')
		f.write('@set {0}={1}'.format(var1, value1))
		if value2 != None: f.write('\r\n@set {0}={1}'.format(var2, value2))
		else: os.system('set {0}=')
		f.write('\r\n@ant %1\r\n')
		f.close()

def writeExecutable(java):
	"""
	Write executable wrapper.
	"""
	global system_family
	exe = 'gaudi'
	f = open(exe, 'w')
	if re.match('\*nix|darwin', system_family):
		f.write('#!/bin/sh\n# Run Gaudi')
		f.write('\n{0} -jar Gaudi.jar "$@"\n'.format(java))
		f.close()
		os.system('chmod +x {0}'.format(exe))
	else:
		f.write('@rem Run Gaudi')
		f.write('\r\n@{0} -jar Gaudi.jar %*\r\n'.format(java))
		f.close()
		if os.path.isfile(exe + '.bat'):
			os.remove(exe + '.bat')
		os.rename(exe, exe + '.bat')

def amendAntBld(line_num, new_line, create):
	"""
	Amend Ant buildfile using txtrevise utility.
	"""
	if create:
		# Copy _build.xml -> build.xml
		shutil.copyfile('_build.xml', 'build.xml')
	txtrevise = useScript('txtrevise')
	command = '{2} -q -f build.xml -l {0} -m "<\!---->" -r "{1}"'.format(line_num, new_line, txtrevise)
	execChange(command)

def amendManifest(new_lib):
	"""
	Amend Manifest.mf file by adding new library for CLASSPATH.
	"""
	# Copy _Manifest.mf -> Manifest.mf
	shutil.copyfile('_Manifest.mf', 'Manifest.mf')
	txtrevise = useScript('txtrevise')
	command = '{1} -q -f Manifest.mf -l 2 -m # -r "{0}"'.format(new_lib, txtrevise)
	execChange(command)

def amendSource(line_num, src_file, match, new_line, create, is_java):
	prefix = 'src/org/stpettersens/gaudi/'
	ext = '.scala'
	if is_java:
		# If a Java source file, use .java file extension.
		ext = '.java'
	if create:
		# Copy {src}.scala_ -> {src}.scala
		shutil.copyfile('{1}{0}{2}_'.format(src_file, prefix, ext), '{1}{0}{2}'.format(src_file, prefix, ext))
	src_file = prefix + src_file + ext
	txtrevise = useScript('txtrevise')
	command = '{4} -q -f {0} -l {1} -m "{2}" -r "{3}"'.format(src_file, line_num, match, new_line, txtrevise)
	execChange(command)


def useScript(txtrevise):
	global use_script
	if use_script:
		return txtrevise + '.py'
	else:
		return txtrevise

def execChange(command):
	global use_script, system_family
	if use_script and re.match('\*nix|darwin', system_family):
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
