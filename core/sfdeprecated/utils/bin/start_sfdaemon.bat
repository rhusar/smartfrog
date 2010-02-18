@echo off
@echo off
echo --------
echo Usage:   %0 jdk_home sfhome (classic/hotspot/server)
echo NOTE:    You MAY NOT use spaces in the path names. 
echo          JDK 1.3 does not come with hotpot server by default, you must
echo          install this seperately if you wish to use it.
echo Example: %0 d:\jdk1.3 C:\SmartFrog server [-s] [-d] [http:\\localhost:8080\sfExamples.jar] 
echo --------

copy JavaService.exe %2\bin\Daemon.exe > nul

echo %*
if "%4" == "-s" goto sec
if "%4" == "-d" goto dc
echo "starting in unsecure mode withour classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Xmx256mb -Djava.class.path=%2\lib\smartfrog.jar;%2\lib\sfServices.jar;%2\lib\sfExamples.jar; SFHOME=%2 -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=%2\bin\default.ini -Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=%2\bin\default.sf -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log

goto eof

: dc
echo "starting in unsecure mode with classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Xmx256mb -Djava.class.path=%2\lib\smartfrog.jar;%2\lib\sfServices.jar; SFHOME=%2 -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=%2\bin\default.ini -Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=%2\bin\default.sf -Dorg.smartfrog.codebase="%5" -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log

goto eof

: sec
if "%5" == "-d" goto secdc
echo "starting in secure mode without classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Xmx256mb -Djava.security.policy==%2\private\sf.policy -Djava.security.manager -Djava.class.path=%2\signedLib\smartfrog.jar;%2\signedLib\sfServices.jar;%2\signedLib\sfExamples.jar; SFHOME=%2 -Dorg.smartfrog.sfcore.security.keyStoreName=%2\private\host1\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%2\private\host1\SFSecurity.properties -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=org/smartfrog/default.ini -Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=org/smartfrog/default.sf -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log

goto eof

:secdc
echo "starting in secure mode with classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Xmx256mb -Djava.security.policy==%2\private\sf.policy -Djava.security.manager -Djava.class.path=%2\signedLib\smartfrog.jar;%2\signedLib\sfServices.jar; SFHOME=%2 -Dorg.smartfrog.sfcore.security.keyStoreName=%2\private\host1\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%2\private\host1\SFSecurity.properties -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=org/smartfrog/default.ini -Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=org/smartfrog/default.sf -Dorg.smartfrog.codebase="%6" -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log
goto eof

:eof
