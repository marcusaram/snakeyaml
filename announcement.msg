From: Andrey Somov <asomov@gmail.com>
To: yaml-core@lists.sourceforge.net
Subject: [ANN] SnakeYAML-0.8: Import changes from PyYAML 3.08

========================
 Announcing SnakeYAML-0.8
========================

A new release of SnakeYAML is now available:

    http://trac-hg.assembla.com/snakeyaml/

This release imports changes from PyYAML 3.08 as well as some minor improvements.


Changes
=======

 * Add possibility to use java.io.Reader as input
 * import changes from yYAML 3.07/3.08
 * Use global tags (with !!) to load/dump Java custom classes
 * Fix parsing Long.MIN_VALUE
 * when constructing integers try to create the first in the following order: 
   Integer -> Long -> BigInteger

Resources
=========

SnakeYAML homepage: http://trac-hg.assembla.com/snakeyaml/
SnakeYAML documentation: http://trac-hg.assembla.com/snakeyaml/wiki/Documentation

JAR package: http://www.assembla.com/spaces/snakeyaml/documents/c7WDPe3jGr3z_nab7jnrAJ/download?filename=SnakeYAML-0.8.jar
ZIP archive: http://www.assembla.com/spaces/snakeyaml/documents/bHeFf83jOr3z_nab7jnrAJ/download?filename=SnakeYAML-all-0.8.zip
SnakeYAML Mercurial repository: http://hg.assembla.com/snakeyaml
Submit a bug report: http://trac-hg.assembla.com/snakeyaml/newticket

YAML homepage: http://yaml.org/
YAML-core mailing list: http://lists.sourceforge.net/lists/listinfo/yaml-core


About SnakeYAML
============

YAML is a data serialization format designed for human readability and
interaction with scripting languages.  SnakeYAML is a YAML parser and
emitter for Java.

SnakeYAML features a complete YAML 1.1 parser.
SnakeYAML is applicable for a broad range of tasks from complex
configuration files to object serialization and persistance.


Copyright
=========

SnakeYAML is ported from PyYAML by Andrey Somov <py4fun@gmail.com>.

SnakeYAML is released under the MIT license.

