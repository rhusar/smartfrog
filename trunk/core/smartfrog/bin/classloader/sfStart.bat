@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfStart.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%2) == () goto usage
if (%3) == () goto usage
set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%SFHOME%\lib\sfServices.jar;%SFHOME%\lib\sfTestCases.jar;%CLASSPATH%

set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 

rem if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
rem call %SFHOME%\bin\setClassPath
java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini -Dorg.smartfrog.codebase=%CODEBASE% org.smartfrog.SFSystem -a %2:DEPLOY:%3::%1: -e
GOTO end
:usage
echo Insufficient arguments to use sfStart 
echo Usage: sfStart HostName ApplicationName URL
:end
endlocal
