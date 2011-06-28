package org.smartfrog.services.groovy.install

import java.rmi.Remote
import org.smartfrog.sfcore.compound.Compound
import java.rmi.RemoteException

/**
 * Interface for all components
 */
public interface IComponent extends Compound, Remote {

    // Defaults for installation
    String ATTR_CREATE_DEST_DIR = "createDestDir";
    String ATTR_DEST_DIR = "destDir"
    String ATTR_SCRIPT_DIR = "scriptDir"
    String ATTR_INSTALL = "install"
    String ATTR_PRE_CONFIGURE = "preConfigure"
    String ATTR_POST_CONFIGURE = "postConfigure"
    String ATTR_START = "start"
    String ATTR_STATE = "state"

    String getDestDir() throws RemoteException;
}
