/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
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
        iErrorCode = inErrorCode;
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
        iErrorCode = inError;
    }

    /**
     * Constructs a VIXException with an error code and a cause.
     * @param inError VMware VIX error code.
     * @param inCause Exception cause.
     */
    public VIXException(int inError, Throwable inCause) {
        super(inCause);
        iErrorCode = inError;
    }

    /**
     * Constructs a VIXException with a message, an error code and a cause.
     * @param inMessage Exception message.
     * @param inError VMware VIX error code.
     * @param inCause Exception cause.
     */
    public VIXException(String inMessage, long inError, Throwable inCause) {
        super (inMessage, inCause);
        iErrorCode = inError;
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
