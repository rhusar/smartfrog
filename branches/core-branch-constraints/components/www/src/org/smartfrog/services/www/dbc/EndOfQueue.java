package org.smartfrog.services.www.dbc;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.PrimImpl;

/**
     * Special end-of-queue marker
 */
public class EndOfQueue extends QueuedFile {

    /**
     * Static end of queue marker instance
     */
    public static final EndOfQueue END_OF_QUEUE =new EndOfQueue();

    /**
     * No need to create any new instances, is there?
     */
    private EndOfQueue() {
        super(null,null);
    }


    /**
     * override the base class with an error.
     *
     * @param owner owner for logging and error context
     *
     * @throws SmartFrogDeploymentException if the IO failed
     */
    protected void execute(PrimImpl owner) throws SmartFrogDeploymentException {
        throw new SmartFrogDeploymentException("This is the end of the queue");
    }


    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return "END OF THE QUEUE";
    }
}
