@echo off

echo "Using following environment variables"
echo "===================================="
echo "PATH"
echo %PATH%
echo "ANT_HOME"
echo %ANT_HOME%
echo "SystemRoot"
echo %SystemRoot%
echo "INSTALL_DIR"
echo %INSTALL_DIR%
echo "JUNIT_RELEASE_FILE"
echo %JUNIT_RELEASE_FILE%
echo "SF_RELEASE_FILE"
echo %SF_RELEASE_FILE%
echo "SF_RELEASE"
echo %SF_RELEASE%
echo "JUNIT_RELEASE"
echo %JUNIT_RELEASE%
echo "HOST_NAME"
echo %HOST_NAME%
echo "TEST_HOME"
echo %TEST_HOME%
echo "========================================"
if %INSTALL_DIR% == "" goto error
echo "Unzipping sf release file"
wzunzip -d %SF_RELEASE_FILE% %INSTALL_DIR%
echo "Unzipping sf release file"
wzunzip -d %JUNIT_RELEASE_FILE% %INSTALL_DIR%
echo "Changing to dir:"
echo %INSTALL_DIR%
cd %INSTALL_DIR%
echo "Moving SF_RELEASE to smartfrog dir"
mv %SF_RELEASE% smartfrog
rem call %INSTALL_DIR%\smartfrog\dist\bin\sfDaemon.bat
echo "Chaging dir to jnit dir"
cd %INSTALL_DIR%\%JUNIT_RELEASE%
mv extras %INSTALL_DIR%
mv common.xml %INSTALL_DIR%
rem cd %INSTALL_DIR%\extras\ant
rem call %ANT_HOME%\bin\ant.bat
cd %INSTALL_DIR%\%JUNIT_RELEASE%
echo "Going to execute testcases in test harness"
call ant.bat
if not errorlevel 0 goto continue
:continue
echo "After executing testcases in test harness"
rem move test reports to appropriate folder
echo "Changing dir to %TEST_HOME%"
cd %TEST_HOME%
echo "Creating dir %HOST_NAME%"
mkdir %HOST_NAME%
echo "Moving junit reports to test home"
cd %INSTALL_DIR%\%JUNIT_RELEASE%\build\test
mv -f reports %TEST_HOME%\%HOST_NAME%
:exit
:error "Unable to get env vars"
