@echo off
setlocal

if defined SFHOME goto homeset
  set SFHOME=%~dp0..
:homeset

if (%1) == () goto usage
if (%1) == (-?) goto help
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin

rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

if (%2) == () goto next
set COMPONENT=%2
goto execute
:next
set COMPONENT="rootProcess"


:execute
echo "Pinging %1 in %COMPONENT%"
java %SFCMDPARAMETERS% org.smartfrog.SFSystem -a %COMPONENT%:PING:::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfPing
:help
echo Usage: sfPing HostName [ComponentName]
:end

endlocal
