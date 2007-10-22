@echo on
echo --------
echo Usage:   %0 jdk_home sfhome (classic/hotspot/server)
echo NOTE:    You MAY NOT use spaces in the path names. 
echo          JDK 1.3 does not come with hotpot server by default, you must
echo          install this seperately if you wish to use it.
echo Example: %0 d:\jdk1.3 C:\SmartFrog server [-s] [-d] [http:\\localhost:8080\sfExamples.jar] 
echo --------
echo %5 >> C:\out.txt
wzunzip -d release.zip
echo %ERRORLEVEL% >> C:\out.txt
copy /B JavaService.exe %2\bin\Daemon.exe /B 
set SFHOME=%2

if "%4" == "-s" goto sec

set SFLIBPATH=%2\lib
call "%2\bin\setClassPath"
echo %CLASSPATH% >> C:\out.txt

if "%4" == "-d" goto dc

echo "starting in unsecure mode without classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Djava.class.path=%CLASSPATH% -Xms16M -Xmx32M -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=%2\bin\default.ini -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log

goto eof

: dc
echo %5 >> C:\out.txt
echo "starting in unsecure mode with classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Djava.class.path=%CLASSPATH% -Xms16M -Xmx32M -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=%2\bin\default.ini -Dorg.smartfrog.codebase="%5" -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log

goto eof

: sec
cd %2
md private\host1
move ..\..\mykeys.st private\host1\mykeys.st
move ..\..\SFSecurity.properties private\host1\SFSecurity.properties
move ..\..\smartfrog.jar signedLib\smartfrog.jar
move ..\..\sfServices.jar signedLib\sfServices.jar
move ..\..\sfExamples.jar signedLib\sfExamples.jar
set SFLIBPATH=%2\signedLib
call "%2\bin\setClassPath"

if "%5" == "-d" goto secdc

echo "starting in secure mode without classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll -Djava.security.policy==%2\private\sf.policy -Djava.security.manager -Djava.class.path=%CLASSPATH% -Xms16M -Xmx32M -Dorg.smartfrog.sfcore.security.keyStoreName=%2\private\host1\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%2\private\host1\SFSecurity.properties -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=org/smartfrog/default.ini -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log

goto eof

:secdc
echo "starting in secure mode with classloading"
%2\bin\Daemon.exe -install Daemon %1\jre\bin\%3\jvm.dll  -Djava.security.policy==%2\private\sf.policy -Djava.security.manager -Djava.class.path=%CLASSPATH% -Xms16M -Xmx32M -Dorg.smartfrog.sfcore.security.keyStoreName=%2\private\host1\mykeys.st -Dorg.smartfrog.sfcore.security.propFile=%2\private\host1\SFSecurity.properties -Dorg.smartfrog.sfcore.processcompound.sfProcessName=rootProcess -Dorg.smartfrog.iniFile=org/smartfrog/default.ini -Dorg.smartfrog.codebase="%6" -start org.smartfrog.SFSystem -stop org.smartfrog.SFSystem -out %2\stdout.log -err %2\stderr.log
goto eof

:eof
net start Daemon
