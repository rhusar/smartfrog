echo off

rem general properties
set SERVER=localhost:8080
rem Please edit codebase if you have any other jar file in webserver 
set CODEBASE="http://%SERVER%/sfExamples.jar" 

rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------

if not defined SFDEFAULTINI set SFDEFAULTINI=-Dorg.smartfrog.iniFile=org/smartfrog/default.ini
if not defined SFDEFAULTSF set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=org/smartfrog/default.sf
rem if not defined SFSECURITY set SFSECURITY=-Djava.security.policy==%SFHOME%\private\sf.policy -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName=%SFHOME%\private\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFHOME%\private\SFSecurity.properties
rem if not defined SFCODEBASE set SFCODEBASE=-Dorg.smartfrog.codebase=%CODEBASE%

rem ------------------------------------------------------

rem set SFCMDPARAMETERS=

if defined SFSECURITY   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFSECURITY%
if defined SFDEFAULTINI set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTINI%
if defined SFCODEBASE   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFCODEBASE%

if not defined SFSECURITY call "%SFHOME%\bin\setClassPath" 
if defined SFSECURITY call "%SFHOME%\bin\setsecurityClassPath" 

