package org.smartfrog.services.assertions;

import java.rmi.Remote;

/**

 */
public interface Fail extends Remote {
    /** {@value} */
    String ATTR_MESSAGE="message";

    /** {@value} */
    String ATTR_NORMAL="normal";

    /** {@value} */
    String ATTR_CONDITION="condition";

    /** {@value} */
    String ATTR_DELAY="delay";

    /** {@value} */
    String ATTR_DETACH="detach";

    /** {@value} */
    String ATTR_NOTIFY ="notifyParent";
}
