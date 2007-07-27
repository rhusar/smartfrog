

@echo off
set SFHOME=
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\test.bat" cd ..
  set SFHOME="%cd%"
  cd test
:continue1

delete .\log\*.log
delete .\log\*.out


set SFDEFAULTINI=-Dorg.smartfrog.iniFile="%SFHOME%\test\default.ini"
echo "Start Daemon"
start call "%SFHOME%\bin\sfDaemon.bat" 

pause

echo "Run test"
start call "%SFHOME%\bin\smartfrog.bat" -f org/smartfrog/examples/configurationdescriptor/example.sfcd -e

pause

echo "Stop Daemon"
call "%SFHOME%\bin\sfStopDaemon.bat" localhost
echo "Test finished, logs in %SFHOME%\test\log"
pause
