/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.api;

import java.io.Serializable;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * This is a link to something on the node; it has a name, a protocol, a port and maybe a path
 */

public final class NodeLink implements Serializable, Cloneable {

    private String name;
    private String protocol;
    private int port;
    private String path;
    private URL externalLink;
    private URL internalLink;


    public NodeLink() {
    }

    /**
     * 
     * @param name name of link
     * @param protocol protocol that the URL handlers can handle
     * @param port port, or -1 for protocol-default
     * @param path path under the machine
     */
    public NodeLink(String name, String protocol, int port, String path) {
        this.name = name;
        this.protocol = protocol;
        this.port = port;
        this.path = path;
    }

    public NodeLink(NodeLink source) {
        this.name = source.name;
        this.protocol = source.protocol;
        this.port = source.port;
        this.path = source.path;
        this.internalLink = source.internalLink;
        this.externalLink = source.externalLink;
    }


    public NodeLink(NodeLink source, String internalHostname, String externalHostname) throws MalformedURLException {
        this(source);
        bind(internalHostname, externalHostname);
    }

    public URL makeUrl(String hostname) throws MalformedURLException {
        if(hostname==null) {
            return null;
        }
        URL url = new URL(protocol, hostname, port, path);
        return url;
    }

    /**
     * Bind to an (internal, external) pair
     * @param internalHostname internal hostname (can be null)
     * @param externalHostname external hostname (can be null)
     * @throws MalformedURLException if the URLs cannot be constructed
     */
    public void bind(String internalHostname, String externalHostname) throws MalformedURLException {
        externalLink = makeUrl(externalHostname);
        internalLink = makeUrl(internalHostname);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public URL getExternalLink() {
        return externalLink;
    }

    public URL getInternalLink() {
        return internalLink;
    }

    /**
     * Take a template list of uninstantaited links and convert to a list of bound entries
     * @param links source links
     * @param internalHostname internal name (can be null)
     * @param externalHostname (can be null)
     * @return a list of instantiated links
     * @throws MalformedURLException if the URLS cannot be constructed
     */
    public static NodeLink[] instantiate(NodeLink[] links, String internalHostname, String externalHostname)
            throws MalformedURLException {
        NodeLink[] result = new NodeLink[links.length];
        for (int i=0; i<links.length; i++) {
            result[i]=new NodeLink(links[i], internalHostname, externalHostname);   
        }
        return result;
    }
}
