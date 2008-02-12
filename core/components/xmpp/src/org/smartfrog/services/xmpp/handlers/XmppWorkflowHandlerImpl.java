/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp.handlers;

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.compound.Compound;

import java.rmi.RemoteException;

/**
 *
 * Created 14-Aug-2007 14:16:04
 *
 */

public class XmppWorkflowHandlerImpl extends EventCompoundImpl implements Compound {


    public XmppWorkflowHandlerImpl() throws RemoteException {
    }


    /**
     * This is an override point.
     *
     * @return false
     */
    protected boolean isOldNotationSupported() {
        return false;
    }
}
