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
package org.smartfrog.services.deployapi.notifications;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;
import org.smartfrog.services.deployapi.transport.wsrf.NotificationSubscription;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * A weak reference store of subscriptions. Content added here is not retained unless
 * Other things link to it.
 * created 05-Oct-2006 12:32:38
 */

public class SubscriptionServiceStore  {

    private Log log = LogFactory.getLog(SubscriptionServiceStore.class);

    private HashMap<String, WeakReference<NotificationSubscription>> subscriptions =
            new HashMap<String, WeakReference<NotificationSubscription>>();


    public void add(NotificationSubscription sub) {
        subscriptions.put(sub.getId(), new WeakReference<NotificationSubscription>(sub));
    }

    public void remove(String key) {
        subscriptions.remove(key);
    }

    public void remove(NotificationSubscription sub) {
        subscriptions.remove(sub.getId());
    }

    public NotificationSubscription lookup(String key) {
        WeakReference<NotificationSubscription> ref = subscriptions.get(key);
        if (ref == null) {
            FaultRaiser.raiseBadArgumentFault("No such subscription: " + key);
        }
        NotificationSubscription sub = ref.get();
        if (sub == null) {
            log.info("Purging subscription data for " + key);
            remove(key);
        }
        return sub;
    }
}
