/** (C) Copyright 2004 Hewlett-Packard Development Company, LP */

Documents
=========

-- for user documentation see: docs/sfGui.pdf

Build Instructions
==================
1) The sfGui build depends on the following:

-- existenc of environment variable SFHOME (set to point to the location of
   the smartfrog 'dist' directory)

-- existence of the SmartFrog distribution (e.g. build that first)
   and in particular the files:
   -- <SFHOME>/lib/smartfrog.jar
   -- <SFHOME>/lib/sfExamples.jar
   -- <SFHOME>/lib/sfServices.jar

2) Build sfGui as follows:

-- check it out from the CVS repository
-- cd to <wherever>/SmartFrogComponents/sfGui
-- build it using ant the default target is 'dist'

The build results in the following file being created:

  <wherever>/SmartFrogComponents/sfGui/lib/sfGui.jar

This contains sfGui and its sf files. Put this in the class path to use together
with sfGuiTools.jar.

See the user documentation for further use information.



