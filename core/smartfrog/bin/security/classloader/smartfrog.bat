@echo off
setlocal
if defined SFHOME goto continue1
  if exist "%cd%\smartfrog.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1
if defined SFPRIVATE goto continue2
  set SFPRIVATE=%SFHOME%\private
:continue2
if NOT (%1)==() GOTO check1
:run
set CLASSPATH=%SFHOME%\signedLib\smartfrog.jar;%SFHOME%\signedLib\sfServices.jar;%SFHOME%\signedLib\sfTestCases.jar;%CLASSPATH%  
set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 

java -Djava.security.manager -Djava.security.policy==%SFHOME%\private\sf.policy -Dorg.smartfrog.sfcore.security.keyStoreName=%SFPRIVATE%\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFPRIVATE%\SFSecurity.properties -Dorg.smartfrog.codebase=%CODEBASE% org.smartfrog.SFSystem %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO end
:check1
if NOT (%1)==(-a) GOTO check2
if (%2)==() GOTO usage 
GOTO run
:check2
if NOT (%1)==(-f) GOTO check3
if (%2)==() GOTO usage 
GOTO run
:check3
if NOT (%1)==(-e) GOTO usage
if NOT (%2)==() GOTO usage 
GOTO run
:usage
echo Insufficient arguments to use smartfrog
echo Usage smartfrog [-a URL_DESCRIPTOR] [-f FILE_URL] [-e]
echo    -a URL_DESCRIPTOR: descriptor of the application template to deploy
echo       ex. Deploy a description:- 
echo          -a counterEx:DEPLOY:org/.../example.sf:sfConfig:localhost:process 
echo       ex. Terminate local sfDaemon:- 
echo          -a rootProcess:TERMINATE:::localhost:
echo       Format for URL_DESCRIPTOR: name:ACTION:url:target:HOST:PROCESS
echo           - name: name where to apply ACTION
echo           - ACTION: possible actions: DEPLOY, TERMINATE, DETACH, DETaTERM
echo           - url: description used by ACTION
echo           - target: for now only 'sfConfig' or 'empty' are considered
echo           - HOST: host name or IP where to apply ACTION. When empty it 
echo             assumes localhost
echo           - PROCESS: processname where to apply ACTION. When empty it 
echo             assumes rootProcess
echo    -f FILE_URL: file url with the ConfigurationDescriptors to deploy
echo    -e: exit after deployment is finished
:end
endlocal

