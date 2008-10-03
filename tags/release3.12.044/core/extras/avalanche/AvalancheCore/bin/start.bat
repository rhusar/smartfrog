@echo off
setlocal
if defined SFHOME goto continue1
cd ..\..
set AVALANCHE_HOME="%cd%\server"
set SFHOME="%AVALANCHE_HOME%\smartfrog\dist"
:continue1

if defined XMPP_HOME goto continue2
echo SET XMPP_HOME to the installation directory of XMPP Server

:continue2
if defined TOMCAT_HOME goto security
echo SET TOMCAT_HOME to the installation directory of TOMCAT
:security
set CATALINA_HOME=%TOMCAT_HOME%
if "%SECURITY_ON%"=="false" goto start
set SFSECURITY_ON=ENABLED
set SFSECURERESOURCES_OFF=ENABLED

:start
set PATH="%SFHOME%\bin";"%XMPP_HOME%\bin";"%TOMCAT_HOME%\bin";%PATH%

start sfDaemon.bat
start openfire.exe
set SECURITY_POLICY_FILE="%SFHOME%\private\sf.no.security.policy"
set CLASSPATH="%JAVA_HOME%\lib\tools.jar"
set CLASSPATH=%CLASSPATH%;"%CATALINA_HOME%\bin\bootstrap.jar"
move "%CATALINA_HOME%\common\lib\smartfrog*.jar" "%CATALINA_HOME%\common\lib\smartfrog.jar"
set CLASSPATH=%CLASSPATH%;"%CATALINA_HOME%\common\lib\smartfrog.jar"
set SFSECURITY=
if "%SECURITY_ON%"=="false" goto catalina
if not defined SFPRIVATE set SFPRIVATE="%SFHOME%\private"
if not defined SFHOSTNAME set SFHOSTNAME=host1
set AVALANCHE_ROOT=%cd%
set SECURITY_POLICY_FILE="%SFHOME%\private\sf.policy"
set SFSECURITY=-Dorg.smartfrog.sfcore.security.keyStoreName="%SFPRIVATE%\%SFHOSTNAME%\mykeys.st" -Dorg.smartfrog.sfcore.security.propFile="%SFPRIVATE%\%SFHOSTNAME%\SFSecurity.properties" -Dorg.smartfrog.sfcore.security.secureResourcesOff=true -Davalanche.home="%AVALANCHE_HOME%" -Davalanche.root="%AVALANCHE_ROOT%"
: catalina
java -classpath %CLASSPATH% -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" %SFSECURITY%  -Dcatalina.base="%CATALINA_HOME%" -Dcatalina.home="%CATALINA_HOME%" org.apache.catalina.startup.Bootstrap start
rem call startup.bat


endlocal
