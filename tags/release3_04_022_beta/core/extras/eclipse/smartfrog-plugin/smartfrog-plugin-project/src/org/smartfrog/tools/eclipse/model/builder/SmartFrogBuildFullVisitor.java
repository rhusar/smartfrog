
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


package org.smartfrog.tools.eclipse.model.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;


/**
 * Use rmic compiler to build the RMI classes. For full build
 */
public class SmartFrogBuildFullVisitor extends BasicSmartFrogBuilder
    implements IResourceVisitor
{
    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
     */
    public boolean visit(IResource resource)
        throws CoreException
    {
        if (null == resource) {
            throw ( new CoreException(
                    new Status(IStatus.ERROR,
                        SmartFrogPlugin.getPluginId(), 0,
                        Messages.getString("SmartFrogBuildFullVisitor.0"), null)) ); //$NON-NLS-1$
        }


        checkRmiClass(resource);

        return true;
    }

  
}
