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
import java.net.*;

import sun.misc.*;

/**
    Implements a SF advertiser for Prim components.
    The component to advertise is given in the description file.
*/
public class SFSlpPrimAdvertiserImpl extends SFSlpAdvertiserImpl implements Prim {
    protected Object toAdvertise = null;
    
    public SFSlpPrimAdvertiserImpl() throws RemoteException {
        super();
    }
    
    // lifecycle methods...
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        try {
        super.sfDeploy();  
        // get the component to advertise
        toAdvertise = sfResolve("toAdvertise");
        // find the location (as a rmi reference)
        serviceLocation = getRmiReferenceForObject(toAdvertise);
        }catch(Exception e) {
            e.printStackTrace();
            throw (SmartFrogException)SmartFrogException.forward(e);
        }
    }  
    
    protected String getRmiReferenceForObject(Object p) throws SmartFrogException {
        Object obj = (Prim)p;
        try {
            String encodedRef  = "";
            if (obj instanceof PrimImpl) { //local object --> we need to get the reference
                encodedRef = encodedReference(((PrimImpl)obj).sfExportRef());
            } else if (obj instanceof RemoteStub) { // remote object --> just encode the stub
                encodedRef = encodedReference(((RemoteStub)obj));
            }
            
            String lha = InetAddress.getLocalHost().getHostName();
            String loc = lha + "/" + encodedRef;
            return loc;
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
        * Encode a remote reference.
     * @param ref the reference to encode.
     * @return the 64-encoded string representing the reference.
     */
    protected String encodedReference(Object ref) throws Exception{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(ref);
        oos.close();
        BASE64Encoder encoder = new BASE64Encoder();
        String objRef64enc =encoder.encode(os.toByteArray());
        os.flush();
        os.close();
        return objRef64enc;
    }
}

