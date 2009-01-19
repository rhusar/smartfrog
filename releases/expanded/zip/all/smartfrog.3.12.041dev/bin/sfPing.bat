@echo off
setlocal

if defined SFHOME goto homeset
  set SFHOME=%~dp0..
:homeset

if (%1) == () goto usage
if (%1) == (-?) goto help
if (%1) == (-p) GOTO usage
call "%SFHOME%\bin\setSFProperties"

if (%2) == () goto next
if (%2)==(-p) goto next2
if defined USERNAMEPREFIX_ON goto modify
set COMPONENT=%2
goto run

:modify
set COMPONENT="%USERNAME%_%2"
goto run

:next
set COMPONENT="rootProcess"
goto run
:next2
set COMPONENT="rootProcess"
goto execute1

:run
if (%3) == (-p) goto execute2
echo "Pinging %1 in %COMPONENT%"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":PING:::%1: -e
GOTO end
:execute1
if (%3)==() goto usage
echo "Pinging %1 in %COMPONENT%"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":PING:::%1: -p %3 -e
GOTO end
:execute2
if (%4)==() goto usage
echo "Pinging %1 in %COMPONENT%"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":PING:::%1: -p %4 -e
GOTO end
:usage
echo Insufficient arguments to use sfPing
echo Usage: sfPing HostName [ComponentName] [-p port]
exit /B 69
:help
echo Usage: sfPing HostName [ComponentName] [-p port]
exit /B 0
:end
endlocal
