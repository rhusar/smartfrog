@echo off
rem start the daemon in a seperate window in localhost
start sfDaemon
pause 10
rem open two windows and deploy same component
start sfStart localhost ex1 org/smartfrog/test/system/scripts/tcn14.sf
start sfStart localhost ex1 org/smartfrog/test/system/scripts/tcn14.sf
rem stop the component
call  sfStop localhost ex1
rem stop the daemon
call sfStopDaemon localhost

