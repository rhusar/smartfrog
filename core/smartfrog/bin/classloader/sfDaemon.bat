@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if NOT (%1)==() GOTO usage
set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%SFHOME%\lib\sfServices.jar;%SFHOME%\lib\sfTestCases.jar;%CLASSPATH%  
rem set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%CLASSPATH%

set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 
rem set CODEBASE="http://%SERVER%/sfExamples.jar http://%SERVER%/sfServices.jar" 
java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.codebase=%CODEBASE% -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini -Dorg.smartfrog.iniSFFile=%SFHOME%\bin\default.sf org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:usage
echo Insufficient argument(s) to use sfDaemon
echo Usage: sfDaemon
:end
endlocal

