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

if (%1)==() GOTO usage
if (%1)==(-?) GOTO help

call %SFHOME%\bin\security\setClassPath
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
echo "Stopping sfDaemon in %1"

java -Djava.security.policy==%SFHOME%\private\sf.policy -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties org.smartfrog.SFSystem -a rootProcess:TERMINATE:::%1: -e
GOTO end
:usage
echo Insufficient arguments to use sfStopDaemon
:help
echo Usage: sfStopDaemon HostName
:end 
endlocal
