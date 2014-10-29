# 
# Rakefile to build Gaudi on Travis CI.
# To build on your own system; please use `configure.py` or `configure.pl`
# and `build.sh` or `build.bat` instead.
#
app="gaudi"

task :default => [ :deps, :configure, :build, :test]

task :deps do
	sh "mvn install -DskipTests=true"
end

task :configure do
	sh "python configure.py --minbuild"
end

task :build do
	sh "./build.sh"
end

task :test do
	sh "#{app} -i"
	sh "#{app} -v"
end
