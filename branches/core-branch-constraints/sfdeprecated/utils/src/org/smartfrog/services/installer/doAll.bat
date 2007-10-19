@echo off
setlocal
call %SFHOME%\bin\setSFProperties
java org.smartfrog.services.installer.TemplateGen -h hosts.all -o examples\test.sf -t sfInstaller.vm
java org.smartfrog.services.installer.TemplateGen -s -h hosts.all -o examples\sectest.sf -t sfInstaller.vm
java org.smartfrog.services.installer.TemplateGen -d -h hosts.all -o examples\DLtest.sf -t sfInstaller.vm
java org.smartfrog.services.installer.TemplateGen -s -d -h hosts.all -o examples\secDLtest.sf -t sfInstaller.vm

