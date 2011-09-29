/**
 *
 */

package org.smartfrog.services.hadoop.operations.conf;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.hadoop.operations.core.ClusterBound;
import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

public class WriteClusterConfigImpl extends HadoopComponentImpl implements ClusterBound {

    public WriteClusterConfigImpl() throws RemoteException {
    }

    @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"})
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        ManagedConfiguration configuration = ManagedConfiguration.createConfiguration(this, true, true, false, false);
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
