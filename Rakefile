# 
# Rakefile to build Gaudi on Travis CI or local system.
# Use `rake default` to first get dependencies, configure, build, install and test.
# Use `rake travis` to do the same with Travis CI (first installs Scala distribution).
# Use `rake all` to configure, build, install and test.
#

task :default => [:deps, :configure, :build, :install, :test]
task :travis => [ :deps_travis, :configure_travis, :build, :install, :test]
task :all => [:configure, :build, :install, :test]

task :deps do
	sh "python contrib/getDependencies.py local"
end

task :deps_travis do
	sh "python contrib/getDependencies.py travis"
end

task :configure do
	sh "python contrib/configure.py --minbuild --skiponejar"
end

task :configure_travis do 
	sh "python contrib/configure.py --minbuild --nodeppage --skiponejar --skipdesktop"
end

task :build do
	sh "./build.sh"
	puts ""
end

task :install do
	sh "sudo ./build.sh install"
	puts ""
end

task :test do
	sh "gaudi -i"
	puts ""
	sh "gaudi -v"
	puts ""
	Dir.chdir('examples/HelloWorld') do
		puts ""
		sh "gaudi -f build.json build"
		puts ""
		sh "./hw"
		puts ""
		sh "gaudi -f build.json clean"
	end
end

task :clean do
	sh "./build.sh clean"
end
