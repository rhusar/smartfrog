@echo off
rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------

rem -- User libs directory --
rem set SFUSERHOME=%SFHOME%\mylibs

rem -- default.ini and default.sf without security --
set SFDEFAULTINI=-Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini
set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=%SFHOME%\bin\default.sf

rem -- Dynamic classloading: CODEBASE --
rem set SERVER=localhost:8080
rem set CODEBASE="http://%SERVER%/sfExamples.jar" 
rem set SFCODEBASE=-Dorg.smartfrog.codebase=%CODEBASE%

rem -- Security properties --
rem set SFSECURITY=-Djava.security.policy==%SFHOME%\private\sf.policy -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName=%SFHOME%\private\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%SFHOME%\private\SFSecurity.properties
rem set SFDEFAULTINI=-Dorg.smartfrog.iniFile=org/smartfrog/default.ini
rem set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=org/smartfrog/default.sf

rem -------------------End user properties-------------------------


if defined SFSECURITY   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFSECURITY%
if defined SFDEFAULTINI set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTINI%
if defined SFCODEBASE   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFCODEBASE%

set SFLIBPATH=%SFHOME%\lib
if defined SFSECURITY set SFLIBPATH=%SFHOME%\signedLib

rem reset CLASSPATH
set CLASSPATH=

call "%SFHOME%\bin\setClassPath"

if not defined SFUSERHOME goto continue2
  set SFLIBPATH=%SFUSERHOME%
  CALL "%SFHOME%\bin\setClassPath"
:continue2

if defined srcDir set CLASSPATH=%srcDir%;%classpath%


