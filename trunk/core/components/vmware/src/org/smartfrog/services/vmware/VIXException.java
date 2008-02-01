package org.smartfrog.services.vmware;

import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Exception thrown when error within the VIX API occur.
 */
public class VIXException extends SmartFrogException {
    /**
     * Gets the error code.
     * @return The error code.
     */
    public long getErrorCode() {
        return iErrorCode;
    }

    /**
     * Sets the error code.
     * @param iErrorCode The error code.
     */
    public void setErrorCode(long iErrorCode) {
        this.iErrorCode = iErrorCode;
    }
    // the code of the vix error
    private long iErrorCode = 0;

    /**
     * Constructs a VIXException with error code 0.
     */
    public VIXException() {
        super();
    }

    /**
     * Constructs a VIXException with a message and error code 0.
     * @param inMessage Exception message.
     */
    public VIXException(String inMessage) {
        super(inMessage);
    }

    /**
     * Constructs a VIXException with a cause and error code 0.
     * @param inCause Exception cause.
     */
    public VIXException(Throwable inCause) {
        super(inCause);
    }

    /**
     * Constructs a VIXException with an error code.
     * @param inErrorCode VMware VIX error code.
     */
    public VIXException(long inErrorCode) {
        super();
        this.iErrorCode = inErrorCode;
    }

    /**
     * Constructs a VIXException with a message and a cause.
     * @param inMessage Exception message.
     * @param inCause Exception cause.
     */
    public VIXException(String inMessage, Throwable inCause) {
        super(inMessage, inCause);
    }

    /**
     * Constructs a VIXException with a message and an error code.
     * @param inMessage Exception message.
     * @param inError VMware VIX error code.
     */
    public VIXException(String inMessage, long inError) {
        super(inMessage);
        this.iErrorCode = inError;
    }

    /**
     * Constructs a VIXException with an error code and a cause.
     * @param inError VMware VIX error code.
     * @param inCause Exception cause.
     */
    public VIXException(int inError, Throwable inCause) {
        super(inCause);
        this.iErrorCode = inError;
    }

    /**
     * Constructs a VIXException with a message, an error code and a cause.
     * @param inMessage Exception message.
     * @param inError VMware VIX error code.
     * @param inCause Exception cause.
     */
    public VIXException(String inMessage, long inError, Throwable inCause) {
        super (inMessage, inCause);
        this.iErrorCode = inError;
    }

    /**
     * Gets a string representation for this exception.
     * @return The string representation for this exception.
     */
    public String toString() {
        return toString(", ");
    }

    /**
     * Gets a string representation for this exception.
     * @param nm Message separator.
     * @return The string representation for this exception.
     */
    public String toString(String nm) {
        return "VIXException:: error code: " + iErrorCode + nm + super.toString(nm);
    }
}
