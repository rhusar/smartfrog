@echo off

set originalPath=%PATH%
set originalClassPath=%CLASSPATH%
set originalDir=%cd%

if defined SFHOME goto continue1
  if exist "%cd%\sfGui.bat" cd ..
  set SFHOME=%cd%
  cd ..
:continue1

CALL "%SFHOME%\bin\SFGuiCFG.bat"

if defined JAVA_HOME set path=%JAVA_HOME%\bin

if not defined srcDir set srcDir=%SFHOME%\examples\clasess

rem set JAVA_HEAP_SIZE=64


call "%SFHOME%\bin\setSFProperties"

if defined srcDir set CLASSPATH=%srcDir%;%classpath%

cd "%SFHOME%"

%SFJVM% -cp "%CLASSPATH%" org.smartfrog.tools.gui.browser.SFGui %1 %2

cd %originalDir%
set CLASSPATH=%originalClassPath%
set PATH=%originalPath%

