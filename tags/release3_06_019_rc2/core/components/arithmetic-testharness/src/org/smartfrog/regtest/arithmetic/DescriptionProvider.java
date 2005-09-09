package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.common.*;

public interface DescriptionProvider extends Remote
{
  public ComponentDescription giveDesc(String exp) throws RemoteException;
}
