@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfStart.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%2) == () goto usage
if (%3) == () goto usage
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath
java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini org.smartfrog.SFSystem -h %1 -n %2 %3 -e
GOTO end
:usage
echo Insufficient arguments to use sfStart 
echo Usage: sfStart HostName ApplicationName URL
:end
endlocal
