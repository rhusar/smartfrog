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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.rmi.RemoteException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.InputStream;

/**
 * Contains methods for helping components; a factoring out of common functionality.
 * Component helpers must be bound to Prim classes before use.
 * created 18-May-2004 11:26:15
 */

public class ComponentHelper {

    private Prim owner;

    public ComponentHelper(Prim owner) {
        this.owner = owner;
    }

    public Prim getOwner() {
        return owner;
    }

    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * Note that we detach before terminating; this stops our timely end propagating.
     * @todo what about TerminatorThread; does that do this better?
     */
    public void targetForTermination() {
        //spawn the thread to terminate normally
        Runnable terminator = new Runnable() {
            public void run() {
                Reference name;
                try {
                    name = owner.sfCompleteName();
                } catch (RemoteException e) {
                    name = null;

                }
                try {
                    owner.sfDetachAndTerminate(TerminationRecord.normal(name));
                } catch (RemoteException e) {
                    //we cannot rethrow this as it is not in the signature of the interface
                    logIgnoredException(e);
                }
            }
        };

        new Thread(terminator).start();
    }

    /**
     * get the relevant logger for this component.
     * When logging against a remote class, this is probably the classname of the proxy.
     * @return
     */
    public Log getLogger() {
        return LogFactory.getOwnerLog(owner);
    }

    /**
     * ignore an exception by logging it at the fine level.
     * @param thrown
     */
    public void logIgnoredException(Throwable thrown) {
        Log log=getLogger();
        log.debug("ignoring ",thrown);
    }

    /**
     * Returns the complete name for any component from the root of the
     * application and does not throw any exception. If an exception is
     * thrown it will return null
     *
     * @return reference of attribute names to this component or a null
     */
    public Reference completeNameOrNull() {
        try {
            return owner.sfCompleteName();
        } catch (Throwable thr) {
            return null;
        }
    }

    /**
     * Returns the complete name for any component from the root of the
     * application and does not throw any exception. If an exception is
     * thrown it will return a new empty reference.
     *
     * @return reference of attribute names to this component or an empty reference
     */
    public Reference completeNameSafe() {
        Reference ref=completeNameOrNull();
        if(ref==null) {
            return new Reference();
        } else {
            return ref;
        }
    }


    /**
     * Returns the complete name for any component from the root of the
     * application and does not throw any exception. If an exception is
     * thrown it will return a new empty reference.
     *
     * @return reference of attribute names to this component or an empty reference
     */
    public static Reference completeNameSafe(Prim owner) {
        try {
            return owner.sfCompleteName();
        } catch (Throwable thr) {
            // TODO: log a message to indicate that sfCompleteName failed!
            return new Reference();
        }
    }

    /**
     * load a resource using the classpath of the component
     * at question.
     *
     * @param resourcename name of resource on the classpath
     * @return an input stream if the resource was found and loaded
     * @throws SmartFrogException if the resource is not on the classpath
     */
    public InputStream loadResource(String resourcename)
            throws SmartFrogException, RemoteException {
        String targetCodeBase = getCodebase();

        InputStream in = SFClassLoader.getResourceAsStream(resourcename, targetCodeBase, true);
        if (in == null) {
            throw new SmartFrogException("Not found: " + resourcename);
        }
        return in;
    }

    /**
     * get the codebase of a component
     * @return
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     */
    public String getCodebase() throws SmartFrogResolutionException,
            RemoteException {
        return (String) owner.sfResolve(SmartFrogCoreKeys.SF_CODE_BASE);
    }

    /**
     * find an ancestor of a given type
     * @param node node to look for
     * @param interfaceName full name of interface to look for
     * @param depth: 0 means dont look upwards, -1 means indefinite.
     * @return a parent or null for no match
     * @throws java.rmi.RemoteException
     */
    public static Prim findAncestorImplementing(Prim node, String interfaceName, int depth) throws RemoteException {
        if (depth == 0 || node == null) {
            return null;
        }
        Prim parent = node.sfParent();
        if(parent==null) {
            //we run out here
            return null;
        }
        if ( implementsInterface(parent.getClass(),interfaceName)) {
            return parent;
        }
        return findAncestorImplementing(parent, interfaceName, depth - 1);
    }

    /**
     * find an ancestor of the owner that implements this class.
     *
     * @param interfaceName full name of interface to look for
     * @param depth: 0 means dont look upwards, -1 means indefinite.
     * @return a parent or null for no match
     * @throws RemoteException
     */
    public Prim findAncestorImplementing(String interfaceName, int depth) throws RemoteException {
        return findAncestorImplementing(owner, interfaceName, depth);
    }

    /**
     * recursive search for interface inheritance
     * @param clazz
     * @param interfaceName
     * @return
     */
    public static boolean implementsInterface(Class clazz,String interfaceName ) {
        if(clazz==null) {
            return false;
        }
        Class[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getName().equals(interfaceName)) {
                return true;
            }
        }
        return implementsInterface(clazz.getSuperclass(),interfaceName);
    }

}
