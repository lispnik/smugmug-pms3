Developer Notes
============

General
------------

Currently, smugmug-pms3 is developed with ps3mediaserver trunk. Checkout the ps3mediaserver project from SVN:

    svn co http://ps3mediaserver.googlecode.com/svn/trunk/ps3mediaserver

Build ps3mediaserver as per its own instructions.  Usually all that is required is the invocation of "ant" in the
source directory.

Checkout the smugmug-pms3 using Git:

    git clone git://github.com/lispnik/smugmug-pms3.git

Create a build.properties file in your home directory (i.e. ~/, which corresponds to Java'a ${user.home}).  Set the full
path to the ps3mediaserver source, e.g.:

    pms3.home=/home/mkennedy/projects/personal/ps3mediaserver

Alternatively, you can edit the build.properties file in the source directory of smugmug-pms3.

To build smugmug-pms3, invoke "ant" in the smugmug-pms3 source directory:

    ant deploy

This will compile the source, create a JAR file containing the plugin and finally deploy it in PMS3's plugin directory.

IntelliJ Configuration
------------

 * Checkout both code bases as above.
 * Create an empty project
 * Create a module for ps3mediaserver
 * Create a module for smugmug-pms3
 * Adjust ps3mediaserver module dependencies so that it depends on smugmug-pms3 as a "provided" dependency
 * Adjust smugmug-pms3 module dependencies so that it depends on ps3mediaserver as a "compile" dependency
 * Ensure the lib directory of smugmug-pms3 is included as an IntelliJ "library" for the smugmug-pms3 module


Copyright (C) 2011  Matthew Kennedy
