package org.smartfrog.services.groovy.install

import java.rmi.Remote
import org.smartfrog.sfcore.compound.Compound

/**
 * Interface for all components
 */
public interface IComponent extends Compound, Remote {

    // Defaults for installation
    String TEMP_DIR = "/service"
    String ATTR_STATE = "state"
    String ATTR_INSTALL = "install"
    String ATTR_PRE_CONFIGURE = "preConfigure"
    String ATTR_START = "start"
    String ATTR_POST_CONFIGURE = "postConfigure"
}
