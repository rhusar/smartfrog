/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */
package org.smartfrog.test.system.sfcore.languages.cdl.execute;

import org.smartfrog.sfcore.prim.PrimHook;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;

import java.rmi.RemoteException;

/**
 * This is a class to wait() on to wait for terminations. It will also signal
 * with a timeout in case termination took longer than allowed.
 */
public class TerminationHandler implements PrimHook {

    private boolean normalTerminationExpected;

    String expectedMessageText;

    Prim target;
    private Reference targetName;
    TerminationRecord terminationRecord;


    /**
     * @param normalTerminationExpected (flag to say normal termination is
     *                                  expected)
     * @param expectedMessageText       text to look for in the message (can be
     *                                  null)
     */
    public TerminationHandler(boolean normalTerminationExpected,
                              String expectedMessageText) {
        this.normalTerminationExpected = normalTerminationExpected;
    }

    /**
     * sfHookAction for terminating
     *
     * @param source prim component
     * @param record TerminationRecord object
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *          in case of any error
     */
    public void sfHookAction(Prim source, TerminationRecord record)
            throws SmartFrogException {
        try {
            Reference name = null;
            name = source.sfCompleteName();
            //check for a match
            if (targetName.equals(name)) {
                //and if so, save the record and notify our caller
                terminationRecord = record;
                this.notify();
            }
        } catch (RemoteException remoteTrouble) {
            throw new SmartFrogException(remoteTrouble);
        }
    }

    public void setTarget(Prim target) throws RemoteException {
        this.target = target;
        targetName = target.sfCompleteName();
    }

    /**
     * wait for a node to terminate within a given time.
     *
     * @param prim
     * @param timeoutSeconds
     */
    public void waitForTermination(Prim prim, int timeoutSeconds)
            throws SmartFrogException {
        PrimImpl.sfTerminateWithHooks.addHook(this);
        try {
            try {
                this.wait(timeoutSeconds * 1000);
            } catch (InterruptedException e) {
                throw new SmartFrogException("timeout interrupted");
            }
            //at this point we have terminated.
            if (terminationRecord == null) {
                throw new SmartFrogException("termination record is null");
            }
            boolean normalEnd = TerminationRecord.NORMAL
                    .equals(terminationRecord.errorType);
            String text = terminationRecord.toString();
            if (normalTerminationExpected!=normalEnd) {
                throw new SmartFrogException("unexpected outcome:"
                    +terminationRecord);
            }
            if (expectedMessageText != null
                    && text.indexOf(expectedMessageText) <= 0) {
                throw new SmartFrogException(
                        "failed to find [" + expectedMessageText + "] in " + text);
            }
        } finally {
            unregisterWithPrimImpl();
        }


    }

    private void unregisterWithPrimImpl() {
        try {
            PrimImpl.sfTerminateWithHooks.removeHook(this);
        } catch (SmartFrogLifecycleException ignored) {
            //not possible on the current impl; ignore it
        }
    }

    /**
     * Wait for an application terminating
     *
     * @param prim                      component to wait on. Must be local for
     *                                  proper waiting to work
     * @param timeoutSeconds
     * @param normalTerminationExpected
     * @param expectedText
     *
     * @throws java.rmi.RemoteException
     * @throws InterruptedException
     */
    public static void awaitTermination(Prim prim,
                                        int timeoutSeconds,
                                        boolean normalTerminationExpected,
                                        String expectedText) throws
            RemoteException, SmartFrogException {
        if (!(prim instanceof PrimImpl)) {
            throw new SmartFrogException("Only local process components can be waited on");
        }
        TerminationHandler terminateHandler = new TerminationHandler(
                normalTerminationExpected,
                expectedText);
        terminateHandler.waitForTermination(prim, timeoutSeconds);
    }
}
