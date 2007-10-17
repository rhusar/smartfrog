@echo off
setlocal

if defined SFHOME goto homeset
  set SFHOME=%~dp0..
:homeset

if (%1) == () goto usage
if (%1) == (-?) goto help

call "%SFHOME%\bin\setSFProperties"

if (%2) == () goto next

if defined USERNAMEPREFIX_ON goto modify
set COMPONENT=%2
goto execute

:modify
set COMPONENT="%USERNAME%_%2"
goto execute

:next
set COMPONENT="rootProcess"


:execute
echo "Pinging %1 in %COMPONENT%"
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":PING:::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfPing
echo Usage: sfPing HostName [ComponentName]
exit /B 69
:help
echo Usage: sfPing HostName [ComponentName]
exit /B 0
:end
endlocal
