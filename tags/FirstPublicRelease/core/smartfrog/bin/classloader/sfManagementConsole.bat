@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfManagementConsole.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%SFHOME%\lib\sfServices.jar;%SFHOME%\lib\sfTestCases.jar;%CLASSPATH%  
rem set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%CLASSPATH%

set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 
rem set CODEBASE="http://%SERVER%/sfExamples.jar http://%SERVER%/sfServices.jar" 
java org.smartfrog.services.management.SFDeployDisplay %1 %2 %3 %4 %5 %6 %7 %8
endlocal
