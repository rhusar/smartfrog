@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1)==() GOTO usage
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath
echo "Stopping sfDaemon in %1"
java org.smartfrog.SFSystem -h1 %1 -t rootProcess -e 
GOTO end
:usage
echo Insufficient arguments to use sfStopDaemon
echo Usage: sfStopDaemon HostName
:end 
endlocal
