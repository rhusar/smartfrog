
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

import org.eclipse.ui.IWorkbenchWindow;


/**
 * Let user specify a SmartFrog process to kill
 */
public class SfProcessStopperAction
    extends IRunnerAction
{
    private static SfProcessStopperExt ProcessRunner = null;

    /*
     * (non-Javadoc)
     *
     * @see org.smartfrog.tools.eclipse.ui.project.OpenCreationWizard#createWizard()
     */
    public void run(IAction action)
    {
        bringUpConsole();

        SfProcessStopperWizard wizard = new SfProcessStopperWizard(Messages
                .getString(
                    "SfProcessStopperAction.title.SmartFrogProcessPage"), //$NON-NLS-1$
                getSelectFile()); //$NON-NLS-1$
        ResizableWizardDialog dialog = new ResizableWizardDialog(mWindow
                .getShell(), wizard);
        dialog.open();
        ProcessRunner = wizard.getRunner();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
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
}
