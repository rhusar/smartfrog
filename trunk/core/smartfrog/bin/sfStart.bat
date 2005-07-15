@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfStart.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1) == (-?) goto help
if (%2) == () goto usage
if (%3) == () goto usage
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin

rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

java %SFCMDPARAMETERS% org.smartfrog.SFSystem -a \"%2\":DEPLOY:\"%3\"::%1: -e

GOTO end
:usage
echo Insufficient arguments to use sfStart 
:help
echo Usage: sfStart HostName ApplicationName URL
:end
endlocal
