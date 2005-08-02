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
package org.smartfrog.services.www;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.services.www.LivenessPage;
import org.smartfrog.services.www.LivenessPageChecker;

import java.rmi.RemoteException;
import java.util.Vector;


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
     * enabled flag
     */ 
    private boolean enabled=true;
    
    /**
     * the class that contains all the checking code. This is on the side
     * for reuse in other components.
     */
    private LivenessPageChecker livenessPage;

    /**
     * how often to check
     */
    private int checkFrequency = 1;

    /**
     * when is the next check
     */
    private int nextCheck = 0;

    /**
     * a log
     */
    private Log log;
    
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

        String url = sfResolve(ATTR_URL, (String) null, false);

        if (url != null) {
            livenessPage.bindToURL(url);
        } else {
            livenessPage.setHost(sfResolve(ATTR_HOST, livenessPage.getHost(), false));
            livenessPage.setPort(sfResolve(ATTR_PORT, livenessPage.getPort(), false));
            livenessPage.setProtocol(sfResolve(ATTR_PROTOCOL,
                    livenessPage.getProtocol(), false));
            livenessPage.setPage(sfResolve(ATTR_PAGE, livenessPage.getPage(), false));
            Vector queries = (Vector) sfResolve(LivenessPage.ATTR_QUERIES, (Vector) null, false);
            livenessPage.buildQueryString(queries);
        }

        livenessPage.setFollowRedirects(sfResolve(ATTR_PROTOCOL,
                livenessPage.getFollowRedirects(), false));
        livenessPage.setMinimumResponseCode(sfResolve(ATTR_MINIMUM_RESPONSE_CODE,
                livenessPage.getMinimumResponseCode(), false));
        livenessPage.setMaximumResponseCode(sfResolve(ATTR_MAXIMUM_RESPONSE_CODE,
                livenessPage.getMaximumResponseCode(), false));
        livenessPage.setFollowRedirects(sfResolve(ATTR_FOLLOW_REDIRECTS,
                livenessPage.getFollowRedirects(), false));
        livenessPage.setFetchErrorText(sfResolve(ATTR_ERROR_TEXT,
                livenessPage.getFetchErrorText(), false));
        checkFrequency = sfResolve(ATTR_CHECK_FREQUENCY, checkFrequency, false);


        updateEnabledState();
        //now tell the liveness page it is deployed
        livenessPage.onDeploy();

        log = new ComponentHelper(this).getLogger();
        log.info("Checking " + toString());
    }

    private void updateEnabledState() throws SmartFrogResolutionException, RemoteException {
        enabled = sfResolve(ATTR_ENABLED,enabled,false);
        livenessPage.setEnabled(enabled);
    }


    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *          component is terminated
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        try {
            updateEnabledState();
        } catch (SmartFrogResolutionException e) {
            throw new SmartFrogLivenessException(e);

        }
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
