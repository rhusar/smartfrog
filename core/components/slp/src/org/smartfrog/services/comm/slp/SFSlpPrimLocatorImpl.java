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

import java.util.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.*;

import sun.misc.*;

/**
    Implements a SmartFrog locator for Prim components.
    The result of the discovery is a reference to a running component, or null
    if no component was found.
*/
public class SFSlpPrimLocatorImpl extends SFSlpLocatorImpl implements Prim, SFSlpPrimLocator {
    private Prim discoveredPrim = null;
    
    public SFSlpPrimLocatorImpl() throws RemoteException {
        super();
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
            discoveredPrim = null;
            if(discoveryResults != null) {
                while(discoveredPrim == null && discoveryResults.hasMoreElements()) {
                    ServiceURL theURL = (ServiceURL)discoveryResults.nextElement();
                    try {
                        discoveredPrim = getReferenceFromUrl(theURL);
                    }catch(Exception ex) {
                        discoveredPrim = null;
                    }
                }
            }
            if(discoveredPrim == null) {
                throw new SmartFrogResolutionException("SLP: The requested service was not found");
            }
            // return the discovered component
            return discoveredPrim;
        }
        return obj;
    }    
    
    protected Prim getReferenceFromUrl(ServiceURL url) throws Exception {
        //System.out.println("Getting reference...");
        // get rmi reference...
        String objectReference = url.getURLPath();
        // remove the / at the start of the url path !
        if (objectReference.startsWith("/")) objectReference = objectReference.substring(1);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] byteArray = decoder.decodeBuffer(objectReference);
        ByteArrayInputStream isr = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(isr);
        RemoteStub b = (RemoteStub) ois.readObject();
        ois.close();
        
        return (Prim)b;
    }
}
