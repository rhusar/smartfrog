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
package org.smartfrog.services.xunit.base;

import org.smartfrog.services.xunit.utils.Utils;
import org.smartfrog.services.xunit.log.TestListenerLog;

import java.io.Serializable;
import java.util.Properties;

/**
 * This is the configuration to run created 17-May-2004 17:22:03 The clone policy creates a shallow clone, and retains
 * the same listener
 */

public class RunnerConfiguration implements Serializable, Cloneable {

    /**
     * System properties
     */
    private Properties sysProperties = new Properties();

    /**
     * who listens to the tests? This is potentially remote
     */
    private TestListenerFactory listenerFactory;

    private TestListenerLog testLog;

    /**
     * flag to identify whether the task should fail when it is time
     */
    private boolean keepGoing = true;

    /**
     * timeout in milliseconds. less than or equal to zero means no timeout
     */
    private int timeout = 0;

    public TestListenerFactory getListenerFactory() {
        return listenerFactory;
    }

    public void setListenerFactory(TestListenerFactory listenerFactory) {
        this.listenerFactory = listenerFactory;
    }

    public boolean getKeepGoing() {
        return keepGoing;
    }

    public void setKeepGoing(boolean keepGoing) {
        this.keepGoing = keepGoing;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public TestListenerLog getTestLog() {
        return testLog;
    }

    public void setTestLog(TestListenerLog testLog) {
        this.testLog = testLog;
    }



    /**
     * the shallow clone copies all the simple settings, but shares the test listener.
     * and test log. There is a deeper clone of the system properties
     * @return the clone
     * @throws CloneNotSupportedException
     */
    protected Object clone() throws CloneNotSupportedException {
        RunnerConfiguration cloned = (RunnerConfiguration) super.clone();
        cloned.sysProperties = (Properties) sysProperties.clone();
        return cloned;
    }

    /**
     * Get the local system properties
     *
     * @return the system properties
     */
    public Properties getSysProperties() {
        return sysProperties;
    }

    /**
     * Apply system properties. This adds them to the current JVM, and does not unapply it afterwards
     */
    public void applySysProperties() {
        Utils.applySysProperties(sysProperties);
    }

}
