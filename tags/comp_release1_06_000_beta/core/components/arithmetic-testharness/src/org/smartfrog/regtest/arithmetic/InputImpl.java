package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;

public class InputImpl extends PrimImpl implements Prim, Input, Remote {

  String me;
  NetElem function;

  // standard constructor
  public InputImpl() throws RemoteException{
	super();
  }  
  // Input methods
  public void input(int value) throws RemoteException {
	function.doit(me, value);
  }  

  // lifecycle methods
  public void sfDeploy() throws SmartFrogException, RemoteException {
	// get the function part; currently the parent (perhaps should be a link...)
	function =  (NetElem) (sfParent().sfParent());
	// get my name in the "inputs" context for use as my ID
	me = (String) (sfParent().sfAttributeKeyFor(this));
  }  
}
