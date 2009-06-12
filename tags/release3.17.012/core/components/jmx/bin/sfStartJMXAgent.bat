@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfStartJMXAgent.bat" cd ..
  set SFHOME=%cd%
 cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

rem call %SFHOME%\bin\setClassPath


java org.smartfrog.SFSystem  -a \"sfJMXAgent\":DEPLOY:\"%SFHOME%/bin/sfJMXAgent.sf\"::localhost: -e

rem java com.hp.SmartFrog.SFSystem -h localhost -n sfJMXAgent %SFHOME%/bin/sfJMXAgent.sf -e

endlocal
