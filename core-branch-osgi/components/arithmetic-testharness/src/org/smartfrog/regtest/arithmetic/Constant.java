package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.sfcore.common.*;

public class Constant extends NetElemImpl implements Remote {
    public Constant() throws java.rmi.RemoteException {
	super();
    }


    public void sfStart() throws SmartFrogException, RemoteException {
	super.sfStart();
        int v = ((Integer) sfResolve("constant")).intValue();
        System.out.println("CONSTANT: "+" Result: "+ v +", "+sfCompleteNameSafe());
	addValue(v);
    }
}
