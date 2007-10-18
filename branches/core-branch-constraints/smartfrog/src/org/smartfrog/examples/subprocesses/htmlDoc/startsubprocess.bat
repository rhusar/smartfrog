cd /D %SFEXAMPLEHOME%\subprocesses
call %SFEXAMPLEHOME%\setEnv.bat
sfStart localhost subprocess subprocess.sf
pause
