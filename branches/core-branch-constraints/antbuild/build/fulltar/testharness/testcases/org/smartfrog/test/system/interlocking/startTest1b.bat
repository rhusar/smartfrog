cd  /D %SFHOMETEST%\test1
call %SFHOMETEST%\setEnv.bat
sfStart localhost test1b test1b.sf 
pause
