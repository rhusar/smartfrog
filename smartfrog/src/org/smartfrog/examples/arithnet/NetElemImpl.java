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

package org.smartfrog.examples.arithnet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;
import org.smartfrog.sfcore.common.Context;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;


/**
 *  Defines the basic methods for the netcomponents.
 *
 *  Make the class abstract as the evaluate() method must be completed correctly
 *  before use.
 */
public abstract class NetElemImpl extends CompoundImpl implements Compound,
    NetElem, Remote {
    /**
     * Outputs generatd by the component.
     */
    Context outputs;
    /**
     * Name reference
     */
    Reference nameRef;
    /**
     *
     */
    String name;
    /**
     * Current Values.
     */
    Vector currentValues = new Vector();

    /** Need a thread to decouple incoming RPC thread in from the RPCs out
     * otherwise the RPCs will block until the entire NetElem tree has been
     * traversed
     */
    Thread outputer = null;

    /**
     * Constructs NetElemImpl object.
     *
     * @throws RemoteException In case of network/rmi error.
     */
    public NetElemImpl() throws RemoteException {
        super();
    }

    /**
     * Adds the given value to the current values and notifys all.
     *
     * @param i value to be added
     */
    protected void addValue(int i) {
        synchronized (currentValues) {
            currentValues.addElement(new Integer(i));
            currentValues.notifyAll();
        }
    }

    /**
     * Outputs all the current values.
     */
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

                    for (Enumeration o = outputs.keys(); o.hasMoreElements();) {
                        Object keyName = o.nextElement();

                        if (outputs.get(keyName) instanceof Output) {
                            Output out = (Output) outputs.get(keyName);

                            try {
                                out.output(value);
                            } catch (Exception e) {
                                if (sfLog().isErrorEnabled()){
                                  sfLog().error("Uncaught Exception: ", e);
                                }
                                //Logger.log("Uncaught Exception: ", e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                sfLog().error(e);
            }
        }
    }

    // NetElem  methods
    /**
     * Interface method of NetElem
     * @param from place holder for input
     * @param value interger value
     */
    public synchronized void doit(String from, int value) {
        addValue(evaluate(from, value)); // set correctly in the sub classes!
    }

    /**
     * Method that must be over-ridden in each subclass.
     *
     * @param from the string representaion of the component that evaluates
     * @param value the value
     *
     * @return the evaluated value
     */
    protected int evaluate(String from, int value) {
        return value; // by default do nothing
    }

    // lifecycle methods
    /**
     * Deploys the component.
     * @throws SmartFrogException if framework is unable to deploy the
     * component
     * @throws RemoteException if remote or network error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            // get the list of outputs
            super.sfDeploy();
            outputs = ((Prim) sfResolveHere("outputs")).sfContext();
            nameRef = sfCompleteName();
            name = nameRef.toString();

            // start the thread here because we need to make sure that when the
            // constants and generators issue their values - triggered in sfStart()
            // the outputer is waiting to pass them on.
            outputer = new Outputer();
            outputer.start();
            System.out.println(name + " deployed");
        } catch (SmartFrogException sfex) {
            // add the context in case of failure
            sfex.put(SmartFrogCoreKeys.SF_DEPLOY_FAILURE, this.sfContext);

            // trigger termination of component
            Reference refName = sfCompleteNameSafe();
            terminateComponent(this, sfex, refName);
            throw sfex;
        }
    }
    /**
     * Starts the component.
     * @throws SmartFrogException if framework is unable to start the
     * component
     * @throws RemoteException if remote or network error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
        } catch (SmartFrogException sfex) {
            // add the context in case of failure
            sfex.put(SmartFrogCoreKeys.SF_START_FAILURE, this.sfContext);

            // trigger termination of component
            Reference refName = sfCompleteNameSafe();
            terminateComponent(this, sfex, refName);
            throw sfex;
        }
    }
    /**
     * Terminates the component.
     * @param tr information about the component termination
     */

    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            if (outputer != null) {
                outputer.stop();
            }
        } catch (Exception e) {
        }

        System.out.println(name + " has terminated with " + tr.toString());
        super.sfTerminateWith(tr);
    }
    /**
     * Inner class which extends thread.
     */
    class Outputer extends Thread {
        /**
         * calls doOutputs in a thread.
         */
        public void run() {
            try {
                doOutputs();
            } finally {
                try {
                    System.out.println(sfCompleteName() +
                        " Thread terminated ");
                } catch (Exception e) {
                }
            }
        }
    }
}
