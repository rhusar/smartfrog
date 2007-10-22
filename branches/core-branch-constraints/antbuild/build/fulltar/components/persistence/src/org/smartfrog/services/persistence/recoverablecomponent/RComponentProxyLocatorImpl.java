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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.storage.StorageAgent;
import org.smartfrog.services.persistence.storage.StorageRef;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.RootLocator;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * Implementation of the RComponentProxyLocator.
 * The RComponentProxyLocator is a service that can be used to rebind to
 * a recoverable component (i.e. obtain a reference). This implementation
 * assumes the existence of a (possibly) remote storage agent that can
 * be used to obtain a reference for the target recoverable component.
 */
public class RComponentProxyLocatorImpl implements RComponentProxyLocator {

    protected String agenturl;
    protected StorageRef storef;


    /**
     * Constructor
     * @param agenturl a url pointing to the machine containing the storage agent
     * @param storef a reference to the storage
     */
    public RComponentProxyLocatorImpl( String agenturl, StorageRef storef ) {
        this.agenturl = agenturl;
        this.storef = storef;
    }



    /**
     * Test for a component being dead.
     * isDead determines if the target recoverable component has terminated
     * (as opposed to failed). A terminated component will not be recovered.
     * If the target is not dead it may be operable or it may have failed.
     *
     * @return true if the target has terminated
     * @throws ProxyLocatorException
     */
    public boolean isDead() throws ProxyLocatorException {
        StorageAgent sagent = null;
        try {
            RootLocator rl = SFProcess.getRootLocator();
            ProcessCompound pcmp = rl.getRootProcessCompound( InetAddress.getByName( agenturl ) );
            sagent = ( StorageAgent ) pcmp.sfResolve( StorageAgent.ServiceName );
            return sagent.isDead( storef );
        } catch ( SmartFrogException exc ) {
            throw new ProxyLocatorException( "Failure while recovering Proxy", exc );
        } catch ( RemoteException exc ) {
            throw new ProxyLocatorException( "No Agent found at specified URL", exc );
        } catch ( UnknownHostException exc ) {
            throw new RuntimeException( "Malformed StorageAgent's URL:" + agenturl, exc );
        } catch ( Exception exc ) {
            throw new RuntimeException( "Problems while dealing with stable storage", exc );
        }
    }


    /**
     * getRComponentStub returns a stub for the recoverable component.
     *
     * @return the stub
     * @throws ProxyLocatorException
     */
    public RComponent getRComponentStub() throws ProxyLocatorException {

        StorageAgent sagent = null;
        try {
            RootLocator rl = SFProcess.getRootLocator();
            ProcessCompound pcmp = rl.getRootProcessCompound( InetAddress.getByName( agenturl ) );
            sagent = ( StorageAgent ) pcmp.sfResolve( StorageAgent.ServiceName );
            Object obj = sagent.getComponentStub( storef );
            return ( RComponent ) obj;
        } catch ( SmartFrogException exc ) {
            throw new ProxyLocatorException( "Failure while recovering Proxy", exc );
        } catch ( RemoteException exc ) {
            throw new ProxyLocatorException( "No Agent found at specified URL", exc );
        } catch ( UnknownHostException exc ) {
            throw new RuntimeException( "Malformed StorageAgent's URL:" + agenturl, exc );
        } catch ( Exception exc ) {
            throw new RuntimeException( "Problems while dealing with stable storage", exc );
        }
    }


    /**
     * a string representation of this object.
     *
     * @return String
     */
    public String toString() {
        return agenturl + ":" + storef.toString();
    }



    /**
     * Two proxy locators are equal if the have the same url and store reference.
     * @param obj Object
     * @return boolean
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof RComponentProxyLocatorImpl ) ) {
            return false;
        }

        RComponentProxyLocatorImpl nobj = ( RComponentProxyLocatorImpl ) obj;

        return ( this.agenturl.equals( nobj.agenturl ) &&
                 this.storef.equals( nobj.storef ) );
    }
}
