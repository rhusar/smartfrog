@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfTerminate.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1) == (-?) goto help
if (%2) == () goto usage
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin

call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

echo "Terminating %1"
java %SFCMDPARAMETERS% org.smartfrog.SFSystem -a %2:TERMINATE:::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfTerminate
:help
echo Usage: sfTerminate HostName ComponentName
:end

endlocal
