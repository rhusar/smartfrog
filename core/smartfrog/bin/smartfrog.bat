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
rem SET CLASSARGS=
rem :start
rem IF /I "%1"=="-J" GOTO readJARG 
rem SET CLASSARGS=%CLASSARGS% %1
rem GOTO test

rem :readJARG
rem SHIFT
rem SET SFCMDPARAMETERS=%SFCMDPARAMETERS% %1

rem :test
rem SHIFT

rem IF NOT "%1"==" GOTO start

rem  :end

rem  %SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem %CLASSARGS%

%SFJVM% %SFCMDPARAMETERS% org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9

endlocal
