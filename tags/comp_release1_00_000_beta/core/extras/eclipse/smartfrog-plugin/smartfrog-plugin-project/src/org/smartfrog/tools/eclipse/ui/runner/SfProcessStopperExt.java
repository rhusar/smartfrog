
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

import org.smartfrog.tools.eclipse.model.ExceptionHandler;


/**
 * Stop the specified SmartFrog process
 */
class SfProcessStopperExt
    extends ISfRunnerExt
{
    private Shell mShell;

    private String mFile;

    private String mProcessName;

    /**
     *
     */
    public SfProcessStopperExt(Shell shell, String file, String processName)
    {
        mShell = shell;
        mFile = file;
        mProcessName = processName;
    }

    /**
     * Stop SmartFrog process
     */
    public void run()
    {
        Object cmdStart = createCmd();

        if (null == cmdStart) {
            ExceptionHandler.handleInSWTThread(
                new Exception(
                    Messages.getString("SfProcessStopperExt.error.notProcessException") + mProcessName), //$NON-NLS-1$
                Messages.getString("SfProcessStopperExt.error.noProcess"), null); //$NON-NLS-1$
        } else {
        	if (cmdStart instanceof String)
        	{
        		executeCmd((String)cmdStart);
        	} 

        	if (cmdStart instanceof String[])
        	{
        		executeCmd((String[])cmdStart);
        	} 

        }
    }

    /**
     * 
     * @return look up the stop command with the process name
     */
    private Object createCmd()
    {
    	Object cmd = null;

        InfoProcess process = MngProcess.getInstance().deleteProcess(
                mProcessName);

        if (null != process) {
            cmd = process.getCmdStopObj();
        }

        return cmd;
    }
}
