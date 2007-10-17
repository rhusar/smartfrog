@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

rem sfDefault files are only needed in sfDaemon and sfRun
if defined SFDEFAULTSF  set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTSF%
rem RMI server hostname only needed in sfDaemon and sfRun
if defined SFRMIHOSTNAME  set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFRMIHOSTNAME%

rem sort cmd line args into JVMARGS or CLASSARGS.
rem JVMARGS are declared using -J token 
rem e.g. -J "-Djava.library.path=/libs -Xmx400M"
rem e.g. -J "-Djava.library.path=/libs" -J -Xmx400M
rem (JVMARGS are appended to SFCMDPARAMETERS)
SET CLASSARGS=
:start
IF /I "%1"=="-J" GOTO readJARG 
SET CLASSARGS=%CLASSARGS% %1
GOTO test

:readJARG
SHIFT
SET SFCMDPARAMETERS=%SFCMDPARAMETERS% %1

:test
SHIFT
IF NOT "%1"=="" GOTO start

:end
@echo off
rem for JMX remote agent add: -Dcom.sun.management.jmxremote 
%SFJVM% %SFCMDPARAMETERS% -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
