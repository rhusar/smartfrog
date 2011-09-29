@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfStopJMXAgent.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

java org.smartfrog.SFSystem  -a \"sfJMXAgent\":TERMINATE:::localhost: -e


rem java com.hp.SmartFrog.SFSystem -h localhost -t sfJMXAgent -e
endlocal
