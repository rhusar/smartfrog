
/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.recoverablecomponent;

import java.rmi.*;
import java.rmi.server.RemoteObject;
import java.lang.reflect.*;

import org.smartfrog.services.persistence.storage.*;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.compound.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.TerminationRecord;




import java.util.*;
import java.io.*;


/* 
 * This class implements a Recoverable Prim component. This 
 * component has the special feature of surviving crashes and 
 * restarting after the transient problem that has
 * caused its failure has been solved.
 */
public class RComponentImpl extends CompoundImpl implements RComponent, Serializable {

    Storage stableLog;  
	
    String[] volatileEntries = {"sfHost","sfLog"};
    //String[] volatileEntries = {"sfHost","sfLog","sfProcess","sfProcessName"};
	
    public RComponentImpl() throws RemoteException {
    	super();
    }

    static protected Storage getNewStableStorage(String classname, String repository) throws StorageException{
    	Class storageclass = null;
    	try{
    		storageclass = Class.forName(classname);
    	} catch(ClassNotFoundException cause){
    		throw new StorageException("Storage class not found!",cause);
    	}
    	Class[] constparam = new Class[1];
    	constparam[0] = String.class;
    	Constructor storageconstructor = null;
    	try{
    		storageconstructor = storageclass.getConstructor(constparam);
    	} catch(NoSuchMethodException cause){
    		throw new StorageException("Storage constructor method not found!",cause); 
    	}
    	Object[] params = new Object[1];
    	params[0] = repository;

    	try{
    		return (Storage) storageconstructor.newInstance(params);
    	} catch (Exception cause){
    		throw new StorageException("Problems instantiating stable storage", cause);
    	}
    }
	
    public RComponentProxyLocator getProxyLocator() throws RemoteException{
    	return new RComponentProxyLocatorImpl(stableLog.getAgentUrl(),stableLog.getStorageRef());
    }
	
    protected boolean isVolatile(String entryname) throws StorageException{
	for (int i=0;i<volatileEntries.length; i++){
	    if (volatileEntries[i].equals(entryname))
		return true;
	}
	return false;
    }
    
    protected void createEntry(String entryname, String directory) throws StorageException{
    	if(!isVolatile(entryname))
    		stableLog.createEntry(entryname, directory);
    }

    protected boolean hasEntry(String entryname) throws StorageException{
    	return isVolatile(entryname) || stableLog.hasEntry(entryname);
    }
    
    protected void addEntry(String entryname, Serializable value) throws StorageException{
    	if(!isVolatile(entryname))
    		stableLog.addEntry(entryname,value);
    }
    
    protected void removeEntry(String entryname) throws StorageException{
    	if(!isVolatile(entryname))
    		stableLog.deleteEntry(entryname);
    }
    
	
    /**
     * Adds an attribute to this component under given name.
     *
     * @param name name of attribute
     * @param value value of attribute
     *
     * @return added attribute if non-existent or null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfAddAttribute(Object name, Object value)
        throws SmartFrogRuntimeException, RemoteException {

    	Object retvalue = super.sfAddAttribute(name,value);
    	
    	try{
    	    if (!hasEntry((String)name)){
    	    	createEntry((String)name,ATTRIBUTESDIRECTORY);
    	    }
    		addEntry((String)name, (Serializable)value);
    		stableLog.commit();
    	} catch(StorageException exc) {
    		throw new SmartFrogRuntimeException("Error while writing attribute on stable storage",exc);
    	}
    	
   		return retvalue;
    }	

    /**
     * Replace named attribute in component context. If attribute is not
     * present it is added to the context.
     *
     * @param name of attribute to replace
     * @param value value to add or replace
     *
     * @return the old value if present, null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfReplaceAttribute(Object name, Object value)
        throws SmartFrogRuntimeException, RemoteException {
	
    	Object retvalue = super.sfReplaceAttribute(name,value);
    	try{
    	    if (!hasEntry((String)name)){
    	    	createEntry((String)name,ATTRIBUTESDIRECTORY);
    	    }
    	    addEntry((String)name, (Serializable)value);
            stableLog.commit();      
    	} catch(StorageException exc) {
    		throw new SmartFrogRuntimeException("Error while writing attribute on stable storage",exc);
    	}

    	return retvalue;
    }
    
    
    /**
     * Removes an attribute from this component.
     *
     * @param name of attribute to be removed
     *
     * @return removed attribute value if successfull or null if not
     *
     * @throws SmartFrogRuntimeException when name is null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfRemoveAttribute(Object name)
    	throws SmartFrogRuntimeException, RemoteException {

    	Object retvalue = super.sfRemoveAttribute(name);
    	
    	try{
    		if (hasEntry((String)name)){
    			removeEntry((String) name);
    		}
    		stableLog.commit();
    	} catch(StorageException exc) {
    		throw new SmartFrogRuntimeException("Error while writing attribute on stable storage",exc);
    	}
    	return retvalue;
    }

    
    /**
     * Basically, the internal state of a Smartfrog Component is composed of
     * the following pieces:
     * 
     * Context sfContext
     * 
     * @throws IOException
     */
    private void saveState(Context cxt) throws StorageException, IOException {
    	Iterator attributes = cxt.sfAttributes();
    	Iterator values = cxt.sfValues();
    	while(attributes.hasNext()){
    		String entryname = (String) attributes.next();
    		Serializable value = (Serializable) values.next();
	    
    		createEntry(entryname,ATTRIBUTESDIRECTORY);
    		addEntry(entryname,value);
    	}
    }

    
    /**
     * Creates a new child for this component
     *
     * @param target target to heartbeat
     */
    public void sfAddChild(Liveness target) throws RemoteException {
    	super.sfAddChild(target);
    	try{
    		addEntry(SFCHILDREN,sfChildren);
    	} catch(StorageException cause) {
    		cause.printStackTrace();
    		throw new RuntimeException("Error while writing to Stable Storage",cause);
    	}
    }
    

    /**
     * Removes a specific child 
     *
     * @param target object to remove from heartbeat
     *
     * @return true if child is removed successfully else false
     */
    public boolean sfRemoveChild(Liveness target) throws SmartFrogRuntimeException, RemoteException  {

    	boolean res = super.sfRemoveChild(target);
    	if (res){
    		try{
    			addEntry(SFCHILDREN,sfChildren);
    		} catch(StorageException cause) {
    			throw new RuntimeException("Error while writing to Stable Storage",cause);
    		}
    	}
        return res;
    }    
    
    /**
     * Detatching will have to be implemented in the future making use of the sfPing mechanism
     * to ensure consistency even in the presence of failures.
     */
//    public synchronized void sfDetach()
//    throws SmartFrogException, RemoteException {
//    	throw new SmartFrogException("sfDetach is not possible with Recoverable components");
//    }
    
    public synchronized void sfDetachAndTerminate(TerminationRecord status) {
    	throw new RuntimeException("sfDetatchAndTerminate is not possible with Recoverable components");
    }

    /**
     * Private method to set up newly created component. Primitives should only
     * override sfDeploy since this is the one which does the actual work
     *
     * @param parent parent of component
     * @param cxt context for component
     *
     * @throws SmartFrogDeploymentException In case of any error while
     *         deploying the component
     * @throws RemoteException In case of network/rmi error
     */
    public void sfDeployWith(Prim parent, Context cxt) throws
	SmartFrogDeploymentException, RemoteException {
	
    	boolean recovering = cxt.sfContainsAttribute(STORAGEATTRIB);
    	
    	try{
    		if (recovering){
    			stableLog = ((StorageRef)cxt.sfRemoveAttribute(STORAGEATTRIB)).getStorage();
    			sfIsDeployed = true;
    			sfIsStarted = true;
        		stableLog.disableCommit();
    		} else {
    			String stoclass = (String)cxt.sfRemoveAttribute(STORAGECLASSATTRIB);
    			String storep = (String)cxt.sfRemoveAttribute(STORAGEREPOSITORY);
    			
    			stableLog = getNewStableStorage(stoclass,storep);
    			
        		stableLog.createEntry(SFPARENT,CHILDRENSDIRECTORY);
        		stableLog.createEntry(SFCHILDREN,CHILDRENSDIRECTORY);
        		stableLog.addEntry(SFCHILDREN,sfChildren);
    			stableLog.createEntry(WFSTATUSENTRY,WFSTATUSDIRECTORY);
    			stableLog.addEntry(WFSTATUSENTRY,WFSTATUS_DEAD);
        		stableLog.enableCommit();
        		stableLog.disableCommit();
    			saveState(cxt);
    		}
    	} catch (Exception cause){
    		throw new SmartFrogDeploymentException(cause);
    	}

    	super.sfDeployWith(parent, cxt);

    	// the following piece of code should be placed where it is for
    	// it must be executed after the component has registered itself
    	// it also cannot be executed at sfStart for it is necessary during recovery
    	try{
    		if(recovering){
    			stableLog.enableCommit(); // if not recovering, this is done 
    			stableLog.commit();       // at sfStart()
    		}else{
    			if (sfParent==null || sfParent instanceof ProcessCompound){
    				addEntry(SFPARENT,null);
    			}else if (sfParent instanceof RComponent){
    				addEntry(SFPARENT,(Serializable) sfParent);
    			}else if( sfIsRemote(sfParent) ){
    				addEntry(SFPARENT,(Serializable) RemoteObject.toStub(sfParent));
    			}else {
    				addEntry(SFPARENT,null); // this is a "kind of" hack!!!
    			}
    			createEntry(DBStubEntry,DBStubDirectory); 
    		}
    		if (sfExportRef == null){
    			System.out.println("sfExportRef is equal to null");
    			try{
    			sfExportRef(0);
    			} catch(Exception exc) {}
    		}
    		Object obj = sfExportRef;

    		stableLog.addEntry(DBStubEntry, (Serializable) obj );
    		stableLog.commit(); // is executed only when recovering
    							// otherwise commit will be disabled and postponed until sfStart
    	
    	} catch(StorageException cause){
    		try{stableLog.close();}catch(Exception exc){}
    		throw new SmartFrogDeploymentException(cause);
    	}
    		
    }
	 
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
    	System.out.println("Deploying");
    	super.sfDeploy();
    }
    
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
    	super.sfStart();
    	try{
    		addEntry(WFSTATUSENTRY,WFSTATUS_STARTED);
    		stableLog.enableCommit();
    		stableLog.commit();
    	} catch (StorageException cause){
    		throw new SmartFrogException("Impossible to change status on stable storage",cause);
    	}
    }
    
    
    public synchronized void sfTerminateWith(TerminationRecord status) {
    	try{
    		stableLog.addEntry(WFSTATUSENTRY,WFSTATUS_DEAD);
    		stableLog.commit();
    	} catch (StorageException cause) {
    		throw new RuntimeException("Impossible to write on stable storage",cause);
    	}
    	super.sfTerminateWith(status);
    }
    
    
    /**
     * Extends PrimImpl's method so that now it checks whether the parent failed and,
     * if the parent is recoverable and not dead, considers it as simply remporarily absent.
     */
    protected void sfLivenessFailure(Object source, Object target,
            Throwable failure) {
    	
    	if(target.equals(sfParent)){
    		if(sfParent instanceof RComponentProxyStub){
    			RComponentProxyStub RParent = (RComponentProxyStub) sfParent;
    			if(! RParent.isDead()){
    				synchronized(this){
    					sfLivenessCount = sfLivenessFactor;
    				}
    				return;
    			}
    		}
    	}
    	super.sfLivenessFailure(source,target,failure);
    }
    
    /**
     * Method that has to be implemented by superclasses with appropriate recovery
     * actions
     */
    public synchronized void sfRecover() throws SmartFrogException, RemoteException {
    	try{
    		sfParent = (Prim) stableLog.getEntry(SFPARENT);
    		sfParentageChanged();
    		sfChildren = (Vector) stableLog.getEntry(SFCHILDREN);
    	} catch ( StorageException cause ){
    		throw new SmartFrogException(cause);
    	}
    }    
    
    /**
     * Implemented to provide correct equality checking. 
     *
     * @param o object to compare with
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object o) {
    	if (! (o instanceof RComponent)){
    		return false;
    	}
    	if (o instanceof RComponentProxyStub){
    		return RComponentProxyInvocationHandler.sfGetProxy(this).equals(o);
    	} else {
    		return super.equals(o);
    	}
    }
    
    /**
     * Replaces the component by its dynamic proxy with recovery properties during
     * serialization.
     *  
     * @return A new proxy object linked to this recoverable component
     * @throws ObjectStreamException in case an error occurs
     */
    public Object writeReplace() throws ObjectStreamException {
    	return RComponentProxyInvocationHandler.sfGetProxy(this);
    }
    
}


