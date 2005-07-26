@echo off

for /f "tokens=*" %%i in (
 'dir /s /b "%SFUSERHOME%\*.jar"'
) do call :add2path %%i

popd&goto :MORE

:add2path 
set file=%*
if not defined cd set file=%file:~1%

set CLASSPATH=%file%;%CLASSPATH%

goto :EOF

:MORE

