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


package org.smartfrog.services.jetty;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.services.jetty.contexts.ServletContextIntf;
import org.mortbay.http.HttpServer;
import org.mortbay.http.HttpHandler;
import org.mortbay.jetty.servlet.ServletHttpContext;

import java.rmi.RemoteException;

/**
 * This helper class contains all the binding policy for use in contexts and servlets.
 * Date: 21-Jun-2004
 * Time: 22:02:20
 */
public class JettyHelper {

    /**
     * who owns this class
     */
    Prim owner;

    Reference serverNameRef = new Reference(JettyIntf.SERVER);

    public JettyHelper(Prim owner) {
        this.owner = owner;
    }

    /**
     * locate jetty
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     * @param mandatory
     */
    public HttpServer findJettyServer(boolean mandatory) throws SmartFrogException, RemoteException {
        Reference serverName=null;
        serverName = owner.sfResolve(serverNameRef, serverName, true);
        ProcessCompound process = SFProcess.getProcessCompound();
        HttpServer server = (HttpServer) process.sfResolveId(serverName);
        if ( mandatory && server == null ) {
            throw new SmartFrogException("Could not locate a Jetty Server");
        }
        return server;
    }

    /**
     * save the jetty info for retrieval
     * @param serverName
     * @param server
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void cacheJettyServer(String serverName, HttpServer server)
            throws SmartFrogException, RemoteException {
        ProcessCompound process = SFProcess.getProcessCompound();
        process.sfAddAttribute(serverName, server);

    }


    /**
     * locate jettyhome
     * @return jetty home or null
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public String findJettyHome() throws SmartFrogException, RemoteException {
        ProcessCompound process = SFProcess.getProcessCompound();
        String jettyhome = (String) process.sfResolveId(JettyIntf.JETTY_HOME);
        return jettyhome;
    }

    /**
     * save jetty home for retrieval
     * @param jettyhome
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void cacheJettyHome(String jettyhome) throws SmartFrogRuntimeException, RemoteException {
        ProcessCompound process = SFProcess.getProcessCompound();
        process.sfAddAttribute(JettyIntf.JETTY_HOME, jettyhome);
    }

    /**
     * for servlets: get the servlet context.
     *
     * @param mandatory set this to true if you want an exception if there is no context
     * @return context, or null if there is not one found
     * @throws SmartFrogException
     * @throws RemoteException
     * @param mandatory
     */
    public  ServletHttpContext getServletContext(boolean mandatory)
            throws SmartFrogException,RemoteException {
        Prim parent = owner.sfParent();
        Prim grandParent = parent.sfParent();
        ServletHttpContext context = (ServletHttpContext) grandParent.
                sfResolveId(ServletContextIntf.CONTEXT);
        if(mandatory && context==null) {
            throw new SmartFrogException("Could not locate "
                    + ServletContextIntf.CONTEXT+" in the grandparent");
        }

        return context;

    }

    public void addHandler(HttpHandler handler) throws SmartFrogException, RemoteException{
        ServletHttpContext context=getServletContext(true);
        context.addHandler(handler);

    }
}
