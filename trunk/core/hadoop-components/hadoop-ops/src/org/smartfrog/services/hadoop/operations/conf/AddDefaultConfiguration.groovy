package org.smartfrog.services.hadoop.operations.conf

import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl
import org.smartfrog.sfcore.utils.ComponentHelper

/**
 * This component adds a default configuration
 */
class AddDefaultConfiguration extends HadoopComponentImpl {

    /**
     * inject a new default configuration into the list. optionally terminate
     */
    @Override
    synchronized void sfStart() {
        super.sfStart()
        String resourceName = sfResolve("resource", "", true)
        ManagedConfiguration.addNewDefaultResource(resourceName)
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, null, sfCompleteName(), null)
    }


}
