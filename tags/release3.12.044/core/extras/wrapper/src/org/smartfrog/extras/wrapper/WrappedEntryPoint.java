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
package org.smartfrog.extras.wrapper;

/**
 * This interface exists so that the wrapped system can be loaded on a different
 * classpath and yet still worked with; this is the interface that it works
 * with.
 *
 * @see org.smartfrog.extras.wrapper.launcher.WrappedSFSystem created Dec 1,
 *      2004 2:23:01 PM
 */


public interface WrappedEntryPoint {
    void start();

    void stop();

    void emergencyStop();

    Thread getThread();

    int getExitCode();

    boolean isSystemExitOnRootProcessTermination();

    void setSystemExitOnRootProcessTermination(
            boolean systemExitOnRootProcessTermination);

    boolean waitTillStopped(long seconds);

    /**
     * give the expected time to shut down the system.
     *
     * @return
     * @todo make this configurable, somehow.
     */
    int getExpectedShutdownTime();

    String[] getArgs();

    void setArgs(String[] args);
}
