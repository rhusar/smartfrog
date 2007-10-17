@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfUpdate.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1) == (-?) goto help
if (%2) == () goto usage
if (%3) == () goto usage

call "%SFHOME%\bin\setSFProperties"

if defined USERNAMEPREFIX_ON goto modify
set APPNAME=%2
goto execute

:modify
set APPNAME="%USERNAME%_%2"

:execute
rem %SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%2\":UPDATE:\"%3\"::%1: -e
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%APPNAME%\":UPDATE:\"%3\"::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfUpdate 
echo Usage: sfUpdate HostName ApplicationName URL
exit /B 69
:help
echo Usage: sfUpdate HostName ApplicationName URL
exit /B 0
:end
endlocal
