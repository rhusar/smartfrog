@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\smartfrog.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"

rem echo %SFCMDPARAMETERS%
rem echo java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

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
echo %SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem %CLASSARGS%
%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem %CLASSARGS%
goto finish

:usage2
echo smarfrog shell script error: -J second arg missing
echo JVMARGS are declared using -J token 
echo e.g. -J "-Djava.library.path=/libs -Xmx400M"
echo e.g. -J "-Djava.library.path=/libs" -J -Xmx400M
goto finish

:usage
echo " "
echo Usage: smartfrog -?
%SFJVM% org.smartfrog.SFSystem -?
echo " "
echo %1 %2 %3 %4 %5 %6 %7 %8 %9
echo sfcmd %SFCMDPARAMETERS%
echo classargs %CLASSARGS%

:finish
endlocal
