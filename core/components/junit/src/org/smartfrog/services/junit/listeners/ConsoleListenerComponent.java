/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.junit.TestInfo;
import org.smartfrog.services.junit.ThrowableTraceInfo;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.PrintStream;
import java.rmi.RemoteException;

/**
 * The basic listener for trouble; the console listener. created 14-May-2004
 * 15:42:31
 */

public class ConsoleListenerComponent extends PrimImpl
        implements ConsoleListener {

    /**
     * cache the prinstream so that when system.out is used to capture output we
     * still go to the original console (i.e. no recursion)
     */

    PrintStream outstream = System.out;

    public ConsoleListenerComponent() throws RemoteException {
    }

    public void addError(TestInfo test) throws RemoteException {
        logTrouble("Error:", test);
    }

    public void addFailure(TestInfo test) throws RemoteException {
        logTrouble("Failure:", test);
    }

    public void endTest(TestInfo test) throws RemoteException {
        log("   ending " + test.getClassname() + " on " + test.getHostname());

    }

    public void startTest(TestInfo test) throws RemoteException {
        log("Starting " + test.getClassname() + " on " + test.getHostname());
    }

    private void log(String s) {
        outstream.println(s);
    }

    private void flush() {
        outstream.flush();
    }

    private void logTrouble(String message, TestInfo test) {
        StringBuffer buffer = new StringBuffer(128);
        buffer.append(message);
        buffer.append(test.getClassname());
        buffer.append(" on ");
        buffer.append(test.getHostname());
        buffer.append('\n');
        buffer.append(test.getText());
        log(buffer.toString());
        flush();
        logTrace(test.getFault());
    }

    private void logTrace(ThrowableTraceInfo fault) {
        if (fault == null) {
            return;
        }
        log(fault.getClassname());
        log(fault.getMessage());
        StackTraceElement stack[] = fault.getStack();
        for (int i = 0; i < stack.length; i++) {
            log(stack[i].toString());
        }
        flush();
        //recurse
        logTrace(fault.getCause());
    }
}
