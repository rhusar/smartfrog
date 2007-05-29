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

package org.smartfrog.services.logger;


import java.sql.Timestamp;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * SmartFrog LogFormatter class used to format the log message. 
 * @Author Ashish Awasthi
 */ 
public class SFLogFormatter extends Formatter {

    private final String separateStr = " | " ;

    /**
     * Constructs SFLogFormatter object.
     */
    public SFLogFormatter() {
    }
    /**
     * Formats the log message.
     * @param record The log record
     * @return The formatted log message
     */
    public String format (LogRecord record) {
        StringBuffer log = new StringBuffer();
        long time = record.getMillis();
        log.append(new Timestamp(time).toString())
           .append(separateStr)
           .append(record.getLevel())
           .append(separateStr)
           .append(record.getSourceClassName())
           .append(separateStr)
           .append(record.getSourceMethodName())
           .append(separateStr)
           .append(record.getMessage())
           .append("\n");
        return log.toString();
    }
}
