@echo off
rem ------------------------------------------------------------------------
rem Batch file for setting SFHOME & PATH variables for executing the examples.
rem ------------------------------------------------------------------------

set SFHOME=..\..\..\..\..\dist
set PATH=%SFHOME%\bin;%PATH%
set CLASSPATH=.;%CLASSPATH%;

