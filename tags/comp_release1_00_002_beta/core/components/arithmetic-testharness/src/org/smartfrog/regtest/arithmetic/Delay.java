package org.smartfrog.regtest.arithmetic;

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import java.rmi.*;

public class Delay extends NetElemImpl implements Remote {
  private int delay = 10;

  public Delay() throws java.rmi.RemoteException {
  }

  protected int evaluate(String from, int value) {
	try {
          Thread.sleep(delay*1000);
	} catch (Exception e){}
        return value;
  }
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    try {
      delay = ((Integer) this.sfResolve("delay")).intValue();
    } catch (Exception e){
      //e.printStackTrace();
    }
  }

}
