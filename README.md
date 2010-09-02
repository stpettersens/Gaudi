Gaudi
=====

What is it?
-----------
Gaudi is a platform agnostic build tool
written in Scala which runs on a Java virtual machine (JVM).

As it is a build tool, it is named after an architect, *Antoni Gaudi*;
the designer of the famous *Sagrada Familia*.

Gaudi can be thought of as being similar to [Apache Ant](http://ant.apache.org) in that it *too*
also abstracts commands related to building software away from the operating system 
(e.g. `:erase` instead of `rm` on Unix-likes or `del` on Windows); 
but differs in that:

- Its build files are based on a [JSON](http://www.json.org) format rather than an XML format.

- It is *not* tailored to offer advanced features for a particular programming
  language, unlike Ant which is highly specialised for Java development.

Inspiration
-----------
Gaudi's implementation has been inspired by both Ant (command agnosticism) and [GNU Make](http://www.gnu.org/software/make) (task notation).

Status
------
Gaudi is still in the *earlier* stages of development.
If you are curious, please feel free to look at the [source code](/stpettersens/Gaudi/tree/master/src/org/stpettersens/gaudi).
Though, as-is in this repository Gaudi is *not yet ready* for general
use.

Run Compatibility
-----------------
Gaudi should be compatible with most modern JVMs
and has been tested with and is known to work 
with the following (e.g. I ran Gaudi with...):

- [Sun's Java HotSpot/OpenJDK JVM](http://java.sun.com)

- [Apache Harmony/DRLVM](http://harmony.apache.org)

- [GNU GIJ (GCJ project)](http://gcc.gnu.org/java)

Distributions
-------------
Gaudi will be available as installable Windows executable
(courtesy of [NSIS](http://nsis.sourceforge.net) and 
[Launch4j](http://launch4j.sourceforge.net)) and
also a an all-in-jar executable JAR (courtesy of [One-JAR](http://one-jar.sourceforge.net)).
If you want to specify which JVM other than the system default you want to use or
are using a system other than Windows, please use the all-in-one JAR version.

For more information, consult the Wiki. (Coming soon).

Dependencies
------------
Gaudi depends on the following third-party JVM libraries:

- [JSON.simple](http://code.google.com/p/json-simple)

- [Apache Commons IO Library](http://commons.apache.org/io)

- [Scala Library](http://www.scala-lang.org)

If building Gaudi with Gaudi (as in the case of potential upgrading),
it supports dependency gathering like Apache Ant does. 
Dependencies will be checked automatically before building the project,
but you will be prompted to download any, unless the `-a` switch is provided.

License
------------
Gaudi and its dependencies, [except the Scala Library](http://www.scala-lang.org/node/146), are licensed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
For the summary of the license, please see the [LICENSE](http://github.com/stpettersens/Gaudi/blob/master/LICENSE) file.

The distributable all-in-one JAR is additionally licensed in part under
a [BSD-style license](http://one-jar.sourceforge.net/index.php?page=documents&file=license),
included within the distributed JAR.

Usage
-----
For instructions on how to use Gaudi, please run Gaudi itself with the information (`-i`) switch,
like so (assuming in system PATH):
>`$ gaudi -i`

For more in-depth explanation of usage, please refer to the Wiki. (Coming later).

__DISCLAIMER: GAUDI IS A WORK-IN-PROGRESS.
ANYTHING IN THIS README IS NOT FINAL, UNTIL FIRST RELEASE.__

