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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.cddlm.generated.api.callbacks.DeploymentNotificationSoapBindingStub;
import org.smartfrog.services.cddlm.generated.api.types.LifecycleEventRequest;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.URL;
import java.rmi.RemoteException;

/**
 * A notification sends a message to the caller created Sep 9, 2004 4:23:58 PM
 */

public class NotificationAction extends BaseAction {
    /**
     * log
     */
    private static final Log log = LogFactory.getLog(NotificationAction.class);
    /**
     * uri to notify
     */
    private URL url;

    /**
     * data to send
     */
    private LifecycleEventRequest message;

    public static final int DEFAULT_TIMEOUT = 10 * 60 * 1000;
    /**
     * callback timeout
     */
    private Integer timeout = new Integer(DEFAULT_TIMEOUT);

    /**
     * sleep time. this is mostly for demos
     */

    private int sleepTime;

    /**
     * constructor
     *
     * @param url
     * @param data
     */
    public NotificationAction(URL url, LifecycleEventRequest data) {
        assert data != null;
        assert url != null;
        this.url = url;
        this.message = data;
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
     * set the optional sleep time in seconds
     *
     * @param sleepTimeInSeconds
     */
    public void setSleepTime(int sleepTimeInSeconds) {
        this.sleepTime = sleepTimeInSeconds;
    }

    public LifecycleEventRequest getMessage() {
        return message;
    }

    public void setMessage(LifecycleEventRequest message) {
        this.message = message;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * issue a notification
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void execute() throws SmartFrogException, RemoteException {
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime * 1000);
            } catch (InterruptedException e) {

            }
        }
        DeploymentNotificationSoapBindingStub callback = new DeploymentNotificationSoapBindingStub(
                url, null);
        if (timeout != null) {
            callback.setTimeout(timeout.intValue());
        }
        String path = url.toString();
        log.info("sending notification to " +
                path +
                " # " +
                message.getIdentifier());
        callback.notification(message);
        //send a notification
        super.execute();
    }


}
