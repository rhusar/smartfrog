/**
 *
 */

package org.smartfrog.services.hadoop.components.cluster;

import org.smartfrog.services.hadoop.conf.ClusterBound;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.rmi.RemoteException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteClusterConfigImpl extends HadoopComponentImpl implements ClusterBound {

    public WriteClusterConfigImpl() throws RemoteException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        ManagedConfiguration configuration = ManagedConfiguration.createConfiguration(this, true, true, false);
        File destFile = FileSystem.lookupAbsoluteFile(this, FileUsingComponent.ATTR_FILENAME, null, null, true, null);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destFile);
            configuration.writeXml(fos);
        } catch (IOException e) {
            throw new SmartFrogDeploymentException(e);
        } finally {
            FileSystem.close(fos);
        }

    }
}
