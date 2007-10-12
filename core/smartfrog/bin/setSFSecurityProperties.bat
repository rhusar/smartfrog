@echo off

rem #################################################################
rem # This file is run only when SFSECURITY_ON is defined
rem #################################################################

rem set default values if the user doesn't provide them
if not defined SFPRIVATE set SFPRIVATE=%SFHOME%\private
if not defined SFHOSTNAME set SFHOSTNAME=host1

rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------
rem -- Security properties --
set SFSECURITY=-Djava.security.policy=="%SFPRIVATE%\sf.policy" -Djava.security.manager -Dorg.smartfrog.sfcore.security.keyStoreName="%SFPRIVATE%\%SFHOSTNAME%\mykeys.st" -Dorg.smartfrog.sfcore.security.propFile="%SFPRIVATE%\%SFHOSTNAME%\SFSecurity.properties"

if defined SFSECURERESOURCES_OFF set SFSECURITY=%SFSECURITY% -Dorg.smartfrog.sfcore.security.secureResourcesOff=true

rem  -- default.ini and default.sf with security. These files need to be inside a jar file--
set SFDEFAULTINI=-Dorg.smartfrog.iniFile=org/smartfrog/default.ini
set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=org/smartfrog/default.sf

rem -------------------End user properties-------------------------
