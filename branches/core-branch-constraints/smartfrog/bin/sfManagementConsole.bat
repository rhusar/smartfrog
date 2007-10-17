@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfManagementConsole.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

%SFJVM% %SFCMDPARAMETERS% org.smartfrog.services.management.SFDeployDisplay %1 %2 %3 %4 %5 %6 %7 %8
endlocal
