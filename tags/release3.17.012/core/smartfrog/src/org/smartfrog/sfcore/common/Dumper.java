/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.prim.Dump;

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * @since 3.11.001
 */

public interface Dumper extends Remote {

    void visiting(String name, Integer numberOfChildren) throws RemoteException;

    void visited(String name) throws RemoteException;

    void modifyCD(Reference from, Context stateCopy) throws Exception, RemoteException;

    public void setTimeout(long timeout) throws RemoteException;

    public void sfKeysToBeRemoved (String[] sfKeysToBeRemoved) throws RemoteException;

    public String[] getSFKeysToBeRemoved () throws RemoteException;

    public Reference getRootRef() throws RemoteException;

    public Dump getDumpVisitor() throws RemoteException;
}
