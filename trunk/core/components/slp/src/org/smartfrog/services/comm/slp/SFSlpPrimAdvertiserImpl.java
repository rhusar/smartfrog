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
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.util.Vector;
import java.util.Iterator;

/**
    Implements a SF advertiser for Prim components.
    The component to advertise is given in the description file.
*/
public class SFSlpPrimAdvertiserImpl extends SFSlpAdvertiserImpl implements Prim, SFSlpPrimAdvertiser {
   // protected Prim toAdvertise = null;
        
    public SFSlpPrimAdvertiserImpl() throws RemoteException {
        super();
    }
    
    protected void buildURLs(Vector toAdvertise, Vector serviceTypes, Vector lifetimes) throws SmartFrogException, RemoteException {
        Iterator srvIter = toAdvertise.iterator();
        Iterator typeIter = serviceTypes.iterator();
        Iterator lifeIter = lifetimes.iterator();
        
        while(srvIter.hasNext()) {
            Prim p = (Prim)sfResolve( (Reference)srvIter.next() );
            RemoteStub s;
            if(p instanceof RemoteStub) s = (RemoteStub)p;
            else s = (RemoteStub)((PrimImpl)p).sfExportRef();
            
            String location = ServiceURL.objectToString(s);
            String sType = (String)typeIter.next();
            if(sType.indexOf(":") != -1) {
                throw new SmartFrogException("SLP: The given service type for a Prim should be a single word.");
            }    
            
            if(sType.equals("")) sType = PRIM_SERVICE_TYPE;
            else sType = PRIM_SERVICE_TYPE+":"+sType;
            
            int lifetime = ((Integer)lifeIter.next()).intValue();
            
            ServiceURL url = new ServiceURL(sType+":///"+location, lifetime);
            serviceURLs.add(url);
        }
    }
    
    public void registerPrim(Prim p, String type, Vector attributes, int lifetime) throws ServiceLocationException {
        RemoteStub s;
        try {
            if(p instanceof RemoteStub) s = (RemoteStub)p;
            else s = (RemoteStub)((PrimImpl)p).sfExportRef();
        }catch(SmartFrogException ex) {
            throw new ServiceLocationException(ServiceLocationException.INVALID_REGISTRATION,
                                               "Failed to get RemoteStub for Prim");
        }
        
        String location = ServiceURL.objectToString(s);
        if(type.indexOf(":") != -1) {
            throw new ServiceLocationException(ServiceLocationException.INVALID_REGISTRATION,
                                               "Service type not valid");
        }
        String srvType = PRIM_SERVICE_TYPE;
        if(!type.equals("")) srvType += ":"+type;
        
        ServiceURL url = new ServiceURL(srvType+":///"+location, lifetime);
        advertiser.register(url, attributes);
        serviceURLs.add(url);
    }
    
    public void deregisterPrim(Prim p) throws ServiceLocationException {
        Iterator iter = serviceURLs.iterator();
        while(iter.hasNext()) {
            ServiceURL u = (ServiceURL)iter.next();
            Prim prim = (Prim)u.getURLPathObject();
            if(prim.equals(p)) {
                advertiser.deregister(u);
                iter.remove();
            }
        }
    }
}   

