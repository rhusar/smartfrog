/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.messages.SLPDAAdvMessage;

import java.util.Vector;

/**
 * This class is used to store information about DAs. It is used by Service and User Agents to store a list of available
 * DAs.
 */
class DAInfo {
    /** The hostname for the host on which the DA runs. */
    private String host;
    /** The port the DA listens to. This should in most cases be the default SLP port. */
    private int port;
    /** A list of scopes supported by the DA */
    private Vector scopes;
    /** A list of attributes for the DA. */
    private Vector attributes;
    /** The stateless boot timestamp for the DA. */
    private int timestamp;

    /**
     * Creates a new DAInfo object.
     *
     * @param host The host on which the DA runs
     * @param port The port the DA listens on
     */
    public DAInfo(String host, int port) {
        this.host = host;
        this.port = port;
        scopes = null;
        attributes = null;
        timestamp = 0;
    }

    /**
     * Creates a new DAInfo object.
     *
     * @param host The host address of the DA.
     * @param port The port the DA listens on.
     * @param msg  The DAAdvert message describing the new DA.
     */
    public DAInfo(String host, int port, SLPDAAdvMessage msg) {
        this.host = host;
        this.port = port;
        scopes = msg.getScopes();
        attributes = msg.getAttributes();
        timestamp = msg.getTimestamp();
    }

    /** Returns the host on which the DA runs. */
    public String getHost() {
        return host;
    }

    /** Returns the port on which the DA listens for unicast requests. */
    public int getPort() {
        return port;
    }

    /**
     * Checks if the DA supports the given scope.
     *
     * @param scope The scope we want the DA to support.
     * @return true if the scopes is supported.
     */
    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }

    /** Returns the scopes supported by the DA. */
    public Vector getScopes() {
        return scopes;
    }

    /** Returns the DAs attributes. */
    public Vector getAttributes() {
        return attributes;
    }

    /** Returns the timestamp. */
    public int getTimestamp() {
        return timestamp;
    }

    /** Sets the scopes... */
    void setScopes(Vector s) {
        scopes = s;
    }


    public String toString() {
        return "DirectoryAgent at " + getHost() + ":" + getPort();
    }
}
