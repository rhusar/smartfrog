package org.smartfrog.services.cddlm.cdl.demo;

import org.smartfrog.services.cddlm.cdl.cmp.CmpComponent;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**

 */
public interface Open extends CmpComponent {

    public static final String ATTR_FILENAME="filename";

    public static final String ATTR_EXECUTABLE="executable";

    /**
     * open any file
     * @param filename
     */
    int open(String filename) throws RemoteException, SmartFrogException;
}
