package org.smartfrog.sfcore.utils;

import java.rmi.Remote;

/**
 * these attributes are used by components that want to have workflow semantics
 * The actual fetching and interpretation is handled in
 * {@link ComponentHelper#sfSelfDetachAndOrTerminate(String, String, org.smartfrog.sfcore.reference.Reference, Throwable)} 
 */
public interface ShouldDetachOrTerminate extends Remote {

    /**
     * Flag indicating that the component should terminate after doing its work.
     * {@value}
     */
    public static final String ATTR_SHOULD_TERMINATE = "sfShouldTerminate";

    /**
     * Flag to indicate the component should terminate quietly after doing its work.
     * {@value}
     */
    public static final String ATTR_SHOULD_TERMINATE_QUIETLY = "sfShouldTerminateQuietly";

    /**
     * Property indicating that after starting, the component should detach from its
     * parent.
     * {@value}
     */
    public static final String ATTR_SHOULD_DETACH = "sfShouldDetach";
}
