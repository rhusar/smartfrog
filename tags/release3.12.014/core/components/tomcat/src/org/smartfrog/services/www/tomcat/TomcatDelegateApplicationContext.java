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
package org.smartfrog.services.www.tomcat;

import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * base class for contexts
 */
public abstract class TomcatDelegateApplicationContext implements ApplicationServerContext {


    private TomcatServer server;
    private Prim declaration;

    protected TomcatDelegateApplicationContext(TomcatServer server, Prim declaration) {
        this.server = server;
        this.declaration = declaration;
    }

    public TomcatServer getServer() {
        return server;
    }

    public Prim getServerPrim() {
        return (Prim) server;
    }

    public Prim getDeclaration() {
        return declaration;
    }

    /**
     * deploy this thing
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public abstract void deploy() throws SmartFrogException, RemoteException;
}
