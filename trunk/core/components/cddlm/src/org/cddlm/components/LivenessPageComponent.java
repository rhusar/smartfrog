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
package org.cddlm.components;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.logging.Log;

import java.rmi.RemoteException;
import java.util.logging.Logger;


/**
 * Initial liveness component.
 * The initial implementation does a liveness check every sfPing, and only every
 * sfPing(); a revision would run the checks in a separate thread at its own
 * rate and then report errors. That revision could cache information about the
 * GET with remote access, too.
 * created 21-Apr-2004 13:46:23
 */
public class LivenessPageComponent extends PrimImpl implements LivenessPage {

    /**
     * the class that contains all the checking code. This is on the side
     * for reuse in other components.
     */
    LivenessPageChecker livenessPage;

    /**
     * how often to check
     */
    int checkFrequency = 1;

    /**
     * when is the next
     */
    int nextCheck = 0;

    /**
     * empty constructor
     *
     * @throws RemoteException
     */
    public LivenessPageComponent() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposed. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        livenessPage = new LivenessPageChecker(this);

        String url = sfResolve(URL, (String) null, false);

        if (url != null) {
            livenessPage.bindToURL(url);
        } else {
            livenessPage.setHost(sfResolve(HOST, livenessPage.getHost(), false));
            livenessPage.setPort(sfResolve(PORT, livenessPage.getPort(), false));
            livenessPage.setProtocol(sfResolve(PROTOCOL,
                    livenessPage.getProtocol(), false));
            livenessPage.setPage(sfResolve(PAGE, livenessPage.getPage(), false));
        }

        livenessPage.setFollowRedirects(sfResolve(PROTOCOL,
                livenessPage.getFollowRedirects(), false));
        livenessPage.setMinimumResponseCode(sfResolve(MINIMUM_RESPONSE_CODE,
                livenessPage.getMinimumResponseCode(), false));
        livenessPage.setMaximumResponseCode(sfResolve(MAXIMUM_RESPONSE_CODE,
                livenessPage.getMaximumResponseCode(), false));
        livenessPage.setFollowRedirects(sfResolve(FOLLOW_REDIRECTS,
                livenessPage.getFollowRedirects(), false));
        livenessPage.setFetchErrorText(sfResolve(FETCH_ERROR_TEXT,
                livenessPage.getFetchErrorText(), false));
        checkFrequency = sfResolve(CHECK_FREQUENCY, checkFrequency, false);

        //now tell the liveness page it is deployed
        livenessPage.onDeploy();

        log = new ComponentHelper(this).getLogger();
        log.info("Deployed " + toString());
    }

    /**
     * a log
     */
    Log log;

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *          component is terminated
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);

        //check the counter
        if (nextCheck-- <= 0) {
            //reset it
            nextCheck = checkFrequency;

            //hand off to our liveness helper class
            livenessPage.onPing();
        }
    }

    /**
     * @return string form for this component
     */
    public String toString() {
        //delegate
        if (livenessPage != null) {
            return livenessPage.toString();
        } else {
            return "undeployed liveness checker";
        }
    }
}
