@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath
java -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini -Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=%SFHOME%\bin\default.sf org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:usage
echo Insufficient argument(s) to use sfDaemon
echo Usage: sfDaemon
:end

endlocal
