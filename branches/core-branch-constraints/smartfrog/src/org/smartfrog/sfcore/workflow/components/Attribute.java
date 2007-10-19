/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.components;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


/**
 * This component adds or removes an attribute from a component. Attributes are
 * documented in Terminator.sf
 */
public class Attribute extends EventPrimImpl implements Prim {
    private TerminationRecord term = null;
    public static final String ATTR_ATTRIBUTE_NAME = "attributeName";
    private static final String ATTR_OF_COMPONENT = "ofComponent";

    /**
     * Constructs Attribute.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public Attribute() throws RemoteException {
        super();
    }

    /**
     * Starts the component and on start adds and removes the attribute and
     * terminates.
     * Overrides PrimImpl.sfStart.
     *
     * @throws RemoteException In case of any network error
     * @throws SmartFrogException In case of any error while starting
     *         the component
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        Object value = null;
        super.sfStart();
        Reference id = sfCompleteName();
        term = TerminationRecord.normal(id);

        String name = (String) sfResolve(ATTR_ATTRIBUTE_NAME);
        Prim component = (Prim) sfResolve(ATTR_OF_COMPONENT);
        value = sfResolve("value");

        try {
            if (name!=null){
              if (value != null) {
                component.sfReplaceAttribute(name, value);
              }
              else {
                component.sfRemoveAttribute(name);
              }
            }
        } catch (Exception e) {
            term = TerminationRecord.abnormal(e.toString(), id);
        }
        Runnable terminator = new Runnable() { public void run() { sfTerminate(term); } };
        new Thread(terminator).start();
    }
}
