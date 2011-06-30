package org.smartfrog.services.groovy.install

import java.rmi.Remote
import java.rmi.RemoteException
import org.smartfrog.sfcore.compound.Compound

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
    String ATTR_EXEC_TIMEOUT = "execTimeout";
    public String ATTR_TERMINATOR = "terminate"

    String getDestDir() throws RemoteException

    ;
}
