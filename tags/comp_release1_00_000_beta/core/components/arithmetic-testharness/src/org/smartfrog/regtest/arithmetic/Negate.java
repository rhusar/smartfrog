package org.smartfrog.regtest.arithmetic;

import java.rmi.*;

public class Negate extends NetElemImpl implements Remote {


  public Negate() throws java.rmi.RemoteException {
  }

  protected int evaluate(String from, int value) {
	return -value;
  }
}
