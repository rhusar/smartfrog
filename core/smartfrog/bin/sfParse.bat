@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfParse.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if (%1) == () goto usage
if (%1) == (-f) goto second
goto run
:second
if (%2) == () goto usage
:run
if exist "%SFHOME%\jre\bin\java.exe" set path=%SFHOME%\jre\bin
call %SFHOME%\bin\setClassPath
java -Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini org.smartfrog.SFParse %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:usage
echo Insufficient arguments to use sfParse 
echo "Usage: sfParse [-v] [-q] [-d] [-r] [-R] [{-f filename}|URL]" 
echo sfParse -? 
:end
endlocal
