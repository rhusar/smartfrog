package org.smartfrog.services.quartz.collector;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for all components which will be polled by a graph in order to
 * provide the data to display
 */
public interface DataSource extends Remote {
    /**
     * Returns an int containing the data
     *
     * @return DOCUMENT ME!
     */
    public int getData() throws RemoteException;
}
