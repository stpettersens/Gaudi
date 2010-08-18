Gaudi
=====

Gaudi is a platform agnostic build tool
written in [Scala](http://www.scala-lang.org) which runs on a Java virtual machine (JVM).

As it is a build tool, it is named after an architect, [*Antoni Gaudi*](http://en.wikipedia.org/wiki/Antoni_Gaudi);
the designer of the famous *Sagrada Familia*.

Gaudi can be thought of as being similar to
[Apache Ant](http://ant.apache.org) in that it *too* also abstracts commands related
to building software away from the operating system 
(e.g. `rmve` instead of `rm` on Unix-likes or `del` on Windows); 
but differs in that:

- Its build files are based on a [JSON](http://www.json.org) rather than an XML format.

- It is *not* tailored to offer advanced features for a particular programming
  language, unlike Ant which is highly specialised for Java development.

- It is licensed under the [MIT License](/blob/master/LICENSE) rather than the Apache License
  (except third party libraries where applicable, see [DEP.LICENSE](/blob/master/DEP.LICENSE)).

Gaudi's implementation has been inspired by both Ant and Make.

Gaudi should be compatible with most modern JVMs
and has been tested with and is known to work
with Sun's [Java HotSpot/OpenJDK JVM](http://java.sun.com) and [Apache Harmony/DRLVM](http://harmony.apache.org).
