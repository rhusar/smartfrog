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

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Dump;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.reference.Reference;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Enumeration;


/**
 * @since 3.11.001
 */
public class DumpVisitorImpl implements Dump, Serializable {

    static final long serialVersionUID = -812223322957783371L;

    //Reference to the class collecting results from visits
    private transient Dumper dumper = null;

    public DumpVisitorImpl (Dumper dumper){
        this.dumper = dumper;
    }
    /**
      * Components use this method to dump their state to when requested (using
      * sfDumpState).
      *
      * @param state state of component (application specific)
      * @param from source of this call
      *
      * @throws RemoteException In case of Remote/nework error
      */
     public void dumpState(Object state, Prim from) throws RemoteException {
        //System.out.println(" - DumpState from1: "+from.sfCompleteName());
        Integer numberOfChildren = new Integer(0);
        if (from instanceof Compound) {
           int numberC = 0;
           for (Enumeration<Liveness> e = ((Compound)from).sfChildren(); e.hasMoreElements();) {
             e.nextElement();
             numberC++;
           }
           numberOfChildren = new Integer (numberC);
        }

        dumper.visiting(from.sfCompleteName().toString(),numberOfChildren);

        Context stateCopy =  (Context)((Context)state).clone();

        Reference searchRef =  (Reference)from.sfCompleteName().copy();

        try {
            dumper.modifyCD(searchRef, stateCopy);
        } catch (Exception e) {
            throw new RemoteException("Failed to modifyCD from "+from.sfCompleteName().toString(),e);
        }

        dumper.visited(from.sfCompleteName().toString());
    }

}
