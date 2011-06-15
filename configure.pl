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
# This script was a rewrite of the Python script.
#
# This script depends on txtrevise utility - which it
# may download & install when prompted.
#
# Usage:
# sh mark-exec.sh
# ./configure.pl [arguments]

use strict;
use warnings;
use Getopt::Long;

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
	Getopt::Long::GetOptions(
		'usegnu' => \$usegnu,
		'nojython' => \$nojython,
		'nogroovy' => \$nogroovy,
		'nonotify' => \$nonotify,
		'noplugins' => \$noplugins,
		'minbuild' => \$minbuild,
		'log' => \$logconf,
		'nodeppage' => \$nodeppage,
	);

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
	my $uname = `uname -s`;
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
	if($systemfamily eq '*nix') {

		my $systemdesktop = $ENV{'DESKTOP_SESSION'};
				
		if($systemdesktop =~ /x.*/) {
			$systemdesktop = 'Xfce';
			$usegtk = 1;
		}
		print "Detected destkop:\n\t$systemdesktop\n"
	}
	elsif($systemfamily eq 'default') {
		$systemdesktop = 'KDE';
	}
	elsif($systemfamily eq 'gnome') {
		$systemdesktop = 'GNOME';
		$usegtk = 1;

	}
}

configureBuild();
