@echo off
call ant initCA
call ant signJars
call ant initCA
call ant newDaemon
cd %SFHOME%\private\host*
copy *.*  %SFHOME%\private
cd %SFHOME%
start sfDaemon
call sfStart localhost ex1 org/smartfrog/test/system/cleanup/tcn20.sf
call sfStopDaemon localhost
