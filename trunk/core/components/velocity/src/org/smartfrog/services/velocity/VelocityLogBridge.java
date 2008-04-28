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
package org.smartfrog.services.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

/**
 * This component bridges from the velocity log to SmartFrog log. It is deprecated from velocity 1.5+.
 */


public class VelocityLogBridge implements LogSystem {
    private LogSF log;

    public void init(RuntimeServices runtimeServices) throws Exception {
        log = LogFactory.getLog(VelocityLogBridge.class);
    }

    public void logVelocityMessage(int level, String s) {
        switch (level) {
            case DEBUG_ID:
                log.debug(s);
                break;
            case INFO_ID:
                log.info(s);
                break;
            case WARN_ID:
                ;
                log.warn(s);
                break;
            case ERROR_ID:
            default:
                log.error(s);
                break;
        }
    }
}
