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
echo "Chaging dir to jnit dir"
cd %INSTALL_DIR%\%JUNIT_RELEASE%
mv extras %INSTALL_DIR%
mv common.xml %INSTALL_DIR%
cd %INSTALL_DIR%\extras\ant
call %ANT_HOME%\bin\ant.bat
cd %INSTALL_DIR%\%JUNIT_RELEASE%
call %ANT_HOME%\bin\ant.bat
:exit
:error "Unable to get environment variables"
