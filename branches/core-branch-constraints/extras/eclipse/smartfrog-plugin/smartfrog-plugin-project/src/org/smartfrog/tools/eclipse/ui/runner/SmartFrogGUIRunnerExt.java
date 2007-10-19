
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
import org.smartfrog.tools.eclipse.model.Util;
import org.smartfrog.tools.eclipse.ui.preference.SmartFrogPreferencePage;


class SmartFrogGUIRunnerExt
    extends IRunner
{
    private static final String GUI_PARAMETER = "-eclipse"; //$NON-NLS-1$
    private static final String RELATIVE_BIN_DIR = "bin"; //$NON-NLS-1$
    private static final String SFGUI_COMMAND_LINUX = "sfGui"; //$NON-NLS-1$
    private static final String SFGUI_COMMAND_WINDOWS = "sfGui.bat"; //$NON-NLS-1$
    private Shell mShell;

    private boolean mRunning = false;

    private String mFilePath;

    /**
     *
     */
    public SmartFrogGUIRunnerExt(Shell shell, String filePath)
    {
        super();
        mShell = shell;
        mFilePath = filePath;
    }

    /**
     * Launch netscape browser to point to OA&M url
     */
    public void run()
    {
        String sfLocation = SmartFrogPreferencePage.getSmartFrogLocation();

        String batFileName = SFGUI_COMMAND_WINDOWS;

        if (!Util.isWindows()) {
            batFileName = SFGUI_COMMAND_LINUX;
        }

//        String cmd = ISmartFrogConstants.DOUBLE_QUOTE + sfLocation + ISmartFrogConstants.FILE_SEPARATOR +
//            RELATIVE_BIN_DIR + ISmartFrogConstants.FILE_SEPARATOR +
//            batFileName + ISmartFrogConstants.DOUBLE_QUOTE + ISmartFrogConstants.WHITE_SPACE + 
//            ISmartFrogConstants.DOUBLE_QUOTE + mFilePath + ISmartFrogConstants.DOUBLE_QUOTE + ISmartFrogConstants.WHITE_SPACE +
//			GUI_PARAMETER ;

        String cmds[] = new String[3];
        cmds[0] =  sfLocation + ISmartFrogConstants.FILE_SEPARATOR +
        RELATIVE_BIN_DIR + ISmartFrogConstants.FILE_SEPARATOR + batFileName ;
        cmds[1] = mFilePath;
        cmds [2] = GUI_PARAMETER;
        
        executeCmd(cmds);
        mRunning = true;
    }

 
    public boolean isRunning()
    {
        return mRunning;
    }
}
