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
package org.smartfrog.services.deployapi.notifications.muws;

import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.base.SoapElement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * created 10-Oct-2006 15:36:11
 */

public class MuwsEventReceiver implements Iterable<ReceivedEvent>{

    private int size;

    private List<ReceivedEvent> buffer;

    public String id;

    private int count=0;
    public static final int DEFAULT_SIZE = 16;


    public MuwsEventReceiver() {
        this(DEFAULT_SIZE);
    }

    public MuwsEventReceiver(int size) {
        this.size = size;
        buffer = new ArrayList<ReceivedEvent>(size);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public int getCount() {
        return count;
    }


    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public ListIterator<ReceivedEvent> iterator() {
        return buffer.listIterator();
    }

    public synchronized void muwsEventReceived(MessageContext messageContext, SoapElement event) {
        count++;
        if(buffer.size()>=size) {
            buffer.remove(0);
        }
        buffer.add(new ReceivedEvent(messageContext, event));
    }

}
