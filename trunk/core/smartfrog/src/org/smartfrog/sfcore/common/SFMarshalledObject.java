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

package org.smartfrog.sfcore.common;

import java.rmi.MarshalledObject;

import java.io.IOException;


public class SFMarshalledObject {

    private Object value = null;

    public SFMarshalledObject(Object value) {
        set(value);
    }

    private boolean wasMarshalled = false;

    public Object get() {
        if (value == null) return null;
        if (value instanceof MarshalledObject && !wasMarshalled) {
            try {
                return ((MarshalledObject)value).get();
            } catch (ClassNotFoundException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        } else {
            return value;
        }
    }

    public Object  set(Object value){
        if (value instanceof SFMarshalledObject) {
            System.out.println(((SFMarshalledObject)value).get().toString());
        }
        Object oldValue = get();
        synchronized (value) {
            if (value instanceof MarshalledObject) {
                wasMarshalled = true;
                this.value = (MarshalledObject)value;
            } else {
                try {
                    this.value = new MarshalledObject(value);
                } catch (IOException ex) {
                    this.value = value;
                }
            }
        }
        return oldValue;
    }

}
