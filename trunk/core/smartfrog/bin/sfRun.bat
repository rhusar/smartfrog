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

rem sfDefault files are only needed in sfDaemon and sfRun
if defined SFDEFAULTSF  set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTSF%

%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem  -a :DEPLOY:\"%1\"::: %2
GOTO end
:usage
echo Insufficient arguments to use sfRun
echo Usage: sfRun URL [-e]
exit /B 69
:help
echo Usage: sfRun URL [-e]
exit /B 0
:end
endlocal
