@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfStart.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1) == (-?) goto help
if (%1) == (-p) GOTO usage
if (%2) == (-p) GOTO usage
if (%3) == (-p) GOTO usage
if (%2) == () goto usage
if (%3) == () goto usage

call "%SFHOME%\bin\setSFProperties"

if defined USERNAMEPREFIX_ON goto modify
set APPNAME=%2
goto run

:modify
set APPNAME="%USERNAME%_%2"
:run
if (%4)==(-p) GOTO execute
rem %SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%2\":DEPLOY:\"%3\"::%1: -e
%SFJVM% %SFCMDPARAMETERS% %SFLIBRARYPATH% org.smartfrog.SFSystem -a \"%APPNAME%\":DEPLOY:\"%3\"::%1: -e
GOTO end
:execute
if (%5)==() goto usage
%SFJVM% %SFCMDPARAMETERS% %SFLIBRARYPATH% org.smartfrog.SFSystem -a \"%APPNAME%\":DEPLOY:\"%3\"::%1: -p %5 -e


GOTO end
:usage
echo Insufficient arguments to use sfStart 
echo Usage: sfStart HostName ApplicationName URL [-p port]
exit /B 69
:help
echo Usage: sfStart HostName ApplicationName URL [-p port]
exit /B 0
:end
endlocal
