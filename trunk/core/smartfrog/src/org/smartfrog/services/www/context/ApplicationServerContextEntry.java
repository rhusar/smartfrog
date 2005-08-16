/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.context;

import org.smartfrog.services.www.ApplicationServerContext;

import java.io.Serializable;

/**
 * Entry of an application server context
 */
public class ApplicationServerContextEntry implements Serializable {
    public static final String ERROR_WRONG_TYPE = "Wrong entry type for context ";

    public ApplicationServerContextEntry(int type, String contextHandle, ApplicationServerContext implementation) {
        this.type = type;
        this.contextHandle = contextHandle;
        this.implementation = implementation;
    }

    /**
     * empty ctor for serialization
     */
    public ApplicationServerContextEntry() {
    }

    public static final int TYPE_NONE = 0;
    public static final int TYPE_WAR = 1;
    public static final int TYPE_EAR = 2;
    public static final int TYPE_SERVLET_CONTEXT = 3;
    public static final int TYPE_OTHER = 4;

    /**
     * type of the entry
     */
    private int type;

    /**
     * context handle
     */
    private String contextHandle;

    /**
     * Server side implementation class.
     */
    private ApplicationServerContext implementation;

    private Object data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContextHandle() {
        return contextHandle;
    }

    public void setContextHandle(String contextHandle) {
        this.contextHandle = contextHandle;
    }

    /**
     * Get the implementation
     * @return an implementation
     */
    public ApplicationServerContext getImplementation() {
        return implementation;
    }

    public void setImplementation(ApplicationServerContext implementation) {
        this.implementation = implementation;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
