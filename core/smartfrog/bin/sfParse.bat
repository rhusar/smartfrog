@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfParse.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath
java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini org.smartfrog.SFParse %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
