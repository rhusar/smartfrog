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
package org.smartfrog.services.cddlm.engine;

import org.smartfrog.services.cddlm.generated.api.callbacks.DeploymentCallbackSoapBindingStub;
import org.smartfrog.services.cddlm.generated.api.types._lifecycleEventCallbackRequest;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.URL;
import java.rmi.RemoteException;

/**
 * this raises a lifecycle event created Sep 14, 2004 11:28:23 AM
 */

public class CallbackAction extends BaseAction {

    public CallbackAction() {
    }

    public CallbackAction(URL url, _lifecycleEventCallbackRequest message) {
        this.url = url;
        this.message = message;
    }

    private URL url;

    private _lifecycleEventCallbackRequest message;

    /**
     * send the event to the system
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void execute() throws SmartFrogException, RemoteException {
        DeploymentCallbackSoapBindingStub remote = new DeploymentCallbackSoapBindingStub(
                url, null);
        remote.callback(message);
    }


    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public _lifecycleEventCallbackRequest getMessage() {
        return message;
    }

    public void setMessage(_lifecycleEventCallbackRequest message) {
        this.message = message;
    }
}
