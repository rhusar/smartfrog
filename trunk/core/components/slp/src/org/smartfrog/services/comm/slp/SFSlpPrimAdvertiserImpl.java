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

/**
    Implements a SF advertiser for Prim components.
    The component to advertise is given in the description file.
*/
public class SFSlpPrimAdvertiserImpl extends SFSlpAdvertiserImpl implements Prim, SFSlpPrimAdvertiser {
    protected Prim toAdvertise = null;
    protected Reference toAdvertiseRef = null;
    protected ServiceURL referenceURL = null;
    protected String referenceServiceType;
    protected boolean advertiseComponent;
    protected boolean advertiseReference;
    
    public SFSlpPrimAdvertiserImpl() throws RemoteException {
        super();
    }
    
    // lifecycle methods...
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
            super.sfDeploy();  
            // get the component to advertise
            toAdvertise = (Prim)sfResolve("toAdvertise");
            referenceServiceType = (String)sfResolve("referenceServiceType");
            advertiseComponent = ((Boolean)sfResolve("advertiseComponent")).booleanValue();
            advertiseReference = ((Boolean)sfResolve("advertiseReference")).booleanValue();
            
            if(referenceServiceType.endsWith(":")) 
                referenceServiceType = referenceServiceType.substring(0, referenceServiceType.length()-1);
            
            toAdvertiseRef = toAdvertise.sfCompleteName();
            
            // build service URL
            if(advertiseComponent) serviceLocation = "/" + toAdvertiseRef.toString();
            else if(advertiseReference) {
                serviceType = referenceServiceType;
                serviceLocation = "/" + ServiceURL.objectToString(toAdvertiseRef);
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw (SmartFrogException)SmartFrogException.forward(e);
        }
    }  
    
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if(advertiseComponent && advertiseReference) {
            // need to advertise the reference since only one is 
            // handled by the super class.
            try {
                // create url from type + location
                referenceURL = new ServiceURL(referenceServiceType, toAdvertiseRef, serviceLifetime);
                advertiser.register(referenceURL, serviceAttributes);
            }catch(Exception ex) {
                throw (SmartFrogException) SmartFrogException.forward(ex);
            }
        }
    }
    
    public synchronized void sfTerminateWith(TerminationRecord r) {
        if(advertiseComponent && advertiseReference) {
            // stop advertising reference.
            try {
                advertiser.deregister(referenceURL);
            }catch(ServiceLocationException ex) { }
        }
        
        super.sfTerminateWith(r);
    }
}   

