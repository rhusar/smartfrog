@echo on
setlocal

if defined SFHOME goto continue1
  if exist "%cd%\MBeanBrowser.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

call "%SFHOME%\bin\setSFProperties"
rem call %SFHOME%\bin\setClassPath.bat

echo %CLASSPATH%

rem java org.smartfrog.services.jmx.mbeanbrowser.MBeanBrowser rmi://localhost:3800/RMIConnectorServer
java org.smartfrog.services.jmx.mbeanbrowser.MBeanBrowser %1 %2 %3 %4 %5 %6 %7 %8
endlocal
