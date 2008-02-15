/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp.presence;

import org.smartfrog.services.xmpp.Xmpp;

/**
 * This component checks for the presence of a remote individual/entity.
 *
 * Created 12-Feb-2008 12:20:52
 */


public interface XmppPresenceChecker extends Xmpp {

    /**
     * {@value}
     */
    String ATTR_TARGET = "target";

    /**
     * {@value}
     */
    String ATTR_CHECK_ON_LIVENESS = "checkOnLiveness";

    /**
     * {@value}
     */
    String ATTR_TERMINATEWHENTARGETOFFLINE = "terminateWhenTargetOffline";

    /**
     * Subscription mode
     *
     * @link org.jivesoftware.smack.Roster
     */
    String ATTR_SUBSCRIPTION_MODE = "subscriptionMode";

    /**
     * Time (in milliseconds) to give for roster startup before the absence of someone's availability is deemed to be an
     * error
     */
    String ATTR_DELAY = "delay";
}
