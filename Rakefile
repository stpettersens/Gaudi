# 
# Rakefile to build Gaudi on Travis CI.
# To build on your own system; please use `configure.py` or `configure.pl`
# and `build.sh` or `build.bat` instead.
#
app="gaudi"

task :default => [:configure, :build, :test]

task :configure do
	sh "python configure.py --minbuild"
end

task :build do
	sh "build.sh"
end

task :test do
	sh "#{app} -i"
	sh "#{app} -v"
end
