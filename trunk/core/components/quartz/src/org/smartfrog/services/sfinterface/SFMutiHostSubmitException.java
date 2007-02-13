/*
 * Created on May 5, 2005
 *
 */
package org.smartfrog.services.sfinterface;


public class SFMutiHostSubmitException extends Exception {

    /**
     *
     */
    public SFMutiHostSubmitException() {
        super();
    }

    /**
     * @param arg0
     */
    public SFMutiHostSubmitException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SFMutiHostSubmitException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SFMutiHostSubmitException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public void addFailedHost(String host, Throwable e) {
// to do
    }

    /**
     * Returns a map of host - SFSubmitException for all failed hosts.
     *
     * @return
     */
    public java.util.Map getFailedHosts() {
        return null;
    }

}
