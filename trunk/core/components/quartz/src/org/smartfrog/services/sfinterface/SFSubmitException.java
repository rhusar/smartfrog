/*
 * Created on May 5, 2005
 *
 */
package org.smartfrog.services.sfinterface;

/**
 * @author sanjay, May 5, 2005
 *         <p/>
 *         TODO
 */
public class SFSubmitException extends Exception {

    /**
     *
     */
    public SFSubmitException() {
        super();
    }

    /**
     * @param arg0
     */
    public SFSubmitException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SFSubmitException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SFSubmitException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
