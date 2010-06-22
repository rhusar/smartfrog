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
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

/**
 * created 09-May-2006 17:17:53
 */

public class CommonsLogFactory extends LogFactoryImpl {

    private final LogSF logSf = sfLog("org.smartfrog.services.logging.jcl.front.CommonsLogFactory");
    
    public CommonsLogFactory() {
        logSf.info("created");
    }

    public CommonsLogFactory(String name) {
        logSf.info("created: "+ name);
    }

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
     * @param name The name of the log to look for
     * @return Logger implementing LogSF and Log
     */
    private LogSF sfLog(String name) {
        try {
            //try to create a log and register it
            return LogFactory.getLog(name, true);
        } catch (SmartFrogLogException e) {
            //if we can't register, get a simpler log
            return LogFactory.getLog(name);
        }
    }


    /**
     * Create a new instance of this log
     * @param name
     * @return a new log
     * @throws LogConfigurationException
     */
    @Override
    protected Log newInstance(String name) throws LogConfigurationException {
        LogSF backEnd = sfLog(name);
        return createInstance(backEnd);
    }

    /**
     * Create a commons log from a SmartFrog log
     * @param backEnd
     * @return the new logging instance
     */
    public static Log createInstance(LogSF backEnd) {
        return new CommonsLogFrontEnd(backEnd);
    }


}
