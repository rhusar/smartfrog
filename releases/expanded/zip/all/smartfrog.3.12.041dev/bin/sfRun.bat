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

rem sfDefault files are only needed in sfDaemon and sfRun
if defined SFDEFAULTSF  set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTSF%
if (%2)==(-p) GOTO execute
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem  -a :DEPLOY:\"%1\"::: %2
GOTO end
:execute
if (%3)==() goto usage
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem  -a :DEPLOY:\"%1\"::: -p %3 %4

GOTO end
:usage
echo Insufficient arguments to use sfRun
echo Usage: sfRun URL [-p port] [-e]
exit /B 69
:help
echo Usage: sfRun URL [-p port] [-e]
exit /B 0
:end
endlocal
