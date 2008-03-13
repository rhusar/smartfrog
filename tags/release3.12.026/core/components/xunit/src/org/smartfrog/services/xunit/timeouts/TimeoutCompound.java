package org.smartfrog.services.xunit.timeouts;

import java.rmi.Remote;

/**
 * This component is a compound that kills its children after a defined time in
 * seconds if they have not already terminated of their own accord.
 *
 * It lets us add timeouts around a test suite or other component, without
 * adding lots of timeout logic to the component itself.
 *
 * Warning: Java does not really like threads being killed. The safest way to
 * work with this is to run the child components in their own processes.
 <pre>
 TimeoutCompoundSchema extends Schema {
 failOnTimeout extends Boolean;
 //message to get logged at info level
 timeoutMessage extends String;
 //timeout in seconds. If <=0 the timeout is disabled
 timeout extends Integer;
 }
 </pre>

 */
public interface TimeoutCompound extends Remote {


    /**
     * what is the timeout in milliseconds?
     * {@value}
     */
    String ATTR_TIMEOUT="timeout";

    /**
     * should the component fail with an abnormal termination if it timed out
     * {@value}
     */
    String ATTR_FAIL_ON_TIMEOUT="failOnTimeout";

    String ATTR_TIMEOUT_MESSAGE="timeoutMessage";


}
