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
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.SFDeployer;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.services.filesystem.FileSystem;

import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Contains methods for helping components; a factoring out of common functionality.
 * Component helpers must be bound to Prim classes before use.
 * created 18-May-2004 11:26:15
 */

public class ComponentHelper {

    private Prim owner;


    /**
     * construct a component helper and bind to a prim class
     * @param owner
     */
    public ComponentHelper(Prim owner) {
        this.owner = owner;
    }

    /**
     * return the prim that this helper is bound to
     * @return
     */
    public Prim getOwner() {
        return owner;
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
     * Method that can be invoked in any PrimImpl to trigger the detach and/or termination of a component
     * according to the values of the boolean attributes 'sfShouldDetach', 'sfShouldTerminate'
     * and 'sfShouldTerminateQuietly'
     * Example: new ComponentHelper(this).sfSelfDetachAndOrTerminate("normal","Copy ",this.sfCompleteNameSafe(),null);
     * @param terminationType - termination type, system recognized types are "normal", "abnormal" and "externalReferenceDead".
     * @param terminationMessage - description of termination
     * @param refId Reference - id of terminating component
     * @param thrown Thrown fault
     */
    public void sfSelfDetachAndOrTerminate(String terminationType,
                                           String terminationMessage,
                                           Reference refId, Throwable thrown) {
        /** Flag indicating detachment. */
        boolean shouldDetach = false;
        /** Flag indicating if the sfShouldDetach attribute was read. */
        boolean shouldDetachRead = false;
        /**
         * flag indicating termination.
         * whenever execution ends
         */
        boolean shouldTerminate = true;
        /** Flag indicating if the sfShouldTerminate attribute was read. */
        boolean shouldTerminateRead = false;

        /**
         * flag indicating termination.
         * whenever execution ends
         */
        boolean shouldTerminateQuietly = false;
        /** Flag indicating if the sfShouldTerminate attribute was read. */
        boolean shouldTerminateQuietlyRead = false;

        try {
            shouldTerminate = owner.sfResolve(
                    ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE, shouldTerminate, true);
            shouldTerminateRead = true;
        } catch (RemoteException ex) {
        } catch (SmartFrogResolutionException ex) {
        }

        try {
            shouldTerminateQuietly = owner.sfResolve(ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE_QUIETLY, shouldTerminateQuietly, true);
            shouldTerminateQuietlyRead = true;
        } catch (RemoteException ex) {
        } catch (SmartFrogResolutionException ex) {
        }

        try {
            shouldDetach = owner.sfResolve(ShouldDetachOrTerminate.ATTR_SHOULD_DETACH, shouldDetach, true);
            shouldDetachRead = true;
        } catch (RemoteException ex) {
        } catch (SmartFrogResolutionException ex) {
        }

        if (shouldTerminateRead) {
            if (terminationMessage==null) {
                terminationMessage = "Self Detatch and\\or Termination: ";
            }

            TerminationRecord termR = new TerminationRecord(terminationType,
                                            terminationMessage,
                                            completeNameSafe(),
                                            thrown);
            TerminatorThread terminator = new TerminatorThread(owner, termR);

            if (shouldDetachRead&&shouldDetach) {
                terminator.detach();
            }

            if ((shouldTerminateQuietlyRead)&&(!shouldTerminateQuietly)) {
                terminator.quietly();
            }

            if ((shouldTerminateRead)&&(!shouldTerminate)) {
                terminator.dontTerminate();
            }

            terminator.start();
        } else {
            if (shouldDetachRead) {
                new TerminatorThread(owner, null).dontTerminate().detach().start();
            }
        }
    }


    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * @param record record to terminate with
     * @param detach detach first?
     * @param quietly terminate quietly?
     */
    public void targetForTermination(TerminationRecord record, boolean detach, boolean quietly) {

        TerminatorThread terminator = new TerminatorThread(owner, record);
        if(detach) {
            terminator.detach();
        }
        if(quietly) {
            terminator.quietly();
        }
        terminator.start();
    }

    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * Note that we detach before terminating; this stops our timely end propagating.
     */
    public void targetForTermination() {

        Reference name;
        try {
            name = owner.sfCompleteName();
        } catch (RemoteException e) {
            name = null;
        }
        TerminationRecord record = TerminationRecord.normal(name);
        targetForTermination(record, false,false);
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
     * Load a resource into a string
     * @param resourcename
     * @param encoding
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public String loadResourceToString(String resourcename, Charset encoding) throws SmartFrogException, RemoteException {
        InputStream in= loadResource(resourcename);
        InputStreamReader isr = null;
        try {
            isr=new InputStreamReader(in,encoding);
            StringBuffer buffer=new StringBuffer();
            int ch;
            while((ch=isr.read())>=0) {
                buffer.append((char)ch);
            }
            return buffer.toString();
        } catch(IOException ioe) {
            throw SmartFrogException.forward("when reading "+resourcename,ioe);
        } finally{
            FileSystem.close(isr);
            FileSystem.close(in);
        }
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
     * @param depth 0 means dont look upwards, -1 means indefinite.
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
     * @param depth 0 means dont look upwards, -1 means indefinite.
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

    /**
     * An low-level SmartFrog method.
     * It deploys a compiled component and makes it an attribute of the
     * parent compound. Also start heartbeating the deployed component
     * if the component registers. Note that the remaining lifecycle methods must
     * still be invoked on the created component - namely sfDeploy() and sfStart().
     * This is primarily an internal method - the prefered method for end users is
     * #sfCreateNewChild.
     * <p/>
     * Note that the remaining lifecycle methods must
     * still be invoked on the created component - namely sfDeploy() and sfStart().
     *
     * @param name   name to name deployed component under in context
     * @param parent of deployer component
     * @param cmp    compiled component to deploy
     * @param parms  parameters for description; can be null
     * @return newly deployed component
     * @throws org.smartfrog.sfcore.common.SmartFrogDeploymentException
     *          failed to deploy compiled component
     */
    public Prim deployComponentDescription(Object name, Prim parent,
                                             ComponentDescription cmp, Context parms)
            throws SmartFrogDeploymentException {
        Log log = getLogger();
        if (parms == null) {
            parms = new ContextImpl();
        }
        // check for attribute already named like given name
        try {
            Object res = ((parent == null) || (name == null)) ? null : owner.sfResolveHere(name, false);

            if ((res != null) && !(res instanceof ComponentDescription)) {
                throw new SmartFrogDeploymentException(null, parent.sfCompleteName(),
                        name, cmp, parms, MessageUtil.
                        formatMessage(MessageKeys.MSG_NON_REP_ATTRIB, name), null, null);
            }

            if (log.isTraceEnabled()) {
                StringBuffer message = new StringBuffer();
                try {
                    message.append(completeNameSafe());
                    message.append(" is deploying: ");
                    if (name != null) {
                        message.append(name);
                    } else {
                        message.append("no-name");
                    }
                    if (parent != null) {
                        message.append(", Parent: ");
                        message.append(parent.sfCompleteName());
                    }
                    message.append(", Component description: ");
                    message.append(cmp.toString());
                    if (parms != null) {
                        message.append(", Params: ");
                        message.append(parms.toString());
                    }
                } catch (Throwable thr) {
                    log.trace("", thr);
                }
                log.trace(message.toString());
            }

            // try to deploy
            Prim result = SFDeployer.deploy(cmp, null, parent, parms);

            /**
             *
             * @TODO don't like this, we need to make the attribute over-write atomic with child registration (Patrick).
             *
             */
            if (parent != null) {
                if (name != null) {
                    parent.sfReplaceAttribute(name, result);
                    result.sfParentageChanged(); // yuk.... see todo above!
                } else {
                    //@TODO - Review after refactoring ProcessCompound
                    //This should throw an excetion when a
                    //component is registered without a name
                    //in a processcompound, but compound should not know anything
                    //about processcompound
                }
            }
            return result;
        } catch (SmartFrogDeploymentException dex) {
            // It will build source recursively
            Reference newRef = new Reference();
            if (name == null) {
                //@todo review methods for compDesc
                if (cmp.sfContext().containsKey(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME))
                    name = cmp.sfContext().get(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME);
                try {
                    newRef = parent.sfCompleteName();
                } catch (Exception ex) {
                    // LOG ex
                    LogFactory.sfGetProcessLog().ignore("could not get complete name", ex);
                }
            }
            if ((dex.get(SmartFrogDeploymentException.OBJECT_NAME)) != null) {
                newRef.addElement(ReferencePart.here(name));
            } else {
                dex.add(SmartFrogDeploymentException.OBJECT_NAME, name);
            }
            if (dex.get(SmartFrogDeploymentException.SOURCE) != null) {
                newRef.addElements((Reference) dex.get(SmartFrogDeploymentException.SOURCE));
            }

            if (newRef.size() != 0) {
                dex.put(SmartFrogDeploymentException.SOURCE, newRef);
            }
            throw dex;
        } catch (Throwable thr) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(thr);
        }
    }
}
