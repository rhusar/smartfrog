@echo off
setlocal

if defined SFHOME goto homeset
  set SFHOME=%~dp0..
:homeset

if (%1) == () goto usage
if (%1) == (-?) goto help
if (%1) == (-p) GOTO usage
call "%SFHOME%\bin\setSFProperties"

if (%2) == () goto next2
if (%2) == (-p) goto next2p
if defined USERNAMEPREFIX_ON goto modify
set COMPONENT=%2
goto check3

:modify
set COMPONENT="%USERNAME%_%2"
goto check3

:next2
set COMPONENT="rootProcess"
:check3
if (%3) == () goto next3
if (%3) == (-p) goto nextp
set PROCESS=%3
goto run
:next3
set PROCESS=rootProcess
goto run
:next2p
set COMPONENT="rootProcess"
if (%3)==() goto usage
if (%4) == () goto next3p
set PROCESS=%4
goto execute1
:next3p
set PROCESS=rootProcess
goto execute1
:nextp
set PROCESS=rootProcess
if (%4)==() goto usage
goto execute2
:run
if (%4) == (-p) goto execute3
echo Creating diagnostics report (%1) for %COMPONENT% in %PROCESS%
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":DIAGNOSTICS:::%1:%PROCESS% -e

GOTO end
:execute1
echo Creating diagnostics report (%1) for %COMPONENT% in %PROCESS%
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":DIAGNOSTICS:::%1:%PROCESS% -p %3 -e
GOTO end
:execute2
echo Creating diagnostics report (%1) for %COMPONENT% in %PROCESS%
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":DIAGNOSTICS:::%1:%PROCESS% -p %4 -e
goto end
:execute3
echo Creating diagnostics report (%1) for %COMPONENT% in %PROCESS%
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%COMPONENT%\":DIAGNOSTICS:::%1:%PROCESS% -p %5 -e
goto end
:usage
echo Insufficient arguments to use sfDiagnostics
echo Usage: sfDiagnostics HostName [ComponentName] [ProcessName] [-p port]
exit /B 69
:help
echo Usage: sfDiagnostics HostName [ComponentName] [ProcessName] [-p port]
exit /B 0
:end
endlocal
