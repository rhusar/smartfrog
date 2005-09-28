
package org.smartfrog.services.persistence.recovery;

import java.io.File;
import java.lang.reflect.Constructor;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * 
 * A RecoveryAgent is a Component responsible for recovering basic
 * components in the system. What it does now is to deploy the components
 * found in the stable storage directory under its own ProcessCompound. 
 * 
 * @author schmirod
 */
public class RecoveryAgent extends CompoundImpl implements Compound {

	Recoverer recoverer = null;
	String classname = null;
	
	
	private class Recoverer extends Thread{
		
		public void run (){
			String dirname = null;
			try{
				dirname = (String) sfResolve(RComponent.STORAGEREPOSITORY);
			}catch(Exception exc){
				System.out.println("No attribute sfRepository found.");
				return;
			}
			
			File dir = new File(dirname);
			
			
//          The recovery agent used to be inside a while loop, periodically restarted failed components.
//			
//			while(true){
//				try{
//					Thread.sleep(2000);
//				} catch(InterruptedException exc){}

				File[] filenames = dir.listFiles();

//				if(filenames==null) continue;

				for(int i = 0; i<filenames.length; i++){
					if (filenames[i].isDirectory())
						try{	
							Storage storage = openStorage(classname,dir.getAbsolutePath(),filenames[i].getName());
							System.out.println("Recovering "+filenames[i].getName());
							if (! storage.getEntry(RComponent.WFSTATUSENTRY).equals(RComponent.WFSTATUS_DEAD))
								restoreFromStorage(storage);
							else
								System.out.println(filenames[i].getName()+" has already finished.");
						} catch (StorageException exc) {
							exc.printStackTrace();
							System.err.println("Component "+ filenames[i].getName()+ " is apparently running.");
						}
				}

//			}
		}
	}
	
	
	static public Storage openStorage (String classname, String repository, String dbname) throws StorageException{
    	Class storageclass = null;
    	try{
    		storageclass = Class.forName(classname);
    	} catch(ClassNotFoundException cause){
    		throw new StorageException("Storage class not found!",cause);
    	}
    	Class[] constparam = new Class[2];
    	constparam[0] = String.class;
    	constparam[1] = String.class;
    	
    	Constructor storageconstructor = null;
    	try{
    		storageconstructor = storageclass.getConstructor(constparam);
    	} catch(NoSuchMethodException cause){
    		throw new StorageException("Storage constructor method not found!",cause); 
    	}
    	Object[] params = new Object[2];
    	params[0] = repository;
    	params[1] = dbname;
    	
    	try{
    		return (Storage) storageconstructor.newInstance(params);
    	} catch (Exception cause){
    		throw new StorageException("Problems instantiating stable storage", cause);
    	}
	}
	
	public RecoveryAgent() throws RemoteException {
		super();
	}

	private void restoreFromStorage( Storage storage ){
		try{
			Object[] v = storage.getEntries(RComponent.ATTRIBUTESDIRECTORY);
			ContextImpl cntxt = new ContextImpl();
			for (int i=0;i<v.length;i++){
				String entryname = (String) v[i];
				cntxt.sfAddAttribute(entryname, storage.getEntry(entryname));
				storage.commit();
			}
			
			cntxt.sfAddAttribute(RComponent.STORAGEATTRIB,storage.getStorageRef());
			storage.close();
			ComponentDescriptionImpl compdesc = new ComponentDescriptionImpl(null,cntxt,true);
			
			
			RComponent nprim = (RComponent) SFProcess.getProcessCompound().sfDeployComponentDescription(null, null, compdesc, null);
			
			nprim.sfRecover();
			//System.out.println(nprim.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
    public void sfDeployWith(Prim parent, Context cxt) throws
	SmartFrogDeploymentException, RemoteException {

    	try{
    		classname = (String)cxt.sfRemoveAttribute(RComponent.STORAGECLASSATTRIB);
    	} catch (Exception cause){
    		throw new SmartFrogDeploymentException(cause);
    	}
    	super.sfDeployWith(parent,cxt);
    }
	
	public synchronized void sfStart() throws SmartFrogException, RemoteException {
		super.sfStart();
		recoverer = new Recoverer();
		recoverer.start();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
	}
	
}
