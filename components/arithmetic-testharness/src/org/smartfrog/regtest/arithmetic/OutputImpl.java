package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import java.util.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;

public class OutputImpl extends PrimImpl implements Prim, Output, Remote {
  Input to;
 
  // standard constructor
  public OutputImpl() throws RemoteException{
	super();
  }  
  // public component methods
  public void output (int value) throws RemoteException {
	to.input(value);
  }  
  // lifecycle methods
  public void sfDeploy() throws SmartFrogException, RemoteException {
	// get the binding to forward to, if non-existant fail
	to = (Input) sfResolve("to");
  }  
}
