@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDetachAndTerminate.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1)==(-?) GOTO help
if (%2) == () goto usage
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

java %SFCMDPARAMETERS%  org.smartfrog.SFSystem -a %2:DETaTERM:::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfDetachAndTerminate
:help
echo Usage: sfDetachAndTerminate HostName ComponentName
:end

endlocal
