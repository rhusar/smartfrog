@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\smartfrog.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%SFHOME%\lib\sfServices.jar;%SFHOME%\lib\sfTestCases.jar;%CLASSPATH%
rem set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%CLASSPATH%

set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver
set CODEBASE="http://%SERVER%/sfExamples.jar"
java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini -Dorg.smartfrog.codebase=%CODEBASE% org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal

