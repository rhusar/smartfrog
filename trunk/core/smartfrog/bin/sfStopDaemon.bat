@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1)==() GOTO usage
if (%1)==(-?) GOTO help
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

echo "Stopping sfDaemon in %1"
java %SFCMDPARAMETERS% org.smartfrog.SFSystem -a rootProcess:TERMINATE:::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfStopDaemon
:help
echo Usage: sfStopDaemon HostName
:end 
endlocal
