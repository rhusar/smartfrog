@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME="%cd%"
  cd bin
:continue1

if exist "%SFHOME%\jre\bin\java.exe" set path="%SFHOME%\jre\bin"

rem call "%SFHOME%\bin\setClassPath"
call "%SFHOME%\bin\setSFProperties"

rem sfDefault files are only needed in sfDaemon and sfRun
if defined SFDEFAULTSF  set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTSF%

java %SFCMDPARAMETERS% -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
