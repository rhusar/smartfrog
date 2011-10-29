package org.smartfrog.services.hadoop.instances

/**
 *
 */
public interface ClusterURI extends Closeable {

    /**
     * Return the URI of this cluster
     * @return the cluster URI
     */
    
    String getURI();
    
}