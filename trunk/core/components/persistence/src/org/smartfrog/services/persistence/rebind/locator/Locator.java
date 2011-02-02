/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.rebind.locator;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * Locator is a static class implementing methods that can be used to locate a 
 * SmartFrog component at any of a list of hosts. If can be used in a non-smartfrog
 * JVM. This version iterates through the list of hosts in sequence.
 */
public class Locator {
    
    
 
    /**
     * A utility method to initially find a component that may be located through a smartfrog
     * daemon at one of several alternative hosts. It tries the given name at each host in turn before
     * giving up. This method can be used outside a SmartFrog daemon by a non-SmartFrog client.
     * 
     * @param hosts the list of hosts
     * @param name the name
     * @return the resolved object
     * @throws SmartFrogResolutionException
     */
    public static Object multiHostSfResolve(Vector<String> hosts, String name) throws SmartFrogResolutionException {
        return multiHostSfResolve(hosts, -1, name);
    }
    
    /**
     * A utility method to initially find a component that may be located through a smartfrog
     * daemon at one of several alternative hosts. It tries the given name at each host in turn before
     * giving up. This method can be used outside a SmartFrog daemon by a non-SmartFrog client.
     * 
     * @param hosts the list of hosts
     * @param port - the port used by the SmartFrog daemon (-1 = default)
     * @param name the name
     * @return the resolved object
     * @throws SmartFrogResolutionException
     */
    public static Object multiHostSfResolve(Vector<String> hosts, int port, String name) throws SmartFrogResolutionException {
                
        Prim reg = null;
        Object obj = null;
        StringBuffer reasons = new StringBuffer();

        Iterator<String> hostIter = hosts.iterator();
        while (hostIter.hasNext()) {
            
            String host = hostIter.next();
            
            try {
                /**
                 * Get the remote register 
                 */
                reg = (Prim) SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(host), port);

                /**
                 * Get the component 
                 */
                obj = reg.sfResolve(name);
                
                return obj;
                
            } catch (Exception e) {
                reasons.append("   [").append(host).append("]: ").append(e.getClass().getName()).append(" - ").append(e.getMessage()).append("\n");
            } 
        }
        
        throw new SmartFrogResolutionException("Failed to resolve " + name + " for the following reasons: \n" + reasons);
    }
    

}
