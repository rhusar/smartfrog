/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.framework.activator;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceManager;
import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.services.persistence.rcomponent.RComponentImpl;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentMissingException;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.ProcessReferencePart;
import org.smartfrog.sfcore.reference.Reference;

/**
 * The register is responsible for loading and unloading components on 
 * activation and deactivation. It identifies components to load using
 * the registration table in the persistent store. It also contains 
 * methods that perform sanity checks across the components in the
 * persistent store and generate a report containing any issues.
 */
public class Register extends CompoundImpl implements Compound {

    
    private static String MANDATORY_ATTR = "mandatory";
    private static String DISPOSAL_ATTR = "disposal";
    private static boolean isTiming = Boolean.parseBoolean(System.getProperty(RComponent.DIAG_TIMING_PROP));
    
    private Storage storage;
    private ComponentDescription mandatoryComponents;
    private RComponentTerminator loaderAndTermintator;
    private InterfaceManager interfaceManager;
    
    public Register() throws RemoteException {
        // TODO Auto-generated constructor stub
    }
    
    

    /**
     * This is a debug utility that allows the user to cause a load or unload
     * through the smartfrog console by adding or replacing an attribute called
     * "action" with the value "load" or "unload" on the register component.
     */
    public synchronized Object sfReplaceAttribute(Object name, Object value) throws SmartFrogRuntimeException, RemoteException {
        Object retVal = super.sfReplaceAttribute(name, value);
        try {
            if( "action".equals(name) ) {
                if( "unload".equals(value) ) {
                    sfUnloadAll();
                } else if( "load".equals(value) ) {
                    sfLoadAll();
                }
            }
        } catch (SmartFrogException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retVal;
    }

    private Object name(Prim prim) {
        try {
            HereReferencePart hrp = (HereReferencePart)prim.sfCompleteName().lastElement();
            return hrp.getValue();
        } catch (RemoteException e) {
            return "<failed to get name>";
        }
    }
    


    @Override
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        mandatoryComponents = (ComponentDescription)sfResolve(MANDATORY_ATTR);
        loaderAndTermintator = (RComponentTerminator)sfResolve(DISPOSAL_ATTR);
        interfaceManager = (InterfaceManager)sfResolve(RComponent.INTERFACE_MGR_REF_ATTR);
        sfAddAttribute("action", "initial");
    }



    /**
     * <p>
     * This method loads all components in the recovery store from their recovery roots
     * and deals with orphaned components. If the recovery store does not already have 
     * register data it will initialize it.
     * </p>
     * <p>
     * A recovery root is the root component
     * of a hierarchy of recoverable component. All active components have a recovery
     * root, but orphaned components do not. Components are terminated asynchronously, 
     * so in the event of a crash failure it is possible that some components had not terminated 
     * when their parents had, leaving them as orphans - in fact orphans will be arranged in
     * arbitrary tree fragments. These orphaned tree fragments are recovered from their 
     * fragment roots and then terminated to ensure they go through correct termination.
     * </p>
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void sfLoadAll() throws SmartFrogResolutionException, RemoteException, SmartFrogException {
        try {
            /**
             * Open access to storage
             */
            storage = Storage.openStorage((ComponentDescription) sfResolve(RComponentImpl.STORAGE_DATA_ATTR));
            storage.exceptionNotifications(interfaceManager);
            
            /**
             * Initialise register
             */
            storage.initialiseRegister(Transaction.nullTransaction);
            storage.initialiseAttributes(Transaction.nullTransaction);
            
            /**
             * Start the disposal worker and initialise the load worker.
             * the load worker needs to get a connection.
             */
            loaderAndTermintator.startDisposal();
            loaderAndTermintator.newLoader(storage.getTransaction());

            /**
             * Get recovery roots from the storage.
             */
            Set roots = storage.getRecoveryRoots(Transaction.nullTransaction);
            Set<Prim> recoveredComponents = new HashSet<Prim>();
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Storage contains " + roots.size() + " tree fragments to recover");
            }

            /**
             * Initiate recovery from each root
             */
            Iterator rootIter = roots.iterator();
            while (rootIter.hasNext()) {

                ComponentDescription config = (ComponentDescription) rootIter.next();
                String name = (String) config.sfResolveHere(Storage.COMPONENT_NAME_ATTR);

                RComponent rcomponent = getRComponent(name);
                if (rcomponent != null) {
                    
                    /**
                     * a component already exists with same name
                     */
                    if (sfLog().isErrorEnabled()) {
                        sfLog().error(
                                "Found existing component " + rcomponent.sfCompleteName() + " when trying to recover "
                                        + name);
                    }

                } else {
                    
                    /**
                     * recover the component
                     */
                    try {
                        
                        long startTime = 0;
                        if( isTiming ) {
                            startTime = System.currentTimeMillis();
                        }
                        RComponent comp = (RComponent) sfDeployComponentDescription(name, null, config, null);
                        recoveredComponents.add(comp);
                        
                        long endTime = 0;
                        if( isTiming && sfLog().isInfoEnabled() ) {
                            endTime = System.currentTimeMillis();
                            sfLog().info( "Startup recovery for " + name(comp) + " (" + name + ") completed in " + (endTime - startTime) + " millis.");                            
                        }

                    } catch (SmartFrogException e) {
                        if( ( e instanceof StorageAccessException ) || 
                            ( e instanceof StorageDeploymentAccessException )) {
                            throw e;
                        } else if( e instanceof StorageDeploymentMissingException ) {
                            if( sfLog().isInfoEnabled() ) {
                                sfLog().info("Sub-components of " + name + " have terminated or are missing, cleaning up this component", e);
                            }
                        } else {
                            if( sfLog().isErrorEnabled() ) {
                                sfLog().error("Failed to load component " + name + ", continuing without", e);
                            }
                        }
                    } 
                }
            }
            
            
            /**
             * deployWith any mandatory components and add them to the list of recovered components.
             * Mandatory components may or may not be recoverable, so they may or may not have been
             * constructed in the above load phase - only deployWith them if there is not already a
             * component with their name in the process compound.
             */
            Iterator mIter = mandatoryComponents.sfAttributes();
            while( mIter.hasNext() ) {
                String name = (String)mIter.next();
                try {
                    SFProcess.getProcessCompound().sfResolve(name);
                    if( sfLog().isDebugEnabled() ) {
                        sfLog().debug("Did not construct mandatory component: " + name + " - a component with that name already exists" );
                    }
                } catch( SmartFrogResolutionException e ) {
                    ComponentDescription cd = mandatoryComponents.sfResolve(name, (ComponentDescription)null, true);
                    Prim prim;
                    try {
                        Context context = new ContextImpl();
                        context.put("sfProcessComponentName", name);
                        prim = sfDeployComponentDescription(name, null, cd, context);
                        recoveredComponents.add(prim);
                    } catch (SmartFrogDeploymentException e1) {
                        if( sfLog().isErrorEnabled() ) {
                            sfLog().error("Failed to deploy mandatory component: " + name, e1);
                        }
                    }
                }
            }

            
            /**
             * deploy and start all the recovered components. This is done as a seperate iteration
             * to deployWith in case the components refer to one another with references that are
             * resolved at deploy/start. If a deploy/start fails we terminate the deployment.
             */
            Iterator<Prim> rIter = recoveredComponents.iterator();
            while (rIter.hasNext()) {
                Prim comp = rIter.next();
                String sfName = comp.sfResolve("sfProcessComponentName", (String)null, false);
                ComponentDescription scd = (ComponentDescription)comp.sfResolveHere(RComponent.STORAGE_DATA_ATTR);
                String name = (String)scd.sfResolveHere(Storage.COMPONENT_NAME_ATTR);
                try {
                    
                    comp.sfDeploy();
                    comp.sfStart();
                    
                } catch(StorageDeploymentAccessException e) { 
                    /**
                     * This case implies we are being shutdown
                     */
                    throw e;
                } catch(StorageAccessException e) { 
                    /**
                     * This case implies we are being shutdown
                     */
                    throw e;
                } catch (Throwable e) {
                    /**
                     * This case implies an arbitrary error. We will attempt to terminate the 
                     * component. All exceptions will be contained.
                     */
                    if(e instanceof RemoteException) {
                        if (sfLog().isErrorEnabled()) {
                            sfLog().error("Remote exception in deploy/start component " + name + " with sf name " + 
                                    sfName + ", Shouldn't happen!!!");
                        }                        
                    } else {
                        if (sfLog().isErrorEnabled()) {
                            sfLog().error("Failed to deploy/start component " + name + " with sf name " + sfName + ", terminating this one", e);
                        }                        
                    }
                    
                    try {
                        comp.sfTerminate(TerminationRecord.abnormal("Failed to deploy or start", comp.sfCompleteName()));
                    } catch (Throwable e1) {
                        if (sfLog().isErrorEnabled()) {
                            sfLog().error("Failed to terminate component " + name + " with sf name " + sfName + " after deploy/start failure");
                            sfLog().error("Continuing with recovery, but this component is in an unknown state");
                            sfLog().error("Reason for temination failure", e1);
                        }   
                    }
                } 
            }

            /**
             * Get orphans and initiate their recovery (load and terminate)
             */
            Set orphans = storage.getOrphanRoots(Transaction.nullTransaction);
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Storage contains " + orphans.size() + " orphan tree fragments to recover and terminate");
            }
            
            loaderAndTermintator.handleOrphans(orphans);
            loaderAndTermintator.startLoading();



        } catch (StorageException e) {

            throw (SmartFrogException) SmartFrogException.forward(e);

        } finally {
            /**
             * close the store
             */
            if (storage != null) {
                try {
                    storage.close();
                } catch (Exception e) {
                }
                storage = null;
            }
        }
    }
    

    public void sfUnloadAll() throws RemoteException {
        
        loaderAndTermintator.stopDisposal();
        loaderAndTermintator.stopLoading();
        
        if( sfLog().isDebugEnabled() ) {
            sfLog().debug("Unloading components");
        }

        Enumeration e = SFProcess.getProcessCompound().sfChildren();
        while( e.hasMoreElements() ) {

            Prim prim = (Prim)e.nextElement();
            if( prim.sfParent() != null ) {
                continue;
            }

            if( prim instanceof RComponent ) {

                RComponent rcomponent = (RComponent)prim;
                rcomponent.sfUnload();

            } else if( mandatoryComponents.sfContext().containsKey( SFProcess.getProcessCompound().sfAttributeKeyFor(prim) )) {

                prim.sfTerminate(TerminationRecord.normal(prim.sfCompleteName()));

            }
        } 
    }
    
    /**
     * <p>
     * This method unloads all the recoverable components that are children of
     * the process compound that are referenced as roots in its storage. To do
     * this it has to read registrations from the recovery store. If the store
     * has not been initialized with registration data the method will throw a
     * SmartFrogException. It does not initialize the recovery store.
     * </p>
     * <p>
     * Note that this method will only unload recoverable components that are
     * described in the recovery store. If there are components that are not
     * described in the recovery store they will be left untouched. This allows
     * components backed up in different recovery stores to be treated
     * seperately.
     * </p>
     * 
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void sfUnloadAllRegistered() throws SmartFrogResolutionException, RemoteException, SmartFrogException {
        
        try {
            /**
             * Open the recovery store
             */
            storage = Storage.openStorage((ComponentDescription) sfResolve(RComponentImpl.STORAGE_DATA_ATTR));

            /**
             * Get recovery roots from the storage. 
             */
            Set roots = storage.getRecoveryRoots(Transaction.nullTransaction);
            if( sfLog().isDebugEnabled() ) {
                sfLog().debug("Storage contains " + roots.size() + " tree fragments to recover");
            }
            
            /**
             * Initiate recovery from each root
             */
            Iterator rootIter = roots.iterator();
            while (rootIter.hasNext()) {
                
                ComponentDescription config = (ComponentDescription) rootIter.next();
                String name = (String) config.sfResolveHere(Storage.COMPONENT_NAME_ATTR);
                
                RComponent unloadTarget = getRComponent(name);
                if( unloadTarget != null ) {

                    if( sfLog().isDebugEnabled() ) {
                        sfLog().debug("Unloading " + name);
                    }

                    unloadTarget.sfUnload();
                    
                } else {

                    if( sfLog().isDebugEnabled() ) {
                        sfLog().debug("Failed to locate root for stored unload target " + name);
                    }
                    
                }
                
            }
        } catch (StorageException e) {
            
            throw (SmartFrogException)SmartFrogException.forward(e);
            
        } finally {
            /**
             * close the store
             */
            if( storage != null ) {
                try {
                    storage.close();
                } catch(Exception e) {
                }
                storage = null;
            }
        }   
    }
    
    /**
     * Find an RComponent with the given storage component name (the name in its storage configuration, 
     * not its attribute name) under the process compound. This returns the component if it can find 
     * it or null if it can not. 
     * 
     * @param name the storage component name (
     * @return the component or null if not found
     * @throws RemoteException 
     * @throws SmartFrogResolutionException failed to resolve storage or name attribute for an RComponent
     */
    private RComponent getRComponent(String name) throws RemoteException, SmartFrogResolutionException {
        ProcessCompound pc = SFProcess.getProcessCompound();
        Iterator iter = pc.sfValues();
        while( iter.hasNext() ) {
            Object obj = iter.next();
            if( obj instanceof RComponent ) {
                RComponent rcomponent = (RComponent)obj;
                ComponentDescription storageConfig = (ComponentDescription)rcomponent.sfResolveHere(RComponent.STORAGE_DATA_ATTR);
                if( name.equals(storageConfig.sfResolveHere(Storage.COMPONENT_NAME_ATTR))) {
                    return rcomponent;
                }
            }
        }
        return null;
    }
    
    
    /**
     * Performs a sanity check on the data structures.
     * 
     * @param out
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void checkSanity(StringBuffer out) throws RemoteException, SmartFrogException {
        /**
         * create storage
         */
        storage = Storage.openStorage((ComponentDescription) sfResolve(RComponentImpl.STORAGE_DATA_ATTR));
        storage.initialiseRegister(Transaction.nullTransaction);
        storage.initialiseAttributes(Transaction.nullTransaction);

        checkRecoveryRoots(out);
        checkOrphans(out);
    }
    
    
    /**
     * checkRecoveryRoots traverses the data strcutures for the recovery roots to see if
     * there are any parts missing.
     * 
     * @param out
     * @throws RemoteException
     * @throws SmartFrogException
     */
    private void checkRecoveryRoots(StringBuffer out) throws RemoteException, SmartFrogException {


        /**
         * Get recovery roots from the storage.
         */
        Set<ComponentDescription> roots = storage.getRecoveryRoots(Transaction.nullTransaction);
        out.append("################\n");
        if( roots.isEmpty() ) {
            out.append("## There are no models in the storage\n");
        } else {
            out.append("## Number of models to recover: " + roots.size() + "\n");
        }
        
        /**
         * Examine the models found
         */
        StringBuffer modelOut = new StringBuffer();
        ErrorCounter err = new ErrorCounter();
        Set<String> cleanup = new HashSet<String>();
        Set<String> names = new HashSet<String>();
        for(ComponentDescription config : roots ) {
            Reference ref = new Reference(new ProcessReferencePart());
            Storage storage = Storage.openStorage(config);
            Context context = storage.getContext(Transaction.nullTransaction);
            storage.close();
            
            if( context.sfContainsAttribute(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME) ) {
                String name = (String)context.get(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME);
                ref.addElement(new HereReferencePart(name));
                if( !names.add(name) ) {
                    out.append("## model root with duplicate name: " + ref + "\n");
                    out.append("## if the other model with this name is valid, this model will be cleaned up on startup\n");
                    err.inc();
                } else {
                    out.append("## model root: " + ref + "\n");
                }
            } else {
                ref.addElement(new HereReferencePart("unknown"));
                out.append("## model root with no name" + "\n");
                err.inc();
            }
            
            int beforeErrorCount = err.count();
            checkContext(modelOut, err, context, ref);
            if( err.count() != beforeErrorCount ) {
                out.append("## this model will be cleaned up on startup due to the following errors\n");
                out.append(modelOut);
            }
        }
    }
    
    
    /**
     * performs a recursive check over the context with the 
     * 
     * @param out
     * @param context
     * @param ref
     */
    private void checkContext(StringBuffer out, ErrorCounter err, Context context, Reference ref) {
        
        Iterator iter = context.sfAttributes();
        while( iter.hasNext() ) {
            
            /**
             * Get attribute name and value - if its not
             * a storage description go on to next
             */
            String name = (String)iter.next();
            Object obj = context.get(name);
            if( !Storage.isStorageDescription(obj)) {
                continue;
            }
            
            /**
             * Don't do lazy storage description - these are data
             */
            ComponentDescription config = (ComponentDescription)obj;
            if( !config.getEager() ) {
                continue;
            }
            
            /**
             * Add child name to reference
             */
            ref.addElement(new HereReferencePart(name));
            
            /**
             * Gets childs context and check that too
             */
            Context childContext;
            try {
                /**
                 * get context
                 */
                Storage storage = Storage.openStorage(config);
                if( storage.exists(Transaction.nullTransaction)) {
                    
                    /**
                     * check the childs context
                     */
                    checkContext(out, err, storage.getContext(Transaction.nullTransaction), ref);
                    
                } else {
                    
                    /**
                     * The child's storage is missing
                     */
                    out.append("## model sub-branch missing: " + ref + "\n");
                    err.inc();
                    
                }
                storage.close();
                
                
            } catch (StorageException e) {
                out.append("## failed to read model sub-branch: " + ref + "\n");
                err.inc();
            }
            
            /**
             * this child done - remove child name from reference
             */
            ref.removeElement(ref.size() - 1);
            
        }
        
    }
    
    private void checkOrphans(StringBuffer out) throws StorageException {
        /**
         * Get orphans 
         */
        Set orphans = storage.getOrphanRoots(Transaction.nullTransaction);
        out.append("################\n");
        if( orphans.isEmpty() ) {
            out.append("## There are no orphaned model fragments\n");
        } else {
            out.append("## Number of orphan model fragments: " + orphans.size() + "\n");
            out.append("## these are not errors; they will be cleaned up on startup\n");
        }
    }
    
    private class ErrorCounter {
        private int count = 0;
        public void inc() { count++; }
        public String toString() { return "" + count; }
        public int count() { return count; }
    }
}
