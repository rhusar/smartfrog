package org.smartfrog.services.groovy.install

import java.rmi.Remote
import org.smartfrog.sfcore.compound.Compound

/**
 * Interface for all components
 */
public interface IComponent extends Compound, Remote {

    // Defaults for installation
    static final String TEMP_DIR = "/service"

}
