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
if NOT (%1)==(-c) GOTO name 
if (%2)==() GOTO usage 
:run
call %SFHOME%\bin\security\setClassPath
java -Djava.security.policy==%SFHOME%\private\sf.policy -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:name 
if NOT (%1)==(-n) GOTO usage 
if (%2)==() GOTO usage 
if (%3)==() GOTO usage
GOTO run
:usage
echo Insufficient arguments to use sfRun
echo "Usage: sfRun (-c URL | -n NAME URL)* [-e]"
:end
endlocal

