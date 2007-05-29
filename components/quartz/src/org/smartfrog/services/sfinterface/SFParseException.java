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
public class SFParseException extends Exception {

    /**
     *
     */
    public SFParseException() {
        super();
    }

    /**
     * @param arg0
     */
    public SFParseException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SFParseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public SFParseException(Throwable arg0) {
        super(arg0);
    }
}
