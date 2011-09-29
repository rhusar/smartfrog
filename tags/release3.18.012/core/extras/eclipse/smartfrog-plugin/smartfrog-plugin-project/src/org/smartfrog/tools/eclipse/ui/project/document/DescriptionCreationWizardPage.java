
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

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.IHelpContextIds;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.help.WorkbenchHelp;


/**
 * Description creation main page
 */
public class DescriptionCreationWizardPage
    extends BaseFileCreationWizardPage
{
    public static final String DEFAULT_DESCRIPTION_EXT = "sf"; //$NON-NLS-1$
    public static final String DEFAULT_DESCRIPTION_EXT_WITH_DOT = "."+DEFAULT_DESCRIPTION_EXT; //$NON-NLS-1$

    /**
     * Constructor
     * @param  workbench         The current workbench
     * @param  selection         The current resource selection
     * @param  selectedProject   The project that is highlighted or contain the highlighted folder
     *
     */
    public DescriptionCreationWizardPage(IWorkbench workbench,
        IStructuredSelection selection, IProject selectedProject)
    {
        super(Messages.getString(
                "DescriptionCreationWizardPage.Title.DescritpionWizard"), //$NON-NLS-1$
            workbench, selection, //$NON-NLS-1$
            selectedProject);
        this.setTitle(Messages.getString(
                "DescriptionCreationWizardPage.Title.ComponentWizard")); //$NON-NLS-1$
        this.setDescription(Messages.getString(
                "DescriptionCreationWizardPage.Message.ComponentWizard")); //$NON-NLS-1$
        this.mWorkbench = workbench;
    }

    /*
     * Returns the default description file extension
     */
    public String getFileExtension()
    {
        return DEFAULT_DESCRIPTION_EXT_WITH_DOT;
    }

    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
    public void createControl(Composite parent)
    {
        super.createControl(parent);

        Composite composite = (Composite)getControl();
        WorkbenchHelp.setHelp(composite,
            IHelpContextIds.NEW_DESCRIPTION_MAIN_PAGE_HELP_ID);
    }

    /**
     * Create the new description file, if it is not exists before
     */
    public boolean finish()
    {
        boolean ret = true;
        String fileName = getFileName();

        if (( fileName != null ) &&
                !fileName.endsWith(DEFAULT_DESCRIPTION_EXT_WITH_DOT)) {
            setFileName(fileName + DEFAULT_DESCRIPTION_EXT_WITH_DOT);
        }

        IFile newPromptTextFile = createNewFile();

        if (null == newPromptTextFile) {
            ret = false;
        }

        openDocumentInEditor(newPromptTextFile);

        return ret;
    }

    /**
     * The <code>WizardNewFileCreationPage</code> implementation of this
     * <code>Listener</code> method handles all events and enablements for controls
     * on this page. Subclasses may extend.
     */
    public void handleEvent(Event event)
    {
        setPageComplete(validatePage());
    }

    /**
     * Make sure the new file is not exists before
     */
    public boolean validatePage()
    {
        IPath path = getContainerFullPath();

        if (null == path) {
            //Overwrite the default behavior of fileCreation Wizard that
            // make the empty folder as "slient" error
            setErrorMessage(Messages.getString(
                    "DescriptionCreationWizardPage.Error.FolderNameEmpty")); //$NON-NLS-1$

            return false;
        }

        boolean valid = super.validatePage();

        if (!valid) {
            return false;
        }

        String fileName = getFileName();
        setErrorMessage(null);

        if (( fileName != null ) &&
                !fileName.endsWith(DEFAULT_DESCRIPTION_EXT_WITH_DOT)) {
            fileName = fileName + DEFAULT_DESCRIPTION_EXT_WITH_DOT;

            path = getContainerFullPath();

            if (( path != null ) &&
                    SmartFrogPlugin.getWorkspace().getRoot().exists(
                        path.append(fileName))) {
                setErrorMessage(Messages.getString(
                        "DescriptionCreationWizardPage.Error.FileExist")); //$NON-NLS-1$

                return false;
            }
        }

        return true;
    }


}
