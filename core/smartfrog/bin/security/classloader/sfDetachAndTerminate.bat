@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDetachAndTerminate.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if defined SFPRIVATE goto continue2
  set SFPRIVATE=%SFHOME%\private
:continue2
if (%1) == () goto usage
if (%2) == () goto usage
rem call %SFHOME%\bin\security\setClassPath

set CLASSPATH=%SFHOME%\signedLib\smartfrog.jar;%SFHOME%\signedLib\sfServices.jar;%SFHOME%\signedLib\sfTestCases.jar;%CLASSPATH%  
set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 

java -Djava.security.manager -Djava.security.policy==%SFHOME%\private\sf.policy -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.codebase=%CODEBASE% -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties org.smartfrog.SFSystem -h %1 -d %2 -e

GOTO end
:usage
echo Insufficient arguments to use sfDetachAndTerminate
echo Usage: sfDetachAndTerminate HostName ComponentnName
:end
endlocal
