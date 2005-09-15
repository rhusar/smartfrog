@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfVersion.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

echo SmartFrog Version:
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.Version

endlocal
