package org.smartfrog.services.ssh;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: mahrt
 * Date: Jul 18, 2007
 * Time: 1:35:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class InlinePasswordProviderImpl extends PrimImpl implements PasswordProvider {


    public InlinePasswordProviderImpl() throws RemoteException {
    }

    public static final String ATTR_PASSWORD="password";
    /**
     * {@inheritDoc}
     * @return  the value of the password attribute
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public String getPassword() throws SmartFrogException, RemoteException {
        return sfResolve(ATTR_PASSWORD,(String)null,true);
    }
}
