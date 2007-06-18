package org.smartfrog.regtest.arithmetic;

import java.rmi.*;

// dup is basically a null operation, acting as a fan out
public class Dup extends NetElemImpl implements Remote { 
  public Dup() throws java.rmi.RemoteException {
  }
}
