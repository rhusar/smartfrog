
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


package org.smartfrog.tools.eclipse.ui.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.smartfrog.tools.eclipse.core.ui.NewJavaProjectWizardBase;
import org.smartfrog.tools.eclipse.model.builder.SmartFrogProjectBuilder;

/**
 * SmartFrog projet wizard
 */
public class SmartFrogProject
    extends NewJavaProjectWizardBase
{
    private static final String LIB_DIR = "lib"; //$NON-NLS-1$
    private static String[] sourceFolders = { "src" }; //$NON-NLS-1$
    private static String binaryFolder = "bin"; //$NON-NLS-1$

    /**
     * The constructor.
     */
    public SmartFrogProject()
    {
        super(( new Configuration(new JavaCoreWrapper()) ), ( "SmartFrog" ), //$NON-NLS-1$
            ( "SmartFrog project" ), sourceFolders, binaryFolder); //$NON-NLS-1$
    }

    /**
     *  Creates project structure.
     *
     * @param  project object
     * @param  progress monitor to show while in the process
     *
     * @exception  error while creating project structure.
     */
    protected void processPages(IProject arg0, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.worked(1);
        SmartFrogProjectBuilder.configureSmartFrogProjectBuilder( this.getNewProject(  ) );

    }
}
