package org.smartfrog.sfcore.workflow.conditional.conditions;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/** Condition that looks for a property being set */
public class IsPropertySet extends PrimImpl implements Condition {


    /** takes the string value of a property to look for: {@value} */
    public static final String ATTR_PROPERTY = "property";


    private String property;

    public IsPropertySet() throws RemoteException {
    }

    public String getProperty() {
        return property;
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();
        property = sfResolve(ATTR_PROPERTY, property, true);
    }


    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     */
    public boolean evaluate() {
        return resolveProperty() !=null;
    }

    protected String resolveProperty() {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            //security prevents access to that property. So imply it is null
            sfLog().debug("Security exception when evaluating "+property,e);
            return null;
        }
    }
}
