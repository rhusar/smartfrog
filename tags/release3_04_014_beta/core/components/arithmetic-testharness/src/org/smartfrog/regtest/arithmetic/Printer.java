package org.smartfrog.regtest.arithmetic;

import java.rmi.*;

public class Printer extends NetElemImpl implements Remote {

  public Printer() throws java.rmi.RemoteException {
  }

  public int evaluate(String from, int value) {
	System.out.println(name + " received value " + value);
	return value;
  }
}
