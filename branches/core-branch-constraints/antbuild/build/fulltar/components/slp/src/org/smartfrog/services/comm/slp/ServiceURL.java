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

package org.smartfrog.services.comm.slp;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A class representing a service url. It contains the service type, service access point (hostname) and URL path needed
 * to reach the service.
 */
public class ServiceURL implements java.io.Serializable {
    /** Indicates that no port information is available for this URL. */
    public static final int NO_PORT = 0;
    /** zero lifetime. */
    public static final int LIFETIME_NONE = 0;
    /** Default lifetime (3 hours). */
    public static final int LIFETIME_DEFAULT = 10800;
    /** Maximum lifetime. */
    public static final int LIFETIME_MAXIMUM = 65535;
    /** Unlimited lifetime. The URL are continuously re-registered until the application exits. */
    public static final int LIFETIME_PERMANENT = -1;
    /** default transport */
    public static final String DEFAULT_TRANSPORT = ""; // IP

    // variables.
    URI uri = null;
    String serviceURL;
    ServiceType serviceType;
    int lifetime;
    String urlPath;
    String transport = DEFAULT_TRANSPORT;

    public ServiceURL(String url) throws IllegalArgumentException {
        this(url, LIFETIME_DEFAULT);
    }

    public ServiceURL(String type, Object obj) throws IllegalArgumentException {
        this(type, obj, LIFETIME_DEFAULT);
    }

    public ServiceURL(String type, Object obj, int lifetime) throws IllegalArgumentException {
        this(type + ":///" + objectToString(obj), lifetime);
    }

    public ServiceURL(String url, int lifetime) throws IllegalArgumentException {
        int index = url.indexOf("://");
        if (index == -1) {
            throw new IllegalArgumentException("No valid URL given");
        }
        // check if lifetime is valid
        if ((lifetime < LIFETIME_NONE || lifetime > LIFETIME_MAXIMUM) && lifetime != LIFETIME_PERMANENT) {
            throw new IllegalArgumentException("Invalid lifetime");
        }

        // create servicetype
        serviceType = new ServiceType(url.substring(0, index));

        // find URL path.
        int pathIndex = url.indexOf("/", index + 3);
        if (pathIndex == -1 || pathIndex == url.length() - 1) {
            urlPath = "";
        } else {
            urlPath = url.substring(pathIndex);
        }

        // parse host and port.
        String host = url.substring(index);

        if (pathIndex != -1) {
            host = host.substring(0, pathIndex - index);
        }

        if (!host.equals("://")) {
            try {
                uri = new URI("srv" + host);
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Illegal URL");
            }
        }

        serviceURL = url;
        this.lifetime = lifetime;

    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public final void setServiceType(ServiceType type) throws ServiceLocationException {
        throw new ServiceLocationException(ServiceLocationException.NOT_IMPLEMENTED);
    }

    public String getTransport() {
        return transport;
    }

    public String getHost() {
        if (uri == null) {
            return "";
        }

        return uri.getHost();
    }

    public int getPort() {
        if (transport.equals(DEFAULT_TRANSPORT)) {
            if (uri == null) return NO_PORT;
            int p = uri.getPort();
            if (p == -1) return NO_PORT;

            return p;
        }
        return NO_PORT;
    }

    public String getURLPath() {
        return urlPath;
    }

    public Object getURLPathObject() {
        Object obj = null;
        try {
            obj = objectFromString(urlPath.substring(1));
        } catch (Exception ex) {
        }

        return obj;
    }

    public int getLifetime() {
        return lifetime;
    }

    public boolean equals(Object obj) {
        boolean result = false;
        try {
            ServiceURL u = (ServiceURL) obj;
            result = u.getServiceType().equals(serviceType) &&
                    u.getHost().equals(getHost()) &&
                    u.getPort() == getPort() &&
                    u.getURLPath().equals(getURLPath()) &&
                    u.getTransport().equals(getTransport());
        } catch (ClassCastException ex) {
        }

        return result;
    }

    public String toString() {
        return serviceURL;
    }

    public int hashCode() {
        return serviceURL.hashCode();
    }

    public static String objectToString(Object obj) throws IllegalArgumentException {
        String objRef64enc;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.close();
            BASE64Encoder encoder = new BASE64Encoder();
            objRef64enc = encoder.encode(os.toByteArray());
            os.flush();
            os.close();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Not possible to convert object to String");
        }
        return objRef64enc;
    }

    public static Object objectFromString(String objString) throws Exception {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] byteArray = decoder.decodeBuffer(objString);
        ByteArrayInputStream isr = new ByteArrayInputStream(byteArray);
        ObjectInputStream ois = new ObjectInputStream(isr);
        Object obj = ois.readObject();
        ois.close();

        return obj;
    }
}

