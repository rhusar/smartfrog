
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
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;

/**
 * Stop local SmartFrog daemon
 */
class SfDaemonStopperExt
    extends ISfRunnerExt
{

    private static final String DEFAULT_HOST = "127.0.0.1"; //$NON-NLS-1$
    private Shell mShell;
    private String mFilePath;

    /**
     * 
     */
    public SfDaemonStopperExt(Shell shell)
    {
        mShell = shell;
    
    }
    
    public void run()
    {
//        String cmd = JAVA   + ISmartFrogConstants.WHITE_SPACE
//        + "-cp " + mClassPath+ ISmartFrogConstants.WHITE_SPACE //$NON-NLS-1$
//        + SfSystemClass + ISmartFrogConstants.WHITE_SPACE
//        + "-a"+ISmartFrogConstants.WHITE_SPACE //$NON-NLS-1$
//        +  SfDaemonProcessName + ":TERMINATE:::" //$NON-NLS-1$
//        + "" + DEFAULT_HOST+ ":"+ ISmartFrogConstants.WHITE_SPACE //$NON-NLS-1$ //$NON-NLS-2$
//        + "-e"; //$NON-NLS-1$
        
        
        String cmds[] = new String[7];
        cmds[0]= JAVA;
        cmds[1] = "-cp";
        cmds[2] = mClassPath ;
        cmds[3] = SfSystemClass;
        cmds[4] = "-a";
        cmds[5] = SfDaemonProcessName + ":TERMINATE:::"+ "" + DEFAULT_HOST+ ":";
        cmds[6] = "-e";
        
        executeCmd(cmds);
        
    }
 

    
}
