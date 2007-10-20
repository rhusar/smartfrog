
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


package org.smartfrog.tools.eclipse.ui.project.document;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IWorkbench;
import org.eclipse.jdt.core.IJavaElement;

/**
 * Description creation wizard
 */
public class DescriptionCreationWizard
    extends BaseFileCreationWizard
{
    private static final String DESCRIPTION_FOLDER = "src"; //$NON-NLS-1$
    private DescriptionCreationWizardPage mDescriptionPage;

    /**
     * Add wizard pages to the wizard
     */
    public void addPages()
    {
        IProject selectedProject = moveToFolder(DESCRIPTION_FOLDER);
        mDescriptionPage = new DescriptionCreationWizardPage(mWorkbench,
                mSelection, selectedProject);
        addPage(mDescriptionPage);
    }

    /**
     * Init wizard page
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.mWorkbench = workbench;
        this.mSelection = selection;
        initializeDefaultPageImageDescriptor("icons/create_description_file.gif"); //$NON-NLS-1$
        setWindowTitle(Messages.getString(
                "DescriptionCreationWizard.Title.DescriptionWizard")); //$NON-NLS-1$
    }

    /** (non-Javadoc)
     * Method declared on IWizard
     */
    public boolean performFinish()
    {
        return mDescriptionPage.finish();
    }

    protected void finishPage(IProgressMonitor monitor)
        throws InterruptedException, CoreException
    {
//        mDescriptionPage.createType(monitor); // use the full progress monitor
    }

    public IJavaElement getCreatedElement() {
    	return null;
    }
}
