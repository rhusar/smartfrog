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

package org.smartfrog.services.utils.setproperty;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Enumeration;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;


/**
 * Service Resource Manager that mediates between FF and Utility Resource
 * Manager.
 */
public class SFSetPropertyImpl extends CompoundImpl implements Compound, SFSetProperty {

    String name = null;
    Object value = null;
    boolean replace = true;
    private Log log;

    /**
     * Constructor for the SFServiceResourceManagerImpl object.
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public SFSetPropertyImpl() throws RemoteException {
    }

    /**
     * sfDeployWith.
     *
     * @throws SmartFrogException In case of error while deployment
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeployWith(Prim parent, Context cxt) throws SmartFrogDeploymentException, RemoteException {
        try {
          log = this.sfGetApplicationLog();//.sfGetLog(sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, "", true));
          // Mandatory attributes.
          try {
            name = (String)cxt.sfResolveAttribute(ATR_NAME);
            value = cxt.sfResolveAttribute(ATR_VALUE);
          } catch (SmartFrogException e) {
            if (log.isErrorEnabled())
              log.error(
                  "Failed to read mandatory attribute: " + e.toString(), e);
            throw e;
          }
          try {
            replace = ((Boolean)cxt.sfResolveAttribute(ATR_REPLACE)).booleanValue();
          } catch (SmartFrogContextException e) {
            if (log.isDebugEnabled())
              log.debug(
                  "Failed to read optional attribute: " + e.toString());
          }


          String oldValue = System.getProperty(name, "non defined");
          if (replace)  System.setProperty(name,value.toString());
          else   System.setProperty(name,oldValue+value.toString());
          if (log.isDebugEnabled()) {
            log.debug("Setting property: " + name +
                      ", NEW VALUE: " + value.toString() +
                      ", OLD VALUE: " + oldValue+
                      ", VALUE READ:" + System.getProperty(name));
          }

        } catch (Throwable t) {
          if (log.isErrorEnabled())
            log.error(t);
          throw new SmartFrogDeploymentException(t, this);
        }
        //super.sfDeploy();
        super.sfDeployWith(parent, cxt);
    }
}
