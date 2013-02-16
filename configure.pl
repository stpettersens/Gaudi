#!/usr/bin/env perl

# Gaudi build configuration script.
#---------------------------------
#
# Perl-based configuration script to
# detect dependencies and amend Ant build file (build.xml)
# and Manifest.mf as necessary and to generate script
# wrappers depending on target platform and chosen
# configuration.
#
# This script requires Perl 5+.
# This is a rewrite of the Python script, configure.py.
#
# This script depends on the txtrevise utility - which it
# may download & install when prompted.
#
# Usage:
# ./configure.pl [arguments]
use strict;
use warnings;
use Getopt::Long;
use LWP::Simple;
use IO::Handle;
use File::Copy;

# Globals
my $usegnu = 0;
my $usegtk = 0;
my $usegrowl = 0;
my $nogroovy = 0;
my $nojython = 0;
my $nonotify = 0;
my $logconf = 0;
my $nodeppage = 0;
my $usescript = 0;
my $systemfamily = '';

sub configureBuild {
	##
	# Configure build; entry method.
	##
	# Handle any command line arguments
	my $noplugins = 0;
	my $minbuild = 0;
	my $help = 0;
	GetOptions(
		'usegnu' => \$usegnu,
		'nojython' => \$nojython,
		'nogroovy' => \$nogroovy,
		'nonotify' => \$nonotify,
		'noplugins' => \$noplugins,
		'usegrowl' => \$usegrowl,
		'minbuild' => \$minbuild,
		'log' => \$logconf,
		'nodeppage' => \$nodeppage,
		'usescript' => \$usescript,
		'help' => \$help
	);

	if($logconf == 1) {
		# Do not show dep page for any missing dependencies when logging.
		$nodeppage = 1;
		print "Saving output to log file...\n";
		open OUTPUT, '>', 'configure.log' or die $!;
		STDOUT->fdopen( \*OUTPUT, 'w') or die $!;
	}

	if($help == 1) {
		showUsage();
	}
	
	if($noplugins == 1 || $minbuild == 1) {
		$nojython = 1;
		$nogroovy = 1;
	}
	
	if($minbuild == 1) {
		$nonotify = 1;
	}

	# Use boolean notation for display, instead of 0 and 1.
	my $busegnu = toBool($usegnu);
	my $bnojython = toBool($nojython);
	my $bnogroovy = toBool($nogroovy);
	my $bnonotify = toBool($nonotify);

	# Define a hash of all libraries (potentially) used by Gaudi.
	my %alllibs = (
	'json', 'json_simple-1.1.jar',
	'io', 'commons-io-2.2.jar',
	'groovy', 'groovy-all-1.8.0.jar',
	'jython', 'jython.jar',
	'gtk', 'gtk.jar',
	'growl', 'libgrowl.jar',
	'scala', 'scala-library.jar'
	);

	# Print configuration.
	print <<INFO;
---------------------------------------
Build configuration for Gaudi
---------------------------------------
Use GNU GCJ & GIJ: $busegnu
Jython plug-in support disabled: $bnojython
Groovy plug-in support disabled: $bnogroovy
Notification support disabled: $bnonotify
---------------------------------------

INFO

	# Detect operating system.
	my $uname = `uname -s 2>&1`;
	if($uname =~ /.*n[i|u]x|.*BSD/) {
		print "Detected system:\n\tLinux/Unix-like (not Mac OS X).\n";
		$systemfamily = '*nix';
	}
	elsif($uname =~ /.*Darwin/) {
		print "Detected system:\n\tDarwin/Mac OS X.\n";
		$systemfamily = 'darwin';
		$usegrowl = 1;
	}
	elsif($uname =~ /.*CYGWIN/) {
		print "Detected system:\n\tCygwin.\n";
		print "\nCygwin is currently unsupported. Sorry.\n\n";
		exit 1;
	}
	else {
		print "Detected system:\n\tWindows.\n";
		$systemfamily = 'windows';
	}

	# Detect desktop environment on Unix-likes (not Mac OS X).
	my $systemdesktop = '';
	if($systemfamily eq '*nix') {
		$systemdesktop = $ENV{'DESKTOP_SESSION'};	
		if($systemdesktop =~ /x.*/) {
			$systemdesktop = 'Xfce';
			if($nonotify == 0 && $usegrowl == 0) {
				$usegtk = 1;
			}
		}
		elsif($systemdesktop eq 'default') {
			$systemdesktop = 'KDE';
			if($nonotify == 0) {
				$usegrowl = 1;
			}
		}
		elsif($systemdesktop eq 'gnome') {
			$systemdesktop = uc($systemdesktop);
			if($nonotify == 0 && $usegrowl == 0) {
				$usegtk = 1;
			}
		}
		print "\nDetected desktop:\n\t$systemdesktop\n\n";
	}
	print "\n";
	
	# Check for txtrevise utility,
	# if not found, prompt to download script (with --usescript option)
	# from code.google.com/p/sams-py
	# On Unix-likes, detect using `find`. On Windows, use `where`.
	my $txtrevise;
	my $tool;
	my $util = 'txtrevise';
	if($systemfamily =~ /\*nix|darwin/) {
		$tool = 'whereis';
	}
	else {
		$tool = 'where';
	}
	
	if($usescript == 1) {
		$util = $util . '.pl';
		if($systemfamily =~ /\*nix|darwin/) {
			$tool = 'find';
		}
		else {
			$tool = 'where';
		}
	}
	if($systemfamily =~ /\*nix|darwin/) {
	 	$txtrevise = `$tool $util 2>&1`;
	}
	else {
		$txtrevise = `$tool $util 2>&1`;
	}
	checkDependency('txtrevise utility', $txtrevise, 'txtrevise', $tool, $systemfamily);
	
	# Find required JRE, JDK (look for a Java compiler),
	# Scala distribution and associated tools necessary to build Gaudi on this system.
	my @tnames = ( 'JRE (Java Runtime Environment)', 'JDK (Java Development Kit)', 
	'Scala distribution', 'Ant' );
	
	my @tcommands = ( 'java', 'javac', 'scala', 'ant' );
	
	# If user specified to use GNU Foundation software
	# where possible, substitute `java` and `javac` for `gij` and `gcj`.
	if($usegnu == 1) {
		$tnames[0] = 'JRE (GNU GIJ)';
		$tnames[1] = 'JDK (GNU GCJ)';
		$tcommands[0] = 'gij';
		$tcommands[1] = 'gcj';
	}

	# On *nix, detect using `whereis`. On Windows, use `where'.
	my $i = 0;
	my $scaladir;
	my $o;
	foreach(@tcommands) {
		my $c = $_;
		if($systemfamily =~ /\*nix|darwin/) {
			$o = `whereis $c 2>&1`;
			$tool = 'whereis';
		}
		else {
			$o = `where $c 2>&1`;
			$tool = 'where';
		}
		
		# Find location of Scala distribution.
		if($o =~ /scala/) {
			if($systemfamily =~ /\*nix|darwin/) {
				if($o =~ /\/+\w+\/+scala\-*\d*\.*\d*\.*\d*\.*\d*/) {
					$scaladir = $&;
				}
			}
			else {
				if($o =~ /[\w\:]+[^\/]+scala\-*\d*\.*\d*\.*\d*\.*\d*/) {
					my $p = $&;
					my @fp = split('bin', $p);
					$scaladir = $fp[0];
				}
			}
		}
		checkDependency($tnames[$i], $o, $c,  $tool, $systemfamily);
		$i++;
	}

	# Find location of One-Jar Ant task JAR [only on *nix].
	my $onejar = "";
	if($systemfamily=~ /\*nix|darwin/) {
		print "One-Jar Ant task JAR:\n";
		$onejar = `sudo find / -name one-jar-ant-task-0.97.jar 2>&1`;
		chomp($onejar);
		if($onejar ne " ") {
			print "\tFOUND.\n\n";
		}
		else {
			print "\tNOT FOUND.\n\n";
		}
	}

	# Write environment variable to a build file.
	writeEnvVars('SCALA_HOME', $scaladir, 'ONEJAR_TOOL', $onejar, $systemfamily);

	# Write executable wrapper.
	writeExecutable($tcommands[0], $systemfamily);
	
	# Find required JAR libraries necessary to build Gaudi on this system.
	my @lnames = ( 'JSON.simple', 'Commons-IO' );
	my @ljars = ( $alllibs{json}, $alllibs{io} );
	
	# When enabled, use plug-in support for Groovy and Jython.
	if($nogroovy == 0) {
		push(@lnames, 'Groovy');
		push(@ljars, $alllibs{groovy});
	}
	if($nojython == 0) {
		push(@lnames, 'Jython');
		push(@ljars, $alllibs{jython});
	}
	
	# When use GTK and use notifications are enabled,
	# add java-gnome [GTK] library to libraries list.
	if($usegtk == 1 && $nonotify == 0) {
		push(@lnames, 'java-gnome');
		push(@ljars, $alllibs{gtk});
	}

	# When Growl is selected as notification system,
	# add libgrowl to libraries list.
	if($usegrowl == 1 && $nonotify == 0) {
		push(@lnames, 'libgrowl');
		push(@ljars, $alllibs{growl});
	}
	
	# On *nix, detect using `find`. On Windows, use `where' again.
	$i = 0;
	foreach(@ljars) {
		my $l = $_;
		if($systemfamily =~ /\*nix|darwin/) {
			$o = `find lib/$l 2>&1`;
			$tool = 'find';
			$l = 'lib/' . $l;
		}
		else {
			$o = `where lib:$l 2>&1`;
			$tool = 'where';
		}
		checkDependency($lnames[$i], $o, $l, $tool, $systemfamily);
		$i++;
	}
	
	# Copy scala-library.jar from Scala installation to Gaudi lib folder.
	my $src;
	my $target;
	if($systemfamily =~ /\*nix|darwin/) {
		$src = "$scaladir/lib/$alllibs{scala}";
		$target = "lib/$alllibs{scala}";
	}
	else {
		$src = "$scaladir" . "lib\\$alllibs{scala}";
		$target ="lib\\$alllibs{scala}";
	}
	copy($src, $target) || die "Copy failed: $!";
	print "Copied \"$src\" -> \"$target\".\n";

	# Write build.xml with chosen configuration.
	amendAntBld(5, '<!-- Partially generated by configure script. -->', 1);
	amendSource(1, 'GaudiPluginSupport', '/\*', '/*', 1, 0);
	#amendSource(1, 'IGaudiPlugin', '/\*', '/*', 1, 1);
	amendSource(1, 'GaudiApp', '/\*', '/*', 1, 0);
	if($nogroovy == 1 && $nojython == 1) {
		amendSource(29, 'GaudiPluginSupport', '//', "/*", 0, 0);
		amendSource(31, 'GaudiPluginSupport', '//', "*/", 0, 0);
		amendSource(32, 'GaudiPluginSupport', '/\*', '//', 0, 0);
		amendSource(34, 'GaudiPluginSupport', '\*/', '//', 0, 0);
	}
	if($nogroovy == 0) {
		amendSource(1, 'GaudiPluginLoader', '/\*', '/*', 1, 0);
		amendSource(1, 'GaudiGroovyPlugin', '/\*', '/*', 1, 1);
		amendAntBld(46, 
		'<property name=\'groovy-lib\' location=\'\${lib.dir}/groovy-all-1.8.0.jar\'/>', 0);
		amendAntBld(63,
		'<pathelement location=\'\${groovy-lib}\'/>', 0);
	}
	if($nojython == 0) {
		amendSource(1, 'GaudiPluginLoader', '/\*', '/*', 1, 0);
		amendSource(1, 'GaudiJythonPlugin', '/\*', '/*', 1, 1);
		amendAntBld(47, 
		'<property name=\'jython-lib\' location=\'\${lib.dir}/jython.jar\'/>', 0);
		amendAntBld(64,
		'<pathelement location=\'\${jython-lib}\'/>', 0);
	}
	if($usegtk == 1) {
		amendAntBld(48, 
		'<property name=\'gtk-lib\' location=\'\${lib.dir}/gtk.jar\'/>', 0);
		amendAntBld(65,
		'<pathelement location=\'\${gtk-lib}\'/>', 0);
		amendSource(24, 'GaudiApp', '//', 'import org.gnome.gtk.Gtk', 0, 0);
		amendSource(52, 'GaudiApp', '/\*', '//', 0, 0);
		amendSource(57, 'GaudiApp', '\*/', '//', 0, 0);
	}
	if($usegrowl == 1) {
		amendAntBld(49, 
		'<property name=\'growl-lib\' location=\'\${lib.dir}/libgrowl.jar\'/>', 0);
		amendAntBld(66,
		'<pathelement location=\'\${growl-lib}\'/>', 0);
	}
	print "Amended source code and wrote \"build.xml\".\n";

	# Done; now prompt user to run build script.
	print "\nDependencies met. Now run:\n";

	if($systemfamily =~ /\*nix|darwin/) {
		print "\n./build.sh";
		print "\n./build.sh clean";
		print "\nsudo build.sh install";
	}
	else {
		print "\nbuild.bat";
		print "\nbuild.bat clean";
		print "\nbuild.bat install";
	}
	print "\n";
	if($logconf) {
		close(STDOUT);
	}
	# FIN!
}

sub checkDependency {
	## 
	# Check for a dependency.
	##
	print "$_[0]:\n";
	if($_[3] eq 'find') {
		if($_[1] =~ /^($_[2])/) {
			print "\tFOUND.\n\n";
		}
		else {
			requirementNotFound($_[0]);
		}
	}
	elsif($_[3] eq 'whereis') {
		if($_[1] =~ /\//) {
			print "\tFOUND.\n\n";
		}
		else {
			requirementNotFound($_[0]);
		}
	}
	elsif($_[3] eq 'where') {
		if($_[1] =~ /($_[2])/) {
			print "\tFOUND.\n\n";
		}
		else {
			requirementNotFound($_[0]);
		}
	}
	else {
		requirementNotFound($_[0]);
	}
}

sub requirementNotFound {
	##
	# requirementNotFound.
	# This mirrors the purpose of the exception in the Python script.
	##
	print "\tNOT FOUND.\n";
	print "\nA requirement was not found. Please install it:";
	print "\n$_[0].\n\n";
		
	if($usescript == 1 && substr($_[0], 0, 9) eq 'txtrevise') {
		my $loop = 1;
		my $choice;
		while($loop == 1) {
			print "Download and install it now? (y/n):\n";
			$choice = <STDIN>;
			if($choice =~ /y/i) {
				my $url = 'http://sams-py.googlecode.com/svn/trunk/txtrevise/txtrevise.pl';
				my $util = 'txtrevise.pl';
	
				# Download utility.
				getstore($url, $util);

				# Mark as executable on *nix/darwin.
				if($_[1] =~ /\*nix|darwin/) {
					system("chmod +x $util");
				}
					
				print "\nNow rerun this script.\n\n";
				$loop = 0;
			}
			elsif($choice =~ /n/i) {
				$loop = 0;
			}
		}
	}
	elsif($nodeppage == 0) {
		my @a = split(' ', $_[0]);
		my $b = lc($a[0]);
		my $wurl = "http://stpettersens.github.com/Gaudi/dependencies.html#$b";
		if($systemfamily eq 'windows') {
			system("start $wurl");
		}
		elsif($systemfamily eq 'darwin') {
			my $pid = fork();
			if($pid == 0) {
				system("open -a safari $wurl");
				exit 0;
			}
		}
		else {
			my $pid = fork();
			if($pid == 0) {
				system("firefox $wurl");
				exit 0;
			}
		}
	}
	if($logconf) {
		close(STDOUT);
	}
	exit 1;
}

sub writeEnvVars {
	##
	# Write environment variables to build shell script
	# or batch file.
	##
	# Generate shell script on Unix-likes / Mac OS X.
	if($_[4] =~ /\*nix|darwin/) {
		open(FILE, ">build.sh");
		print FILE "#!/bin/sh\nexport $_[0]\=\"$_[1]\"";
		if($_[3] ne " ") {
			print FILE "\nexport $_[2]\=\"$_[3]\"";
		}
		else {
			system("export $_[2]=");
		}
		print FILE "\nant \$1\n";
		close(FILE);
		# Mark shell script as executable.
		system('chmod +x build.sh');
	}
	# Generate batch file on Windows.
	else {
		open(FILE, '>build.bat');
		print FILE "\@set $_[0]\=$_[1]";
		if($_[3] =~ /INFO/)  {
			system("set $_[2]=")	
		}
		else {
			print FILE "\r\n\@set $_[2]\=$_[3]";
		}
		print FILE "\r\n\@ant \%1\r\n";
		close(FILE);
	}
}

sub writeExecutable {
	##
	# Write executable wrapper.
	##
	my $exe = 'gaudi';
	open(FILE, ">$exe");
	if($_[1] =~ /\*nix|darwin/) {
		print FILE "#!/bin/sh\n# Run Gaudi";
		print FILE "\n$_[0] -jar Gaudi.jar \"\$\@\"\n";
		close(FILE);
		system("chmod +x $exe");
	}
	else {
		print FILE "\@rem Run Gaudi";
		print FILE "\r\n\@$_[0] -jar Gaudi.jar %*\r\n";
		close(FILE);
		rename($exe, $exe . '.bat');
	}
}

sub amendAntBld {
	##
	# Amend Ant buildfile using txtrevise utility.
	##
	if($_[2] == 1) {
		# Copy _build.xml -> build.xml
		copy('_build.xml', 'build.xml') || die "Copy failed: $!";
	}
	my $txtrevise = useScript('txtrevise');
	my $command = "$txtrevise -q -f build.xml -l $_[0] -m \"<\!---->\" -r \"$_[1]\"";
	execChange($command);
}

sub amendManifest {
	##
	# Amend Manifest.mf file by adding new library for CLASSPATH.
	##
	# Copy _Manifest.mf -> Manifest.mf
	copy('_Manifest.mf', 'Manifest.mf') || die "Copy failed: $!";
	my $txtrevise = useScript('txtrevise');
	my $command = "$txtrevise -q -f build.xml -l 2 m # -r \"$_[0]\"";
	execChange($command);
}

sub amendSource {
	my $prefix = 'src/org/stpettersens/gaudi/';
	my $ext = '.scala';
	if($_[5] == 1) {
		# If a Java source file, use .java file extension.
		$ext = '.java';
	}
	if($_[4] == 1) {
		# Copy {src}.scala_ -> {src}.scala
		copy("$prefix$_[1]$ext\_", "$prefix$_[1]$ext") || die "Copy failed: $!";
	}
	my $src_file = $prefix . $_[1] . $ext;
	my $txtrevise = useScript('txtrevise');
	my $command = "$txtrevise -q -f $src_file -l $_[0] -m \"$_[2]\" -r \"$_[3]\"";
	execChange($command);
}

sub useScript {
	if($usescript == 1) {
		return $_[0] . '.pl';
	}
	else {
		return $_[0];
	}
}

sub execChange {
	if($usescript == 1 && $_[1] =~ /\*nix|darwin/) {
		system('./' . $_[0]);
	}
	else {
		system($_[0]);
	}
}

sub showUsage {	
	print <<USAGE;
usage: configure.pl [-h] [--usegnu] [--nojython] [--nogroovy] ... etc.

Configuration script for building Gaudi.

optional arguments:
  -h, --help   Show this help message and exit
  --usegnu     Use GNU software - GCJ and GIJ
  --nojython   Disable Jython plug-in support
  --nogroovy   Disable Groovy plug-in support
  --nonotify   Disable notification support
  --noplugins  Disable all plug-in support
  --usegrowl   Use Growl as notification system over libnotify 
  --minbuild   Use only core functionality; disable plug-ins, disable
               notifications
  --log        Log output of script to file instead of terminal
  --nodeppage  Do not open dependencies web page for missing dependencies
  --usescript  Use txtrevise script in current directory
  --doc	       Show documentation for script and exit

USAGE
	exit 0;
}

sub toBool {
	##
	# Perl does not have booleans.
	# Convert 1 and 0 to "boolean strings", just for representation.
	##
	my $bool;
	if($_[0] == 1) {
		$bool = 'True';
	}
	else {
		$bool = 'False';
	}
	return $bool;
}

configureBuild();
