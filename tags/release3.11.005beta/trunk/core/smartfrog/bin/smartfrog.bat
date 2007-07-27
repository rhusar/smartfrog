@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\smartfrog.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

rem echo %SFCMDPARAMETERS%
rem echo java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
