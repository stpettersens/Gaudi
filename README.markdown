*Please note: This repository has been emptied temporarily as I have chosen to use Gaudi as part of my Individual Study 
Project at University. To protect myself from claims of plagarism the project is for the time being closed-source

I intend to make the source avilable to all again at a later date.*

Thanks for your interest in Gaudi.

- Sam.


Gaudi
=====

Overview
--------
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

Status
------
Gaudi is still in the *earlier* stages of development.
If you are curious, please feel free to look at the [source code](/stpettersens/Gaudi/tree/master/src/org/stpettersens/gaudi).
Though, as-is in this repository Gaudi is *not yet ready* for general
use.

License
-------
Gaudi and its dependencies, except the [Scala Library](http://www.scala-lang.org/node/146) and [Jython](http://www.jython.org/license.html), are licensed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
For the summary of the license, please see the [LICENSE](http://github.com/stpettersens/Gaudi/blob/master/LICENSE) file.

The distributable all-in-one JAR is additionally licensed in part under
a [BSD-style license](http://one-jar.sourceforge.net/index.php?page=documents&file=license),
included within the distributed JAR.
