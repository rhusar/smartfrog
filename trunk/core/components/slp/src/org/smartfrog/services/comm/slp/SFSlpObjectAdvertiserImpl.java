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
import java.util.Iterator;

/**
Advertises any serializable java object.
 The object is made part of the URL, and a copy can be created from the URL
 when a service reply is received.
 */
public class SFSlpObjectAdvertiserImpl extends SFSlpAdvertiserImpl implements Prim, SFSlpObjectAdvertiser {    
    public SFSlpObjectAdvertiserImpl() throws RemoteException {
        super();
    }
    
    protected void buildURLs(Vector toAdvertise, Vector serviceTypes, Vector lifetimes) throws SmartFrogException, RemoteException {
        Iterator srvIter = toAdvertise.iterator();
        Iterator typeIter = serviceTypes.iterator();
        Iterator lifeIter = lifetimes.iterator();
        
        while(srvIter.hasNext()) {
            Object obj = srvIter.next();
            if(obj instanceof Reference) {
                obj = sfResolve((Reference)obj);
            }
                        
            String location = ServiceURL.objectToString(obj);
            String sType = (String)typeIter.next();
            if(!sType.startsWith(SERVICE_PREFIX)) sType = SERVICE_PREFIX+sType;
            int lifetime = ((Integer)lifeIter.next()).intValue();
            
            ServiceURL url = new ServiceURL(sType+":///"+location, lifetime);
            serviceURLs.add(url);
        }
    }
    
    public void registerObject(Object obj, String type, Vector attributes, int lifetime) throws ServiceLocationException {
        String location = ServiceURL.objectToString(obj);
        String sType = type;
        if(!sType.startsWith(SERVICE_PREFIX)) sType = SERVICE_PREFIX+sType;
        
        ServiceURL url = new ServiceURL(sType+":///"+location, lifetime);
        advertiser.register(url, attributes);
        serviceURLs.add(url);
    }
    
    public void deregisterObject(Object obj) throws ServiceLocationException {
        Iterator iter = serviceURLs.iterator();
        while(iter.hasNext()) {
            ServiceURL u = (ServiceURL)iter.next();
            Object o = u.getURLPathObject();
            if(o.equals(obj)) {
                advertiser.deregister(u);
                iter.remove();
            }
        }
    }
}
