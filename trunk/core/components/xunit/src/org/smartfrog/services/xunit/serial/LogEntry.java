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
package org.smartfrog.services.xunit.serial;

import org.smartfrog.sfcore.logging.LogLevel;

import java.io.Serializable;
import java.util.Locale;

/**
 * This class represents an entry in the log.
 */

public final class LogEntry implements Serializable, Cloneable {

    /**
     * extra log key for stdout
     */
    public static final int LOG_LEVEL_STDOUT = -1;

    /**
     * extra log key for stderr
     */
    public static final int LOG_LEVEL_STDERR = -2;

    /**
     * timestamp
     * @serial
     */

    public long timestamp = System.currentTimeMillis();

    /**
     * Log level
     * @serial
     */
    public int level;

    /**
     * Log text
     * @serial
     */
    public String text;

    /**
     * Any exception that got thrown
     * @serial
     */
    public ThrowableTraceInfo thrown;

    /**
     * name of the host on which the test ran
     *
     * @serial
     */
    private String hostname;


    /**
     * Create an empty entry
     */
    public LogEntry() {
    }

    /**
     * Create an entry
     * @param level log level
     * @param text text
     * @param thrown optional exception, whose contents are serialized
     */
    public LogEntry(int level, String text, Throwable thrown) {
        this.level = level;
        this.text = text;
        if (thrown != null) {
            this.thrown = new ThrowableTraceInfo(thrown);
        }
    }

    /**
     * Create a log entry
     * @param level log level
     * @param text text
     * @param thrown optional exception, whose contents are serialized
     */
    public LogEntry(int level, String text, ThrowableTraceInfo thrown) {
        this.level = level;
        this.text = text;
        this.thrown = thrown;
    }

    /**
     * Create a log entry
     * @param level log level
     * @param text text
     */
    public LogEntry(int level, String text) {
        this(level,text,(ThrowableTraceInfo)null);
    }

    /**
     * Create a log entry from another entry
     * @param that the other log entry
     */
    public LogEntry(LogEntry that) {
        level = that.level;
        text = that.text;
        timestamp = that.timestamp;
        hostname = that.hostname;
        if (that.thrown != null) {
            thrown = new ThrowableTraceInfo(that.thrown);
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return the value of the text field
     */
    public String toString() {
        return text;
    }

    public static LogEntry out(String text, Throwable thrown) {
        return new LogEntry(LOG_LEVEL_STDOUT, text, thrown);
    }

    public static LogEntry err(String text, Throwable thrown) {
        return new LogEntry(LOG_LEVEL_STDERR, text, thrown);
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     * @see Cloneable
     */
    protected Object clone() {
        return new LogEntry(this);
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLevel() {
        return level;
    }

    public String getText() {
        return text;
    }

    public ThrowableTraceInfo getThrown() {
        return thrown;
    }

    /**
     * Convert the level enumeration into text
     * @return the level as a text string
     */
    public String levelToText() {
        switch(level) {
            case LOG_LEVEL_STDERR:
                return "stderr";
            case LOG_LEVEL_STDOUT:
                return "stdout";
            case LogLevel.LOG_LEVEL_DEBUG:
                return "debug";
            case LogLevel.LOG_LEVEL_TRACE:
                return "trace";
            case LogLevel.LOG_LEVEL_IGNORE:
                return "ignore";
            case LogLevel.LOG_LEVEL_INFO:
                return "info";
            case LogLevel.LOG_LEVEL_WARN:
                return "warn";
            case LogLevel.LOG_LEVEL_ERROR:
                return "error";
            case LogLevel.LOG_LEVEL_FATAL:
                return "fatal";
            default:
                return "unknown";
        }
    }
    
    /**
     * Creates a full log string with the log level
     * @return
     */
    public String logString() {
        if(level==LOG_LEVEL_STDERR || level==LOG_LEVEL_STDOUT) {
            return text+"\n";
        }
        StringBuilder buffer=new StringBuilder();
        buffer.append('[');
        buffer.append(levelToText().toUpperCase(Locale.ENGLISH));
        buffer.append(" ]");
        buffer.append(text);
        if(thrown!=null) {
            buffer.append(thrown.toString());
        }
        
        return buffer.toString();
    }
}
