@echo off
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\sfDaemon.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if defined SFPRIVATE goto continue2
  set SFPRIVATE=%SFHOME%\private
:continue2
if (%1) == () goto usage
if (%1) == (-f) goto second
goto run
:second
if (%2) == () goto usage
:run
call %SFHOME%\bin\security\setClassPath
java -Djava.security.policy==%SFHOME%\private\sf.policy -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties org.smartfrog.SFParse %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:usage
echo Insufficient arguments to use sfParse 
echo "Usage: sfParse [-v] [-q] [-d] [-r] [-R] [{-f filename}|URL]" 
echo sfParse -? 
:end
endlocal

