cd /D %SFEXAMPLEHOME%\datareaders
call %SFEXAMPLEHOME%\setEnv.bat
sfStart localhost consumer dataConsumer.sf
pause
