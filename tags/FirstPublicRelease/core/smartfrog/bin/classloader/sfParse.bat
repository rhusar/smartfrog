@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfParse.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1) == (-f) goto second
goto run
:second
if (%2) == () goto usage
:run

set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%SFHOME%\lib\sfServices.jar;%SFHOME%\lib\sfTestCases.jar;%CLASSPATH%  
rem set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%CLASSPATH%

set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 
rem set CODEBASE="http://%SERVER%/sfExamples.jar http://%SERVER%/sfServices.jar"

java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini -Dorg.smartfrog.codebase=%CODEBASE% org.smartfrog.SFParse %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:usage
echo Insufficient arguments to use sfParse 
echo "Usage: sfParse [-v] [-q] [-d] [-r] [-R] [{-f filename}|URL]" 
echo sfParse -? 
:end
endlocal
