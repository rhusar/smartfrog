package org.smartfrog.services.slp;

import java.util.Enumeration;

public interface ServiceLocationEnumeration extends Enumeration {

/**
 * Returns the next value or block until it becomes available.
 */
  public abstract Object next() throws ServiceLocationException;
}
