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
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.services.filesystem.FileSystem;

import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;
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
     * @param owner  the owner to which this helper should be bound
     */
    public ComponentHelper(Prim owner) {
        this.owner = owner;
    }

    /**
     * return the prim that this helper is bound to
     * @return Prim the owner this helper is bound to
     */
    public Prim getOwner() {
        return owner;
    }



    /**
     * get the relevant logger for this component.
     * When logging against a remote class, this is probably the classname of the proxy.
     * @return  Log Logger for this component
     */
    public Log getLogger() {
        return LogFactory.getOwnerLog(owner);
    }

    /**
     * ignore an exception by logging it at the fine level.
     * @param thrown exception to be logged
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
        } catch (Throwable ignored) {
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
     * Checks is prim is a remote reference
      * @param prim reference to a component
     *  @return true is prim is an instance of RemoteStub
     */
   public static boolean isRemote (Prim prim){
       return (prim instanceof java.rmi.server.RemoteStub);
   }

    /**
     * Returns the complete name for any component from the root of the
     * application and does not throw any exception. If an exception is
     * thrown it will return a new empty reference.
     * @param owner component whose completename is to be returned
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
     * Get the short name of a component.
     *
     * @return the final name in the list, or null for no match
     * @throws RemoteException network trouble
     */
    public String shortName() throws RemoteException {
        Object key;
        if (owner.sfParent() == null) {
            key = SFProcess.getProcessCompound().sfAttributeKeyFor(this);
        } else {
            key = owner.sfParent().sfAttributeKeyFor(this);
        }
        if (key != null) {
            return key.toString();
        } else {
            return null;
        }
    }

    /**
     * Resolve an attribute without complaining if something went wrong
     * even a remote exception.
     * @param attribute attribute name
     * @param defval default value
     * @return the attribute value, or, failing that, the default value
     */
    private boolean resolveQuietly(String attribute,boolean defval) {
        try {
            return owner.sfResolve(
                    attribute,
                    defval,
                    false);
        } catch (RemoteException ignored) {
        } catch (SmartFrogResolutionException ignored) {
        }
        return false;
    }

    /**
     * Method that can be invoked in any PrimImpl to trigger the detach and/or termination of a component
     * according to the values of the boolean attributes 'sfShouldDetach', 'sfShouldTerminate'
     * and 'sfShouldTerminateQuietly'
     * Example: new ComponentHelper(this).sfSelfDetachAndOrTerminate("normal","Copy ",this.sfCompleteNameSafe(),null);
     * Termination is initiated in a new thread.
     * @param terminationType - termination type, system recognized types are "normal", "abnormal" and "externalReferenceDead".
     *  If this is null, then normal/abnormal is chosen based on whether thrown is null or not
     * @param terminationMessage - description of termination. Can be null
     * @param refId Reference - id of terminating component. If null, triggers a call to sfCompleteNameSafe.
     * @param thrown Thrown fault
     * @see TerminationRecord
     * @return true if termination has been scheduled.
     */
    public boolean sfSelfDetachAndOrTerminate(String terminationType,
                                           String terminationMessage,
                                           Reference refId,
                                           Throwable thrown) {

        //create a termination record if we are exitiing
        TerminationRecord record;
        record = createTerminationRecord(terminationType, terminationMessage, refId, thrown);
        return sfSelfDetachAndOrTerminate(record);
    }

    /**
     * Method that can be invoked in any PrimImpl to trigger the detach and/or termination of a component
     * according to the values of the boolean attributes 'sfShouldDetach', 'sfShouldTerminate'
     * and 'sfShouldTerminateQuietly'.
     *
     * @param record the pre-constructed termination record to use if termination is started
     * @return true if termination has been scheduled.
     */

     public boolean sfSelfDetachAndOrTerminate(TerminationRecord record) {
        boolean shouldTerminate = resolveQuietly(
                ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE,
                false);

        boolean shouldTerminateQuietly = resolveQuietly(
                ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE_QUIETLY,
                false);

        boolean shouldDetach = resolveQuietly(
                ShouldDetachOrTerminate.ATTR_SHOULD_DETACH,
                false);

        //should we terminate (either noisily or not)
        boolean terminateRequired = shouldTerminate || shouldTerminateQuietly;

        if (terminateRequired || shouldDetach) {
            targetForTermination(record, !terminateRequired, shouldDetach, shouldTerminateQuietly);
        }
        return terminateRequired;
    }

    /**
     *
     * @param terminationType - termination type, system recognized types
     *  are "normal", "abnormal" and "externalReferenceDead".
     *  If this is null, then normal/abnormal is chosen based on whether thrown is null or not
     * @param terminationMessage - description of termination. Can be null
     * @param refId Reference - id of terminating component. If null, triggers a call to sfCompleteNameSafe.
     * @param thrown Thrown fault
     * @return the new termination record
     */
    public TerminationRecord createTerminationRecord(String terminationType, String terminationMessage,
                                                     Reference refId, Throwable thrown) {
        TerminationRecord record;
        //set up a termination record
        if (terminationType == null) {
            //select a default termination type
            terminationType = thrown == null ?
                    TerminationRecord.NORMAL : TerminationRecord.ABNORMAL;
        }
        if (terminationMessage == null) {
            //fill in a default termination message
            terminationMessage = "Self Detach and\\or Termination: ";
        }
        if (refId == null) {
            refId = completeNameSafe();
        }
        //create the new record.
        record = new TerminationRecord(terminationType,
                terminationMessage,
                refId,
                thrown);
        return record;
    }

    /**
     * This schedules a component for termination, but chooses the notification/detach attributes off
     * the parent, namely {@link ShouldDetachOrTerminate#ATTR_SHOULD_TERMINATE_QUIETLY} and
     * {@link ShouldDetachOrTerminate#ATTR_SHOULD_DETACH}.
     * @param record record to send with the termination.
     */
    public void targetForWorkflowTermination(TerminationRecord record) {
        boolean shouldTerminateQuietly = resolveQuietly(
                ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE_QUIETLY,
                false);

        boolean shouldDetach = resolveQuietly(
                ShouldDetachOrTerminate.ATTR_SHOULD_DETACH,
                false);
        targetForTermination(record,false,shouldDetach,shouldTerminateQuietly);
    }

    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * @param record  record to terminate with
     * @param dontTerminate set to true to  not actually terminate
     * @param detach  detach first?
     * @param quietly terminate quietly?
     */
    public void targetForTermination(TerminationRecord record, boolean dontTerminate,
                                     boolean detach, boolean quietly) {


        try {
            if(isComponentTerminating()) {
                return;
            }
        } catch (RemoteException ignored) {
            //that didn't work. We had either a transient or permanent
            //fault talking to the far end. what to do? Right now we
            //take the cautious option of running the termination thread
            //anyway, just in case the far end is reachable again in a moment.
        }
        TerminatorThread terminator = new TerminatorThread(owner, record);
        if (detach) {
            terminator.detach();
        }
        if (quietly) {
            terminator.quietly();
        }
        if (dontTerminate) {
            terminator.dontTerminate();
        }
        terminator.start();
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
        targetForTermination(record,false,detach,quietly);
    }

    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * Note that we detach before terminating; this stops our timely end propagating.
     * <i>Important.</i> This operation is implicitly harmless to use during termination. It will
     * not interfere with a component that is already closing down, nor with its notification options.
     */
    public void targetForTermination() {

        Reference name= completeNameOrNull();
        TerminationRecord record = TerminationRecord.normal(name);
        targetForTermination(record,false, false,false);
    }

    /**
     * Checks for the component terminating or being terminated. There is
     * a possible race condition, because the component (in a separate thread)
     * may enter the terminating phase during the test, and we wont pick it up.
     * As termination is one way, we do know that if this method returns true
     * then termination is underway and irreversible.
     * @return true if the owner is in termination phase or has terminated.
     *
     * @throws RemoteException for networking trouble
     */
    public boolean isComponentTerminating() throws RemoteException {
        return owner.sfIsTerminating() || owner.sfIsTerminated();
    }

    /**
     * load a resource using the classpath of the component
     * at question.
     *
     * @param resourcename name of resource on the classpath
     * @return an input stream if the resource was found and loaded
     * @throws SmartFrogException if the resource is not on the classpath
     * @throws RemoteException in case of Remote/network error
     */
    public InputStream loadResource(String resourcename)
            throws SmartFrogException, RemoteException {
        String targetCodeBase = getCodebase();

        InputStream in = SFClassLoader.getResourceAsStream(resourcename, targetCodeBase, true);
        if (in == null) {
            throw new SmartFrogException("Not found: " + resourcename+" in "+targetCodeBase);
        }
        return in;
    }

    /**
     * Load a resource into a string
     * @param resourcename name of resource on the classpath
     * @param encoding encoding to be used
     * @return String if the resource was found and loaded
     * @throws SmartFrogException if the resource is not on the classpath
     * @throws RemoteException in case of Remote/network error
     */
    public String loadResourceToString(String resourcename, Charset encoding)
            throws SmartFrogException, RemoteException {
        InputStream in= loadResource(resourcename);
        try {
            return FileSystem.readInputStream(in, encoding).toString();
        } catch (IOException ioe) {
            throw SmartFrogException.forward("when reading "+resourcename,ioe);
        }
    }

    /**
     * get the codebase of a component
     * @return String codebase of a component
     * @throws SmartFrogResolutionException if failed to resolve
     * @throws RemoteException in case of Remote/network error
     */
    public String getCodebase() throws SmartFrogResolutionException,
            RemoteException {
        return (String) owner.sfResolve(SmartFrogCoreKeys.SF_CODE_BASE);
    }


    /**
     * Load a class in the classloader, using the SmartFrog classloader.
     * {@link SFClassLoader#forName(String, String, boolean)}
     * @param classname
     * @return class
     * @throws SmartFrogResolutionException if the class could not be found
     * @throws RemoteException for network problems
     */
    public Class loadClass(String classname) throws SmartFrogResolutionException, RemoteException {
        String targetCodeBase = getCodebase();

        try {
            return SFClassLoader.forName(classname, targetCodeBase, true);
        } catch (ClassNotFoundException ignored) {
            throw new SmartFrogResolutionException("Not found: " + classname + " in " + targetCodeBase);

        }
    }

    /**
     * find an ancestor of a given type
     * @param node node to look for
     * @param interfaceName full name of interface to look for
     * @param depth 0 means dont look upwards, -1 means indefinite.
     * @return a parent or null for no match
     * @throws RemoteException in case of Remote/network error
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
     * @throws RemoteException in case of Remote/network error
     */
    public Prim findAncestorImplementing(String interfaceName, int depth) throws RemoteException {
        return findAncestorImplementing(owner, interfaceName, depth);
    }

    /**
     * recursive search for interface inheritance
     * @param clazz  Class name
     * @param interfaceName full name of interface to look for
     * @return boolean true is the interface is found in the recursive search
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
