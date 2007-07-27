#!/bin/csh
# This file contains environment variables needed by the Smartfrog Daemon

set SFHOME="${rpm.install.dir}"

# Add the directory of the SmartFrog executables to the path
if ( "$$path" !~ "*/$$SFHOME/bin*" ) then
  set path="$$SFHOME/bin $$path"
  echo $path
endif


