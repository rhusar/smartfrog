@echo off

rem INFO
rem JVMARGS are declared using -J token in the command line
rem e.g. -J "-Djava.library.path=/libs -Xmx400M"
rem e.g. -J "-Djava.library.path=/libs" -J -Xmx400M

rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------

rem -- User libs directories --
rem set SFUSERHOME=%SFHOME%\mylibs
rem set SFUSERHOME1=%SFHOME%\mylibs1
rem set SFUSERHOME2=%SFHOME%\mylibs2
rem set SFUSERHOME3=%SFHOME%\mylibs3
rem set SFUSERHOME4=%SFHOME%\mylibs4
rem set SFUSERCLASSPATH=%SFHOME%\myotheruserlibs

rem -- server hostname for multihomed machines
rem set SFSERVERHOSTNAME=localhost

rem -- default.ini and default.sf without security --
if not defined SFDEFAULTINI set SFDEFAULTINI=-Dorg.smartfrog.iniFile="%SFHOME%\bin\default.ini"
if not defined SFDEFAULTSF set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault="%SFHOME%\bin\default.sf"

if defined SFSERVERHOSTNAME set SFRMIHOSTNAME=-Djava.rmi.server.hostname="%SFSERVERHOSTNAME%"

rem -------------------End user properties-------------------------
