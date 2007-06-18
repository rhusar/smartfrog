package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;

public class DescriptionProviderImpl extends PrimImpl implements Prim, DescriptionProvider {
  ComponentDescription gen, op;

  public DescriptionProviderImpl() throws RemoteException {}


  public void sfDeploy() throws SmartFrogException, RemoteException {
    try {
    gen = (ComponentDescription) sfResolve("genDesc");
    op = (ComponentDescription) sfResolve("opDesc");
    } catch (Exception e){e.printStackTrace();}
  }
  public ComponentDescription giveDesc(String exp) throws RemoteException {
//    System.out.println("Asked for "+ exp);
    if ((exp == null) || exp.equals("gen"))
      return gen;
    else
      return op;
  }
}
