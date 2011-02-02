
/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/


package org.smartfrog.tools.eclipse.ui.runner;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.core.resources.IFile;
import org.smartfrog.tools.eclipse.ui.project.Configuration;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;
import org.smartfrog.tools.eclipse.model.SmartFrogProjectUtil;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.Util;

/**
 * Launch SmartFrog process
 */
class SfProcessRunnerExt
    extends ISfRunnerExt
 
    {
    
	private Shell mShell;

    private String mFilePath;

    private String mFile;

    private String mHostName;

    private String mProcessName;
    
    private IFile mselectedIFile;

    public SfProcessRunnerExt(Shell shell, String file, String hostName,
        String processName, IFile mIFile)
    {
        mShell = shell;
        mFile = file;
        mHostName = hostName;
        mProcessName = processName;
	mselectedIFile = mIFile;
    }

    /**
     * Launch process
     */
    public void run()
    {
        //String cmdStart = createCmd();
       // String cmdStart = createCmd();

        //executeCmd(cmdStart);
        executeCmd(createCmd());
    }

    private String[] createCmd()
    {
        String batchDir = "bin"; //$NON-NLS-1$
        String dir = SmartFrogPreferencePage.getSmartFrogLocation() +
          
        ISmartFrogConstants.FILE_SEPARATOR + batchDir +
        ISmartFrogConstants.FILE_SEPARATOR;

//        String cmdStart = CMD_SFPROCESS_START;
//        String cmdStop = CMD_SFPROCESS_TERMINATE;

        String cmdGeneral[] =  getCommandGeneralArray();

//        cmdStart = cmdGeneral + ISmartFrogConstants.WHITE_SPACE + ISmartFrogConstants.DOUBLE_QUOTE  + 
//		ISmartFrogConstants.DOUBLE_QUOTE + dir + cmdStart+ISmartFrogConstants.DOUBLE_QUOTE + ISmartFrogConstants.WHITE_SPACE + mHostName + ISmartFrogConstants.WHITE_SPACE +
//            mProcessName + ISmartFrogConstants.WHITE_SPACE + ISmartFrogConstants.DOUBLE_QUOTE + mFile + ISmartFrogConstants.DOUBLE_QUOTE+ ISmartFrogConstants.WHITE_SPACE
//			+ISmartFrogConstants.DOUBLE_QUOTE ; //$NON-NLS-1$ //$NON-NLS-2$
        
	String classpath = SmartFrogProjectUtil.getbinPathName(mselectedIFile);
    	if (null == classpath)
    	    return null;
	// removing binPathName from classpath
     classpath = classpath + Util.getClassSeparator()+ mClassPath + SmartFrogPlugin.getmClassPath(mselectedIFile) ;
    	
      //   classpath = mClassPath + SmartFrogPlugin.getmClassPath(mselectedIFile) ;
    	
    	System.out.println("sf- process cp............................." + classpath);
    	
    
    	
	String cmdsStart[] = new String[8];
        cmdsStart[0]= JAVA;
        cmdsStart[1] = "-cp";
        cmdsStart[2] = classpath;
        cmdsStart[3] = SfDaemonDefIniFileProperty+ mSfDaemonDefIniFile;
        cmdsStart[4] =SfSystemClass;
	cmdsStart[5]="-a";
	if (Configuration.isWindows())
		cmdsStart[6] = mProcessName+":DEPLOY:\\\"" + mFile +"\\\"::"+ mHostName+": ";
	else
		cmdsStart[6] = mProcessName+":DEPLOY:\"" + mFile +"\"::"+ mHostName+": ";
	cmdsStart[7] =  "-e";
	/*String cmdsStart = "";//new String[cmdGeneral.length+4];
         for (int i=0;  i < cmdGeneral.length; i++)
        {
         	cmdsStart += cmdGeneral[i] + " ";
        }
        cmdsStart +=  SmartFrogPreferencePage.getSmartFrogLocation() + ISmartFrogConstants.FILE_SEPARATOR + "bin"+ ISmartFrogConstants.FILE_SEPARATOR + CMD_SFPROCESS_START  +" " ;
        cmdsStart += "-a ";
	if (Configuration.isWindows())
        cmdsStart +=mProcessName+":DEPLOY:\\\"" + mFile +"\\\"::"+ mHostName+": ";
	else
        cmdsStart +=mProcessName+":DEPLOY:\"" + mFile +"\"::"+ mHostName+": ";
        cmdsStart += "-e";
	*/
//      cmdsStart[cmdGeneral.length+1] = mHostName;
//		cmdsStart[cmdGeneral.length+2] = mProcessName;
//		cmdsStart[cmdGeneral.length+3] = mFile ;
	
         	
        
//        cmdStop = cmdGeneral + ISmartFrogConstants.WHITE_SPACE + ISmartFrogConstants.DOUBLE_QUOTE +
//			ISmartFrogConstants.DOUBLE_QUOTE +dir + CMD_SFPROCESS_TERMINATE + ISmartFrogConstants.DOUBLE_QUOTE+  ISmartFrogConstants.WHITE_SPACE +
//            mHostName + ISmartFrogConstants.WHITE_SPACE + mProcessName
//			+ISmartFrogConstants.DOUBLE_QUOTE ;

        //cmd.exe /C  .\bin\smartfrog -a ProcessName:TERMINATE:::127.0.0.1: -e  
       
        String cmdsStop[] = new String[8];
        cmdsStop[0] = JAVA;
        cmdsStop[1] = "-cp";
        cmdsStop[2] = classpath;
        cmdsStop[3] = SfDaemonDefIniFileProperty+ mSfDaemonDefIniFile;
        cmdsStop[4] = SfSystemClass;
        cmdsStop[5] = "-a";
        cmdsStop[6] = mProcessName+":TERMINATE:::"+mHostName+": ";
        cmdsStop[7] =  "-e";
	
	/*	String cmdsStop = "";//new String[cmdGeneral.length+3];
        for (int i=0;  i < cmdGeneral.length; i++)
       {
        	cmdsStop += cmdGeneral[i]+ " ";
       }
        cmdsStop += SmartFrogPreferencePage.getSmartFrogLocation() + ISmartFrogConstants.FILE_SEPARATOR + "bin"+ ISmartFrogConstants.FILE_SEPARATOR + CMD_SFPROCESS_START  +" " ;
        cmdsStop += "-a ";
        cmdsStop +=mProcessName+":TERMINATE:::"+mHostName+": ";
        cmdsStop += "-e";

	*/
//        cmdsStop[cmdGeneral.length+0] = dir + CMD_SFPROCESS_TERMINATE ;
//        cmdsStop[cmdGeneral.length+1] = mHostName;
//        cmdsStop[cmdGeneral.length+2] = mProcessName;

		
        addProcess(mProcessName, cmdsStart, cmdsStop,
            SmartFrogPreferencePage.getSmartFrogLocation());

        return cmdsStart;
    }

    public void stopProcess()
    {
        if (mProcess != null) {
            mProcess.destroy();
        }

        mProcess = null;
    }

    private void addProcess(String processName, Object startCmd, Object stopCmd,
        String workDir)
    {
        InfoProcess process = new InfoProcess(processName, startCmd, stopCmd,
                workDir);
        MngProcess.getInstance().addProcess(process, true);
    }
}
