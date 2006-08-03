
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import org.smartfrog.tools.eclipse.core.CoreUtilities;
import org.smartfrog.tools.eclipse.core.SDKEnvironment;

import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;


/**
 *
 * This class can be used as the base class for Wizards extending org.eclipse.ui.NewWizard
 * Subclasses can simply override the processPages methods (potentially using some of
 * the protected utility methods) to add any additional project specific info.
 *
 * Subclasses are also free to override the Wizard addPages method, but should
 * call super.addPages() as the first line in the method as this class provides
 * the pages that allow the user to specify the project name and the Java project
 * properties.
 *
 *
 */
abstract public class NewJavaProjectWizardBase
    extends BasicNewResourceWizard
    implements INewWizard, IExecutableExtension
{
    /**
     * The config element set during setInitializationData. Used in performFinish
     * to change the ending perspective to the value listed as finalPerspective
     * in the Wizard's Extension Definition (i.e. in plugin.xml)
     */
    private IConfigurationElement fConfigElement;

    /**
     * The new project creation page.
     */
    private WizardNewProjectCreationPage fProjectPage;

    /**
     * The Java Project Properties page
     */
    private NewJavaWizardPage fJavaPage;

    /**
     * The SDK Environment. This is passed into the CTOR.
     */
    private SDKEnvironment fSDKEnvironment;

    /**
     * The units of work to use in the call to
     * org.eclipse.core.runtime.IProgressMonitor#beginTask
     */
    private int fUnitsOfWork = IProgressMonitor.UNKNOWN;

    /**
     * Members to maintain the title and description to be used for
     * the Project and Java Properties pages.
     */
    private String fTitle = null;
    private String fDescription = null;

    /**
     * Static members
     */
    private final static String NEW_PROJECT_WIZARD_ID =
        "Wizard.NewProjectWizardID"; //$NON-NLS-1$
    private final static String NEW_PROJECT_WIZARD_TASK_TITLE =
        "Wizard.NewProjectWizardTaskTitle"; //$NON-NLS-1$
    private final static String NEW_PROJECT_WIZARD_DEFAULT_TITLE =
        "Wizard.NewProjectWizardDefaultTitle"; //$NON-NLS-1$
    private final static String NEW_PROJECT_WIZARD_DEFAULT_DESCRIPTION =
        "Wizard.NewProjectWizardDefaultDescription"; //$NON-NLS-1$
    private final static String NEW_PROJECT_WIZARD_WINDOW_TITLE =
        "Wizard.NewProjectWizardWindowTitle"; //$NON-NLS-1$
    private String[] fSourceFolder;
    private String fBinaryFolder;

    /**
     * Publically available CTOR for NewJavaProjectWizardBase.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     *
     * @param Title to be used for the Project and Java Properties pages.
     * @param Description to be used for the Project and Java Properties pages.
     * @param The units of work to be used in the call to
     * org.eclipse.core.runtime.IProgressMonitor#beginTask
     */
    public NewJavaProjectWizardBase(SDKEnvironment env, String title,
        String description, int unitsOfWork)
    {
        super();
        initialize(env, title, description, null, unitsOfWork);
    }

    /**
     * Publically available CTOR for NewJavaProjectWizardBase.
     * Subsequent call to org.eclipse.core.runtime.IProgressMonitor#beginTask will
     * use IProgressMonitor.UNKNOWN as the parameter.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     * @param Title to be used for the Project and Java Properties pages.
     * @param Description to be used for the Project and Java Properties pages.
     */
    public NewJavaProjectWizardBase(SDKEnvironment env, String title,
        String description)
    {
        super();
        initialize(env, title, description, null, IProgressMonitor.UNKNOWN);
    }

    /**
     * Publically available CTOR for NewJavaProjectWizardBase.
     * Subsequent call to org.eclipse.core.runtime.IProgressMonitor#beginTask will
     * use IProgressMonitor.UNKNOWN as the parameter.
     * Default title and descriptions will be used.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     */
    public NewJavaProjectWizardBase(SDKEnvironment env)
    {
        super();
        initialize(env,
            CoreUtilities.getResourceString(NEW_PROJECT_WIZARD_DEFAULT_TITLE),
            CoreUtilities.getResourceString(
                NEW_PROJECT_WIZARD_DEFAULT_DESCRIPTION), null,
            IProgressMonitor.UNKNOWN);
    }

    /**
     * Default CTOR assumes that set methods will be called later. Useful for
     * the Extensible subclass.
     */
    protected NewJavaProjectWizardBase()
    {
        initialize(null,
            CoreUtilities.getResourceString(NEW_PROJECT_WIZARD_DEFAULT_TITLE),
            CoreUtilities.getResourceString(
                NEW_PROJECT_WIZARD_DEFAULT_DESCRIPTION), null,
            IProgressMonitor.UNKNOWN);
    }

    /**
     * Publically available CTOR for NewJavaProjectWizardBase.
     * Subsequent call to org.eclipse.core.runtime.IProgressMonitor#beginTask will
     * use IProgressMonitor.UNKNOWN as the parameter.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     * @param Title to be used for the Project and Java Properties pages.
     * @param Description to be used for the Project and Java Properties pages.
     * @param The folder to use as the source folder. Should
     * be relative to the project directory e.g. "/src". If null
     * then the default value of "/src" is used.
     */
    public NewJavaProjectWizardBase(SDKEnvironment env, String title,
        String description, String[] sourceFolder)
    {
        super();
        initialize(env, title, description, sourceFolder,
            IProgressMonitor.UNKNOWN);
    }

    /**
     * Publically available CTOR for NewJavaProjectWizardBase.
     * Subsequent call to org.eclipse.core.runtime.IProgressMonitor#beginTask will
     * use IProgressMonitor.UNKNOWN as the parameter.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     * @param Title to be used for the Project and Java Properties pages.
     * @param Description to be used for the Project and Java Properties pages.
     * @param The folder to use as the source folder. Should
     * be relative to the project directory e.g. "/src". If null
     * then the default value of "/src" is used.
     * @param The folder to use as the binary folder. Should
     * be relative to the project directory e.g. "/bin". If null
     * then the default value of "/bin" is used.
     */
    public NewJavaProjectWizardBase(SDKEnvironment env, String title,
        String description, String[] sourceFolder, String binaryFolder)
    {
        super();
        initialize(env, title, description, sourceFolder, binaryFolder,
            IProgressMonitor.UNKNOWN);
    }

    /**
     * Private method to initialize the instance variables.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     * @param Title to be used for the Project and Java Properties pages.
     * @param Description to be used for the Project and Java Properties pages.
     * @param The folder to use as the source folder.
     * @param The units of work to be used in the call to
     * org.eclipse.core.runtime.IProgressMonitor#beginTask
     */
    private void initialize(SDKEnvironment env, String title,
        String description, String[] sourceFolder, int unitsOfWork)
    {
        fSDKEnvironment = env;
        fTitle = title;
        fDescription = description;
        fUnitsOfWork = unitsOfWork;
        fSourceFolder = sourceFolder;
        setWindowTitle(CoreUtilities.getResourceString(
                NEW_PROJECT_WIZARD_WINDOW_TITLE));
    }

    /**
     * Private method to initialize the instance variables.
     *
     * @param Object that satisfies the SDKEnvironment interface. Current functionality
     * is to allow subclasses to specify the Java classpath used on the Java Properties
     * page.
     * @param Title to be used for the Project and Java Properties pages.
     * @param Description to be used for the Project and Java Properties pages.
     * @param The folder to use as the source folder.
     * @param The folder to use as the binary folder.
     * @param The units of work to be used in the call to
     * org.eclipse.core.runtime.IProgressMonitor#beginTask
     */
    private void initialize(SDKEnvironment env, String title,
        String description, String[] sourceFolder, String binaryFolder,
        int unitsOfWork)
    {
        this.initialize(env, title, description, sourceFolder, unitsOfWork);
        fBinaryFolder = binaryFolder;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     *
     * Calls the base class's addPages and then creates and adds pages
     * for Project creation and the Java Properties settings.
     */
    public void addPages()
    {
        super.addPages();

        fProjectPage = new NewProjectCreationPage(CoreUtilities
                .getResourceString(NEW_PROJECT_WIZARD_ID));

        fProjectPage.setTitle(fTitle);

        fProjectPage.setDescription(fDescription);
        this.addPage(fProjectPage);

        //add the second page
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        fJavaPage = new NewJavaWizardPage(root, fProjectPage, fSDKEnvironment,
                fSourceFolder, fBinaryFolder);

        fJavaPage.setTitle(fTitle);

        fJavaPage.setDescription(fDescription);
        addPage(fJavaPage);
    }

    /**
     * Delete any resources.
     * This method is final and only public since it must be called by the
     * Framework.
     *
     * @see org.eclipse.jface.wizard.IWizard#dispose()
     */
    public final void dispose()
    {
        super.dispose();
        fSDKEnvironment = null;
        fTitle = null;
        fDescription = null;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     *
     *        The user has pressed Finish.  Instruct self's pages to finish, and
     *        answer a boolean indicating success.
     *
     * This method is final and only public since it must be called by the
     * Framework when the user selects the Finish button. Rather than overriding
     * this method, subclasses implement the processPages method.
     *
     * This method uses Eclipse classes to modify the workspace.
     * Uses these classes it calls the runnable in the Java Properties page
     * to establish the Java settings, then it will call the runnable
     * that wraps the call to the subclasses processPages method.
     *
     *        @return True if all was OK, False if either the user canceled or an error
     * occurred.
     */
    public boolean performFinish()
    {
        IRunnableWithProgress javaop = new WorkspaceModifyDelegatingOperation(
                fJavaPage.getRunnable());
        IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
                getRunnable());

        try {
            getContainer().run(false, true, javaop);
            getContainer().run(false, true, op);
        } catch (InvocationTargetException e) {
            InvocationTargetExceptionHandler(e);

            return false;
        } catch (InterruptedException e) {
            return false;
        }

        // Everyting was successful assure that the perspective switches
        // to the Resource perspective and that the project is revealed.
        BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
        selectAndReveal(getNewProject());

        return true;
    }

    /**
     * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(IConfigurationElement, String, Object)
     *
     *
     * Stores the configuration element for the wizard.  The config element will be used
     * in <code>performFinish</code> to set the result perspective.
     *
     *
     */
    public void setInitializationData(IConfigurationElement cfig,
        String propertyName, Object data)
        throws CoreException
    {
        fConfigElement = cfig;
    }

    /**
     * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#getNewProject()
     *
     * This method is final and only public since it is defined as such by super.
     */
    public final IProject getNewProject()
    {
        return fProjectPage.getProjectHandle();
    }

    /**
     *         Provides the IRunnableWithProgress instance that is used
     *  for calling the subclass's processPages method.
     *
     * The IRunnableWithProgress#run method will call the subclasses processPages.
     * It will delete the project that has been created by the Project page if there
     * is any CoreException thrown from processPages.
     *
     * @exception Wrapper exception for any CoreException that may have happened during
     * the call to processPages.
     * @exception User canceled the operation.
     *
     * @see org.eclipse.core.runtime.IProgressMonitor.
     * @see org.eclipse.jface.operation.IRunnableWithProgress
     */
    private IRunnableWithProgress getRunnable()
    {
        return new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException
                {
                    if (monitor == null) {
                        monitor = new NullProgressMonitor();
                    }

                    monitor.beginTask(CoreUtilities.getResourceString(
                            NEW_PROJECT_WIZARD_TASK_TITLE), fUnitsOfWork);

                    try {
                        processPages(getNewProject(), monitor);
                    }
                    // WIth any type of CoreException remove the project from the Workspace
                    // and then throw the InvocationTargetException to performFinish
                    // THis will force a message to the user and a return
                    // to the last page of the Wizard
                    catch (CoreException e) {
                        removeProject(monitor);
                        throw new InvocationTargetException(e);
                    } finally {
                        if (monitor != null) {
                            monitor.done();
                        }
                    }
                }
            };
    }

    /**
     *         Removes this Wizard's project resource from the Workspace. Subclasses cannot
     * use this method and instead should throw a Throwable from their processPages
     * implementation
     *
     * @param monitor - A progress monitor
     *
     * @exception InvocationTargetException - Any exception is wrapped with
     * the InvocationTargetException so it is caught during performFinish.
     *
     */
    private void removeProject(IProgressMonitor monitor)
        throws InvocationTargetException
    {
        IProject project = getNewProject();

        if (project != null) {
            try {
                // Assure that we do not delete any filesystem resources that
                // may have existed in the project's directory. For example,
                // if the file <project name>.wsdl existed the Wizard will fail
                // but we cannot remove this file.
                project.delete(false, false,
                    new SubProgressMonitor(monitor, 1));
            } catch (Exception e) {
                throw new InvocationTargetException(e);
            }
        }
    }

    /**
     * processPages must be overriden by a subclass. It is in this method
     * that the subclass should do any specific processing it requires.
     *
     * @param The project instance that has been created. All information
     * known by this base class is contained in the IProject or the retrievable
     * IProjectDescription. Note that if processPages throws a CoreException
     * the project will be removed and the project handle will be invalid.
     *
     * @param Subclasses can indicate progress using this object's worked method.
     *
     * @see org.eclipse.core.runtime.IProgressMonitor
     */
    abstract protected void processPages(IProject p, IProgressMonitor monitor)
        throws CoreException;

    /**
     * A utility method for adding an Eclipse nature to the created project
     * By default the created project will already have the Java nature, so this
     * method need only be used if the subclass wishes to add other natures.
     *
     * @param The nature id as listed in plugin.xml.
     *
     * @exception An exception thrown from either IProject#getDescription or IProject#setDescription
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IProject#getDescription
     * @see org.eclipse.core.resource.IProject#setDescription
     */
    protected final void addNature(String nature)
        throws CoreException
    {
        addNature(getNewProject(), nature);
    }

    /**
     * An accompanying private addNature that allows the code base to be shared.
     *
     */
    private static void addNature(IProject p, String nature)
        throws CoreException
    {
        IProjectDescription description = p.getDescription();
        String[] natures = description.getNatureIds();
        String[] newNatures = new String[ natures.length + 1 ];
        System.arraycopy(natures, 0, newNatures, 0, natures.length);
        newNatures[ natures.length ] = nature;
        description.setNatureIds(newNatures);
        p.setDescription(description, null);
    }

    /**
     * A utility method that will create a file in the Project's root.
     *
     * @param The InputStream that is to be used as the initial contents of the file.
     * This parameter should not be null.
     * @param The name to give the file.
     * @param An IProgresMonitor to be used in calls to the Framework.
     *
     * @exception An exception thrown from either IFile#create
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IFile#create
     */
    protected final void createProjectFile(InputStream s, String name,
        IProgressMonitor monitor)
        throws CoreException
    {
        IFile f = getNewProject().getFile(name);
        createFile(f, s, monitor);
    }

    /**
     * A utility method for creating a file in the Project's default source
     * folder (i.e. /src)
     * @param The InputStream that is to be used as the initial contents of the file.
     * This parameter should not be null.
     * @param The name to give the file.
     * @param The package name for the source file. If this is not null or the
     * empty string then this method will create the appropriate folder structure
     * under the Project.
     * @param An IProgresMonitor to be used in calls to the Framework.
     *
     * @exception An exception thrown from either IFolder#create or IFile#create
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IFile#create
     * @see org.eclipse.core.resource.IFolder#create
     */
    protected final void createSourceFile(InputStream s, String name,
        String packageName, IProgressMonitor monitor)
        throws CoreException
    {
        String folderName = fJavaPage.getSourcePath();

        if (( packageName != null ) && ( packageName.length() > 0 )) {
            createFolder(folderName, monitor);

            // Setup for the case where the package name is a single word
            int start = 0;
            int index = packageName.indexOf('.', start);

            if (index == -1) {
                folderName += ( "/" + packageName ); //$NON-NLS-1$
            }

            while (index != -1) {
                folderName += ( "/" + packageName.substring(start, index) ); //$NON-NLS-1$

                createFolder(folderName, monitor);
                start = index + 1;
                index = packageName.indexOf('.', start);

                if (index == -1) {
                    // Last Folder Name should be used as the parameter to the createFileInFolder
                    // method.
                    folderName += ( "/" + packageName.substring(start) ); //$NON-NLS-1$
                }
            }
        }

        createFileInFolder(s, folderName, name, monitor);
    }

    /**
     * A utility method for creating a file in a first-level arbitrary Project folder.
     *
     * @param The InputStream that is to be used as the initial contents of the file.
     * This parameter should not be null.
     * @param The path of the folder (e.g. /test). The folder will be created if it does not
     * exist. Note that for now there is only support for one level below the project.
     * @param The name to give the file.
     * @param An IProgresMonitor to be used in calls to the Framework.
     *
     * @exception An exception thrown from either IFolder#create or IFile#create
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IFile#create
     * @see org.eclipse.core.resource.IFolder#create
     */
    protected final void createFileInFolder(InputStream s, String folderName,
        String name, IProgressMonitor monitor)
        throws CoreException
    {
        IFile file = createFolder(folderName, monitor).getFile(name);
        createFile(file, s, monitor);
    }

    /**
     * A utility method for creating a folder under the Project.
     *
     * @param The path of the folder (e.g. /test). The folder will be created if it does not
     * exist. Note that for now there is only support for one level below the project.
     * @param An IProgresMonitor to be used in calls to the Framework.
     *
     * @exception An exception thrown from IFolder#create
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IFolder#create
     */
    protected final IFolder createFolder(String folderName,
        IProgressMonitor monitor)
        throws CoreException
    {
        return createFolder(getNewProject(), folderName, monitor);
    }

    /**
     * An accompanying private createFolder that allows the code base to be shared.
     *
     */
    private static IFolder createFolder(IProject p, String folderName,
        IProgressMonitor monitor)
        throws CoreException
    {
        IFolder folder = p.getFolder(folderName);

        if (!folder.exists()) {
            folder.create(true, true, monitor);
        }

        return folder;
    }

    /**
     * A utility method for creating a file in the Workspace.
     *
     * @param The IFile representation of the file. This has been obtained from
     * a Framework call (e.g. IProject#getFile(filename) )
     * @param An IProgresMonitor to be used in calls to the Framework.
     *
     * @exception An exception thrown from IFile#create
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IFile#create
     */
    protected final void createFile(IFile f, InputStream s,
        IProgressMonitor monitor)
        throws CoreException
    {
        if (!f.exists()) {
            f.create(s, false, monitor);
        }
    }

    /**
     * Returns the name of the Project.
     *
     * @return The name of the project. This is the value the
     * user typed into the Project page.
     * @exception An exception thrown from either IProject#getDescription
     *
     * @see org.eclipse.core.runtime.CoreException
     * @see org.eclipse.core.resources.IProject#getDescription
     *
     */
    protected final String getProjectName()
        throws CoreException
    {
        IProject p = getNewProject();

        if (p != null) {
            IProjectDescription pd = p.getDescription();

            if (pd != null) {
                return pd.getName();
            }
        }

        return ""; //$NON-NLS-1$
    }

    /**
     *         Display messages based on e
     *
     * @param e, an InvocationTargetException to tell the user about.
     */
    private void InvocationTargetExceptionHandler(InvocationTargetException e)
    {
        MessageDialog.openError(getShell(), fTitle,
            e.getTargetException().getMessage());
    }

    /**
     * Sets the fSDKEnvironment.
     * @param fSDKEnvironment The fSDKEnvironment to set
     */
    protected void setFSDKEnvironment(SDKEnvironment fSDKEnvironment)
    {
        this.fSDKEnvironment = fSDKEnvironment;
    }

    /**
     * Sets the fTitle.
     * @param fTitle The fTitle to set
     */
    protected void setFTitle(String fTitle)
    {
        this.fTitle = fTitle;
    }

    /**
     * Sets the fDescription.
     * @param fDescription The fDescription to set
     */
    public void setFDescription(String fDescription)
    {
        this.fDescription = fDescription;
    }
}
