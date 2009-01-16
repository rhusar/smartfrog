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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.core.SFHadoopRuntimeException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

/**
 * This component can read in from the resources [] and files [] conf files to read in. All resources are read before
 * all files. Created 14-Jan-2009 13:43:46 The values are read in during sfDeploy() time unless readEarly is false; this
 * ensures the values are there for other components
 */

public class HadoopConfigurationImpl extends PrimImpl implements HadoopConfiguration {

    private ManagedConfiguration managedConf;
    private boolean readEarly;
    private static final Reference refRequired = new Reference(ATTR_REQUIRED);

    public HadoopConfigurationImpl() throws RemoteException {
    }

    /**
     * In the deploy phase, ths component loads in a configuration
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        readEarly = sfResolve(ATTR_READ_EARLY, true, true);
        if(readEarly) {
            loadConfiguration();
        }
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
        if (!readEarly) {
            loadConfiguration();
        }
    }

    private void loadConfiguration() throws RemoteException, SmartFrogException {
        boolean loadDefaults = sfResolve(ATTR_LOAD_DEFAULTS, true, true);
        List<String> resources = ListUtils.resolveStringList(this, new Reference(ATTR_RESOURCES), true);
        Vector<String> files = FileSystem.resolveFileList(this, new Reference(ATTR_FILES), null, true, null);
        Configuration baseConf = new Configuration(loadDefaults);

        //run through all the resources
        for (String resource : resources) {
            loadXmlResource(baseConf, resource);
        }

        //run through the filenames
        for (String filename : files) {
            loadXmlFile(baseConf, filename);
        }

        //this now creates a baseConf which is full of all our values.
        //the next step is to override with any in-scope attributes.

        managedConf = new ManagedConfiguration(this);
        managedConf.addProperties(baseConf);

        //dump it to the log at debug level or if the dump attribute is true, in which
        //case it comes out at INFO level
        boolean toDump = sfResolve(ATTR_DUMP, true, true);
        boolean debugEnabled = sfLog().isDebugEnabled();
        if(toDump || debugEnabled) {
            String dump = managedConf.dump();
            if(toDump) {
                sfLog().info(dump);}
            if(debugEnabled) {
                sfLog().debug(dump);
            }
        }
        managedConf.validateListedAttributes(refRequired);
    }


    /**
     * Clone our configuration
     * @return a copy of the configuration, one that is bound to the same parent component
     * @throws SFHadoopRuntimeException if the cloning fails. This will only happen if a parent class of the (final) class
     * {@link ManagedConfiguration} intercepts and rejects the cloning operation.
     */
    ManagedConfiguration cloneConfiguration()  {
        try {
            return (ManagedConfiguration) managedConf.clone();
        } catch (CloneNotSupportedException e) {
            throw new SFHadoopRuntimeException("Unable to clone an instance of ManagedConfiguration",e);
        }
    }


    /**
     * Load an XML resource in
     *
     * @param baseConf base configuration
     * @param resource resource name
     * @throws SmartFrogException on any resource load failure
     * @throws RemoteException network problems
     */
    private void loadXmlResource(Configuration baseConf, String resource) throws SmartFrogException, RemoteException {
        if(sfLog().isDebugEnabled()) {
            sfLog().debug("Adding resource " + resource);
        }
        baseConf.addResource(resource);
    }

    /**
     * Load an XML file
     * @param baseConf base configuration
     * @param file file to load
     * @throws SmartFrogException on any resource load failure
     * @throws RemoteException network problems
     */
    private void loadXmlFile(Configuration baseConf, String file) throws SmartFrogException, RemoteException {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug("Adding file" + file);
        }
        Path path = new Path(file);
        baseConf.addResource(path);
    }
}
