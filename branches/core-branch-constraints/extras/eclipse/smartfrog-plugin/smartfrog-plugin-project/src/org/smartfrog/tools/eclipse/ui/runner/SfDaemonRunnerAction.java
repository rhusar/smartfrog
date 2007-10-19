
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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.IWorkbenchWindow;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;


/**
 * Handle daemon button
 */
public class SfDaemonRunnerAction
    extends IRunnerAction
{
    private static SfDaemonRunnerExt DAEMON_PROCESS = null;

    /**
     * Kill the previous launch daemon, if have any, launch a new daemon
     */
    public void run(IAction action)
    {
        if (null == mSelectedIFile) {
            MessageDialog.openError(mWindow.getShell(),
                
            Messages.getString("SfDaemonRunnerAction.title.selectFile"), //$NON-NLS-1$
                Messages.getString(
                    "SfDaemonRunnerAction.description.selectFile")); //$NON-NLS-1$

            return;
        }

        stopDaemonProcess();
        bringUpConsole();

        SfDaemonRunnerExt sfRunner = new SfDaemonRunnerExt(SmartFrogPlugin
                .getWorkbenchWindow().getShell(), mSelectedIFile);
        sfRunner.start();
        DAEMON_PROCESS = sfRunner;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        //noop
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window)
    {
        mWindow = window;
        mActivePage = window.getActivePage();
    }

    /**
     * Kill the previous launch daemon, if have any
     */
    private void stopDaemonProcess()
    {
        if (null != DAEMON_PROCESS) {
            DAEMON_PROCESS.stopProcess();
            DAEMON_PROCESS = null;
        }
    }
}
