/** (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.ssh;

import com.jcraft.jsch.Logger;
import org.smartfrog.sfcore.logging.LogLevel;
import org.smartfrog.sfcore.logging.LogSF;


/**
 * Bridge from Jsch Logging to SmartFrog
 */
public final class JschLogger implements Logger {

    private LogSF sflog;

    private static final int[] map =
            {
                    LogLevel.LOG_LEVEL_DEBUG,
                    LogLevel.LOG_LEVEL_INFO,
                    LogLevel.LOG_LEVEL_WARN,
                    LogLevel.LOG_LEVEL_ERROR,
                    LogLevel.LOG_LEVEL_FATAL
            };

    public JschLogger(LogSF sflog) {
        this.sflog = sflog;
    }

    public boolean isEnabled(int level) {
        return sflog.isLevelEnabled(map[level]);
    }

    public void log(int level, String message) {
        switch (level) {
            case DEBUG:
                sflog.debug(message);
                break;

            case WARN:
                sflog.warn(message);
                break;
            case ERROR:
                sflog.error(message);
                break;
            case FATAL:
                sflog.fatal(message);
                break;

            case INFO:
                //fall through
            default:
                sflog.info(message);
                break;

        }


    }
}
