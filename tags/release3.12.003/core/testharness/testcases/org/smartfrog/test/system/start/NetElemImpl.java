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

package org.smartfrog.test.system.start;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;


// make the class abstract as the evaluate() method must be completed correctly before use
public abstract class NetElemImpl extends CompoundImpl implements Compound,
    NetElem, Remote {
    protected Context outputs;
    protected Reference nameRef;
    protected String name;
    protected Vector currentValues = new Vector();

    // need a thread to decouple incoming RPC thread in from the RPCs out
    // otherwise the RPCs will block until the entire NetElem tree has been traversed
    protected Thread outputter = null;

    // standard constructor
    protected NetElemImpl() throws RemoteException {
    }

    protected void addValue(int i) {
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

                    for (Enumeration o = outputs.keys(); o.hasMoreElements();) {
                        Object named = o.nextElement();

                        if (outputs.get(named) instanceof Output) {
                            Output out = (Output) outputs.get(named);

                            try {
                                out.output(value);
                            } catch (Exception e) {
                                //Logger.log("Uncaught Exception " + e);
                                if (sfLog().isErrorEnabled()){
                                  sfLog().error("Uncaught Exception ", e);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                sfLog().error("Exception ", e);
            }
        }
    }

    // NetElem  methods
    public synchronized void doit(String from, int value) {
        addValue(evaluate(from, value)); // set correctly in the sub classes!
    }

    // Method that must be over-ridden in each subclass
    protected int evaluate(String from, int value) {
        return value; // by default do nothing
    }

    // lifecycle methods
    public void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            // get the list of outputs
            super.sfDeploy();
            outputs = ((Prim) sfResolveHere("outputs")).sfContext();
            nameRef = sfCompleteName();
            name = nameRef.toString();

            // start the thread here because we need to make sure that when the
            // constants and generators issue their values - triggered in sfStart()
            // the outputer is waiting to pass them on.
            outputter = new Outputter();
            outputter.start();
            sfLog().info(name + " deployed");
        } catch (SmartFrogException sfex) {
            // add the context in case of failure
            sfex.put("sfDeployFailure", sfContext);

            // trigger termination of component
            try {
                Reference componentName = sfCompleteName();
                terminateComponent(this, sfex, componentName);
                throw sfex;
            } catch (Throwable t) { // the call to sfCompleteName has failed
                terminateComponent(this, sfex, null);
                throw sfex;
            }
        } catch (Throwable t) {
            // trigger termination of component
            try {
                Reference componentName = sfCompleteName();
                terminateComponent(this, t, componentName);
                throw new SmartFrogDeploymentException(t, this);
            } catch (Throwable th) { // the call to sfCompleteName has failed
                terminateComponent(this, t, null);
                throw new SmartFrogDeploymentException(t, this);
            }
        }
    }

    public void sfStart() throws SmartFrogException, RemoteException {
        try {
            super.sfStart();
        } catch (Exception ex) {
            // any exception causes termination
            Reference componentName = sfCompleteNameSafe();
            sfTerminate(TerminationRecord.abnormal("Compound sfStart failure: " + ex,
                    componentName));
        }
    }

    public void sfTerminateWith(TerminationRecord tr) {
        try {
            if (outputter != null) {
                outputter.stop();
            }
        } catch (Exception e) {
        }

        sfLog().info(name + " has terminated with " + tr.toString());
        super.sfTerminateWith(tr);
    }

    class Outputter extends Thread {
        public void run() {
            try {
                doOutputs();
            } finally {
                try {
                    sfLog().info(sfCompleteNameSafe() +
                        " Thread terminated ");
                } catch (Exception e) {
                }
            }
        }
    }
}
