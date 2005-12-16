
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


package org.smartfrog.tools.eclipse.core.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.help.WorkbenchHelp;

import org.smartfrog.tools.eclipse.core.SDKEnvironment;
import org.smartfrog.tools.eclipse.model.IHelpContextIds;


/**
 *
 * An extension of the JDT's NewJavaProjectWizardPage that will
 * update the page's classpath information using the supplied SDKEnvironment
 * object.
 *
 * This class establishes a default Source directory (/src) and a default
 * Output directory (/bin). It does not read the Java Preferences to determine
 * the desired behavior (but probably should).
 *
 */
public class NewJavaWizardPage
    extends NewJavaProjectWizardPage
{
    private static final String DEFAULT_RELATIVE_SOURCE_FOLDER = "/src"; //$NON-NLS-1$
    private static final String DEFAULT_RELATIVE_BIN_FOLDER = "/bin"; //$NON-NLS-1$

    /**
     *
     */
    private SDKEnvironment fSDKEnvironment = null;
    private String[] fRelativeSourceFolder = null;
    private String fRelativeBinFolder = null;
    private boolean mCannotFindSmartFrogLib;

    /**
     * Constructor for NewIUMJavaWizardPage.
     * @param root - An instance of IWorkspaceRoot
     * @param mainpage - The initial page of the Wizard.
     * @param An SDKEnvironment implementor specific to an SDK.
     * @deprecated Use the constructor that allows the source
     * folder to be set.
     */
    public NewJavaWizardPage(IWorkspaceRoot root,
        WizardNewProjectCreationPage mainpage, SDKEnvironment env)
    {
        super(root, mainpage);
        setPageComplete(false);
        fSDKEnvironment = env;
        fRelativeSourceFolder = new String[ 1 ];
        fRelativeSourceFolder[ 0 ] = DEFAULT_RELATIVE_SOURCE_FOLDER;
        fRelativeBinFolder = DEFAULT_RELATIVE_BIN_FOLDER;
    }

    /**
     * Constructor for NewIUMJavaWizardPage.
     * @param root - An instance of IWorkspaceRoot
     * @param mainpage - The initial page of the Wizard.
     * @param An SDKEnvironment implementor specific to an SDK.
     * @param The folder to use as the source folder. Should
     * be relative to the project directory e.g. "/src". If null
     * then the default value of "/src" is used.
     */
    public NewJavaWizardPage(IWorkspaceRoot root,
        WizardNewProjectCreationPage mainpage, SDKEnvironment env,
        String[] relativeSourceFolder)
    {
        super(root, mainpage);
        setPageComplete(false);
        fSDKEnvironment = env;

        if (relativeSourceFolder != null) {
            fRelativeSourceFolder = relativeSourceFolder;
        } else {
            fRelativeSourceFolder = new String[ 1 ];
            fRelativeSourceFolder[ 0 ] = DEFAULT_RELATIVE_SOURCE_FOLDER;
        }

        fRelativeBinFolder = DEFAULT_RELATIVE_BIN_FOLDER;
    }

    /**
     * Constructor for NewIUMJavaWizardPage.
     * @param root - An instance of IWorkspaceRoot
     * @param mainpage - The initial page of the Wizard.
     * @param An SDKEnvironment implementor specific to an SDK.
     * @param The folder to use as the source folder.
     * @param The folder to use as the binary folder. Should
     * be relative to the project directory e.g. "/bin". If null
     * then the default value of "/bin" is used.
     */
    public NewJavaWizardPage(IWorkspaceRoot root,
        WizardNewProjectCreationPage mainpage, SDKEnvironment env,
        String[] relativeSourceFolder, String relativeBinFolder)
    {
        this(root, mainpage, env, relativeSourceFolder);

        if (relativeBinFolder != null) {
            fRelativeBinFolder = relativeBinFolder;
        } else {
            fRelativeBinFolder = DEFAULT_RELATIVE_BIN_FOLDER;
        }
    }

    public void createControl(Composite parent) {
        super.createControl(parent);
    	WorkbenchHelp.setHelp(getControl(),
                IHelpContextIds.SMARTFROG_PROJECT_JAVA_WIZARD_PAGE_HELP_ID);
    }
    /**
     * Creates classpath entries
     *
     * @param The source path to add.
     * @return the created class path entry array, or <code>null</code> if the array
     * was not created
     */
    public IClasspathEntry[] makeClassPathEntries()
    {
        IPath projectPath = getProjectHandle().getFullPath();
        IClasspathEntry[] cpe = null;
        IPath sourcePath = null;
        mCannotFindSmartFrogLib = false;

        try {
            IClasspathEntry[] sdkcpe = fSDKEnvironment.getClasspath();
            int start = 0;

            if (sdkcpe != null) {
                cpe =
                    new IClasspathEntry[ sdkcpe.length +
                    fRelativeSourceFolder.length ];
                System.arraycopy(sdkcpe, 0, cpe, 0, sdkcpe.length);

                start = sdkcpe.length;

                for (int i = 0; i < sdkcpe.length; i++) {
                    IPath path = sdkcpe[ i ].getPath();

                    if (false == path.toFile().exists()) {
                        mCannotFindSmartFrogLib = true;

                        break;
                    }
                }
            } else {
                cpe = new IClasspathEntry[ fRelativeSourceFolder.length ];
            }

            // Add the source path.
            for (int i = 0; i < fRelativeSourceFolder.length; i++) {
                sourcePath = projectPath.append(new Path(
                            fRelativeSourceFolder[ i ]));

                cpe[ start + i ] = JavaCore.newSourceEntry(sourcePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cpe;
    }

    /**
     * Extend this method to set a user defined default class path or output location.
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
        if (visible) {
            IPath projectPath = getProjectHandle().getFullPath();
            IClasspathEntry[] cpe = makeClassPathEntries();

            if (cpe != null) {
                setDefaultClassPath(cpe, true);
            }

            // Due to a bug in the base class we need to always setup src and bin
            // directories.
            setDefaultOutputFolder(projectPath.append(
                    new Path(fRelativeBinFolder)));
        }

        super.setVisible(visible);

        if (true == mCannotFindSmartFrogLib) {
            setErrorMessage(
                "Cannot find the SmartFrog libraries, please set the path in SmartFrog Preferences page."); //$NON-NLS-1$
        }
    }

    /**
     * @return The project relative source path that was set by this page.
     */
    public String getSourcePath()
    {
        return fRelativeSourceFolder[ 0 ];
    }

    /**
     * @return The project relative output path that was set by this page.
     */
    public String getBinPath()
    {
        return fRelativeBinFolder;
    }
}
