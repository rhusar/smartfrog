
package org.smartfrog.services.cloudfarmer.api;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

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
     * @param name     name of link
     * @param protocol protocol that the URL handlers can handle
     * @param port     port, or -1 for protocol-default
     * @param path     path under the machine
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

    /**
     * Make a URL from a nodelink
     * @param hostname hostname to bind to
     * @return the URL
     * @throws MalformedURLException if a URL cannot be built
     */
    public URL makeUrl(String hostname) throws MalformedURLException {
        if (hostname == null) {
            return null;
        }
        URL url = new URL(protocol, hostname, port, path);
        return url;
    }

    /**
     * Bind to an (internal, external) pair
     *
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
     * Take a template list of uninstantiated links and convert to a list of bound entries
     *
     * @param links            source links
     * @param internalHostname internal name (can be null)
     * @param externalHostname (can be null)
     * @return a list of instantiated links
     * @throws MalformedURLException if the URLS cannot be constructed
     */
    public static NodeLink[] instantiate(NodeLink[] links, String internalHostname, String externalHostname)
            throws MalformedURLException {
        NodeLink[] result = new NodeLink[links.length];
        for (int i = 0; i < links.length; i++) {
            result[i] = new NodeLink(links[i], internalHostname, externalHostname);
        }
        return result;
    }
}
