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

import org.apache.axis.types.URI;
import org.smartfrog.services.cddlm.generated.api.callbacks.DeploymentCallbackSoapBindingStub;
import org.smartfrog.services.cddlm.generated.api.types._lifecycleEventCallbackRequest;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.URL;
import java.rmi.RemoteException;

/**
 * A notification sends a message to the caller created Sep 9, 2004 4:23:58 PM
 */

public class NotificationAction extends BaseAction {

    /**
     * uri to notify
     */
    private URI uri;

    /**
     * data to send
     */
    private _lifecycleEventCallbackRequest data;

    private Integer timeout;

    /**
     * constructor
     *
     * @param uri
     * @param data
     */
    public NotificationAction(URI uri, _lifecycleEventCallbackRequest data) {
        assert data != null;
        assert uri != null;
        this.uri = uri;
        this.data = data;
    }

    /**
     * timeout in milliseconds
     *
     * @param timeout
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * issue a notification
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void execute() throws SmartFrogException, RemoteException {
        URL url = URIHelper.toJavaURL(uri);
        DeploymentCallbackSoapBindingStub callback = new DeploymentCallbackSoapBindingStub(
                url, null);
        if (timeout != null) {
            callback.setTimeout(timeout.intValue());
        }
        callback.callback(data);
    }


}
