@echo off
rem unable to locate File or URL :UnknownURL for component :ex1.
rem Reason: The path to URL may be incorrect or file may be missing.
rem 
@echo on
call %SFHOME%\bin\sfStart localhost ex1 UnknownURL
