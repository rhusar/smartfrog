@echo off

rem ###################################################################
rem # This file is run only when SFDYNAMICCLASSLOADING_ON is defined
rem ###################################################################

rem ------------------------------------------------------
rem SF ENV PROPERTIES  - Please edit with your preferences
rem ------------------------------------------------------

rem -- Dynamic classloading: CODEBASE --
set SERVER=http://localhost:8080
set CODEBASE="%SERVER%/sfExamples.jar"

set SFCODEBASE="-Dorg.smartfrog.codebase=%CODEBASE%"

rem -------------------End user properties-------------------------
