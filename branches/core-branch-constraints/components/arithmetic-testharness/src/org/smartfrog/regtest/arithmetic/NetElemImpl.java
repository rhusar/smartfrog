/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import java.lang.*;
import java.util.*;

import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.tools.testharness.TestHelper;

// make the class abstract as the evaluate () method must be completed correctly before use

public abstract class NetElemImpl extends CompoundImpl implements Compound, NetElem, Remote {

    Context outputs;
    Reference nameRef;
    String name;
    Vector currentValues = new Vector();
    int currentInvocation = 0;
    int maxInvocations = 99999999;

    protected void addValue(int i) {
        currentInvocation++;
        System.out.println("                                       (addValue: Invo# " + currentInvocation + ", value " + i + ", maxInvoc " + maxInvocations + ")");
        if (currentInvocation > maxInvocations) {
            TestHelper.printSFStartDone(System.out, false);
        }
        synchronized (currentValues) {
            currentValues.addElement(new Integer(i));
            currentValues.notify();
        }
    }

    protected void doOutputs() {
        while (true) {
            try {

                synchronized (currentValues) {
                    if (currentValues.size() == 0) {
                        currentValues.wait();
                    }
                }
                while (!currentValues.isEmpty()) {
                    int value;
                    synchronized (currentValues) {
                        value = ((Integer) currentValues.firstElement()).intValue();
                        currentValues.removeElementAt(0);
                    }
                    synchronized (outputs) {
                        outputs = ((Prim) sfResolve("outputs")).sfContext();

                        for (Enumeration o = outputs.keys(); o.hasMoreElements();) {
                            Object name = o.nextElement();
                            if (outputs.get(name) instanceof Output) {
                                Output out = (Output) outputs.get(name);
                                try {
                                    //System.out.println("output @ NETELEMIMPL ===============>  " + value);
                                    out.output(value);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // need a thread to decouple incoming RPC thread in from the RPCs out
    // otherwise the RPCs will block until the entire NetElem tree has been traversed
    Thread outputer = null;

    class Outputer extends Thread {
        public void run() {
            try {
                doOutputs();
            } finally {
                try {
                    System.out.println(sfCompleteName() + " Thread terminated ");
                } catch (Exception e) {
                }
            }
        }
    }

    // standard constructor
    public NetElemImpl() throws RemoteException {
        super();
    }

    // NetElem  methods
    public synchronized void doit(String from, int value) {
        currentInvocation++;
        //System.out.println("Invocation number " + currentInvocation);
        if (currentInvocation > maxInvocations) {
            TestHelper.printSFStartDone(System.out, false);
        }
        addValue(evaluate(from, value)); // set correctly in the sub classes!
    }

    // Method that must be over-ridden in each subclass
    protected int evaluate(String from, int value) {
        return value; // by default do nothing
    }

    // lifecycle methods
    public void sfDeploy() throws SmartFrogException, RemoteException {
        // get the list of outputs
        super.sfDeploy();
        outputs = ((Prim) sfResolveHere("outputs")).sfContext();
        nameRef = sfCompleteName();
        name = nameRef.toString();
        try {
            maxInvocations = ((Number) sfResolve("maxInvocations")).intValue();
        } catch (Exception e) {
            // use large default...
        }

        // start the thread here because we need to make sure that when the
        // constants and generators issue their values - triggered in sfStart()
        // the outputer is waiting to pass them on.
        outputer = new Outputer();
        outputer.start();
        //System.out.println(name + " deployed");
    }

    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //System.out.println(name + " started");
    }

    public void sfTerminateWith(TerminationRecord tr) {
        try {
            if (outputer != null) outputer.stop();
        } catch (Exception e) {
        }
        System.out.println(name + " has terminated with " + tr.toString());
        TestHelper.printSFStopDone(System.out, true);
        super.sfTerminateWith(tr);
        System.out.println("sfTerminateWith ending !!!"); //DEBUG
    }
}
