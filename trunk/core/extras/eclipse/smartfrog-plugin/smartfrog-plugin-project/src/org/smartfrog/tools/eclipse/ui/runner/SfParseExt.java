package org.smartfrog.tools.eclipse.ui.runner;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Shell;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;
import org.smartfrog.tools.eclipse.model.SmartFrogProjectUtil;
import org.smartfrog.tools.eclipse.model.Util;
import org.smartfrog.tools.eclipse.model.builder.SmartFrogProjectBuilder;
import org.smartfrog.tools.eclipse.ui.project.SmartFrogProject;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;

import java.io.File;
import java.io.FilenameFilter;

public class SfParseExt extends ISfRunnerExt {

	private Shell mShell;
	private String mFilePath;
	private IFile mSelectedIFile;

	private String file;

	/**
	 * @param selectedIFile
	 * 
	 */
	public SfParseExt(Shell shell, IFile selectedIFile) {
		mShell = shell;
		mSelectedIFile = selectedIFile;

	}

	public void run() {
		String classpath = SmartFrogProjectUtil.getbinPathName(mSelectedIFile);
		if (null == classpath)
			return;
		// classpath = classpath + Util.getClassSeparator()+mClassPath ;
		// removing binPathName from classpath
		classpath = classpath + Util.getClassSeparator() + mClassPath
				+ SmartFrogPlugin.getmClassPath(mSelectedIFile);
		
		
		
		String cmds[] = new String[7];
		cmds[0] = JAVA;
		cmds[1] = "-cp";
		cmds[2] = classpath;
		// cmds[3]= SmartFrogPlugin.getmClassPath(mSelectedIFile);
		cmds[3] = SfParseClass;
		cmds[4] = "-r";
		cmds[5] = "-q";
		//cmds[6] = mSelectedIFile.getProjectRelativePath().toOSString();
		cmds[6] = mSelectedIFile.toString();
		executeCmd(cmds);
	
	}
	
}
