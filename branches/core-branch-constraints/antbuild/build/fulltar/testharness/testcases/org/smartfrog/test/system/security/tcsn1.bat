@echo off
call ant initCA
call ant signJars
call ant newDaemon
copy %SFHOME%\lib\sfTestCases.jar %SFHOME%\signedLib
cd %SFHOME%\private\host*
copy *.*  %SFHOME%\private
cd %SFHOME%
cd ..
start sfDaemon
call sfStart localhost ex1 org/smartfrog/test/system/cleanup/tcn20.sf
call sfStopDaemon localhost
