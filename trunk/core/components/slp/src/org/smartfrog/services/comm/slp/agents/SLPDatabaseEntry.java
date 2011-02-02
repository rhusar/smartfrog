/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.ServiceType;
import org.smartfrog.services.comm.slp.ServiceURL;

import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/** Implements a database entry for a service. This holds all the attributes that are stored in the database. */
class SLPDatabaseEntry {
    /** The Service URL of the services. */
    private ServiceURL url;
    /** The service attributes. */
    private Vector attributes;
    /** The scopes the service is registered in. */
    private Vector scopes;
    /** The language for the service. */
    private Locale locale;
    /** The time the service was added to the database. */
    private long timestamp;
    /** The lifetime of the service. */
    private int lifetime;

    /**
     * Creates a new SLPDatabaseEntry.
     *
     * @param url        The service url.
     * @param attributes The service attributes.
     * @param scopes     The scopes in which to find the service.
     * @param locale     The language for the service.
     */
    public SLPDatabaseEntry(ServiceURL url, Vector attributes, Vector scopes, Locale locale) {
        this.url = url;
        this.attributes = (Vector) attributes.clone();
        if (scopes != null) {
            this.scopes = (Vector) scopes.clone();
        } else {
            this.scopes = null;
        }
        this.locale = locale;
        lifetime = url.getLifetime();
        timestamp = (new Date()).getTime();
    }

    /** Returns the service type. */
    public ServiceType getType() {
        return url.getServiceType();
    }

    /** Returns the service url. */
    public ServiceURL getURL() {
        return url;
    }

    /** Returns the services attributes. */
    public Vector getAttributes() {
        return (Vector) attributes.clone();
    }

    /** Returns the timestamp. */
    public long getTimestamp() {
        return timestamp;
    }

    /** Returns the locale. */
    public Locale getLocale() {
        return locale;
    }

    /** Returns the scope list. */
    public Vector getScopes() {
        return (Vector) scopes.clone();
    }

    /**
     * Returns the full lifetime of the service. This does not take into account that some time has passed since the
     * service was registered.
     */
    public int getLifetime() {
        return lifetime;
    }

    /** Calculates the remaining lifetime of the service. */
    public int getRemainingLifetime() {
        long currentTime = ((new Date())).getTime();
        int elapsedTime = (int) ((currentTime - timestamp) / 1000);
        int newLifetime = lifetime - elapsedTime;
        if (lifetime == ServiceURL.LIFETIME_PERMANENT) newLifetime = ServiceURL.LIFETIME_PERMANENT;

        return newLifetime;
    }
}
