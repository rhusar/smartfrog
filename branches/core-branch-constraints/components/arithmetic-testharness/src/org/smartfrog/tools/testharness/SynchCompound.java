package org.smartfrog.tools.testharness;

/** Configuration variables of a Compound with synchronized
 * termination. 
 *
 */
public interface SynchCompound {

  /** A tag to represent the scheduler that this compound uses. */
  static public final String SCHEDULER="scheduler";

  /** A string to represent a hint for the scheduler to decide on
   * progress. */
  static public final String TERMINATE_TAG="terminateTag";

}
