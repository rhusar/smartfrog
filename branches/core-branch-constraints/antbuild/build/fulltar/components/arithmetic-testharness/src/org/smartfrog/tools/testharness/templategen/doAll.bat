@echo off
setlocal
call %SFHOME%\bin\setSFProperties
java org.smartfrog.tools.testharness.templategen.TemplateGen -h hosts.all -o templateSerrano.sf -t templateSerrano.vm
java org.smartfrog.tools.testharness.templategen.TemplateGen -s -h hosts.all -o templateSecureSerrano.sf -t templateSerrano.vm
java org.smartfrog.tools.testharness.templategen.TemplateGen -d -h hosts.all -o templateDLSerrano.sf -t templateSerrano.vm
java org.smartfrog.tools.testharness.templategen.TemplateGen -s -d -h hosts.all -o templateSecureDLSerrano.sf -t templateSerrano.vm
