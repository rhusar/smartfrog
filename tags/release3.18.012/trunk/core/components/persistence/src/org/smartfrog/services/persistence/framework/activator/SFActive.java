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

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;


/**
 * SFActive is an interface for performing deployment and termination actions on the
 * node at which the interface is implemented. The persistence framework implements this 
 * interface so that it is only available when the framework is active.  
 */
public interface SFActive extends Remote {
	
	/**
	 * the default attribute name for a component implementing the SFActive interface
	 */
    public static String ACTIVE_ATTR = "sfActive";
    
    /**
     * This method invokes a SmartFrog deployment at the component referred to by the 
     * parent parameter. If the parent parameter is null the component description given 
     * is deployed as a new component under the process compound. If the parent is a
     * reference to a component the component description is deployed as a child
     * of the at component.
     * 
     * @param name the attribute name used for the new component
     * @param parent a reference to the parent (if null this is the process compound)
     * @param cd the component description to be deployed
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void deploy(String name, Reference parent, ComponentDescription cd) throws RemoteException, SmartFrogException;
    
    /**
     * This method terminates the component referred to by the reference parameter.
     * @param reference the component to be terminated
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void terminate(Reference reference) throws RemoteException, SmartFrogException;
}