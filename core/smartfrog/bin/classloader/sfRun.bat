@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1)==() GOTO usage 
if (%1)==(-?) GOTO help  
set CLASSPATH=%SFHOME%\lib\smartfrog.jar;%SFHOME%\lib\sfServices.jar;%SFHOME%\lib\sfTestCases.jar;%CLASSPATH%

set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 

rem if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
rem call %SFHOME%\bin\setClassPath
java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini -Dorg.smartfrog.codebase=%CODEBASE% org.smartfrog.SFSystem  -a :DEPLOY:%1::: %2
GOTO end
:usage
echo Insufficient arguments to use sfRun
:help
echo Usage: sfRun URL [-e]
:end
endlocal

