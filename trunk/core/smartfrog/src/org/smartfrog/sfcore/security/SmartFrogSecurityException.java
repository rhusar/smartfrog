package org.smartfrog.sfcore.security;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.security.GeneralSecurityException;

/**
 * Convert any GeneralSecurityException into a subclass of SmartFrogException.
 *
 */

public class SmartFrogSecurityException extends SmartFrogException {

    public SmartFrogSecurityException(final GeneralSecurityException cause) {
        super(cause);
    }
}
