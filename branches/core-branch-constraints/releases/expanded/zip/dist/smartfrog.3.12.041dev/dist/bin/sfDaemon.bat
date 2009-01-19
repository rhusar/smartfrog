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
IF (%1) == (-p) goto check
SET CLASSARGS=%CLASSARGS% %1
GOTO test

:readJARG
if (%2) == () goto usage2
SHIFT
SET SFCMDPARAMETERS=%SFCMDPARAMETERS% %1
goto test

:check
if (%2) == () goto usage
SET CLASSARGS=%CLASSARGS% %1

:test
SHIFT
IF NOT "%1"=="" GOTO start
:end

@echo off
rem for JMX remote agent add: -Dcom.sun.management.jmxremote 
%SFJVM% %SFCMDPARAMETERS% -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess org.smartfrog.SFSystem %CLASSARGS%
goto finish

:usage2
echo sfDaemon shell script error: -J second arg missing
echo JVMARGS are declared using -J token 
echo e.g. -J "-Djava.library.path=/libs -Xmx400M"
echo e.g. -J "-Djava.library.path=/libs" -J -Xmx400M
goto finish

:usage
echo Usage: sfDaemon -?
:finish
endlocal
