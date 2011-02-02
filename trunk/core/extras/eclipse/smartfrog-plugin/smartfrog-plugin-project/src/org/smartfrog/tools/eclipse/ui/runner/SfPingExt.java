package org.smartfrog.tools.eclipse.ui.runner;


import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.model.SmartFrogProjectUtil;
import org.smartfrog.tools.eclipse.model.Util;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;

public class SfPingExt extends ISfRunnerExt {
	private Shell mShell;
	 private String mHostName;

	    private String mProcessName;
	private String file;

	/**
	 * @param selectedIFile
	 * 
	 */
	
	public SfPingExt(Shell shell, String hostName, String processName) {
		mShell = shell;
		mHostName = hostName;
		mProcessName = processName;
		if (mProcessName == null)
			mProcessName = "rootProcess";
		
	}
	public void run() {
		String classpath = mClassPath ;
		String cmds[] = new String[7];
		cmds[0] = JAVA;
		cmds[1] = "-cp";
		cmds[2] = classpath;
		cmds[3] = SfSystemClass;
		cmds[4]= "-a";
		cmds[5]=mProcessName+":PING:::"+mHostName+": ";
		cmds[6] =  "-e";
		
		executeCmd(cmds);
	}


}
