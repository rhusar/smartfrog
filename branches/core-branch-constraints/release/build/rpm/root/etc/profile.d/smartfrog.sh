#!/bin/sh
# This file contains environment variables needed by the Smartfrog Daemon

export SFHOME="/opt/smartfrog"

# Add the directory of the SmartFrog executables to the path
if echo ":${PATH-}:" | grep ":${SFHOME}:" > /dev/null 2>&1
then
	:
else
	PATH="${PATH-}:${SFHOME}/bin"
	export PATH
fi



