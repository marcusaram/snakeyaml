From: Andrey Somov <asomov@gmail.com>
To: yaml-core@lists.sourceforge.net
Subject: [ANN] SnakeYAML-0.8: fix minor issues

========================
 Announcing SnakeYAML-0.8
========================

A new release of SnakeYAML is now available:

    http://trac-hg.assembla.com/snakeyaml/

This release fixes minor bugs.


Changes
=======

* when constructing integers try to create the first in the following order:
  Integer -> Long -> BigInteger

Resources
=========

SnakeYAML homepage: http://trac-hg.assembla.com/snakeyaml/
SnakeYAML documentation: http://trac-hg.assembla.com/snakeyaml/wiki/Documentation

JAR package: http://pyyaml.org/download/pyyaml/SnakeYAML-3.08.zip
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

SnakeYAML is ported from PyYAML by Andrey Somov <asomov@gmail.com>.

SnakeYAML is released under the MIT license.

