/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.hadoop.conf;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.net.NetUtils;
import org.smartfrog.services.hadoop.core.SFHadoopRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.net.URL;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * This is our extended configuration, which takes a Prim component as a source of information
 */
public class ManagedConfiguration extends JobConf implements PrimSource,
        ConfigurationAttributes {

    private Prim source;
    private ComponentHelper helper;

    /**
     * Some attributes that are not listed in the component
     * (so they can be picked up from parents) but which should be
     * discovered.
     */
    private static final String[] REQUIRED_ATTRIBUTES = {
            MAPRED_INPUT_DIR,
            MAPRED_OUTPUT_DIR,
            MAPRED_LOCAL_DIR
    };

    /**
     * A new configuration.
     *
     * @param source source of config information
     */
    public ManagedConfiguration(Prim source) {
        bind(source);
    }

    /**
     * A new configuration with the same settings cloned from another.
     *
     * @param source source of config information
     * @param other  the configuration from which to clone settings.
     */
    public ManagedConfiguration(Configuration other, Prim source) {
        super(other);
        bind(source);
    }

    /**
     * Bind to our owner
     *
     * @param src source component
     */
    private void bind(Prim src) {
        this.source = src;
        helper = new ComponentHelper(src);
    }


    /**
     * A new configuration with the same settings cloned from another.
     *
     * @param conf the configuration from which to clone settings.
     */
    public ManagedConfiguration(Configuration conf) {
        super(conf);
        if (conf instanceof PrimSource) {
            PrimSource primsource = (PrimSource) conf;
            source = primsource.getSource();
        } else {
            throw new SFHadoopRuntimeException(
                    "No Prim source for the configuration");
        }
    }

    /**
     * Return the source
     *
     * @return the source component
     */
    @Override
    public Prim getSource() {
        return source;
    }

    /**
     * Set the <code>value</code> of the <code>name</code> property.
     *
     * @param name  property name.
     * @param value property value.
     */
    @Override
    public void set(String name, String value) {
        super.set(name, value);
    }

    private void setInSource(String name, String value) {
        try {
            source.sfReplaceAttribute(name, value);
        } catch (SmartFrogRuntimeException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }

    /**
     * Get the value of the <code>name</code> property. If no such property exists, then <code>defaultValue</code> is
     * returned.
     *
     * @param name         property name.
     * @param defaultValue default value.
     * @return property value, or <code>defaultValue</code> if the property doesn't exist.
     * @throws SFHadoopRuntimeException if things go wrong on SmartFrog
     */
    @Override
    public String get(String name, String defaultValue) {
        try {
            Object result = source.sfResolve(name, true);
            if (result == null || result instanceof SFNull) {
                return defaultValue;
            }
 /*           if (result instanceof Reference) {
                result = source.sfResolve(name, true);
            }*/
            return result.toString();
        } catch (SmartFrogResolutionException ignored) {
            return defaultValue;
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }

    /**
     * Get the value of the <code>name</code> property, without doing <a href="#VariableExpansion">variable
     * expansion</a>.
     *
     * @param name the property name.
     * @return the value of the <code>name</code> property, or null if no such property exists.
     * @throws SFHadoopRuntimeException if things go wrong on SmartFrog
     */
    @Override
    public String getRaw(String name) {
        return get(name, null);
    }

    /**
     * Get the value of the <code>name</code> property, <code>null</code> if no such property exists. <p/> Values are
     * processed for <a href="#VariableExpansion">variable expansion</a> before being returned.
     *
     * @param name the property name.
     * @return the value of the <code>name</code> property, or null if no such property exists.
     * @throws SFHadoopRuntimeException if things go wrong on SmartFrog
     */
    @Override
    public String get(String name) {
        return get(name, null);
    }

    /**
     * Add a configuration resource.
     *
     * The properties of this resource will override properties of previously added resources, unless they were marked
     * <a href="#Final">final</a>.
     *
     * @param name resource to be added, the classpath is examined for a file with that name.
     * @throws SFHadoopRuntimeException always
     */
    @Override
    public void addResource(String name) {
        resourcesNotSupported();
    }

    /**
     * @throws SFHadoopRuntimeException always
     */
    private void resourcesNotSupported() {
        throw new SFHadoopRuntimeException("This class does not support XML configuration resources");
    }

    /**
     * Add a configuration resource.
     *
     * @param url url of the resource to be added, the local filesystem is examined directly to find the resource,
     *            without referring to the classpath.
     * @throws SFHadoopRuntimeException always
     */
    @Override
    public void addResource(URL url) {
        resourcesNotSupported();
    }

    /**
     * Add a configuration resource.
     *
     * The properties of this resource will override properties of previously added resources, unless they were marked
     * <a href="#Final">final</a>.
     *
     * @param file file-path of resource to be added, the local filesystem is examined directly to find the resource,
     *             without referring to the classpath.
     * @throws SFHadoopRuntimeException always
     */
    @Override
    public void addResource(Path file) {
        resourcesNotSupported();
    }

    /**
     * Write out the non-default properties in this configuration to the give {@link OutputStream}.
     *
     * @param out the output stream to write to.
     * @throws SFHadoopRuntimeException if things go wrong
     */
    @Override
    public void write(OutputStream out) throws IOException {
        try {
            Map<String, String> map = getState();
            writeState(map, out);
        } catch (SmartFrogResolutionException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (TransformerException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }

    /**
     * Enumerate our current state and generate a properties structure of it.
     *
     * @return a newly created properties structure
     * @throws RemoteException              for network problems
     * @throws SmartFrogResolutionException for resolution problems
     */
    private SortedMap<String, String> getState() throws RemoteException, SmartFrogResolutionException {
        SortedMap<String, String> map = new TreeMap<String, String>();
        Iterator<Object> objectIterator = source.sfAttributes();
        while (objectIterator.hasNext()) {
            Object key = objectIterator.next();
            Object value = source.sfResolve(new Reference(key));
            if (!(value instanceof Remote)
                    && !(value instanceof ComponentDescription)
                    && !(value instanceof SFNull)) {
                map.put(key.toString(), value.toString());
            }
        }
        //now add the required stuff if not there
        for (String required : REQUIRED_ATTRIBUTES) {
            if (map.get(required) == null) {
                String value = get(required);
                if (value != null) {
                    map.put(required, value);
                }
            }
        }
        return map;
    }

    /**
     * Get an {@link Iterator} to go through the list of <code>String</code> key-value pairs in the configuration.
     *
     * @return an iterator over the entries.
     * @throws SFHadoopRuntimeException for resolution problems
     */
    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        try {
            Map<String, String> map = getState();
            return map.entrySet().iterator();
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (SmartFrogResolutionException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }

    /**
     * Write the current state to a file
     *
     * @param state our state
     * @param out   output stream
     * @throws TransformerException         XML trouble
     * @throws ParserConfigurationException XML trouble
     */
    private void writeState(Map<String, String> state, OutputStream out)
            throws TransformerException, ParserConfigurationException {
        Document doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element conf = doc.createElement("configuration");
        doc.appendChild(conf);
        conf.appendChild(doc.createTextNode("\n"));
        for (Map.Entry<String, String> entry : state.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            Element propNode = doc.createElement("property");
            conf.appendChild(propNode);

            Element nameNode = doc.createElement("name");
            nameNode.appendChild(doc.createTextNode(name));
            propNode.appendChild(nameNode);

            Element valueNode = doc.createElement("value");
            valueNode.appendChild(doc.createTextNode(value));
            propNode.appendChild(valueNode);

            conf.appendChild(doc.createTextNode("\n"));
        }

        DOMSource domSource = new DOMSource(doc);
        StreamResult result = new StreamResult(out);
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.transform(domSource, result);
    }

    /**
     * Print our prim reference
     *
     * @return a string description
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SmartFrog Managed Configuration bound to ");
        builder.append(helper.completeNameSafe().toString());
        return builder.toString();
    }

    /**
     * Dump our state to a string; triggers a full resolution.
     *
     * @return a complete dump of name "value"; pairs, in order
     * @throws SmartFrogResolutionException problems resolving attributes
     * @throws RemoteException              network trouble
     */
    public String dump() throws SmartFrogResolutionException, RemoteException {
        StringBuilder builder = new StringBuilder();
        SortedMap<String, String> map = getState();
        for (String key : map.keySet()) {
            builder.append(key);
            builder.append(" \"");
            builder.append(map.get(key));
            builder.append("\";\n");
        }
        return builder.toString();
    }

    /**
     * dump quietly; exceptions are turned into strings
     *
     * @return the dump of name value pairs or an error message
     */
    public String dumpQuietly() {
        try {
            return dump();
        } catch (SmartFrogResolutionException e) {
            return '(' + e.toString() + ')';
        } catch (RemoteException e) {
            return '(' + e.toString() + ')';
        }
    }

    /**
     * Bind to a network address; something like  "0.0.0.0:50030" is expected.
     * @param addressName the property for the address
     * @param bindAddressName old style hostname
     * @param bindAddressPort old style host port
     * @return the host/port binding
     * @throws IllegalArgumentException if the arguments are bad
     */
  public InetSocketAddress bindToNetwork(String addressName, String bindAddressName, String bindAddressPort) {
      String infoAddr =
              NetUtils.getServerAddress(this,
                      bindAddressName,
                      bindAddressPort,
                      addressName);
      InetSocketAddress socketAddress = NetUtils.createSocketAddr(infoAddr);
      return socketAddress;
  }
}
