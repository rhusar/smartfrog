@echo off
setlocal
if defined SFHOME goto continue1
cd ..\..
set AVALANCHE_HOME=%cd%\server
set SFHOME=%AVALANCHE_HOME%\smartfrog\dist
:continue1

if defined XMPP_HOME goto continue2
echo SET XMPP_HOME to the installation directory of XMPP Server
set XMPP_HOME=C:\installed\openfire

:continue2
if defined TOMCAT_HOME goto start
echo SET TOMCAT_HOME to the installation directory of TOMCAT
set TOMCAT_HOME=C:\installed\apache-tomcat-5.5.20
:start
set CATALINA_HOME=%TOMCAT_HOME%
rem set PATH=%SFHOME%\bin\security;%XMPP_HOME%\bin;%TOMCAT_HOME%\bin;%PATH%
set PATH=%SFHOME%\bin;%XMPP_HOME%\bin;%TOMCAT_HOME%\bin;%PATH%

rem cd %SFHOME_BASE%
rem call ant initCA
rem call ant signJars
rem call ant newDaemon
rem move %SFHOME_BASE%\dist\private\host???? %SFHOME_BASE%\dist\private\host1
rem copy %SFHOME_BASE%\dist\private\host1 %SFHOME%\private\host1
rem copy %SFHOME_BASE%\dist\signedLib\*.* %SFHOME%\signedLib


rem start sfDaemon.bat -f org/smartfrog/services/quartz/scheduler/SchedulerSetup.sf
start sfDaemon.bat 
start openfire.exe
call startup.bat


endlocal
