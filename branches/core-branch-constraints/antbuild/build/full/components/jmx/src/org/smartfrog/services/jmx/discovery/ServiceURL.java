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

package org.smartfrog.services.jmx.discovery;

import java.net.*;
/**
 * A class representing a service url.
 * It contains the service type, service access point (hostname) and URL path
 * needed to reach the service.
 *
 *
 */
public class ServiceURL implements java.io.Serializable{

  public static final int LIFETIME_NONE = 0;
  public static final int LIFETIME_MAXIMUM = 65535;
  public static final int LIFETIME_DEFAULT = 10800;
  public static final int LIFETIME_PERMANENT = -1;
  public static final int NO_PORT = 0;
  public static final String TRANSPORTLAYER_DEFAULT = ""; //IP for SLPv2


  ServiceType serviceType;
  String namingAuthority;
  String address;
  String transportLayer = TRANSPORTLAYER_DEFAULT;
  int iPort = NO_PORT;
  String URLPath = "";
  int iLifetime = LIFETIME_DEFAULT;

/**
 * Builds a service URL object with default lifetime.
 * @param serviceURL - The service URL as a string.
 */
  public ServiceURL(String serviceURL) throws IllegalArgumentException {
    this(serviceURL,LIFETIME_DEFAULT);
  }

/**
 * Builds a service URL object .
 * @param sURL - The service URL as a string.
 * @param iLifetime - the service lifetime.
 * @throws IllegalArgumentException if the lifetime is out of range, or the URL is invald
 */
  public ServiceURL(String sURL, int iLifetime) throws IllegalArgumentException {
    String _URLPath = "";
 //   System.out.println("Creating "+sURL +" with lifetime " +iLifetime);
    if (sURL == null) throw new IllegalArgumentException(" No URL provided ");
    if (sURL.indexOf("://")==-1) throw new IllegalArgumentException(" No Valid URL provided: "+sURL);
    String sType = sURL.substring(0,sURL.indexOf("://"));
    serviceType = new ServiceType(sType);
    if ((iLifetime>LIFETIME_MAXIMUM)||(iLifetime < LIFETIME_NONE))
      throw new IllegalArgumentException(" Incorrect Lifetime provided ");
    this.iLifetime = iLifetime;

    if (!serviceType.isServiceType) {
      try {
        URL url = new URL(sURL);
        this.address = url.getHost();
        int _port = url.getPort();
        this.iPort = (_port != -1) ? _port : NO_PORT;
        this.URLPath = url.getPath();
        return;
      } catch (MalformedURLException mue){
        throw new IllegalArgumentException(" Malformed URL: "+sURL);
      }
    }
  //  System.out.println(" service Type is " +serviceType.toString());
    _URLPath = sURL.substring(sURL.indexOf("://")+3);
    // Parse the URL Path...
    // and separate port and address of the URL Path

    if (_URLPath.indexOf(":")!=-1){
      // The URL is //address:port[/path]
      String iPortS = "";
      iPortS = _URLPath.substring(_URLPath.indexOf(":")+1);
      try {
        if (iPortS.indexOf("/")!=-1) {
          // The URL is //address:port/path
          iPort = new Integer(iPortS.substring(0,iPortS.indexOf("/"))).intValue();
          this.URLPath = _URLPath.substring(_URLPath.indexOf("/")); // do not remove the initial "/"
        } else {
          // The URL is //address:port
          iPort = new Integer(iPortS).intValue();
        }
      } catch (Exception e) {
        throw new IllegalArgumentException(" Port in the address was not an integer ");
      }
      this.address = _URLPath.substring(0,_URLPath.indexOf(":"));
      if (address.length() == 0 ) {
        throw new IllegalArgumentException(" Empty address provided ");
      }
    } else {
      // The URL is //address[/path]
      if (_URLPath.indexOf("/")!=-1) {
        // The URL is //address/path
        this.address = _URLPath.substring(0,_URLPath.indexOf("/"));
        this.URLPath = _URLPath.substring(_URLPath.indexOf("/")); // remove the initial "/"
      } else {
        // The URL is //address
        if (_URLPath.length()!=0) {
          this.address = _URLPath;
        } else {
          // no address provided --> service location might be hard...
          throw new IllegalArgumentException("Empty address provided");
        }
      }
    }
  }

/**
 * The service type name
 */
  public final ServiceType getServiceType() {
    return serviceType;
  }

/**
 * The service part (host + port + URL path)
 */

  public final String getServicePart() {
    String servPart = address;
    if (iPort != NO_PORT) {
      servPart+= ":"+new Integer(iPort).toString();
    }
    servPart += getURLPath();
    return servPart;
  }


/**
 * The host identifier.
 */
  public final String getHost() {
    return address;
  }

/**
 * The port number, if any.
 */
  public final int getPort() {
    return iPort;
  }

/**
 * The URL path description
 * (the remainder of the whole URL, after host and port number.)
 */
  public final String getURLPath() {
    return URLPath;
  }

/**
 * The service advertisement lifetime.
 */
  public final int getLifetime() {
    return iLifetime;
  }

/**
 * The naming authority for the ServiceURL. An empty string means the default.
 */
  public final String getNamingAuthority() {
    return getServiceType().getNamingAuthority();
  }
/**
 * @return true if the parameter is a serviceURL that matches this one
 * in service type, service access point, and URL path
 */
  public final boolean equals(Object o){
    boolean result = false;
    try {
//      System.out.println( " Comparing " +this +" to "+ o);
      ServiceURL sURL = (ServiceURL) o;
      result =  this.getServiceType().equals(sURL.getServiceType()) &&
                this.getHost().equals(sURL.getHost()) &&
                (this.getPort() == sURL.getPort())&&
                this.getURLPath().equals(sURL.getURLPath());
    } catch (ClassCastException cce){}
    return result;
  }
/**
 * Return a hashcode of the service url
 */
  public int hashCode() {
//    int result = 0;
//    result += ServiceType.servicePrefix.hashCode();
//    result += getServicePart().hashCode();
//    return result;
    return toString().hashCode();
  }
/**
 * The service type and naming authority.
 * The name for a registered service type, including naming authority. They are separated with a "."
 */
  public final String getServiceTypeAndNamingAuthority(){
    String result = "";
    return getServiceType().toString();
  }
/**
 * A String representing this serviceURL
 */
  public final String toString() {
    String result = serviceType.toString()+"://";
    result+= getServicePart();
    return result;
  }


}
