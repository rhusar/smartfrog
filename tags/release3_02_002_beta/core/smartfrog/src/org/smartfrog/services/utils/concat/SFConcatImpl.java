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

package org.smartfrog.services.utils.concat;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;


/**
 * Service Resource Manager that mediates between FF and Utility Resource
 * Manager.
 */
public class SFConcatImpl extends PrimImpl implements Prim, SFConcat {
    /** Shows debug messages? */
    private boolean debug = false;
    /** Create reference. */
    private boolean createReference = false;
    /** StringBuffer for SFServiceResourceManage. */
    public StringBuffer concat = new StringBuffer();
    /** Componentdescription. */
    private ComponentDescription stringCompDesc = null;

    /**
     * Constructor for the SFServiceResourceManagerImpl object.
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public SFConcatImpl() throws RemoteException {
    }

    // LifeCycle

    /**
     * sfDeploy.
     *
     * @throws SmartFrogException In case of error while deployment
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
        // TODO: Exception handling mechanism need to be revisited
        super.sfDeploy();

        try {
            readSFAttributes();

            if (!createReference) {
                try {
                    this.sfAddAttribute(this.ATR_CONCAT, concat.toString());
                } catch (Exception ex) {
                    error("SFConcat.sfDeploy", ex.toString());

                    if (debug) {
                        this.sfAddAttribute(this.ATR_CONCAT, "error");
                    }
                }
            } else {
                try {
                    this.sfAddAttribute(this.ATR_REFERENCE,
                        Reference.fromString(concat.toString()));
                } catch (Exception ex) {
                    error("SFConcat.sfDeploy", ex.toString());

                    if (debug) {
                        this.sfAddAttribute(this.ATR_REFERENCE, "error");
                    }
                }
            }

            if (debug) {
                if (createReference) {
                    log("SFConcat.sfDeploy",
                        "reference solved: " + this.sfResolve(this.REF_REFERENCE) +
                        ", reference: " +
                        this.sfContext.get(this.ATR_REFERENCE));
                } else {
                    log("SFConcat.sfDeploy",
                        "concat: " + this.sfContext.get(this.ATR_CONCAT));
                }
            }

            //log("sfDeploy","SFConcatImpl sfDeploy finished");
        } catch (Throwable t) {
            // TODO: Need to be revisited
            throw new SmartFrogDeploymentException(t, this);
        }
    }

    /**
     * sfStart.
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        //log("sfStart","SFConcatImpl sfStart entered");
        super.sfStart();

        //log("sfStart","SFConcatImpl sfStart finished");
    }

    /**
     * sfTerminate.
     *
     * @param t TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        super.sfTerminateWith(t);
    }

    // End LifeCycle
    // Read Attributes from description

    /**
     * Read Attributes from description.
     *
     * @throws Exception error while readng attirbutes
     */
    private void readSFAttributes() throws Exception {
        //
        // Optional attributes.
        try {
            debug = sfResolve(REF_DEBUG, debug, false);
            createReference = sfResolve(REF_CREATE_REFERENCE, createReference, false);
        } catch (SmartFrogResolutionException e) {
            error("readSFAttributes",
                "Failed to read optional attribute: " + e.toString());
            throw e;
        }

        //
        // Mandatory attributes.
        try {
            stringCompDesc = sfResolve(REF_STRING, stringCompDesc, true);
        } catch (SmartFrogResolutionException e) {
            error("readSFAttributes",
                "Failed to read mandatory attribute: " + e.toString());
            throw e;
        }

        concat.append(createConcatFromContext(stringCompDesc));
    }

    /**
     * Concats the context.
     *
     * @param compDesc componentdescription
     */
    private String createConcatFromContext(ComponentDescription compDesc) {
        Object value = null;
        StringBuffer auxString = new StringBuffer();

        //System.out.println("reading Cmd Attributes...");
        for (Enumeration e = compDesc.getContext().elements();
                e.hasMoreElements();) {
            value = e.nextElement();

            //if (value instanceof String) {
            try {
                if (value != null) {
                    if (value instanceof Reference) {
                        try {
                            // to resolve LAZY
                            ((Reference) value).setEager(true);
                            value = this.sfResolve((Reference) value);
                        } catch (Exception ex) {
                            error("createConcatFromContext", ex.toString());
                        }
                    }

                    if (value instanceof java.net.InetAddress) {
                        auxString.append(((java.net.InetAddress) value).
                    getCanonicalHostName());
                    } else {
                        auxString.append(value.toString());
                    }
                }
            } catch (Exception ex) {
            }

            //}
        }

        //for
        return auxString.toString();
    }

    /**
     * Logs error mesasge at the standard err stream.
     * @param method Name of the method
     * @param message Error Message
     */
    private void error(String method, String message) {
        if (debug) {
            System.err.println(method + " [" + (new Date()).toString() + "]> " +
                message);
        }
    }

    /**
     * Logs mesasge at the standard out stream.
     * @param method Name of the method
     * @param message Log message
     */
    private void log(String method, String message) {
        if (debug) {
            System.out.println(method + " [" + (new Date()).toString() + "]> " +
                message);
        }
    }

    /**
     * Logs exception with stack trace at the standard err stream.
     * @param method Name of the method
     * @param exception The exception object
     */
    private void exception(String method, Throwable exception) {
        if (debug) {
            exception.printStackTrace();
        }
    }

    /**
     * Appends the object.
     *
     * @param obj Object
     */
    public void append(Object obj) {
        concat.append(obj);
        try {
          this.sfAddAttribute(this.ATR_CONCAT, concat.toString());
        } catch (Exception ex){
          //Ignore
        }
    }

    /** Returns the textual representaion.
     *
     * @return textual representation
     */
    public String toString() {
        concat.append(createConcatFromContext(stringCompDesc));

        return concat.toString();
    }
}
