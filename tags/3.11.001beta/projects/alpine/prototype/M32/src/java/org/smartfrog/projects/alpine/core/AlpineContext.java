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

import javax.servlet.ServletContext;
import java.util.HashMap;

/**
 * This is the servlet-specific context. It includes all the endpoint contexts.
 */
public class AlpineContext {
    
    /**
     * unique name of the context for use in the servlet context
     */ 
    public static final String NAME= AlpineContext.class.getCanonicalName();
    
    private EndpointContextMap endpoints=new EndpointContextMap();

    public EndpointContextMap getEndpoints() {
        return endpoints;
    }

    /**
     * yes, singletons are bad :)
     */ 
    private static AlpineContext alpineCtx;
    
    /**
     * get the alpine context from the servlet context ; create it if needed
     *
     * @return the current context
     */
    public static synchronized AlpineContext getAlpineContext() {
        
        if (alpineCtx == null) {
            alpineCtx = createAlpineContext();
        }
        return alpineCtx;
    }
    
    /**
     * Create a new alpine context
     *
     * @return a new context
     */
    private static AlpineContext createAlpineContext() {
        return new AlpineContext();
    }    
}
