@echo off

REM This batch file used tar cygwin utility to extract the Junit test reports 
REM generated from Linux/Unix machines.

echo "Using following environment variables"
echo "===================================="
echo "TEST_HOME"
echo %TEST_HOME%
echo "TEST_REPORT_FILE"
echo %TEST_REPORT_FILE%
echo "========================================"
if %TEST_HOME% == "" goto error
if %TEST_REPORT_FILE% == "" goto error
echo "Chaning directory to TEST_HOME"
cd %TEST_HOME%
tar -zxvf %TEST_REPORT_FILE%
:exit
:error "Unable to get env vars"
