@echo off

if defined SFHOME goto continue1
  if exist "%cd%\setClassPath.bat" cd ..
  set SFHOME=%cd%
  cd bin
:continue1

set CLASSPATH=
set cd=
for /f "tokens=*" %%i in (
 'dir /s /b "%SFHOME%\lib\*.jar"'
) do call :add2path %%i
rem set CLASSPATH
popd&goto :MORE

:add2path 
set file=%*
if not defined cd set file=%file:~1%

set CLASSPATH=%file%;%CLASSPATH%

goto :EOF

:MORE

if not defined SFUSERHOME goto continue2
  CALL "%SFHOME%\bin\setUserClassPath.bat"
:continue2

if defined srcDir set CLASSPATH=%srcDir%;%classpath%