
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

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.model.SmartFrogProjectUtil;
import org.smartfrog.tools.eclipse.model.Util;

/**
 * Launch a local SmartFrog Daemon
 */
class SfDaemonRunnerExt
    extends ISfRunnerExt
{

    private Shell mShell;
    private String mFilePath;
	private IFile mSelectedIFile;

    
    /**
     * @param selectedIFile
     * 
     */
    public SfDaemonRunnerExt(Shell shell, IFile selectedIFile)
    {
        mShell = shell;
        mSelectedIFile = selectedIFile;
    
    }
    
    public void run()
    {
    	
    	
    	String classpath = SmartFrogProjectUtil.getbinPathName(mSelectedIFile);
    	if (null == classpath)
    	    return;
    	classpath = classpath + Util.getClassSeparator()+mClassPath ;
//        String cmd = JAVA + ISmartFrogConstants.WHITE_SPACE
//        + "-cp " + classpath + ISmartFrogConstants.WHITE_SPACE //$NON-NLS-1$
//        + SfDaemonDefIniFileProperty+ mSfDaemonDefIniFile + ISmartFrogConstants.WHITE_SPACE
//        + SfDaemonDefSFFileProperty + mSfDaemonDefSFFile + ISmartFrogConstants.WHITE_SPACE
//        + CMD_SFDaemon + "" + SfDaemonProcessName + ISmartFrogConstants.WHITE_SPACE //$NON-NLS-1$
//        + SfSystemClass
//        + ""; //$NON-NLS-1$
        
        String cmds[] = new String[7];
        cmds[0]= JAVA;
        cmds[1] = "-cp";
        cmds[2] = classpath;
        cmds[3] = SfDaemonDefIniFileProperty+ mSfDaemonDefIniFile;
        cmds[4] =SfDaemonDefSFFileProperty + mSfDaemonDefSFFile;
        cmds[5] =CMD_SFDaemon + "" + SfDaemonProcessName;
        cmds[6] =SfSystemClass;
        executeCmd(cmds);
    }
    

    public void stopProcess()
    {
    	if (mProcess != null)
    	{
    		mProcess.destroy();
    	}
    	mProcess = null;
    	
    }
 
    
}
