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
package org.smartfrog.projects.alpine.wsa;

import java.util.UUID;

/**
 * Something that creates new, unique, message IDs, up to 2^31 of them, anyway.
 * created 06-Jun-2006 16:35:52
 */

public class MessageIDSource {

    private String preamble;

    private int counter;

    public MessageIDSource() {
        UUID uuid=UUID.randomUUID();
        preamble="uuid:"+uuid.toString()+'-';
    }

    public MessageIDSource(String preamble) {
        this.preamble = preamble;
    }

    public synchronized String newID() {
        String id=preamble+counter;
        counter++;
        return id;
    }

    /**
     * Add a new ID to an address
     * @param address
     */
    public void addNewID(AddressDetails address) {
        address.setMessageID(newID());
    }
}
