
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
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;


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

    public SfProcessRunnerExt(Shell shell, String file, String hostName,
        String processName)
    {
        mShell = shell;
        mFile = file;
        mHostName = hostName;
        mProcessName = processName;
    }

    /**
     * Launch process
     */
    public void run()
    {
        String cmdStart = createCmd();

        executeCmd(cmdStart);
    }

    private String createCmd()
    {
        String batchDir = "bin"; //$NON-NLS-1$
        String dir = SmartFrogPreferencePage.getSmartFrogLocation() +
            ISmartFrogConstants.FILE_SEPARATOR + batchDir +
            ISmartFrogConstants.FILE_SEPARATOR;

        String cmdStart = CMD_SFPROCESS_START;
        String cmdStop = CMD_SFPROCESS_TERMINATE;

        String cmdGeneral = getCommandGeneral();

        cmdStart = cmdGeneral + ISmartFrogConstants.WHITE_SPACE + dir + cmdStart + ISmartFrogConstants.WHITE_SPACE + mHostName + ISmartFrogConstants.WHITE_SPACE +
            mProcessName + ISmartFrogConstants.WHITE_SPACE + "\"" + mFile + "\"" + ISmartFrogConstants.WHITE_SPACE; //$NON-NLS-1$ //$NON-NLS-2$

        cmdStop = cmdGeneral + ISmartFrogConstants.WHITE_SPACE + dir + CMD_SFPROCESS_TERMINATE + ISmartFrogConstants.WHITE_SPACE +
            mHostName + ISmartFrogConstants.WHITE_SPACE + mProcessName;

        addProcess(mProcessName, cmdStart, cmdStop,
            SmartFrogPreferencePage.getSmartFrogLocation());

        return cmdStart;
    }

    public void stopProcess()
    {
        if (mProcess != null) {
            mProcess.destroy();
        }

        mProcess = null;
    }

    private void addProcess(String processName, String startCmd, String stopCmd,
        String workDir)
    {
        InfoProcess process = new InfoProcess(processName, startCmd, stopCmd,
                workDir);
        MngProcess.getInstance().addProcess(process, true);
    }
}
