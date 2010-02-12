# This file contains environment variables needed by the Smartfrog Daemon

# This script is copied with property expansion at build time, which can be 
# used to force in dynamic values. Because of this, use $ and braced {} strings
# carefully

set SFHOME="${rpm.install.dir}"

# Add the directory of the SmartFrog executables to the path
if ( "$PATH" !~ "*$SFHOME/bin*" ) then
  setenv PATH "$SFHOME/bin:$PATH" 
  echo $PATH
endif


