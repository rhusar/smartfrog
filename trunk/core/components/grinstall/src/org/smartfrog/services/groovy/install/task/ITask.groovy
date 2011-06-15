package org.smartfrog.services.groovy.install.task

import java.rmi.Remote
import java.rmi.RemoteException
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.prim.Prim

/**
 * User: koenigbe
 * Date: 02/02/11
 * Time: 11:23
 */
public interface ITask extends Prim, Remote {

    void run() throws RemoteException, SmartFrogException


    void addObserver(ITask observer) throws RemoteException, SmartFrogException


    void update() throws RemoteException, SmartFrogException

    String ATTR_FINISHED = "finished"
    String ATTR_FILE = "file"
    String ATTR_DIRECTORY = "directory"
    String ATTR_PRECONDITIONS = "preconditions"
}
