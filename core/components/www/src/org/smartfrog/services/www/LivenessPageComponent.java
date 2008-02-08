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
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;


/**
 * Component to check the health of a web page.
 * The initial implementation does a liveness check
 * every sfPing, and only every sfPing(); a revision would run the checks in a
 * separate thread at its own rate and then report errors. That revision could
 * cache information about the GET with remote access, too.
 *
 * Created 21-Apr-2004 13:46:23
 */
public class LivenessPageComponent extends AbstractLivenessPageComponent
        implements LivenessPage, Condition {

    /**
     * enabled flag
     */
    private boolean enabled = true;
    /**
     * how often to check
     */
    private int checkFrequency = 1;
    private boolean checkOnLiveness;
    /**
     * when is the next check
     */
    private int nextCheck = 0;

    /**
     * a log
     */
    private Log log;
    private ComponentHelper helper;


    /**
     * empty constructor
     *
     * @throws RemoteException In case of network/rmi error
     */
    public LivenessPageComponent() throws RemoteException {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    public int getNextCheck() {
        return nextCheck;
    }

    public Log getLog() {
        return log;
    }

    public synchronized ComponentHelper getHelper() {
        return helper;
    }

    /**
     * Start up by creating the liveness checker
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();

        buildLivenessChecker();

        checkFrequency = sfResolve(ATTR_CHECK_FREQUENCY, checkFrequency, false);
        boolean checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, true, true);
        checkOnLiveness = sfResolve(ATTR_CHECK_ON_LIVENESS, true, true);
        updateEnabledState();
        helper = new ComponentHelper(this);
        log = helper.getLogger();
        String description = getDescription() + toString();
        log.info(description);
        if(checkOnStartup) {
            checkPage();
        }
        //and do a termination if asked for
        if(terminateAfterStartup()) {
            helper.sfSelfDetachAndOrTerminate(null,description,null,null);
        }
    }

    /**
     * override point --should we check for workflow termination after startup
     * @return true if the workflow attributes should be checked during startup
     */
    protected boolean terminateAfterStartup() {
        return true;
    }

    protected String getDescription() {
        return "Checking ";
    }


    /**
     * Turn the enabled state on or off by checking our enabled attribute
     * @throws SmartFrogResolutionException for a failure to resolve the attribute
     * @throws RemoteException  for network problems
     */
    private void updateEnabledState()
            throws SmartFrogResolutionException, RemoteException {
        enabled = sfResolve(ATTR_ENABLED, enabled, false);
        livenessPage.setEnabled(enabled);
    }


    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws RemoteException  for network problems
     * @throws SmartFrogLivenessException on a failure of the check
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if(checkOnLiveness) {
            livenessPing();
        }
    }

    /**
     * This is the routine called in sfPing that checks the liveness.
     * Override it if you want different behaviour on liveness
     * @throws RemoteException  for network problems
     * @throws SmartFrogLivenessException  on a failure of the check
     */
    protected void livenessPing() throws RemoteException,
        SmartFrogLivenessException {
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
     * Check the page, regardless of whether the component is enabled or not.
     * This is the programmatic option.
     *
     * @throws SmartFrogLivenessException on a failure of the check
     */
    public void checkPage() throws SmartFrogLivenessException {
        livenessPage.checkPage();
    }


    /**
     * For liveness we evaluate the page and return true if the page is there
     *
     * @return true if it is successful, false if not
     * @throws RemoteException for network problems
     * @throws SmartFrogException   for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            livenessPage.onPing();
            return true;
        } catch (SmartFrogLivenessException  ignored) {
            return false;
        }
    }
}
