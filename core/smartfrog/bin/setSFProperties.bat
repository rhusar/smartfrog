@echo off

rem  ------------------------------------------------------
rem  SF ENV PROPERTIES  - Please edit with your preferences
rem  ------------------------------------------------------

rem Uncomment to enable Dynamic ClassLoading
rem set SFDYNAMICCLASSLOADING_ON=ENABLED

rem Uncomment to enable Security
rem set SFSECURITY_ON=ENABLED

rem Uncomment to load local descriptions when security is on 
rem set SFSECURERESOURCES_OFF=ENABLED


rem To define a user classpath, use variable SFUSERCLASSPATH
rem set SFUSERCLASSPATH=.

rem To define the jvm executable 
if defined SFJVM goto jvmnext 
rem set SFJVM=javaw.exe
set SFJVM=java.exe
:jvmnext

rem To define the prefix to componentname with username
rem set USERNAMEPREFIX_ON=ENABLED


rem -------------------End user properties-------------------------

if exist "%SFHOME%\jvm\bin\%SFJVM%" set path="%SFHOME%\jre\bin";%path%

call "%SFHOME%\bin\setSFDefaultProperties"
if defined SFDYNAMICCLASSLOADING_ON call "%SFHOME%\bin\setSFDynamicClassLoadingProperties"
if defined SFSECURITY_ON call "%SFHOME%\bin\setSFSecurityProperties"


if defined SFDEFAULTINI set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFDEFAULTINI%
if defined SFCODEBASE   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFCODEBASE%
if defined SFSECURITY   set SFCMDPARAMETERS=%SFCMDPARAMETERS% %SFSECURITY%

set SFLIBPATH=%SFHOME%\lib
if defined SFSECURITY set SFLIBPATH=%SFHOME%\signedLib

rem reset CLASSPATH
set CLASSPATH=

rem SF/lib (SF core libs)
call "%SFHOME%\bin\setClassPath"

rem SF/lib (SF core libs)
rem set SFLIBPATH=%SFHOME%\lib.ext
rem CALL "%SFHOME%\bin\setClassPath"

rem now user defined classpaths
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

if defined SFUSERCLASSPATH set CLASSPATH=%SFUSERCLASSPATH%;%classpath%


