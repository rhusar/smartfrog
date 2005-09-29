
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

import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.storage.*;
import org.smartfrog.sfcore.processcompound.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;


public class RComponentProxyLocatorImpl implements RComponentProxyLocator {

	protected String agenturl;
	protected StorageRef storef;
	
	public RComponentProxyLocatorImpl(String agenturl, StorageRef storef){
		this.agenturl = agenturl;
		this.storef = storef; 
	}
	
	public boolean isDead() throws ProxyLocatorException{
		StorageAgent sagent = null;
		try{
			RootLocator rl = SFProcess.getRootLocator(); 
			ProcessCompound pcmp = rl.getRootProcessCompound(InetAddress.getByName(agenturl));
			sagent = (StorageAgent) pcmp.sfResolve(new Reference(StorageAgent.ServiceName+":"+StorageAgent.ServiceName,true));
			return sagent.isDead(storef);
		} catch (SmartFrogException exc){
			throw new ProxyLocatorException("Failure while recovering Proxy",exc);
		} catch (RemoteException exc){
			throw new ProxyLocatorException("No Agent found at specified URL",exc);
		} catch (UnknownHostException exc){
			throw new RuntimeException("Malformed StorageAgent's URL:"+agenturl,exc);
		} catch (Exception exc){
			throw new RuntimeException("Problems while dealing with stable storage",exc);
		}
	}
	
	public RComponent getRComponentStub() throws ProxyLocatorException {
		
		StorageAgent sagent = null;
		try{
			RootLocator rl = SFProcess.getRootLocator(); 
			ProcessCompound pcmp = rl.getRootProcessCompound(InetAddress.getByName(agenturl));
			sagent = (StorageAgent) pcmp.sfResolve(new Reference(StorageAgent.ServiceName+":"+StorageAgent.ServiceName,true));
			//sagent = (StorageAgent) pcmp.sfResolveWithParser(StorageAgent.ServiceName+":"+StorageAgent.ServiceName);
			return (RComponent) sagent.getComponentStub(storef);
		} catch (SmartFrogException exc){
			throw new ProxyLocatorException("Failure while recovering Proxy",exc);
		} catch (RemoteException exc){
			throw new ProxyLocatorException("No Agent found at specified URL",exc);
		} catch (UnknownHostException exc){
			throw new RuntimeException("Malformed StorageAgent's URL:"+agenturl,exc);
		} catch (Exception exc){
			throw new RuntimeException("Problems while dealing with stable storage",exc);
		}
	}

	public String toString(){
		return agenturl+":"+storef.toString();
	}
	
	public boolean equals (Object obj){
		if(!(obj instanceof RComponentProxyLocatorImpl))
			return false;
		
		RComponentProxyLocatorImpl nobj = (RComponentProxyLocatorImpl) obj; 
		
		return ( this.agenturl.equals( nobj.agenturl ) &&
				 this.storef.equals(nobj.storef)); 
	}
}
