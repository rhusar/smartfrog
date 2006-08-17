@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1)==() GOTO usage
if (%1)==(-?) GOTO help

call "%SFHOME%\bin\setSFProperties"

echo "Stopping sfDaemon in %1"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a rootProcess:TERMINATE:::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfStopDaemon
echo Usage: sfStopDaemon HostName
exit /B 69
:help
echo Usage: sfStopDaemon HostName
exit /B 0
:end 
endlocal
