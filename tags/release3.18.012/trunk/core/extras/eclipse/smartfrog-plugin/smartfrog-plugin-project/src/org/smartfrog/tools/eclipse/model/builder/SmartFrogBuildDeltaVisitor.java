/*
 * (C) Copyright 2003-2004 Hewlett-Packard Development Company, L.P.
 */

package org.smartfrog.tools.eclipse.model.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.smartfrog.tools.eclipse.SmartFrogPlugin;

/**
 * For incremental building 
 */
public class SmartFrogBuildDeltaVisitor extends BasicSmartFrogBuilder implements
        IResourceDeltaVisitor {

    /**
     * Visits the given resource delta.
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
     */
    public boolean visit(IResourceDelta delta) throws CoreException {
        if (null == delta) {
            throw (new CoreException(new Status(IStatus.ERROR,
                    SmartFrogPlugin.getPluginId(), 0,
                    Messages.getString("SmartFrogBuildDeltaVisitor.0"), null))); //$NON-NLS-1$
        }

        IResource resource = delta.getResource();

        checkRmiClass(resource);

        return true;
    }
}