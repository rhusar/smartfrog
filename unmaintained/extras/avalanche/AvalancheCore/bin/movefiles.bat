@echo off
setlocal

move %SLIB%\smartfrog*.jar %SLIB%\smartfrog.jar
move %SLIB%\sfServices*.jar %SLIB%\sfServices.jar
move %SLIB%\sfExamples*.jar %SLIB%\sfExamples.jar

move %SFHOME_BASE%\sf-quartz*.jar %SFHOME_BASE%\sf-quartz.jar
move %SFHOME_BASE%\sf-vmware*.jar %SFHOME_BASE%\sf-vmware.jar
move %SFHOME_BASE%\sf-xmpp*.jar %SFHOME_BASE%\sf-xmpp.jar
move %SFHOME_BASE%\sf-ant*.jar %SFHOME_BASE%\sf-ant.jar
move %SFHOME_BASE%\sf-anubis*.jar %SFHOME_BASE%\sf-anubis.jar
move %SFHOME_BASE%\sf-installer*.jar %SFHOME_BASE%\sf-installer.jar
move %SFHOME_BASE%\sf-jmx*.jar %SFHOME_BASE%\sf-jmx.jar
move %SFHOME_BASE%\sf-loggingservices*.jar %SFHOME_BASE%\sf-loggingservices.jar
move %SFHOME_BASE%\sf-net*.jar %SFHOME_BASE%\sf-net.jar
move %SFHOME_BASE%\sf-ssh*.jar %SFHOME_BASE%\sf-ssh.jar
move %SFHOME_BASE%\sf-rest*.jar %SFHOME_BASE%\sf-rest.jar
move %SFHOME_BASE%\je*.jar %SFHOME_BASE%\je.jar
move %SFHOME_BASE%\smackx-*.jar %SFHOME_BASE%\smackx.jar
move %SFHOME_BASE%\smack-*.jar %SFHOME_BASE%\smack.jar
move %SFHOME_BASE%\sf-emailer*.jar %SFHOME_BASE%\sf-emailer.jar
move %SFHOME_BASE%\mail*.jar %SFHOME_BASE%\mail.jar

endlocal

