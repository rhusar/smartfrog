@echo off

rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------

rem -- User libs directory --
rem set SFUSERHOME=%SFHOME%\mylibs

rem -- default.ini and default.sf without security --
set SFDEFAULTINI=-Dorg.smartfrog.iniFile=%SFHOME%\bin\default.ini
set SFDEFAULTSF=-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=%SFHOME%\bin\default.sf

rem -------------------End user properties-------------------------
