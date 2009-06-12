/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.core.SFHadoopRuntimeException;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.services.hadoop.components.dfs.DfsClusterBoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * This component can read in from the resources [] and files [] conf files to read in. All resources are read before
 * all files. Created 14-Jan-2009 13:43:46 The values are read in during sfDeploy() time unless readEarly is false; this
 * ensures the values are there for other components
 */

public class ConfCheckerImpl extends DfsClusterBoundImpl implements ConfChecker, ClusterBound {

    private static final Reference refRequired = new Reference(ATTR_REQUIRED);
    private static final Reference refMatches = new Reference(ATTR_CONF_MATCHES);

    public ConfCheckerImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        ManagedConfiguration managedConf = createConfiguration();
        //dump it to the log at debug level or if the dump attribute is true, in which
        //case it comes out at INFO level
        boolean toDump = sfResolve(ATTR_DUMP, true, true);
        boolean debugEnabled = sfLog().isDebugEnabled();
        if (toDump || debugEnabled) {
            String dump = managedConf.dump();
            if (toDump) {
                sfLog().info(dump);
            }
            if (debugEnabled) {
                sfLog().debug(dump);
            }
        }
        managedConf.validateListedAttributes(this, refRequired);

        Vector<Vector<String>> matches = ListUtils.resolveStringTupleList(this, refMatches, true);
        for (Vector<String> tuple:matches) {
            String attribute = tuple.get(0);
            String expected = tuple.get(1);
            String actual = managedConf.get(attribute);
            if (!expected.equals(actual)) {
                throwUnexpected(managedConf, attribute, expected, actual);
            }
        }

        //Workflow integration
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "ConfChecker",
                null,
                null);

    }

    private void throwUnexpected(ManagedConfiguration managedConf, String attribute, String expected, String actual)
            throws SFHadoopException {
        throw new SFHadoopException("Expected the attribute \"" + attribute + "\" to be "
                + "\"" + expected + " but it is \"" + actual + "\"",
                this,
                managedConf);
    }


}