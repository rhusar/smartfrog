
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

import org.eclipse.jface.wizard.Wizard;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;


/**
 * Wizard to collect settings for the process to be stopped
 */
class SfProcessStopperWizard
    extends Wizard
{
    private IFile mFile = null;
    private SfProcessRunnerWizardPage mRunnerPage;
    private String mSelectedFile;
    private SfProcessStopperExt mSfRunner;


    /**
     * @param windowTitle
     * @param selectedFile
     */
    public SfProcessStopperWizard(String windowTitle, String selectedFile)
    {
        setWindowTitle(windowTitle);
        mSelectedFile = selectedFile;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages()
    {
        mRunnerPage = new SfProcessRunnerWizardPage(Messages.getString(
                    "SfProcessStopperWizard.title.SmartFrogProcessWizardPage"), //$NON-NLS-1$
                Messages.getString(
                    "SfProcessStopperWizard.description.SmartFrogProcessWizardPage"), //$NON-NLS-1$
                null, true); //$NON-NLS-1$
        addPage(mRunnerPage);
    }

    /**
     * Stop the process that user specified in the wizard
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    public boolean performFinish()
    {
        String processName = mRunnerPage.getProcessName();
        mSfRunner = new SfProcessStopperExt(SmartFrogPlugin.getWorkbenchWindow()
                .getShell(), mSelectedFile, processName);
        mSfRunner.start();

        return true;
    }

    /**
     * return the runner process
     * @return
     */
    public SfProcessStopperExt getRunner()
    {
        return mSfRunner;
    }
}
