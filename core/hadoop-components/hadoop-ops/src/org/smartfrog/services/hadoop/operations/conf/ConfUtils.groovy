package org.smartfrog.services.hadoop.operations.conf

import org.apache.hadoop.conf.Configuration

/**
 *
 */
class ConfUtils {

    /**
     * Copy in the properties to the config, if the props argument is non-null
     * @param conf configuration to update
     * @param props properties (can be null)
     */
    
    static void copyProperties(Configuration conf, Properties props) {
        if (props) {
            props.each { key, value ->
                conf.set(key.toString(), value.toString())
            }
        }
    }
}
