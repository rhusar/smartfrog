/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.slp;


public class ServiceLocationException extends java.lang.Exception {

/**
 * The location service did not have a registration in the language locale
 * of the request, although it did have one in another language locale. 
 * Not properly used throughout the implementation for the moment
 *
 * @author Guillaume Mecheneau
 */
  public static final short LANGUAGE_NOT_SUPPORTED = 1;

/**
 * An error occured while parsing a URL, attribute list, or other part of a service location message.
 */
 public static final short PARSE_ERROR = 2;

/**
 * Upon registration, this error is returned if the URL is invalid or
 * if some other problem occurs with the registration.
 * Upon deregistration it is also returned if the URL is not registered.
 */
 public static final short INVALID_REGISTRATION = 3;

/**
 * An attempt was made to register in a scope not supported.
 */
 public static final short SCOPE_NOT_SUPPORTED = 4;

/**
 * Authentication was missing from a message that required it.
 */
 public static final short AUTHENTICATION_ABSENT = 6;

/**
 * Authentication failed on a message.
 */
 public static final short AUTHENTICATION_FAILED = 7;

/**
 * An attempt was made to update a nonexisting registration.
 */
 public static final short INVALID_UPDATE = 13;

/**
 * The service URL lifetime was rejected by the directory agent.
 */
 public static final short INVALID_LIFETIME = 8;

/**
 * Operation isn't implemented.
 */
 public static final short NOT_IMPLEMENTED = 9;

/**
 * Initialization of the network failed.
 */
 public static final short NETWORK_INIT_FAILED = 10;

/**
 * A TCP connection timed out.
 */
 public static final short NETWORK_TIMED_OUT = 5;

/**
 * An error occured during networking.
 */
 public static final short NETWORK_ERROR = 12;

/**
 * An error occured in the client-side code.
 */
 public static final short INTERNAL_SYSTEM_ERROR = 11;

/**
 * Registration failed to match the service type template or schema.
 */
 public static final short TYPE_ERROR = 14;

/**
 * Packet size overflow on transmission.
 */
 public static final short BUFFER_OVERFLOW = 15;
 short errorCode;

/**
 * Service Location Exception
 */
  public ServiceLocationException() {}
  public ServiceLocationException(String s) {
    super(s);
  }
  public ServiceLocationException(short errorCode) {
    this.errorCode = errorCode;
  }
/**
 * Return the error code.
 *
 * @return The integer error code.
 */
  public short getErrorCode() {
    return errorCode;
  }
/**
 * @return the localized message, in the default locale.
 */
  public String getMessage() {
    return super.getMessage();
  }


}
