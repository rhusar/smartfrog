@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfManagementConsole.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath
java org.smartfrog.services.management.SFDeployDisplay %1 %2 %3 %4 %5 %6 %7 %8
endlocal