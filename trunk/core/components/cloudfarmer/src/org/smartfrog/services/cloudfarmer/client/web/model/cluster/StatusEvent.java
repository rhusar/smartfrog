/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import java.io.Serializable;

/**
 * Created 05-Nov-2009 12:00:05
 */

public class StatusEvent implements Serializable, Cloneable {
    private String message;
    private long time;
    private boolean error;

    public StatusEvent(long time, String message) {
        this.message = message;
        setTime(time);
    }

    public StatusEvent(String message) {
        this.message = message;
        setDefaultTime();
    }

    public StatusEvent() {
    }

    public StatusEvent(boolean error, long time, String message) {
        this.message = message;
        this.time = time;
        this.error = error;
    }

    public StatusEvent(boolean error, String message) {
        this.message = message;
        this.error = error;
        setDefaultTime();
    }

    private void setTime(long time) {
        this.time = time;
    }

    private void setDefaultTime() {
        setTime(System.currentTimeMillis());
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return fullString();
    }
    
    public String fullString() {
        return "["+ time + "] " 
                + getLevel()
                + ": "
                + message;
    }

    public String getLevel() {
        return (error?"ERROR":"INFO");
    }
}
