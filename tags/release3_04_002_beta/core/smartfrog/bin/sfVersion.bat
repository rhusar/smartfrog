@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfVersion.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

echo SmartFrog Version:
java %SFCMDPARAMETERS% org.smartfrog.Version

endlocal
