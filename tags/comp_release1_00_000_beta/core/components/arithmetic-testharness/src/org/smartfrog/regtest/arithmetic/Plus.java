package org.smartfrog.regtest.arithmetic;

import org.smartfrog.sfcore.prim.*;
import java.rmi.*;

public class Plus extends NetElemImpl implements Remote {
	private int lhs = 0;
	private int rhs = 0;

  public Plus() throws java.rmi.RemoteException {
  }

  protected int evaluate(String from, int value) {
	if (from.equals("lhs"))
		lhs = value;
	else
		rhs = value;
	return lhs + rhs;
  }
}
