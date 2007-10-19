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
package org.smartfrog.services.logging.jcl.front;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogLogException;

/**
 * created 09-May-2006 17:17:53
 */

public class CommonsLogFactory extends LogFactoryImpl {

    /**
     * To get the sfCore logger
     *
     * @return Logger implementing LogSF and Log
     */
    private LogSF sfLog() {
        return LogFactory.sfGetProcessLog();
    }


    /**
     * To get the sfCore logger
     *
     * @return Logger implementing LogSF and Log
     */
    private LogSF sfLog(String name) {
        try {
            return LogFactory.getLog(name,true);
        } catch (SmartFrogLogException e) {
            return LogFactory.getLog(name);
        }
    }


    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     * <p/>
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     *
     * @param name Logical name of the <code>Log</code> instance to be
     *             returned (the meaning of this name is only known to the underlying
     *             logging implementation that is being wrapped)
     * @throws org.apache.commons.logging.LogConfigurationException
     *          if a suitable <code>Log</code>
     *          instance cannot be returned
     */
    public Log getInstance(String name) throws LogConfigurationException {

        return new CommonsLogFrontEnd(sfLog(name));
    }


}
