@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1)==() GOTO usage
if (%1)==(-?) GOTO help
if (%1) == (-p) GOTO usage
call "%SFHOME%\bin\setSFProperties"
if (%2)==(-p) GOTO execute
echo "Stopping sfDaemon in %1"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a rootProcess:TERMINATE:::%1: -e
GOTO end
:execute
if (%3)==() goto usage
echo "Stopping sfDaemon in %1 running at port %3"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a rootProcess:TERMINATE:::%1: -p %3 -e



GOTO end
:usage
echo Insufficient arguments to use sfStopDaemon
echo Usage: sfStopDaemon HostName [-p port]
exit /B 69
:help
echo Usage: sfStopDaemon HostName [-p port]
exit /B 0
:end 
endlocal
