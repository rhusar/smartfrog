@echo off
rem ===========================================================================
rem Sets the path for the test cases
rem Edit this file if you add any executable testcase in testharness
rem This assumes SFHOME and PATH Env varables are already set for the SF 
rem framework.
rem ===========================================================================
set PATH=%PATH%;%SFHOME%\..\testharness\bin;%SFHOME%\..\testharness\testcases\org\smartfrog\test\system\scripts;%SFHOME%\..\testharness\testcases\org\smartfrog\test\system\security
