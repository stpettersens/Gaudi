#!/usr/bin/env python

"""
Python-based configuration script to amend Ant build file (build.xml)
as necessary depending on target platform.

Usage: chmod +x configure.py
\t./configure.py
"""
import sys
import optparse
import subprocess

def configureBuild(args):	
	uname = subprocess.check_output(["uname", "-s"])

def showCLIoptions():
	print(__doc__)

configureBuild("args")
