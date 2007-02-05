@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfStartJMXAgent.bat" cd ..
  set SFHOME=%cd%
:continue1

call %SFHOME%\bin\setClassPath

java com.hp.SmartFrog.SFSystem -h localhost -n sfJMXAgent %SFHOME%/bin/sfJMXAgent.sf -e
endlocal
