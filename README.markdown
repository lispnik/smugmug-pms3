smugmug-pms3 
============

Introduction
------------

smugmug-pms3 is a ps3mediaserver DLNA plugin for the SmugMug photo hosting 
service.  With a Play Station 3 and SmugMug account, you can view your 
entire SmugMug photo collection on your TV.

Prerequisites
-------------

ps3mediaserver http://code.google.com/p/ps3mediaserver/ (the BETA version).

Installation
------------

Unpack the smugmug-pms3 ZIP archive into your ps3mediaserver directory.  This
will unpack the required libraries into ps3mediaserver's plugin directory (you
do not need to create a sub directory before unpacking).

Start ps3mediaserver.  On the General Configuration tab you should see SmugMug
listed in the plugins section.

Configuration
-------------

Configuration is done via simple properties file.  Create a file named `.smugmug-psm3.properties` 
in your user home directory (`%USERPROFILE%` on Windows, `$HOME` on GNU/Linux/Unix) 
and list your accounts within it. e.g.

	smugmug.account1.email=yoursmugmugemail@example.com
	smugmug.account1.password=secret
	smugmug.account1.apikey=123123123123123123123132
	
You can find your API key on the Control Panel page on SmugMug when you login.

Building
--------

Edit build.properties and set the directory which contains pms.jar (included 
in ps3mediaserver).  Then run Ant:

	ant dist


Copyright (C) 2010  Matthew Kennedy
