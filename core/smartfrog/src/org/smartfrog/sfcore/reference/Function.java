package org.smartfrog.sfcore.reference;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Interface for SF functions to implement
 */
public interface Function {
    /**
     * Evaluate the function
     *
     * @param c A context that contains all the parameters
     * @throws SmartFrogException Failure to evaluate the function
     */
    public Object doit(Context c, Reference name) throws SmartFrogException;
}
