package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.sfcore.workflow.eventbus.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.parser.*;

public class BlackBox extends NetElemImpl implements Remote {
  public BlackBox() throws java.rmi.RemoteException {
  }

  protected int evaluate(String from, int value) {
	return value;
  }

}
