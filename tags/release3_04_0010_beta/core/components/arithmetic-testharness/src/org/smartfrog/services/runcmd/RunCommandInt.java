package org.smartfrog.services.runcmd;

/**
 * Title:        SmartFrog CVS
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      HP Labs Bristol
 * @author Serrano
 * @version 1.0
 */

import java.rmi.*;

public interface RunCommandInt extends Remote{

   public void start() throws RemoteException;

   public void stop() throws RemoteException;

   public void kill() throws RemoteException;

   public void reloadDescription() throws RemoteException;

   public void setRestart(boolean isRestart) throws RemoteException;

   public String getCmd() throws RemoteException;

   public String getStatus () throws RemoteException;

   public String getProcessName () throws RemoteException;

}
