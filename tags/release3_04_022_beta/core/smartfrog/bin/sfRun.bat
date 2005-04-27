@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1)==() GOTO usage 
if (%1)==(-?) GOTO help 

if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
rem call %SFHOME%\bin\setClassPath
call "%SFHOME%\bin\setSFProperties"

rem sfDefault files are only needed in sfDaemon and sfRun
if defined SFDEFAULTSF  set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTSF%

java %SFCMDPARAMETERS% org.smartfrog.SFSystem  -a :DEPLOY:%1::: %2
GOTO end
:usage
echo Insufficient arguments to use sfRun
:help
echo Usage: sfRun URL [-e]
:end
endlocal
