package org.smartfrog.regtest.arithmetic;

import java.rmi.*;

import org.smartfrog.sfcore.prim.*;
public class Times extends NetElemImpl implements Remote {
	private int lhs = 0;
	private int rhs = 0;

  public Times() throws java.rmi.RemoteException {
  }

  protected int evaluate(String from, int value) {
	if (from.equals("lhs"))
		lhs = value;
	else
		rhs = value;
	return lhs * rhs;
  }
}
