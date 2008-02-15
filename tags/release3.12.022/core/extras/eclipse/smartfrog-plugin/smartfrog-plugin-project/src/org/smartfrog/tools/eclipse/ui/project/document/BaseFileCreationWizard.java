
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
import org.eclipse.core.resources.IResource;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import org.osgi.framework.Bundle;

import org.smartfrog.tools.eclipse.SmartFrogPlugin;
import org.smartfrog.tools.eclipse.model.ExceptionHandler;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Base file creation wizard
 */
public abstract class BaseFileCreationWizard
    extends NewElementWizard
    implements INewWizard
{
    protected IStructuredSelection mSelection;
    protected IWorkbench mWorkbench;

    /**
     * org.smartfrog.tools.eclipse.ui.project.document.DescriptionCreationWizard
     * @param targetFolder
     * @return
     */
    public IProject moveToFolder(String targetFolder)
    {
        IResource folderResource = null;
        IProject selectedProject = null;
        Iterator iter = mSelection.iterator();

        if (iter.hasNext()) {
            Object object = iter.next();

            if (object instanceof IResource) {
                if (object instanceof IProject) {
                    selectedProject = (IProject)object;
                } else {
                    selectedProject = ( (IResource)object ).getProject();
                }
            } else if (object instanceof IJavaProject) {
                selectedProject = ( (IJavaProject)object ).getProject();
            }

            if (null != selectedProject) {
                folderResource = selectedProject.findMember(targetFolder);

                if (null != folderResource) {
                    ArrayList seletectedList = new ArrayList();
                    seletectedList.add(folderResource);
                    mSelection = new StructuredSelection(seletectedList);
                }
            }
        }

        return selectedProject;
    }

    /**
     * Load the wizard image
     */
    public void initializeDefaultPageImageDescriptor(String path)
    {
        try {
            Bundle bundle = SmartFrogPlugin.getDefault().getBundle();
            URL installURL = bundle.getEntry("/"); //$NON-NLS-1$
            URL url = new URL(installURL, path);
            ImageDescriptor desc = ImageDescriptor.createFromURL(url);

            setDefaultPageImageDescriptor(desc);
        } catch (MalformedURLException mue) {
            // Should not happen.
            ExceptionHandler.handle(mue,
                Messages.getString(
                    "BaseFileCreationWizard.Title.ImageNotFound"), //$NON-NLS-1$
                Messages.getString(
                    "BaseFileCreationWizard.Message.ImageNotFound")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
