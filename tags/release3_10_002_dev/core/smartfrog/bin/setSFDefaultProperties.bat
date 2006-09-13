@echo off

rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------

rem -- User libs directory --
rem set SFUSERHOME=%SFHOME%\mylibs
rem -- server hostname for multihomed machines
rem set SFSERVERHOSTNAME=localhost

rem -- default.ini and default.sf without security --
if not defined SFDEFAULTINI set SFDEFAULTINI=-Dorg.smartfrog.iniFile="%SFHOME%\bin\default.ini"
if not defined SFDEFAULTSF set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault="%SFHOME%\bin\default.sf"

if defined SFSERVERHOSTNAME set SFRMIHOSTNAME=-Djava.rmi.server.hostname="%SFSERVERHOSTNAME%"

rem -------------------End user properties-------------------------
