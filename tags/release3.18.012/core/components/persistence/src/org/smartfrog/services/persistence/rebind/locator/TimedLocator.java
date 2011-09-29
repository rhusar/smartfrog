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
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * TimedLocator is a static class implementing methods that can be used to locate a 
 * SmartFrog component at any of a list of hosts. If can be used in a non-smartfrog
 * JVM. This version checks all the hosts in parallel and returns the first successful
 * response.
 */
public class TimedLocator {

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
        return new TimedLocator().resolve(hosts, -1, name);
    }
    
    /**
     * A utility method to initially find a component that may be located through a smartfrog
     * daemon at one of several alternative hosts. It tries the given name at each host in turn before
     * giving up. This method can be used outside a SmartFrog daemon by a non-SmartFrog client.
     * 
     * @param hosts the list of hosts
     * @param port - port of the SmartFrog daemons (-1 = default)
     * @param name the name
     * @return the resolved object
     * @throws SmartFrogResolutionException
     */
    public static Object multiHostSfResolve(Vector<String> hosts, int port, String name) throws SmartFrogResolutionException {
        return new TimedLocator().resolve(hosts, port, name);
    }
    
    private TimedLocator() {
    }
    
    public Object resolve(Vector<String> hosts, int port, String name) throws SmartFrogResolutionException {
        synchronized(monitor) {
            count = hosts.size();
            result = null;
            for( String host : hosts ) {
                new Resolve(host, port, name).start();
            }
            while( result == null && count > 0 ) {
                try { monitor.wait(); } 
                catch (InterruptedException e) { }
            }
            if( result == null ) {
                throw new SmartFrogResolutionException("Failed to resolve " + name + " for the following reasons: \n" + reasons);
            }
            return result;
        }
    }
    
    protected class Resolve extends Thread {
        String host;
        String name;
        int port;
        public Resolve(String h, int p, String n) {
            host = h; 
            port = p;
            name = n;
        }
        public void run() {
            Prim reg = null;
            Object obj = null;
            Exception exception = null;
            try {
                reg = (Prim) SFProcess.getRootLocator().getRootProcessCompound(InetAddress.getByName(host), port);
                obj = reg.sfResolve(name);
            } catch (Exception e) {
                exception = e;
            }
            synchronized(monitor) {
                count--;
                if( exception != null ) {
                    reasons.append("   [").append(host).append("]: ").append(exception.getClass().getName()).append(" - ").append(exception.getMessage()).append("\n");
                } else { 
                    result = obj;
                }
                monitor.notify();
            }
        }
    }

    protected Object       result  = null;;
    protected int          count   = 0;
    protected StringBuffer reasons = new StringBuffer();
    protected Object       monitor = new Object();
    

}
