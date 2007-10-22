/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
package org.smartfrog.services.slp;

import java.net.*;
/**
 * A class representing a service url.
 * It contains the service type, service access point (hostname) and URL path
 * needed to reach the service.
 *
 * @author Guillaume Mecheneau
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
