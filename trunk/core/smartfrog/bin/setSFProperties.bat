@echo off

rem  ------------------------------------------------------
rem  SF ENV PROPERTIES  - Please edit with your preferences
rem  ------------------------------------------------------

rem Uncomment to enable Dynamic ClassLoading
rem  set SFDYNAMICCLASSLOADING_ON=ENABLED

rem Uncomment to enable Security
rem  set SFSECURITY_ON=ENABLED

rem -------------------End user properties-------------------------


call "%SFHOME%\bin\setSFDefaultProperties"
if defined SFDYNAMICCLASSLOADING_ON call "%SFHOME%\bin\setSFDynamicClassLoadingProperties"
if defined SFSECURITY_ON call "%SFHOME%\bin\setSFSecurityProperties"


if defined SFSECURITY   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFSECURITY%
if defined SFDEFAULTINI set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTINI%
if defined SFCODEBASE   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFCODEBASE%

set SFLIBPATH=%SFHOME%\lib
if defined SFSECURITY set SFLIBPATH=%SFHOME%\signedLib

rem reset CLASSPATH
set CLASSPATH=

call "%SFHOME%\bin\setClassPath"

if not defined SFUSERHOME goto continue2
  set SFLIBPATH=%SFUSERHOME%
  CALL "%SFHOME%\bin\setClassPath"
:continue2

if not defined SFUSERHOME1 goto continue3
  set SFLIBPATH=%SFUSERHOME1%
  CALL "%SFHOME%\bin\setClassPath"
:continue3

if not defined SFUSERHOME2 goto continue4
  set SFLIBPATH=%SFUSERHOME2%
  CALL "%SFHOME%\bin\setClassPath"
:continue4


if not defined SFUSERHOME3 goto continue5
  set SFLIBPATH=%SFUSERHOME3%
  CALL "%SFHOME%\bin\setClassPath"
:continue5

if not defined SFUSERHOME4 goto continue6
  set SFLIBPATH=%SFUSERHOME4%
  CALL "%SFHOME%\bin\setClassPath"
:continue6

if defined srcDir set CLASSPATH=%srcDir%;%classpath%


