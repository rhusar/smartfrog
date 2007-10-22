cd /D %SFHOMETEST%\test1
call %SFHOMETEST%\setEnv.bat

sfStart localhost test1a test1a.sf
pause
