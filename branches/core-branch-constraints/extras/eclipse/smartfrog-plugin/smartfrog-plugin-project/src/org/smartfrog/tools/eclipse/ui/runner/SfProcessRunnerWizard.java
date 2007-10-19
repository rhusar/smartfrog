
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
import org.eclipse.core.resources.IProject;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;


/**
 * Wizard to launch the SmartFrog process
 */
class SfProcessRunnerWizard
    extends Wizard
{
    private ImageDescriptor mImage = null;
    private String mDesc = null;
    private String mTitle = null;
    private IFile mFile = null;
    private IProject mProject = null;
    private SfProcessRunnerWizardPage runnerPage;
    private String mSelectedFile;
    private SfProcessRunnerExt mSfRunner;

    /**
     * Constructor
     * @param image
     * @param pageType
     * @param windowTitle
     * @param pageTitle
     * @param desc
     * @param selectedFile
     */
    public SfProcessRunnerWizard(String windowTitle, String pageTitle,
        String desc, String selectedFile, IFile selectedIFile)
    {
        setWindowTitle(windowTitle);

        mTitle = pageTitle;
        mDesc = desc;
        mSelectedFile = selectedFile;
	mFile = selectedIFile;
    }

    public void addPages()
    {
        runnerPage = new SfProcessRunnerWizardPage(Messages.getString(
                    "SfProcessRunnerWizard.title.SFProcess"), //$NON-NLS-1$
                Messages.getString(
                    "SfProcessRunnerWizard.description.SFProcess"), null, //$NON-NLS-1$
                false); //$NON-NLS-1$
        addPage(runnerPage);
    }

    public boolean performFinish()
    {
        String hostName = runnerPage.getHostName();
        String processName = runnerPage.getProcessName();
        mSfRunner = new SfProcessRunnerExt(SmartFrogPlugin.getWorkbenchWindow()
                .getShell(), mSelectedFile, hostName, processName, mFile);
        mSfRunner.start();

        return true;
    }

    public SfProcessRunnerExt getRunner()
    {
        return mSfRunner;
    }
}
