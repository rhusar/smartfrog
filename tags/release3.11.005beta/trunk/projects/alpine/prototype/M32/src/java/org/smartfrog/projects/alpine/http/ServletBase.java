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

package org.smartfrog.projects.alpine.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;

/**
 
 */
public class ServletBase extends HttpServlet {

    private static final Log log =
            LogFactory.getLog(ServletBase.class);
        
    
    public ServletBase() {
    }


    public static Log getLog() {
        return log;
    }
    
    /**
     * our initialize routine; subclasses should call this if they override it
     */
    public void init() throws javax.servlet.ServletException {
    }


    /**
     * get the svc context
     * @return the context
     */ 
    public ServletContext getServletContext() {
        return getServletConfig().getServletContext();
    }    
}
