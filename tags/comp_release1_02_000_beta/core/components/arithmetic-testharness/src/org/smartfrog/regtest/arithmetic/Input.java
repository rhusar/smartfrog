package org.smartfrog.regtest.arithmetic;

import java.rmi.*;

public interface Input extends Remote {
  public void input(int value) throws RemoteException;  
}
