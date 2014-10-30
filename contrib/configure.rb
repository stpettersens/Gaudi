#!/usr/bin/env ruby

# 
# Gaudi build configuration script.
# ---------------------------------
#
# Ruby-based configuration script to
# detect dependenciess and amend Ant build file (build.xml)
# and Manifest.mf as necessary and to generate script
# wrappers depending on target platform and chosen
# configuration.
#
# This script requires Ruby 1.9+
#
# Usage:
# ruby contrib/configure.rb [options]
#
require 'optparse'
require 'ostruct'
require 'pp'

class ConfigureBuild

	def self.parse(args)

		options = OpenStruct.new
		options.help = false
		options.verbose = true

		pp "Configuration script for building Gaudi"
		opt_parser = OptionParser.new do |opts|
		opts.banner = "Usage: configure.rb [options]"
		opts.separator ""
		opts.separator "Specific options:"

		opts.on("-h") do |help|
			options.help = true
		end
	end

		opt_parser.parse!(args)
		options
	end
end

options = ConfigureBuild.parse(ARGV)
pp options
pp ARGV
