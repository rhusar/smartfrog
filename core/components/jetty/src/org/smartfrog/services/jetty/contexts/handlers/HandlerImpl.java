/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.jetty.contexts.handlers;

import org.mortbay.http.HttpHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.smartfrog.services.jetty.JettyHelper;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * Date: 21-Jun-2004
 * Time: 22:40:14
 */
public class HandlerImpl extends PrimImpl {
    JettyHelper jettyHelper = new JettyHelper(this);

    public HandlerImpl() throws RemoteException {
        super();
    }

    /**
     * add a handler to the context of this smartfrog instance
     * @param handler
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     */
    protected void addHandler(HttpHandler handler) throws SmartFrogException, RemoteException {
	     ServletHttpContext cxt;
             cxt=jettyHelper.getServletContext(true);
             cxt.addHandler(handler);
    }

}
