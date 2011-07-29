package org.smartfrog.services.hadoop.instances

import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl
import java.rmi.RemoteException
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration
import org.smartfrog.services.scripting.groovy.GRef
import org.smartfrog.sfcore.utils.ListUtils
import org.apache.hadoop.fs.FileSystem;

/**
 * This class is the Groovy base class for the MiniDFSCluster and MiniMRCluster
 */
class MiniClusterImpl extends HadoopComponentImpl {
    public static final String ATTR_NODE_COUNT = "nodeCount"
    public static final String ATTR_HOSTS = "hosts"
    public static final String ATTR_RACKS = "racks"
    public static final String ATTR_FILESYSTEM_URI = "filesystemURI";

    ManagedConfiguration clusterConfig

    MiniClusterImpl() {
    }

    protected ManagedConfiguration createAndCacheConfig() {
        clusterConfig = createConfiguration()
        return clusterConfig;
    }

    protected ManagedConfiguration getClusterConfig() {
        return clusterConfig;
    }

    /**
     * Create a long array from a vector of Long values
     * @param vector the vector of type Long
     * @return an array of the same size, or null if the vector is empty
     */
    protected long[] longify(Vector<?> vector) {
        long[] longArray = null
        if (!vector.empty) {
            longArray = new long[vector.size()];
            int counter = 0;
            vector.each { elt ->
                longArray[counter++] = (Long) elt;
            }
        }
        return longArray
    }


    protected long[] resolveLongVector(String attr) {
        Vector<?> vector = sfResolve(new GRef(attr), new Vector<Long>(), true);
        long[] longs = longify(vector)
        return longs
    }

    protected String[] resolveListToArray(String attr) {
        String[] array = null
        List<String> list = ListUtils.resolveStringList(this, new GRef(attr), true);
        if (!list.empty) {
            array = list.stringify();
        }
        return array
    }


}
