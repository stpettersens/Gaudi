# 
# Rakefile to build Gaudi on Travis CI or local system.
# Use `rake default` to first get dependencies, configure, build and test.
# Use `rake all` to configure, build and test.
# To build on your own system; please use `configure.py` or `configure.pl`
# and `build.sh` or `build.bat` instead.
#
app="gaudi"

task :default => [ :deps, :configure, :build, :test]
task :all => [:configure, :build, :test]

task :deps do
	sh "python contrib/getDependencies.py"
end

task :configure do
	sh "python contrib/configure.py --minbuild"
end

task :build do
	sh "./build.sh"
end

task :test do
	sh "#{app} -i"
	puts ""
	sh "#{app} -v"
	puts ""
end

task :clean do
	sh "./build.sh clean"
end
