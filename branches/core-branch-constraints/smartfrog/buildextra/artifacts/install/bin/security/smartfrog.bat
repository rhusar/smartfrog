@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\..\sfDaemon.bat" cd ..\..
  set SFHOME="%cd%"
  cd bin
:continue1

rem Uncomment to enable Dynamic ClassLoading
rem set SFDYNAMICCLASSLOADING_ON=ENABLED

rem Security Enabled
set SFSECURITY_ON=ENABLED

call "%SFHOME%\bin\smartfrog" %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal


