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

package org.smartfrog.projects.alpine.core;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * map of endpoint contexts
 */
public class EndpointContextMap {
        
        private Hashtable<String, EndpointContext> map=new Hashtable<String, EndpointContext>();
    
    /**
     * map from a request to an endpoint;
     * @param request the incoming request
     * @return the context or null
     */ 
    public EndpointContext lookup(HttpServletRequest request) {
        String path=request.getPathInfo();
        if(path==null) {
            return null;
        }
        return map.get(path);
    }
    
    /**
     * register an endpoint under the path
     * Also sets the {@link ContextConstants#ATTR_PATH} attribute in the endpoint context
     * @param path path to register on the server
     * @param context the context of the endpoint
     */ 
    public synchronized void register(String path, EndpointContext context) {
        context.put(ContextConstants.ATTR_PATH,path);
        map.put(path, context);
    }
    
    /**
     * unregister a context
     * @param context the context of the endpoint
     * @return true iff the context was found and removed
     */ 
    public synchronized boolean unregister(EndpointContext context) {
        String path=(String) context.get(ContextConstants.ATTR_PATH);
        if(path==null) {
            return false;
        }
        return unregister(path);
    }

    /**
     * Unregister a context by path
     * @param path path  on the server
     * @return true if the path was matched to an endpoint
     */ 
    public boolean unregister(String path) {
        if(map.get(path)==null) {
            return false;
        }
        map.remove(path);
        return true;
    }


}
