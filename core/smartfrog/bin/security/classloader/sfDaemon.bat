@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if defined SFPRIVATE goto continue2
  set SFPRIVATE=%SFHOME%\private
:continue2

set CLASSPATH=%SFHOME%\signedLib\smartfrog.jar;%SFHOME%\signedLib\sfServices.jar;%SFHOME%\signedLib\sfTestCases.jar;%CLASSPATH%  
set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 

java -Djava.security.manager -Djava.security.policy==%SFHOME%\private\sf.policy -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties -Dorg.smartfrog.codebase=%CODEBASE% -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess  org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

GOTO end
:usage
echo Insufficient argument(s) to use sfDaemon
echo Usage: sfDaemon
:end
endlocal

