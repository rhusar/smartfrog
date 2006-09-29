package org.smartfrog.regtest.arithmetic;

import java.rmi.*;


public interface Output extends Remote
{
  public void output(int value) throws RemoteException;
}
