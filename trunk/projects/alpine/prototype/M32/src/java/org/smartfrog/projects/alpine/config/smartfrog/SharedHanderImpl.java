/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.config.smartfrog;

import org.smartfrog.projects.alpine.handlers.InstanceHandlerFactory;
import org.smartfrog.projects.alpine.handlers.SharedHandlerFactory;
import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.interfaces.MessageHandlerFactory;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * created 02-May-2006 13:57:20
 */

public class SharedHanderImpl extends AlpineHandlerImpl implements NonRemotableHandlerFactory {

    public SharedHanderImpl() throws RemoteException {
    }

    /**
     * Get the factory that can provide message handlers
     *
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *
     */
    public MessageHandlerFactory createFactory() throws SmartFrogException, RemoteException {
        String classname = resolveClassname();
        MessageHandler handler = InstanceHandlerFactory.createNewHandler(this.getClass().getClassLoader(),
                classname, null);
        return new SharedHandlerFactory(handler);
    }
}
