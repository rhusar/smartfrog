@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfStop.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if defined SFPRIVATE goto continue2
  set SFPRIVATE=%SFHOME%\private
:continue2

if (%1) == () goto usage
if (%2) == () goto usage

call %SFHOME%\bin\security\setClassPath
java -Djava.security.policy==%SFHOME%\private\sf.policy -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties org.smartfrog.SFSystem -h %1 -t %2 -e

GOTO end
:usage
echo Insufficient arguments to use sfStop
echo Usage: sfStop HostName ApplicationName
:end
endlocal
