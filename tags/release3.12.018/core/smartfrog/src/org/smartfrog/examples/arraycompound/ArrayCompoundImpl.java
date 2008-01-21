/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.examples.arraycompound;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

/**
 *
 *  This class is included in rmitargets that is read by the rmic compiler
 */
public class ArrayCompoundImpl extends CompoundImpl implements Compound, ArrayCompound {
    //component data
    Vector hosts;
    ComponentDescription template;
    Compound parent = this;

    private String myName = "ArrayCompoundImpl";

    /**
     *  Constructor for the ArrayCompound object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public ArrayCompoundImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *  sfDeploy: reads ArrayCompound attributes and configures counter thread
     *  The superclass implementation of sfDeploy is called before the
     *  component specific initialization code (reading ArrayCompound attributes
     *  and configuring counter thread) to maintain correct behaviour of
     *  initial deployment and starting the heartbeat monitoring of this
     *  component
     * @throws SmartFrogException  error in deploying the component
     * @throws RemoteException if any network and RMI error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
            super.sfDeploy();
            /**
             *  Returns the complete name for ArrayCompound component from the root
             *  of application.If an exception is thrown it returns null
             */
            myName = this.sfCompleteNameSafe().toString();
            readSFAttributes();
    }

    /**
     *  sfStart: starts ArrayCompound and deploys children templates!
     * @throws SmartFrogException  error in starting the component
     * @throws RemoteException if any network and RMI error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        deployTemplates(hosts);
    }

    /**
     * Deploy the template description in hosts
     * @param hostlist Vector of hosts
     * @throws SmartFrogException error in deploying
     */
    private void deployTemplates (Vector hostlist) throws SmartFrogException {
        for (Enumeration h = hostlist.elements(); h.hasMoreElements(); ) {
            deployTemplate((String) h.nextElement());
        }
    }

    /**
     * Deploy the template description in the given host
     * @param hostname
     * @return boolean success or failure
     */
    private boolean deployTemplate(String hostname) {
        Prim p = null;
        try {
            Context sfHostContext = new ContextImpl();
            sfHostContext.sfAddAttribute(SmartFrogCoreKeys.SF_PROCESS_HOST, hostname);
            //Create a new copy of the description!
            ComponentDescription newcd = (ComponentDescription)((ComponentDescriptionImpl)template).copy();
            if (parent == null){
              if (sfLog().isDebugEnabled()) {
                  sfLog().debug("Creating new app: "+ hostname +":'"+parent.sfCompleteName()+"'");
              }
              p = this.sfCreateNewApp( "app-"+hostname.toString(),newcd , sfHostContext);
            } else {
              if (sfLog().isDebugEnabled()) {
                  sfLog().debug("Creating new child: "+ hostname +":'"+parent.sfCompleteName()+"'");
              }
              p = this.sfCreateNewChild("ch-"+hostname.toString(), parent,newcd, sfHostContext);
            }
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("deployTemplate: "+hostname, e);
            }
            return false;
        }
        return true;
    }


    // End LifeCycle methods

    // Read Attributes from description

    /**
     *  Reads optional and mandatory attributes
     *
     * @throws SmartFrogException  error in reading the attributes
     * @throws RemoteException if any network and RMI error
     *
     */
    private void readSFAttributes() throws SmartFrogException, RemoteException {
        //
        // Mandatory attributes.
        try {
             //True to Get exception thown!
             template = sfResolve(ATR_TEMPLATE, template, true);
             hosts    = sfResolve(ATR_HOSTS, hosts, true);
             Object parentObj = sfResolve(ATR_PARENT, parent, false);
             if (parentObj instanceof SFNull) {
               parent = null;
             } else {
               // re-resolve to get a compound
               // slow but god error messaging
               parent =  sfResolve(ATR_PARENT, parent, false);
             }
        } catch (SmartFrogResolutionException e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Failed to read mandatory attribute: "+
                     e.toString(), e);
            }
            throw e;
        }
    }

    // Main component action methods
}
