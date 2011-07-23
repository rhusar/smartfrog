package org.smartfrog.services.hadoop.operations.conf

import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl
import org.smartfrog.sfcore.utils.ComponentHelper

/**
 * This component adds a default configuration
 */
class AddDefaultConfiguration extends HadoopComponentImpl {
    
    
    @Override 
    synchronized void sfStart() {
        super.sfStart()
        String resourceName = sfResolve("resource","", true)
        ManagedConfiguration.addDefaultResource(resourceName)
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, null, sfCompleteName(), null)
    }


}
