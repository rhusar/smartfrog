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

import org.smartfrog.sfcore.logging.Log;

import java.util.ArrayList;

/**
 * List of status events.
 * This list implements the SF logging API so it can be remoted and handed off to things. 
 */

public final class StatusEvents extends ArrayList<StatusEvent> implements Cloneable, Log {


    public StatusEvents() {
    }

    /**
     * Add an event to the event list
     * @param error flag to indicate error or not
     * @param message the text
     */
    public void addEvent(boolean error, String message) {
        add(new StatusEvent(error, message));
    }

    private void infoEvent(Object message) {
        addEvent(false, message.toString());
    }

    private void infoEvent(Object message, Throwable t) {
        infoEvent(message);
    }

    private void errorEvent(Object message) {
        addEvent(true, message.toString());
    }

    private void errorEvent(Object message, Throwable t) {
        errorEvent(message);
    }


    /**
    * Shallow clone
    * @return a cloned list
    */
    @Override
    public StatusEvents clone() {
        return (StatusEvents) super.clone();
    }

    /**
     * Return this list in a way that is easy for JSP pages to handle
     * @return the this pointer
     */
    public ArrayList<StatusEvent> getList() {
        return this;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isFatalEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void trace(Object message) {

    }

    @Override
    public void trace(Object message, Throwable t) {

    }

    @Override
    public void debug(Object message) {

    }

    @Override
    public void debug(Object message, Throwable t) {

    }

    @Override
    public void info(Object message) {
        infoEvent(message);
    }

    @Override
    public void info(Object message, Throwable t) {
        infoEvent(message, t);
    }

    @Override
    public void warn(Object message) {
        infoEvent(message);
    }

    @Override
    public void warn(Object message, Throwable t) {
        infoEvent(message, t);
    }

    @Override
    public void error(Object message) {
        errorEvent(message);
    }

    @Override
    public void error(Object message, Throwable t) {
        errorEvent(message, t);
    }

    @Override
    public void fatal(Object message) {
        errorEvent(message);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        errorEvent(message, t);
    }
}
