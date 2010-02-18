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

package org.smartfrog.services.persistence.recovery;

import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * A RecoveryAgent is a Component responsible for recovering basic
 * components in the system. This component scans through the stores it finds
 * looking for components to recover. It assumes that it
 * will fail to access a store if the component that owns that store is
 * running.
 *
 *
 */
public class RecoveryAgent extends CompoundImpl implements Compound {

    Recoverer recoverer = null;
    ComponentDescription configData = null;
    Object monitor = new Object();
    boolean terminated;


    /**
     * A thread that performs the recovery loop.
     *
     */
    private class Recoverer extends Thread {

        public void run() {

            /**
             * The recovery loop
             */
            while ( true ) {

                synchronized ( monitor ) {
                    if ( terminated ) {
                        return;
                    }
                }


                /**
                 * Sleep a while
                 */
                try {
                    Thread.sleep( 2000 );
                } catch ( InterruptedException exc ) {}


                /**
                 * Get a list of all the stores
                 */
                Vector stores = null;
                try {
                    stores = Storage.getStores( configData );
                } catch ( StorageException ex1 ) {
                    ex1.printStackTrace();
                    break;
                }

                /**
                 * Try to access the stores. If a store can be accessed we
                 * assume its component needs to be recovered - stores are
                 * assumed to be locked by their components can can not be
                 * accessed by others while they are alive. This is dependent
                 * on the implementation of the store.
                 */
                System.out.println( "Examining stores" );
                for ( int i = 0; i < stores.size(); i++ ) {
                    String storeName = ( String ) stores.get( i );
                    Storage storage = null;
                    try {
                        configData.sfReplaceAttribute( Storage.NAME_ATTRIB, storeName );
                        storage = Storage.createExistingStorage( configData );
                        System.out.println( "Recovering " + storeName );
                        if ( !storage.getEntry( RComponent.WFSTATUSENTRY ).
                             equals( RComponent.WFSTATUS_DEAD ) ) {
                            restoreFromStorage( storage );
                        } else {
                            System.out.println( storeName + " has already terminated." );
                        }
                    } catch ( StorageException exc ) {
                        System.err.println( "Component " + storeName + " is apparently running." );
                    } catch ( SmartFrogRuntimeException ex ) {
                        System.err.println( "Component " + storeName + " is apparently running." );
                    }
                    try {
                        if( storage != null ) {
                            storage.close();
                        }
                    } catch ( StorageException ex2 ) {
                    }
                }
            }
        }
    }


    public RecoveryAgent() throws RemoteException {
        super();
    }


    /**
     * restoreFromStorage recovers a component from the context saved in the
     * given store.
     *
     * @param storage the store containing the context
     */
    private void restoreFromStorage( Storage storage ) {
        try {
            Object[] v = storage.getEntries();
            ContextImpl cntxt = new ContextImpl();
            for ( int i = 0; i < v.length; i++ ) {
                String entryname = ( String ) v[i];
                cntxt.sfAddAttribute( entryname, storage.getEntry( entryname ) );
                storage.commit();
            }

            /**
             * the recovered component will want its storage, but storage can
             * not be serialized, so pass a reference instead.
             */
            cntxt.sfAddAttribute( RComponent.STORAGEATTRIB, storage.getStorageRef() );
            storage.close();

            /**
             * Get the parent
             */
            Object parentObj = cntxt.sfResolveAttribute(RComponent.SFPARENT);
            Prim parent = (Prim)(parentObj instanceof Prim ? parentObj : null);

            /**
             * Construct the component description from the context and
             * deploy it.
             */
            ComponentDescriptionImpl compdesc = new ComponentDescriptionImpl( null, cntxt, true );
            RComponent nprim = ( RComponent ) SFProcess.getProcessCompound().
                               sfDeployComponentDescription( null, parent, compdesc, null );

            nprim.sfRecover();

        } catch ( Exception e ) {
            System.out.println( "Failed to complete the recovery");
        }

    }


    /**
     * sfDeploy over-ride - get the storage configuration
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        configData = ( ComponentDescription )sfResolve( Storage.CONFIG_DATA );
    }


    /**
     * sfStart over-ride - starts the recovery loop.
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        terminated = false;
        recoverer = new Recoverer();
        recoverer.start();
    }


    /**
     * sfTerminateWith over-ride - stop the recovery loop
     * @param tr TerminationRecord
     */
    public void sfTerminateWith( TerminationRecord tr ) {
        synchronized ( monitor ) {
            terminated = true;
        }
        super.sfTerminateWith( tr );
    }

}
