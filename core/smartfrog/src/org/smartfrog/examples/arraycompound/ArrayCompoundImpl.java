/**(C) Copyright 1998-2003 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.


*/

package org.smartfrog.examples.arraycompound;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Enumeration;

import java.lang.String;
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
     *@exception  Exception  Description of the Exception
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
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        deployTemplates(hosts);
    }


    private void deployTemplates (Vector hosts) throws SmartFrogException {
        for (Enumeration h = hosts.elements(); h.hasMoreElements(); ) {
            deployTemplate((String) h.nextElement());
        }
    }

    private boolean deployTemplate(String hostname) {
        Prim p = null;
        try {
            Context sfHostContext = new ContextImpl();
            sfHostContext.sfAddAttribute(SmartFrogCoreKeys.SF_PROCESS_HOST, hostname);
            //Create a new copy of the description!
            ComponentDescription newcd = (ComponentDescription)((ComponentDescriptionImpl)template).copy();
            if (parent == null){
              if (sflog().isDebugEnabled()) {
                  sflog().debug("Creating new app: "+ hostname +":'"+parent.sfCompleteName()+"'");
              }
              p = this.sfCreateNewApp( "app-"+hostname.toString(),newcd , sfHostContext);
            } else {
              if (sflog().isDebugEnabled()) {
                  sflog().debug("Creating new child: "+ hostname +":'"+parent.sfCompleteName()+"'");
              }
              p = this.sfCreateNewChild("ch-"+hostname.toString(), parent,newcd, sfHostContext);
            }
        } catch (Exception e) {
            if (sflog().isErrorEnabled()) {
                sflog().error("deployTemplate: "+hostname, e);
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
     *@exception  Exception  Description of the Exception
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
            if (sflog().isErrorEnabled()) {
                sflog().error("Failed to read mandatory attribute: "+
                     e.toString(), e);
            }
            throw e;
        }
    }

    // Main component action methods
}
