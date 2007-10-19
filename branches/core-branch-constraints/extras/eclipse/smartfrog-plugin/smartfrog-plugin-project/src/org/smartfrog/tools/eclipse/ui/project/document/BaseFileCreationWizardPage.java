
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import org.smartfrog.tools.eclipse.model.ExceptionHandler;


/**
 * New file creation main page
 */
public abstract class BaseFileCreationWizardPage
    extends WizardNewFileCreationPage
{
    protected IWorkbench mWorkbench;
    protected String mProjectName = null;
    protected boolean mNullSelection = true;
    protected IProject mProject = null;

    /**
     * Constructor
     * @param  pageTitle              The title of the wizard page
     * @param  workbench
     * @param  selection              The current resource selection
     * @param  selectedProject        The project that is highlighted or contain the highlighted folder
     *
     */
    public BaseFileCreationWizardPage(String pageTitle, IWorkbench workbench,
        IStructuredSelection selection, IProject selectedProject)
    {
        super(pageTitle, selection);
        this.mWorkbench = workbench;

        if (null != selectedProject) {
            mNullSelection = false;
            mProjectName = selectedProject.getName();
            mProject = selectedProject;
        }
    }

    /**
     * returns the currently selected project
     * @return mProject - the current selected project
     */
    public IProject getProject()
    {
        return mProject;
    }

    /**
     * returns the project with specified projectName
     * @param projectName
     * @return project - the project with specified projectName
     */
    public IProject getProject(String projectName)
    {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectName);

        return project;
    }

    /**
     * Create the new file, if it is not exists before
     * Then open that file in the editor view
     */
    public boolean finish()
    {
        boolean ret = true;
        String fileName = getFileName();

        if (( fileName != null ) && !fileName.endsWith(getFileExtension())) {
            setFileName(fileName + getFileExtension());
        }

        IFile newFile = createNewFile();

        if (null == newFile) {
            ret = false;
        } else {
            ret = openDocumentInEditor(newFile);
        }

        return ret;
    }

    /**
     * @param IFile
     * @return
     */
    protected boolean openDocumentInEditor(IFile file)
    {
        // Open the document
        try {
            IWorkbenchWindow dwindow = PlatformUI.getWorkbench()
                                                 .getActiveWorkbenchWindow();
            IWorkbenchPage page = dwindow.getActivePage();

            if (page != null) {
                IDE.openEditor(page, file);
            }
        } catch (PartInitException e) {
            ExceptionHandler.handle(e,
                Messages.getString(
                    "BaseFileCreationWizardPage.Title.CantOpenFile"), //$NON-NLS-1$
                
            Messages.getString(
                    "BaseFileCreationWizardPage.Message.CantOpenFile")); //$NON-NLS-1$

            return false;
        }

        return true;
    }

    /**
     * Return the file extension
     */
    public abstract String getFileExtension();

    /**
     * Return the highlighted project name
     * @return String  highlighted project name
     */
    public String getProjectName()
    {
        if (mNullSelection) {
            getSelectedProjectName();
        }

        return mProjectName;
    }

    protected String getSelectedProjectName()
    {
        IPath containerPath = getContainerFullPath();

        if (null != containerPath) {
            int segCount = containerPath.segmentCount() - 1;

            if (0 <= segCount) {
                containerPath = containerPath.removeLastSegments(segCount);
            }

            mProjectName = containerPath.toString().substring(1);
        }

        return mProjectName;
    }

    /**
     * Make sure the file name is valid
     */
    public boolean validatePage()
    {
        boolean valid = super.validatePage();

        if (!valid) {
            return false;
        }

        String fileName = getFileName();
        setErrorMessage(null);

        if (( fileName != null ) && !fileName.endsWith(getFileExtension())) {
            fileName = fileName + getFileExtension();

            if (!isValidFileName(fileName)) {
                setErrorMessage(Messages.getString(
                        "BaseFileCreationWizardPage.Error.noSpecialCharsAllowed")); //$NON-NLS-1$

                return false;
            }
        }

        return true;
    }

    /**
     * Valid file name has to be: digit,letter,"." or "_"
     */
    protected boolean isValidFileName(String str)
    {
        boolean ret = true;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)) &&
                    ( str.charAt(i) != '.' ) && ( str.charAt(i) != '_' )) {
                ret = false;

                break;
            }
        }

        return ret;
    }
}
