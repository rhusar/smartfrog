@echo off
call ant initCA
call ant signJars
md %SFHOME%\temp
copy %SFHOME%\signedLib\sfTestCases.jar %SFHOME%\temp\sfTestCases.jar
rem generate new credentials
call ant initCA
call ant newDaemon
cd %SFHOME%\private\host*
copy *.*  %SFHOME%\private
cd %SFHOME%
cd ..
rem sign jar files with new credentials
call ant signJars
rem copy the old sfTestCases.jar file 
copy %sfhome%\temp\*.jar %sfhome%\signedLib
rem delete the temp dierctory
del %sfhome%\temp\*.jar
rmdir %sfhome%\temp
cd %SFHOME%
cd ..
start sfDaemon
call sfStart localhost ex1 org/smartfrog/test/system/cleanup/tcn20.sf
call sfStopDaemon localhost
