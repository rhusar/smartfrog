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
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

/**
 * This is our extended configuration, which takes a Prim component as a source of information as well as (optionally)
 * the default values. This makes the reload process more complex, as it re-evaluates it from a component
 */
public final class ManagedConfiguration extends JobConf implements PrimSource,
        ConfigurationAttributes {

    private Prim source;
    private ComponentHelper helper;
    private SortedMap<String, String> attributeMap = null;

    /**
     * Some attributes that are not listed in the component (so they can be picked up from parents) but which should be
     * discovered.
     */
    private static final String[] REQUIRED_ATTRIBUTES = {
            MAPRED_INPUT_DIR,
            MAPRED_OUTPUT_DIR,
            MAPRED_LOCAL_DIR
    };
    public static final String MISSING_ATTRIBUTE = "Missing attribute";

    /**
     * A new configuration with the same settings cloned from another.
     *
     * @param conf the configuration from which to clone settings.
     */
/*    public ManagedConfiguration(Configuration conf) {
        super(conf);
        if (conf instanceof PrimSource) {
            PrimSource primsource = (PrimSource) conf;
            source = primsource.getSource();
        } else {
            throw new SFHadoopRuntimeException(
                    "No Prim source for the configuration");
        }
    }*/

    /**
     * A new map/reduce configuration where the behavior of reading from the default resources can be turned off. <p/>
     * If the parameter {@code loadDefaults} is false, the new instance will not load resources from the default files.
     *
     * @param loadDefaults specifies whether to load from the default files
     * @param source       source of config information
     */
    public ManagedConfiguration(boolean loadDefaults, Prim source) throws SmartFrogException,
            RemoteException {
        super(loadDefaults);
        bind(source);
    }

    /**
     * A new configuration. Default values are picked up
     *
     * @param source source of config information
     */
    public ManagedConfiguration(Prim source) throws SmartFrogException,
            RemoteException {
        this(false, source);
    }


    /**
     * Bind to our owner
     *
     * @param src source component
     */
    private void bind(Prim src) throws SmartFrogException, RemoteException {
        if (src == null) {
            throw new SFHadoopException("Cannot bind to a null source component");
        }
        source = src;
        helper = new ComponentHelper(src);
        copyComponentState(src, null);
    }


    /**
     * Override something to build the properties array
     */
/*    @Override
    protected synchronized Properties getProps() {
        buildAttributeMapQuietly();
        Properties props = new Properties();
        for (String key : attributeMap.keySet()) {
            String value = attributeMap.get(key);
            props.put(key, value);
        }
        return props;
    }*/

    /**
     * Build the attribute map from the current set of attributes; turn all exceptions into a runtime exception
     *
     * @throws SFHadoopRuntimeException on any resolution/remoting problem
     */
    private void buildAttributeMapQuietly() throws SFHadoopRuntimeException {
        if (attributeMap == null) {
            try {
                attributeMap = getState();
            } catch (RemoteException e) {
                throw new SFHadoopRuntimeException(e);
            } catch (SmartFrogResolutionException e) {
                throw new SFHadoopRuntimeException(e);
            }
        }
    }

    /**
     * Reload the configuration. This rests our cache. {@inheritDoc}
     */
    @Override
    public synchronized void reloadConfiguration() {
        super.reloadConfiguration();
        attributeMap = null;
    }


    /**
     * Return the source
     *
     * @return the source component
     */
    //@Override
    public Prim getSource() {
        return source;
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
/*    @Override
    public String get(String name, String defaultValue) {
        try {
            return sfResolve(name, defaultValue);
        } catch (SmartFrogResolutionException ignored) {
            return defaultValue;
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }*/

    /**
     * Resolve a configuration valaue
     *
     * @param name         attribute to resolve
     * @param defaultValue the default value
     * @return the default value
     * @throws SmartFrogResolutionException failure to resolve
     * @throws RemoteException              network problems
     */
    public String sfResolve(String name, String defaultValue)
            throws SmartFrogResolutionException, RemoteException {
        Object result = source.sfResolve(name, true);
        if (result == null || result instanceof SFNull) {
            return defaultValue;
        }
        if (result instanceof Reference) {
            result = source.sfResolve(name, true);
        }
        return result.toString();
    }

    /**
     * Get the value of the <code>name</code> property, without doing <a href="#VariableExpansion">variable
     * expansion</a>.
     *
     * @param name the property name.
     * @return the value of the <code>name</code> property, or null if no such property exists.
     * @throws SFHadoopRuntimeException if things go wrong on SmartFrog
     */
/*    @Override
    public String getRaw(String name) {
        return get(name, null);
    }*/

    /**
     * Get the value of the <code>name</code> property, <code>null</code> if no such property exists. <p/> Values are
     * processed for <a href="#VariableExpansion">variable expansion</a> before being returned.
     *
     * @param name the property name.
     * @return the value of the <code>name</code> property, or null if no such property exists.
     * @throws SFHadoopRuntimeException if things go wrong on SmartFrog
     */
/*    @Override
    public String get(String name) {
        return get(name, null);
    }*/

    /**
     * Add a configuration resource.
     *
     * The properties of this resource will override properties of previously added resources, unless they were marked
     * <a href="#Final">final</a>.
     *
     * @param name resource to be added, the classpath is examined for a file with that name.
     * @throws SFHadoopRuntimeException always
     */
/*    @Override
    public void addResource(String name) {
        resourcesNotSupported();
    }*/

    /**
     * @throws SFHadoopRuntimeException always
     */
/*    private void resourcesNotSupported() {
        throw new SFHadoopRuntimeException("This class does not support XML configuration resources");
    }*/

    /**
     * Add a configuration resource.
     *
     * @param url url of the resource to be added, the local filesystem is examined directly to find the resource,
     *            without referring to the classpath.
     * @throws SFHadoopRuntimeException always
     */
/*    @Override
    public void addResource(URL url) {
        resourcesNotSupported();
    }*/

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
/*    @Override
    public void addResource(Path file) {
        resourcesNotSupported();
    }*/

    /**
     * Write out the non-default properties in this configuration to the give {@link OutputStream}.
     *
     * @param out the output stream to write to.
     * @throws SFHadoopRuntimeException if things go wrong
     */
/*    @Override
    public void writeXml(OutputStream out) throws IOException {
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
    }*/

    /**
     * This copies the component state: all attributes directly off this component.
     *
     * @param component the component to copy from
     * @param requiredKeys list of keys to always pull in, can be null
     * @throws RemoteException
     * @throws SmartFrogResolutionException
     */
    private void copyComponentState(Prim component,
                                    List<String> requiredKeys) throws RemoteException, SmartFrogResolutionException {

        Iterator<Object> keys = component.sfAttributes();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = component.sfResolve(new Reference(key));
            if (!(value instanceof Remote)
                    && !(value instanceof ComponentDescription)
                    && !(value instanceof SFNull)) {
                set(key.toString(), value.toString());
            }
            //files get special treatment
            if (value instanceof FileIntf) {
                FileIntf fi = (FileIntf) value;
                set(key.toString(), fi.getAbsolutePath());
            }
        }
        if (requiredKeys != null) {
            for (String required : requiredKeys) {
                if (get(required) == null) {
                    Object value = component.sfResolve(required, false);
                    if (value != null && !(value instanceof SFNull)) {
                        set(required, value.toString());
                    }
                }
            }
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
            //files get special treatment
            if (value instanceof FileIntf) {
                FileIntf fi = (FileIntf) value;
                map.put(key.toString(), fi.getAbsolutePath());
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
/*    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        try {
            Map<String, String> map = getState();
            return map.entrySet().iterator();
        } catch (RemoteException e) {
            throw new SFHadoopRuntimeException(e);
        } catch (SmartFrogResolutionException e) {
            throw new SFHadoopRuntimeException(e);
        }
    }*/

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
     *
     * @param addressName     the property for the address
     * @param bindAddressName old style hostname
     * @param bindAddressPort old style host port
     * @return the host/port binding
     * @throws IllegalArgumentException     if the arguments are bad
     * @throws SmartFrogResolutionException if the address does not resolve
     */
    public InetSocketAddress bindToNetwork(String addressName, String bindAddressName, String bindAddressPort)
            throws SmartFrogResolutionException {
        String infoAddr =
                NetUtils.getServerAddress(this,
                        bindAddressName,
                        bindAddressPort,
                        addressName);
        if (infoAddr == null) {
            throw new SmartFrogResolutionException("Failed to resolve " + addressName);
        }
        InetSocketAddress socketAddress = NetUtils.createSocketAddr(infoAddr);
        return socketAddress;
    }

    /**
     * Copy in the properties from a configuration, by adding them as attributes if the component does not have them
     * already. The configuration is marked for reloading
     *
     * @param conf configuration
     * @throws SmartFrogRuntimeException failure to read or write an attribute
     * @throws RemoteException           network problems
     */
    public void addProperties(Configuration conf) throws SmartFrogRuntimeException, RemoteException {
        assert conf != this;
        for (Map.Entry<String, String> entry : conf) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                source.sfResolveHere(key);
            } catch (SmartFrogResolutionException e) {
                source.sfReplaceAttribute(key, value);
            }
        }
        reloadConfiguration();
    }

    /**
     * Creates and returns a copy of this object. A configuration reload is triggered, so that datastructures are not
     * accidentally shared across instances.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not support the <code>Cloneable</code> interface.
     *                                    Subclasses that override the <code>clone</code> method can also throw this
     *                                    exception to indicate that an instance cannot be cloned.
     * @see Cloneable
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        ManagedConfiguration that = (ManagedConfiguration) super.clone();
        that.reloadConfiguration();
        return that;
    }

    /**
     * Run through the list and check which attrs are present; throw an exception listing them all if not
     *
     * @param requiredAttributes list of attributes that are requires
     * @throws SmartFrogResolutionException for any failure to resolve all the attributes
     * @throws RemoteException              network problems. These are always passed up
     */
    public void validate(List<String> requiredAttributes) throws SmartFrogResolutionException, RemoteException {
        List<String> missing = new ArrayList<String>();
        for (String attr : requiredAttributes) {
            try {
                sfResolve(attr, null);
            } catch (SmartFrogResolutionException e) {
                missing.add(attr);
            }
        }
        int size = missing.size();
        if (size > 0) {
            StringBuilder text = new StringBuilder(MISSING_ATTRIBUTE + (size > 1 ? "s" : "") + ":");
            for (String attr : missing) {
                text.append("\"");
                text.append(attr);
                text.append("\" ");
            }
            throw new SmartFrogResolutionException(text.toString(), source);
        }
    }

    /**
     * This resolves the (smartfrog formatted) list of attributes to look for and checks that they are all present
     *
     * @param attributeRef a reference to the attributes to fetch
     * @throws SmartFrogResolutionException for any failure to resolve all the attributes
     * @throws RemoteException              network problems. These are always passed up
     */
    public void validateListedAttributes(Reference attributeRef) throws SmartFrogResolutionException, RemoteException {
        List<String> required = ListUtils.resolveStringList(source, attributeRef, true);
        validate(required);
    }
}
