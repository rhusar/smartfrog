/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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
package org.smartfrog.services.anubis.partition.util;

import org.smartfrog.services.anubis.partition.util.Epoch;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import java.net.InetAddress;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import java.net.UnknownHostException;

public class Config {

    static String LOCALHOST = new String("localhost");

    static public InetAddress getInetAddress(Prim p, String name) throws
        ConfigException,
        RemoteException,
        SmartFrogResolutionException,
        UnknownHostException {

        Object attr = p.sfResolveWithParser(name);
        if (attr instanceof Reference) {
            return (InetAddress) p.sfResolve( (Reference) attr);
        } else if (attr instanceof String) {
            if (attr.equals(LOCALHOST)) {
                return InetAddress.getLocalHost();
            } else {
                return InetAddress.getByName( (String) attr);
            }
        } else {
            throw new ConfigException("Unexpected value " + attr.toString());
        }
    }


    static public Identity getIdentity(Prim p, String name) throws
        RemoteException,
        SmartFrogResolutionException,
        ConfigException,
        UnknownHostException {
        Prim identity;
        Object obj = p.sfResolveWithParser(name);

        if (obj instanceof Reference) {
            obj = p.sfResolve( (Reference) obj);
        }

        if (obj instanceof Prim) {
            identity = (Prim) obj;
        } else {
            throw new ConfigException(
                "Unexpected value in place of identity -- got " + obj);
        }

        int magic = ( (Integer) identity.sfResolve("magic")).intValue();
        long epoch = ( (Epoch) identity.sfResolve("epoch")).longValue();
        boolean autoId = ( (Boolean) identity.sfResolve("getNodeFromLocalIP")).
            booleanValue();
        int id;

        if (autoId) {
            id = inetAddressToNode(InetAddress.getLocalHost());
        } else {
            id = ( (Integer) identity.sfResolve("node")).intValue();
        }

        return new Identity(magic, id, epoch);
    }


    static public long getLong(Prim p, String name) throws RemoteException,
        SmartFrogResolutionException {
        return ( (Integer) p.sfResolve(name)).longValue();
    }


    static public long getInt(Prim p, String name) throws RemoteException,
        SmartFrogResolutionException {
        return ( (Integer) p.sfResolve(name)).intValue();
    }


    static public boolean getBoolean(Prim p, String name) throws
        RemoteException, SmartFrogResolutionException, ConfigException {
        String boolStr = p.sfResolve(name).toString();
        if (boolStr.equals("true")) {
            return true;
        } else if (boolStr.equals("false")) {
            return false;
        } else {
            throw new ConfigException(name + " not true or false");
        }
    }


    static public boolean includes(Prim p, String name) {
        try {
            p.sfResolve(name);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    static private int inetAddressToNode(InetAddress address) {
        String ipAsString = address.getHostAddress();
        int dotIndex = ipAsString.lastIndexOf(".");
        String nodeStr = ipAsString.substring(dotIndex + 1, ipAsString.length());
        return Integer.parseInt(nodeStr);
    }
}
