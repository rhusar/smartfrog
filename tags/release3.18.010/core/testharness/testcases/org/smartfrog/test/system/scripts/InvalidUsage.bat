@echo off
rem illegal format for options
rem Usage: java -D... org.smartfrog.SFSystem [-h HOST_NAME [-p PROCESS_NAME]] 
rem (-t NAME)* (-c URL | -n NAME URL)* [-e] or: java -D... 
rem org.smartfrog.SFSystem -?

@echo on
call %SFHOME%\bin\sfStart http://org/smartfrog/examples/Counter/Example.sf
