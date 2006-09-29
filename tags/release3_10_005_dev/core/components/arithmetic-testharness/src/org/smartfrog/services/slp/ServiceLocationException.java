package org.smartfrog.services.slp;

import java.util.Locale;

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
 * @Returns: The integer error code.
 */
  public short getErrorCode() {
    return errorCode;
  }
/**
 * Return the localized message, in the default locale.
 */
  public String getMessage() {
    return super.getMessage();
  }


}
