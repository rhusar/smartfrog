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

import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;

import java.rmi.RemoteException;
import java.util.Vector;

/**
    Implements a SmartFrog locator for Prim components.
    The result of the discovery is a reference to a running component, or null
    if no component was found.
*/
public class SFSlpPrimLocatorImpl extends SFSlpLocatorImpl implements Prim, SFSlpPrimLocator {
    private Prim discoveredPrim = null;
    private Vector allPrims = null;
    
    public SFSlpPrimLocatorImpl() throws RemoteException {
        super();
    }
    
    // need some extra code in sfResolve in order to find Prim components
    public synchronized Object sfResolve(Reference r, int index) throws SmartFrogResolutionException {
        Object obj = super.sfResolve(r, index);
        
        if("HERE result".equals(r.elementAt(index).toString()) ) {
            boolean ok = false;
            if(discoveryResults != null) {
                if(returnAll) allPrims = new Vector();
                while(discoveryResults.hasMoreElements()) {
                    discoveredPrim = null;
                    ServiceURL theURL = (ServiceURL)discoveryResults.nextElement();
                    discoveredPrim = getPrimFromURL(theURL);
                    if(discoveredPrim != null) {
                        ok = true;
                        if(returnAll)allPrims.add(discoveredPrim);
                        else break;
                    }
                }
            }
            if(!ok) {
                throw new SmartFrogResolutionException("SLP: The requested service was not found");
            }
            
            // return the discovered component(s)
            if(returnAll) return allPrims;
            return discoveredPrim;
        }
        return obj;
    }
    
    private Prim getPrimFromURL(ServiceURL url) {
        Prim toReturn = null;
        try {
            toReturn = (Prim)url.getURLPathObject();
            toReturn.sfPing(this);
        }catch(Exception ex) { 
            // either not a correct URL, or the remote object has died.
            toReturn = null; 
        }
        
        return toReturn;
    }
}
