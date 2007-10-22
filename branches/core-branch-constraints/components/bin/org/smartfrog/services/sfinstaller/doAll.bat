@echo off
setlocal
call %SFHOME%\bin\setSFProperties

if "%1" == "-s" goto sec
if "%1" == "-d" goto dc

java org.smartfrog.services.sfinstaller.TemplateGen -h hosts.all -o examples\test.sf -t sfInstaller.vm
goto eof

:dc
java org.smartfrog.services.sfinstaller.TemplateGen -d -h hosts.all -o examples\DLtest.sf -t sfInstaller.vm
goto eof

:sec
if "%2" == "-d"  goto secdc
java org.smartfrog.services.sfinstaller.TemplateGen -s -h hosts.all -o examples\sectest.sf -t sfInstaller.vm
goto eof
:secdc
java org.smartfrog.services.sfinstaller.TemplateGen -s -d -h hosts.all -o examples\secDLtest.sf -t sfInstaller.vm
goto eof

:eof
