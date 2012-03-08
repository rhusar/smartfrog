package org.smartfrog.services.hadoop.instances

import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration
import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl
import org.smartfrog.services.scripting.groovy.GRef
import org.smartfrog.sfcore.utils.ListUtils

/**
 * This class is the Groovy base class for the MiniDFSCluster and MiniMRCluster
 */
class MiniClusterImpl extends HadoopComponentImpl {
    /** {@value} */
    public static final String ATTR_NODE_COUNT = "nodeCount"
    /** {@value} */
    public static final String ATTR_HOSTS = "hosts"
    /** {@value} */
    public static final String ATTR_RACKS = "racks"
    /** {@value} */
    public static final String ATTR_FILESYSTEM_URI = "filesystemURI";

    public static final String ATTR_LIVE_URI = "liveURI";

    ManagedConfiguration clusterConfig

    MiniClusterImpl() {
    }

    /**
     * Create a configuration and cache it
     * @return
     */
    protected ManagedConfiguration createAndCacheConfig() {
        clusterConfig = createConfiguration()
        return clusterConfig;
    }

    /**
     * Get the cached configuration
     * @return
     */
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
        if (vector) {
            longArray = new long[vector.size()];
            int counter = 0;
            vector.each { elt ->
                longArray[counter++] = (Long) elt;
            }
        }
        return longArray
    }

    /**
     * Resolve a vector of long values
     * @param attr
     * @return
     */
    protected long[] resolveLongVector(String attr) {
        Vector<?> vector = sfResolve(new GRef(attr), new Vector<Long>(), true);
        long[] longs = longify(vector)
        return longs
    }

    /**
     * Resolve an attribute and convert to a string array, or null if the list is empty
     * @param attr the attribute
     * @return the attributes as an array or null if the list was []
     */
    protected String[] resolveListToArray(String attr) {
        List<String> list = ListUtils.resolveStringList(this, new GRef(attr), true);
        return list ? list.stringify() : null;
    }


}
