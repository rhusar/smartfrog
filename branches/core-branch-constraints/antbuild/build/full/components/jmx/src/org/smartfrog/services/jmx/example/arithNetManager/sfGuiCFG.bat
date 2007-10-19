rem ----To customize for each particular user ---------
rem set SFHOME=xxxx rem your particular Serrano Framework
rem set JAVA_HOME=e:\java\jdk1.3
set srcDir=./examples
rem ---------------------------------------------------

rem -----------------Config parameters for SFGui---------
rem [SFGuiConfig]
rem sfFilesDir=./examples
rem SFSystemClass=com.hp.SmartFrog.SFSystem
rem cmdSFDaemon=-Dcom.hp.SmartFrog.ProcessCompound.sfProcessName=
rem SFDaemonProcessName=rootProcess
rem SFDaemonDefIniFile=./bin/default.ini
rem SFDaemonDefSFFile=./bin/default.sf
rem cmdSFStart=sfStart
rem cmdSFStop=sfStop
rem suffixSecureScrip=S
rem cmdBrowserURL=http://localhost:8080/
rem cmdBrowserWin=explorer
rem cmdBrowserLinux=netscape
rem lookAndFeel=kunststoff
rem -----------------------------------------------------

rem [Processes]
rem processName0=MBeamBrowser
rem processCmdStart0=cmd.exe /C .\bin\sfMBeanBrowser.bat
rem processCmdStop0=
rem processName1=DaemonsMonitorsService(127.0.0.1)
rem processCmdStart1=cmd.exe /C  .\bin\sfStart 127.0.0.1 DaemonsMonitorsService E:\cvs\SmartFrog\dist\examples\launchMonitorDaemon.sf
rem processCmdStop1=cmd.exe /C  .\bin\sfStop 127.0.0.1 DaemonsMonitorsService
rem processName2=Agents(127.0.0.1)
rem processCmdStart2=cmd.exe /C  .\bin\sfStart 127.0.0.1 Agents E:\cvs\SmartFrog\dist\examples\launchSubagents.sf
rem processCmdStop2=cmd.exe /C  .\bin\sfStop 127.0.0.1 Agents
rem processName3=ArithmeticNetService(127.0.0.1)
rem processCmdStart3=cmd.exe /C  .\bin\sfStart 127.0.0.1 ArithmeticNetService E:\cvs\SmartFrog\dist\examples\launchArithExample.sf
rem processCmdStop3=cmd.exe /C  .\bin\sfStop 127.0.0.1 ArithmeticNetService
rem processName4=
rem processCmdStart4=
rem processCmdStop4=
rem processName5=
rem processCmdStart5=
rem processCmdStop5=
rem processName6=
rem processCmdStart6=
rem processCmdStop6=
rem processName7=
rem processCmdStart7=
rem processCmdStop7=
rem processName8=
rem processCmdStart8=
rem processCmdStop8=
rem processName9=
rem processCmdStart9=
rem processCmdStop9=