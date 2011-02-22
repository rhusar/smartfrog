@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfStart.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

if defined USERNAMEPREFIX_ON goto modify
set APPNAME=%2
goto execute

:modify
set APPNAME="%USERNAME%_%2"

:execute
rem %SFJVM% %SFCMDPARAMETERS% org.smartfrog.services.persistence.framework.activator.SFActiveImpl %1 %2 %3 %4 %5 %6 %7 %8 %9
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.services.persistence.framework.activator.SFActiveImpl %1 %2 %3 %4 %5 %6 %7 %8 %9


endlocal
