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
package org.smartfrog.services.hadoop.components.submitter;

import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TaskCompletionEvent;
import org.smartfrog.sfcore.logging.LogSF;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

/**
 *
 * Something to log task completion events
 *
 */

public class TaskCompletionEventLogger implements Iterable<TaskCompletionEvent> {

    private RunningJob job;
    private LogSF log;
    private List<TaskCompletionEvent> events=new ArrayList<TaskCompletionEvent>();
    private int eventCounter=0;

    public TaskCompletionEventLogger(RunningJob job, LogSF log) {
        this.job = job;
        this.log = log;
    }

    public int pollForNewEvents() throws IOException {
        TaskCompletionEvent[] taskCompletionEvents = job.getTaskCompletionEvents(eventCounter);
        int result = -1;
        if (taskCompletionEvents.length > 0) {
            synchronized (this) {
                result=events.size();
                for (TaskCompletionEvent event : taskCompletionEvents) {
                    log.info(event);
                    events.add(event);
                    eventCounter++;
                }
            }
        }
        return result;
    }

    /**
     * Get the named event
     * @param index the event
     * @return the event
     */
    public synchronized TaskCompletionEvent getEvent(int index) {
        return events.get(index);
    }

    /**
     * Returns an iterator over the completion events
     *
     * @return an Iterator.
     */
    public Iterator<TaskCompletionEvent> iterator() {
        return events.iterator();
    }
}
