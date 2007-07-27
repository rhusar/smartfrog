/*
 * Created on May 5, 2005
 *
 */
package org.smartfrog.services.sfinterface;


public class SFMultiHostSubmitException extends Exception {

    /**
     *
     */
    public SFMultiHostSubmitException() {
        super();
    }

    /**
     * @param arg0
     */
    public SFMultiHostSubmitException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SFMultiHostSubmitException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SFMultiHostSubmitException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public void addFailedHost(String host, Throwable e) {
// to do
    }

    /**
     * Returns a map of host - SFSubmitException for all failed hosts.
     *
     * @return a list of failed hosts
     */
    public java.util.Map getFailedHosts() {
        return null;
    }

}
