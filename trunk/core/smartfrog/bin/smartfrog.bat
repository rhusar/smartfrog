@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
rem if NOT (%1)==() GOTO usage

:run
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath

java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

:usage
echo Insufficient arguments to use smartfrog
:end
endlocal

