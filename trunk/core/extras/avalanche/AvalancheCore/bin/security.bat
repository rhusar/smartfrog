@echo off
setlocal
if defined SFHOME goto continue1
cd ..\..
set AVALANCHE_HOME=%cd%\server
set SFHOME=%AVALANCHE_HOME%\smartfrog\dist
:continue1

cd %SFHOME_BASE%
call ant initCA
call ant signJars
call ant newDaemon
move %SFHOME_BASE%\dist\private\host???? %SFHOME_BASE%\dist\private\host1
copy %SFHOME_BASE%\dist\private\host1 %SFHOME%\private\host1

move %SFHOME_BASE%\dist\signedLib\smartfrog*.jar %SFHOME_BASE%\dist\signedLib\smartfrog.jar
move %SFHOME_BASE%\dist\signedLib\sfServices*.jar %SFHOME_BASE%\dist\signedLib\sfServices.jar
move %SFHOME_BASE%\dist\signedLib\sfExamples*.jar %SFHOME_BASE%\dist\signedLib\sfExamples.jar

move %SFHOME_BASE%\dist\lib\avalanche\sf-quartz*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-quartz.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-vmware*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-vmware.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-xmpp*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-xmpp.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-ant*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-ant.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-anubis*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-anubis.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-installer*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-installer.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-jmx*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-jmx.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-loggingservices*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-loggingservices.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-net*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-net.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-ssh*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-ssh.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-rest*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-rest.jar
move %SFHOME_BASE%\dist\lib\avalanche\sf-avalanche-core*.jar %SFHOME_BASE%\dist\lib\avalanche\sf-avalanche-core.jar
move %SFHOME_BASE%\dist\lib\avalanche\quartz*.jar %SFHOME_BASE%\dist\lib\avalanche\quartz.jar
move %SFHOME_BASE%\dist\lib\avalanche\je*.jar %SFHOME_BASE%\dist\lib\avalanche\je.jar
move %SFHOME_BASE%\dist\lib\avalanche\smackx-*.jar %SFHOME_BASE%\dist\lib\avalanche\smackx.jar
move %SFHOME_BASE%\dist\lib\avalanche\smack-*.jar %SFHOME_BASE%\dist\lib\avalanche\smack.jar

ant -f security.xml signExtraJars

endlocal
