@echo off
rem Unable to connect to sfDaemon on: localhost.
rem Reason:sfDaemon may not be running on localhost
rem java.rmi.ConnectException: Connection refused to host: 127.0.0.1; nested excepti on is: java.net.ConnectException: Connection refused: connect
rem 
@echo on
call %SFHOME%\bin\sfStart localhost ex1 org/smartfrog/examples/arithnet/example1.sf
