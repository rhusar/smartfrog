/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import java.rmi.RemoteException;

public class SFSlpObjectLocatorImpl extends SFSlpLocatorImpl implements Prim, SFSlpObjectLocator {
    protected Object discoveredObject = null;
    
    public SFSlpObjectLocatorImpl() throws RemoteException {
        
    }
    
    // need some extra code in sfResolve in order to find Prim components
    public synchronized Object sfResolve(Reference r, int index) throws SmartFrogResolutionException {
        Object obj = null;
        try {
            obj = super.sfResolve(r, index);
        }catch(Exception ex) {
            ex.printStackTrace();
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward(ex);
        }
        if("HERE result".equals(r.elementAt(index).toString()) ) {
            discoveredObject = null;
            if(discoveryResults != null) {
                while(discoveredObject == null && discoveryResults.hasMoreElements()) {
                    ServiceURL theURL = (ServiceURL)discoveryResults.nextElement();
                    try {
                        discoveredObject = theURL.getURLPathObject();
                    }catch(Exception ex) {
                        discoveredObject = null;
                    }
                }
            }
            if(discoveredObject == null) {
                throw new SmartFrogResolutionException("SLP: The requested service was not found");
            }
            // return the discovered component
            return discoveredObject;
        }
        return obj;
    }
}

