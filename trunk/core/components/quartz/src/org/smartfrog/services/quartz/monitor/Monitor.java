package org.smartfrog.services.quartz.monitor;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * <p>
 * Description: CPUMonitor interface.
 * </p>
 *
 */

public interface Monitor extends Remote {
    /* Attributes for CPUMonitor component */
    public final String DELAY = "delay";
    public final String LOGTO = "logTo";

    public int getCurrent() throws RemoteException;

    public int getAverageLastMinute() throws RemoteException;

    public int getAverageLast10Minutes() throws RemoteException;

    public int getAverageLast30Minutes() throws RemoteException;

    public int getAverageLast60Minutes() throws RemoteException;
}
