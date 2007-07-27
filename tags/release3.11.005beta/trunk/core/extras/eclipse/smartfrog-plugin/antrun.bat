@echo off

rem ### AntBuild.bat v2.0 - wrapper for running Ant from an HP-SEN project directory 
rem ### This batch file requires Windows 2000 or later, since it utilizes Win2K/XP Command Extensions.

rem ### Do _not_ edit the two below lines.
set HPSEN_CVS_TUNNEL=hpmdssh2.esr.hp.com
if not x%CMSERVER% == xTRUE goto localbuild

:cmserver

rem ### Ant home directories for Ant versions available on CMPORTAL are listed below.
rem ### Set as appropriate to the version you wish to use to run your builds.

rem ### Ant v1.3:
set CM_ANT1_3_HOME=D:\ant1.3
rem ### Ant v1.4:
set CM_ANT1_4_HOME=D:\ant1.4
rem ### Ant v1.5.1:
set CM_ANT1_5_HOME=D:\ant1.5
rem ### Ant v1.5.3:
set CM_ANT1_5_3_HOME=D:\ant1.5.3

set ANT_HOME=%CM_ANT1_5_3_HOME%

rem ### JDK home directories for JDK versions available on CMPORTAL are listed below.
rem ### Set as appropriate to the version you wish to use to compile your source files.

rem ### JDK 1.1.8:
set CM_JAVA1_1_HOME=D:\jdk1.1.8
rem ### JDK 1.2.2:
set CM_JAVA1_2_HOME=D:\jdk1.2.2
rem ### JDK 1.3.0_02:
set CM_JAVA1_3_HOME=D:\jdk1.3
rem ### JDK 1.4.0:
set CM_JAVA1_4_HOME=D:\jdk1.4
rem ### JDK 1.4.1_05:
set CM_JAVA1_4_1_HOME=D:\jdk1.4.1_05

set JAVA_HOME=%CM_JAVA1_4_1_HOME%

goto common

:localbuild

rem ### When run locally, this batch file will abort if either JAVA_HOME or ANT_HOME is not defined.
if x%JAVA_HOME% == x goto java_home_not_set
if x%ANT_HOME% == x goto ant_home_not_set

if not exist %ANT_HOME%\lib\ant.jar goto ant_jar_not_found
if not exist %ANT_HOME%\lib\hpAntTasks.jar goto hpanttasks_jar_not_found

if not x%CVS_TUNNEL% == x goto cvs_tunnel_is_set
set CVS_TUNNEL=%HPSEN_CVS_TUNNEL%
:cvs_tunnel_is_set

if not x%CVS_USR% == x goto cvs_usr_is_set
set /p CVS_USR="Enter your HP-SEN CVS username (just hit Enter if you use .cvspass): "
:cvs_usr_is_set

if not x%CVS_PWD% == x goto cvs_pwd_is_set
set CVS_PWD=
:cvs_pwd_is_set

:common
set OLDPATH=%PATH%
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%

echo.
echo Date: %DATE%
echo Time: %TIME%
echo.
echo JAVA_HOME is set to '%JAVA_HOME%'
echo ANT_HOME is set to '%ANT_HOME%'
if not x%CVSROOT% == x echo CVSROOT is set to '%CVSROOT%'
if not x%CVS_PASSFILE% == x echo CVS_PASSFILE is set to '%CVS_PASSFILE%'
echo CVS_TUNNEL is set to '%CVS_TUNNEL%'
echo CVS_USR is set to '%CVS_USR%'
echo.

:runANT
if not x%1% == x goto args
ant -f master.xml
goto cleanup_env
:args
ant -f %1.xml %2 %3 %4 %5 %6 %7 %8 %9
goto cleanup_env

:java_home_not_set
echo ERROR: The JAVA_HOME environment variable must be set in your System Environment before this batch file can be run. >&2
set EXIT_CODE=1
goto abort

:ant_home_not_set
echo ERROR: The ANT_HOME environment variable must be set in your System Environment before this batch file can be run. >&2
set EXIT_CODE=2
goto abort

:ant_jar_not_found
echo ANT_HOME is set to '%ANT_HOME%'
echo ERROR: %ANT_HOME%\lib\ant.jar does not exist. >&2
set EXIT_CODE=3
goto abort

:hpanttasks_jar_not_found
echo ANT_HOME is set to '%ANT_HOME%'
echo ERROR: %ANT_HOME%\lib\hpAntTasks.jar does not exist. >&2
echo Please download hpAntTasks.jar from http://anttasks.hpsen.com/ >&2
echo and place it in %ANT_HOME%\lib\. >&2
set EXIT_CODE=4
goto abort

:abort
echo Aborting... >&2
exit /b %EXIT_CODE%

:cleanup_env
set PATH=%OLDPATH%
echo.

