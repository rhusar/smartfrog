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

if (%2) == () goto next2
set COMPONENT=%2
goto execute
:next2
set COMPONENT="rootProcess"

if (%3) == () goto next3
set PROCESS=%3
goto execute
:next3
set PROCESS=rootProcess

:execute
echo Creating diagnostics report (%1) for %COMPONENT% in %PROCESS%
java %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":DIAGNOSTICS:::%1:%PROCESS% -e

GOTO end
:usage
echo Insufficient arguments to use sfDiag
:help
echo Usage: sfDiag HostName [ComponentName] [ProcessName]
:end

endlocal
