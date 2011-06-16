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
# sh mark-exec.sh
# ./configure.pl [arguments]

use strict;
use warnings;
use Getopt::Long;
use LWP::Simple;
use IO::Uncompress::Unzip qw(unzip $UnzipError);

# Globals
my $usegnu = 0;
my $usegtk = 0;
my $nogroovy = 0;
my $nojython = 0;
my $nonotify = 0;
my $logconf = 0;
my $nodeppage = 0;

sub configureBuild {
	##
	# Configure build; entry method.
	##
	# Handle any command line arguments
	my $noplugins = 0;
	my $minbuild = 0;
	my $help = 0;
	Getopt::Long::GetOptions(
		'usegnu' => \$usegnu,
		'nojython' => \$nojython,
		'nogroovy' => \$nogroovy,
		'nonotify' => \$nonotify,
		'noplugins' => \$noplugins,
		'minbuild' => \$minbuild,
		'log' => \$logconf,
		'nodeppage' => \$nodeppage,
		'help' => \$help
	);

	if($help == 1) {
		showUsage();
	}

	print <<INFO;
--------------------------------------
Build configuration for Gaudi
--------------------------------------
Use GNU GCJ & GCJ: $usegnu
Jython plug-in support disabled: $nojython
Groovy plug-in support disabled: $nogroovy
Notification support disabled: $nonotify
-------------------------------------

INFO

	# Detect operating system.
	my $systemfamily;
	my $uname = `uname -s 2>&1`;

	if($uname =~ /.*n[i|u]x|.*BSD|.*CYGWIN/) {
		print "Detected system:\n\tLinux/Unix-like (not Mac OS X).\n";
		$systemfamily = '*nix';
	}
	elsif($uname =~ /.*Darwin/) {
		print "Detected system:\n\tDarwin/Mac OS X.\n";
		$systemfamily = 'darwin';
	}
	else {
		print "Detected system:\n\tWindows.\n";
		$systemfamily = 'windows';
	}
	print "\n";

	# Detect desktop environment on Unix-likes (not Mac OS X).
	my $systemdesktop;
	if($systemfamily eq '*nix') {

		$systemdesktop = $ENV{'DESKTOP_SESSION'};	
			
		if($systemdesktop =~ /x.*/) {
			$systemdesktop = 'Xfce';
			$usegtk = 1;
		}
		print "Detected desktop:\n\t$systemdesktop\n"
	}
	elsif($systemfamily eq 'default') {
		$systemdesktop = 'KDE';
	}
	elsif($systemfamily eq 'gnome') {
		$systemdesktop = 'GNOME';
		$usegtk = 1;
	}
	
	# Check for txtrevise utility,
	# if not found, prompt to download from code.google.com/p/sams-py,
	# the freeze / Py2Exe executable package.
	
	# On Unix-likes, detect using `find`. On Windows, use `where`.
	my $txtrevise = '#';
	if($systemfamily =~ /\*nix|darwin/) {
		$txtrevise = `find txtrevise 2>&1`;
	}
	else {
		$txtrevise = `where txtrevise.exe 2>&1`;
	}
	checkDependency('txtrevise utility', $txtrevise, 'txtrevise');
	
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
		}
		else {
			$o = `where $c 2>&1`;
		}
		
		if($o =~ /\w.+scala/) {
			if($systemfamily =~ /\*nix|darwin/) {
				#...
			}
			else {
				#...
			}
		}
		checkDependency($tnames[$i], $o, $c, $systemfamily);
		$i++;
	}
}

sub checkDependency {
	## 
	# Check for a dependency.
	##
	print "$_[0]:\n";
	
	if($_[1] =~ m/($_[2])/) {
		print "\tFOUND.\n\n";
	}
	else {
		print "\tNOT FOUND.\n";
		print "\nA requirement was not found. Please install it:";
		print "\n$_[0].\n";
		
		if(substr($_[0], 0, 9) eq 'txtrevise') {
			my $loop = 1;
			my $choice;
			while($loop == 1) {
				print "\nDownload and install it now? (y/n):\n";
				$choice = <>;
				if($choice =~ /y/i) {
					my $url = 'http://sams-py.googlecode.com/svn/trunk/txtrevise/txtrevise.py';
					my $file = 'txtrevise';
					if($_[3] eq 'windows') {
						$file = $file . '.exe';
					}
					#getstore($url, $file);
					print "\nNow rerun this script.";
					$loop = 0;
				}
				elsif($choice =~ /n/i) {
					$loop = 0;
				}
			}
		}
		exit;
	}
}

sub writeEnvVar {


}

sub showUsage {
	
	print <<USAGE;
usage: configure.pl [--help] [--usegnu] [--nojython] [--nogroovy] [--nonotify]
                    [--noplugins] [--minbuild] [--log] [--nodeppage] [--doc]

Configuration script for building Gaudi.

optional arguments:
  --help       Show this help message and exit
  --usegnu     Use GNU software - GCJ and GIJ
  --nojython   Disable Jython plug-in support
  --nogroovy   Disable Groovy plug-in support
  --nonotify   Disable notification support
  --noplugins  Disable all plug-in support
  --minbuild   Use only core functionality; disable plug-ins, disable
               notifications
  --log        Log output of script to file instead of terminal
  --nodeppage  Do not open dependencies web page for missing dependencies

USAGE
	exit;
}

configureBuild();
