@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfStart.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if defined SFPRIVATE goto continue2
  set SFPRIVATE=%SFHOME%\private
:continue2

if (%1) == () goto usage
if (%1) == (-?) goto help
if (%2) == () goto usage
if (%3) == () goto usage

call %SFHOME%\bin\security\setClassPath
java -Djava.security.policy==%SFHOME%\private\sf.policy -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties org.smartfrog.SFSystem -a %2:DEPLOY:%3::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfStart 
:help
echo Usage: sfStart HostName ApplicationName URL
:end
endlocal
