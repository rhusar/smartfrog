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

package org.smartfrog.services.dns;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * SmartFrog component representing an update to the DNS server that could
 * imply adding a new view, zone or binding.
 * Uses a DNSManager to perform the update. Typically would share fate with
 * an application through liveness and hence try to clean up bindings as an
 * application terminates or fails.
 */
public class DNSModifierImpl extends PrimImpl implements DNSModifier {

    /** A configuration  update for the DNS server. */ 
    DNSComponent update = null;

    /** A reference to the DNS manager. */ 
    DNSManager manager = null;

    /** Whether the modification is only IP bindings. */
    boolean isOnlyBindings = false;

    /** A static reference for the configuration update. */
    static final Reference REF_UPDATE =
        new Reference(ReferencePart.here("data"));

    /** A static reference for finding the DNS manager. */
    static final Reference REF_MANAGER =
        new Reference(ReferencePart.here("manager"));
        

    
    /**
     * default constructor as required by SmartFrog.
     * @exception RemoteException if an error occurs
     */
    public DNSModifierImpl()
        throws RemoteException {
    }

    /**
     * sfDeploy lifecycle method as required by SmartFrog. It resolves  
     * the binding to the DNS manager and the binding parameters.
     *
     * @exception SmartFrogException if an error occurs
     * @exception RemoteException if an error occurs
     */
    public void sfDeploy()
        throws SmartFrogException, RemoteException {

        super.sfDeploy();
        ComponentDescription updateCD = 
            sfResolve(REF_UPDATE, 
                      /* The "default" value is irrelevant. */
                      new ComponentDescriptionImpl(null,null,false),
                      true); // ignore default...
        update =  DNSComponentImpl.deployComponent(null, updateCD);
        isOnlyBindings = checkOnlyBindings(update);
        manager = (DNSManager) sfResolve(REF_MANAGER);
    }

    /**
     *  Checks if a hierarchy contains no zone, view or data components.
     *
     *
     * @param comp A hierarchy to be checked.
     * @return true if a hierarchy contains no zone, view or data components.
     * @exception DNSModifierException if an error occurs while 
     * traversing the hierarchy.
     */
    public static boolean checkOnlyBindings(DNSComponent comp) 
        throws DNSModifierException {
        
        try {
            Boolean[] results = {Boolean.FALSE}; // default value irrelevant
            CDVisitor vis =  DNSComponentImpl.getOnlyBindingsVisitor(results);
            comp.visit(vis, true);
            return results[0].booleanValue();
        } catch (Exception e) {
            throw new  DNSModifierException("Can't check only bindings", e);
        }
    }

    /**
     * sfStart lifecycle method as required by SmartFrog. It registers
     * the update with the manager.
     *
     * @exception SmartFrogException if an error occurs
     * @exception RemoteException if an error occurs
     */
    public void sfStart()
        throws SmartFrogException, RemoteException {

        super.sfStart();
        manager.register(this);

    }

    /**
     * sfTerminateWith lifecycle method as required by SmartFrog. 
     * It tries to unregister the update with the DNS manager.
     * @param tr A reason for termination.
     */
    public void sfTerminateWith(TerminationRecord tr) {
        
        try {
            manager.unregister(this);
        } catch (Exception e) {
            System.out.println("Cannot unregister update" 
                               + "got exception" + e);
        } finally {
            super.sfTerminateWith(tr);
        }
    }

    /**
     * Gets the component description that encapsulates the configuration 
     * changes submitted to the DNS server.
     *
     * @return The component description that encapsulates the configuration 
     * changes submitted to the DNS server.
     *
     */
    public DNSComponent getUpdate() {

        return update;
    }


   /**
     * Gets whether this update contains only bindings and no structural
     * changes.
     *
     * @return True if this update contains only bindings and no structural
     * changes.
     */
     public boolean isOnlyBindings() {

        return isOnlyBindings;
    }

    /**
     * Gets the manager that performed the DNS binding update.
     *
     * @return The manager that performed the DNS binding update.
     */
    public DNSManager getDNSManager() {
        
        return manager;
    }

}
