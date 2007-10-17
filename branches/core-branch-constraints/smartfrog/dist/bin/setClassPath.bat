@echo off

if defined SFHOME goto continue1
  if exist "%cd%\setClassPath.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

if not defined SFLIBPATH set SFLIBPATH=%cd%

rem set CLASSPATH=  (This is reset in setSFProperties.bat)
set cd=
for /f "tokens=*" %%i in (
 'dir /s /b "%SFLIBPATH%\*.jar"'
) do call :add2path %%i
rem set CLASSPATH
popd&goto :MORE

:add2path
set file=%*
if not defined cd set file=%file:~1%

set CLASSPATH=%file%;%CLASSPATH%

goto :EOF

:MORE

