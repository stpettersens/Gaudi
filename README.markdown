Gaudi [![Build Status](https://travis-ci.org/stpettersens/Gaudi.png?branch=master)](https://travis-ci.org/stpettersens/Gaudi)
-----

Gaudi is a platform agnostic build tool
written primarily in [Scala](http://www.scala-lang.org) (along with some Java) which runs on a Java virtual machine (JVM).

As it is a build tool, it is named after an architect, [*Antoni Gaudi*](http://en.wikipedia.org/wiki/Antoni_Gaudi);
the designer of the famous [*Sagrada Familia*](http://en.wikipedia.org/wiki/Sagrada_Familia).

Gaudi can be thought of as being similar to [Apache Ant](http://ant.apache.org) in that it *too*
also abstracts commands related to building software away from the operating system 
(e.g. `:erase` instead of `rm` on Unix-likes or `del` on Windows); 
but differs in that:

- Its build files are based on a [JSON](http://www.json.org) format rather than an XML format.

- It is *not* tailored to offer advanced features for a particular programming
  language, unlike Ant which is highly specialised for Java development.

Additionally, Gaudi supports additional functionality via plug-ins written 
in the [Groovy](http://groovy.codehaus.org) or [Jython](http://www.jython.org) programming languages.

Gaudi's implementation has been inspired by
both Ant (command agnosticism) and [GNU Make](http://www.gnu.org/software/make) (task notation).

##### Dissertation paper

Saint-Pettersen, S. (2012) *Gaudi: A platform agnostic build tool for the JVM.*  BSc Dissertation, University of Worcester.

##### Copyright

&copy; 2010-2014 Sam Saint-Pettersen. 

##### License

Gaudi and its dependencies, except the [Scala Library](http://www.scala-lang.org/node/146) and [Jython](http://www.jython.org/license.html), are licensed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
For the summary of the license, please see the [LICENSE](http://github.com/stpettersens/Gaudi/blob/master/LICENSE) file.

The distributable all-in-one JAR is additionally licensed in part under
a [BSD-style license](http://one-jar.sourceforge.net/index.php?page=documents&file=license),
included within the distributed JAR.
