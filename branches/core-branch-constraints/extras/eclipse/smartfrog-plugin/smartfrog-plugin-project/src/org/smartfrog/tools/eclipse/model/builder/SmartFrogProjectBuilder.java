/**
 * (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * For more information: www.smartfrog.org
 *  
 */

package org.smartfrog.tools.eclipse.model.builder;

import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.smartfrog.tools.eclipse.model.ISmartFrogConstants;

/**
 * Invoke the SmartFrog RMIC builder to compile the generate rmi skeleton and 
 * stubs classes
 */
public class SmartFrogProjectBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = "org.smartfrog.tools.ComponentBuilder"; //$NON-NLS-1$

    /**
     * gets called whenever project gets built
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
            throws CoreException {
        if (kind == IncrementalProjectBuilder.FULL_BUILD) {
            fullBuild(monitor);
        } else if ((kind == IncrementalProjectBuilder.AUTO_BUILD)
                || (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD)) {
            IResourceDelta delta = getDelta(getProject());

            if (null != delta) {
                incrementalBuild(delta, monitor);
            }
        } else {
            return new IProject[0];
        }

        return null;
    }

    /**
     * call visitor to do full build.
     * 
     * @param monitor
     */
    public int fullBuild(IProgressMonitor monitor) throws CoreException {
        if (null == monitor) {
            return ISmartFrogConstants.ERROR;
        }

        IProject project = getProject();
        SmartFrogBuildFullVisitor builder = new SmartFrogBuildFullVisitor();
        getProject().accept(builder);

        return ISmartFrogConstants.SUCCESS;

    }

    /**
     * call visitor to do incremental build.
     * 
     * @param delta
     * @param monitor
     */
    public int incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
            throws CoreException {
        if ((null == monitor) || (null == delta)) {
            return ISmartFrogConstants.ERROR;
        }

        SmartFrogBuildDeltaVisitor builder = new SmartFrogBuildDeltaVisitor();

        delta.accept(builder);

        return ISmartFrogConstants.SUCCESS;
    }

    /**
     * Configure a particular project to use this builder.
     * 
     * @param project
     * @throws CoreException
     */
    public static int configureSmartFrogProjectBuilder(IProject project)
            throws CoreException {
        if (null == project) {
            return ISmartFrogConstants.ERROR;
        }

        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();
        boolean found = false;

        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(BUILDER_ID)) {
                found = true;

                break;
            }
        }

        if (!found) {
            //add builder to project
            ICommand command = desc.newCommand();
            command.setBuilderName(BUILDER_ID);

            ICommand[] newCommands = new ICommand[commands.length + 1];

            // Add it before other builders.
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            newCommands[commands.length] = command;
            desc.setBuildSpec(newCommands);
            project.setDescription(desc, null);
        }

        return ISmartFrogConstants.SUCCESS;
    }

}