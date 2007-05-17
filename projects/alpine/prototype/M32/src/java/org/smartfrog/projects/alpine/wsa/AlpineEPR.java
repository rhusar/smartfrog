/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.wsa;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.interfaces.XomSource;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.projects.alpine.om.soap11.Envelope;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.xmlutils.NodeIterator;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.MalformedURLException;

/*
<wsa:EndpointReference
 xmlns:wsa="http://www.w3.org/2005/08/addressing"
 xmlns:wsaw="http://www.w3.org/2005/03/addressing/wsdl"
 xmlns:fabrikam="http://example.com/fabrikam"
 xmlns:wsdli="http://www.w3.org/2005/08/wsdl-instance"
 wsdli:wsdlLocation="http://example.com/fabrikam
 http://example.com/fabrikam/fabrikam.wsdl">
<wsa:Address>http://example.com/fabrikam/acct</wsa:Address>
<wsa:Metadata>
<wsaw:InterfaceName>fabrikam:Inventory</wsaw:InterfaceName>
</wsa:Metadata>
<wsa:ReferenceParameters>
<fabrikam:CustomerKey>123456789</fabrikam:CustomerKey>
<fabrikam:ShoppingCart>ABCDEFG</fabrikam:ShoppingCart>
</wsa:ReferenceParameters>
</wsa:EndpointReference>
*/

/**
 * Alpine model of an EndpointReference
 * created 22-Mar-2006 14:56:06
 * <code>
 *
 * @see <a href="http://www.w3.org/TR/2005/CR-ws-addr-soap-20050817/">WS-A specification</a>
 *      </code>
 */

public final class AlpineEPR implements Validatable, AddressingConstants, XomSource {


    private String address;

    private Element metadata;

    private Element referenceParameters;


    /**
     * The anonymous To: endpoint
     */
    public static AlpineEPR EPR_ANONYMOUS = new AlpineEPR(WSA_ADDRESS_ANON);

    /**
     * The not-an-endpoint endpoint
     */
    public static AlpineEPR EPR_NONE = new AlpineEPR(WSA_ADDRESS_NONE);

    public AlpineEPR() {
    }

    public AlpineEPR(String address) {
        this.address = address;
    }

    public AlpineEPR(URL url) {
        this.address = url.toExternalForm();
    }

    /**
     * read the address from the XML; see {@link #read(nu.xom.Element, String)}
     *
     * @param element   element to read
     * @param namespace WS-A namespace; if null is inferred from the element
     */
    public AlpineEPR(Element element, String namespace) {
        read(element, namespace);
    }

    public AlpineEPR(AlpineEPR that) {
        address = that.address;
        if (that.metadata != null) {
            metadata = (Element) that.metadata.copy();
        }
        if (that.referenceParameters != null) {
            referenceParameters = (Element) that.referenceParameters.copy();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Element getMetadata() {
        return metadata;
    }

    public void setMetadata(Element metadata) {
        this.metadata = metadata;
    }

    public Element getReferenceParameters() {
        return referenceParameters;
    }

    public void setReferenceParameters(Element referenceParameters) {
        this.referenceParameters = referenceParameters;
    }

    /**
     * validate an instance.
     * Return if the object is valid, thrown an exception if not.
     * It is imperative that this call has <i>No side effects</i>.
     * <p/>
     * Why is this a boolean? For insertion into assert ; statements.
     *
     * @return true unless an exception is thrown
     * @throws org.smartfrog.projects.alpine.faults.ValidationException
     *          with text if not valid
     */
    public boolean validate() throws ValidationException {
        if (getAddress() == null || getAddress().length() == 0) {
            throw new ValidationException("Missing or empty " + WSA_TO + " attribute");
        }
        return true;
    }

    /**
     * Add the address to a SOAP message as the To: element. This will also replace any existing headers of the same name
     * This triggers a call to {@link #validate()} to validate the address
     *
     * @param message        message to add to
     * @param namespace      which xmlns to use
     * @param prefix         prefix for elements
     * @param markReferences whether to mark references or not as references (the later specs require this)
     * @param mustUnderstand should the address + actions headers be mustUnderstand=true?
     */
    public void addressMessage(MessageDocument message,
                               String role,
                               String namespace,
                               String prefix,
                               boolean markReferences,
                               boolean mustUnderstand) {
        validate();
        String prefixColon = prefix + ":";
        Envelope env = message.getEnvelope();
        Header header = env.getHeader();
        Element to = new SoapElement(prefixColon + WSA_TO, namespace, getAddress());
        header.setHeaderElement(to, mustUnderstand);
        if (referenceParameters != null) {
            for (Node node : new NodeIterator(referenceParameters)) {
                if (node instanceof Element) {
                    Element e = (Element) node;
                    Element copy = (Element) e.copy();
                    if (markReferences) {
                        Attribute isRef = new Attribute(prefixColon + WSA_ATTR_IS_REFERENCE_PARAMETER,
                                namespace,
                                "true");
                        copy.appendChild(isRef);
                    }
                    header.addOrReplaceChild(copy);
                }
            }
        }
    }

    /**
     * Convert the message into a Xom element
     *
     * @return the element
     */
    public Element toXom() {
        return toXom(WSA_ADDRESS, XMLNS_WSA_2005, "wsa");
    }

    /**
     * Convert it to a Xom element tree.
     *
     * @param localname local name of root element (e.g "To"
     * @param namespace namespace, e.g. {@link AddressingConstants#XMLNS_WSA_2005}
     * @param prefix    prefix, e.g. wsa2005
     * @return an address containing all the parts of the address as children
     */
    public SoapElement toXom(String localname, String namespace, String prefix) {
        return toXomInNewNamespace(
                localname,namespace,prefix,namespace, prefix);
    }


    /**
     * Convert to a Xom graph in a namespace of choice for the toplevel node; children
     * are in the XML ns of choice
     * @param rootname root name
     * @param rootNs top
     * @param rootPrefix root prefix without the colon
     * @param wsaNs xmlns for the WSA children
     * @param wsaPrefix prefix for the WSA children
     * @return the graph in a new namespace
     */
    public SoapElement toXomInNewNamespace(String rootname,
                                           String rootNs,String rootPrefix,
                                           String wsaNs, String wsaPrefix) {
        String prefixColon = wsaPrefix + ":";
        SoapElement root = new SoapElement(rootPrefix +":"+rootname, rootNs);
        root.addNamespaceDeclaration(wsaPrefix,wsaNs);
        if (address != null) {
            Element to = new SoapElement(prefixColon + WSA_ADDRESS, wsaNs, getAddress());
            root.appendChild(to);
        }
        if (referenceParameters != null) {
            SoapElement elt = new SoapElement(prefixColon + WSA_REFERENCE_PARAMETERS, wsaNs);
            elt.copyChildrenFrom(referenceParameters);
            root.appendChild(elt);
        }
        if (metadata != null) {
            SoapElement elt = new SoapElement(prefixColon + WSA_METADATA, wsaNs);
            elt.copyChildrenFrom(metadata);
        }
        return root;
    }


    /**
     * Clone by creating a new instance. this is a deep copy, all the way down.
     *
     * @return a full deep copy of the xml
     */
    public AlpineEPR clone() {
        return new AlpineEPR(this);
    }

    /**
     * Read in the EPR from an element, cloning bits.
     * the namespace defines the namespace to look for. If null, use the xmlns of the element passed in.
     * Other elements in the same namespace are ignored; there is no post-read validation.
     * the WSA:Address element is trimmed, but not checked for being empty or null. Use validate() to check that
     * @param element   element to start at
     * @param namespace namespace to use
     * @throws InvalidXmlException if there was no namespace
     */
    public void read(Element element, String namespace) {
        if (namespace == null) {
            namespace = element.getNamespaceURI();
            if (namespace == null) {
                throw new InvalidXmlException("No namespace on " + element);
            }
        }
        for (Node n : new NodeIterator(element)) {
            if (n instanceof Element) {
                Element elt = (Element) n;
                String eltNamespace = elt.getNamespaceURI();
                if (namespace.equals(eltNamespace)) {
                    String localname = elt.getLocalName();
                    if (WSA_ADDRESS.equals(localname)) {
                        address = elt.getValue();
                        if(address!=null) {
                            address = address.trim();
                        }
                    } else if (WSA_REFERENCE_PARAMETERS.equals(localname)) {
                        referenceParameters = (Element) elt.copy();
                    } else if (WSA_METADATA.equals(localname)) {
                        metadata = (Element) elt.copy();
                    }
                }
            }
        }

    }

    /**
     * Read in the EPR from the headers of a message, cloning bits.
     * the namespace defines the namespace to look for.
     *
     * @param headers   element to start at
     * @param namespace namespace to use
     * @throws InvalidXmlException if there was no such message
     */
    public boolean readFromHeaders(Header headers, String namespace, boolean required) {
        QName name = new QName(namespace, WSA_TO);
        Element to = headers.getFirstChildElement(name);
        if (to != null) {
            address = to.getValue();
            return true;
        } else if (required) {
            throw new InvalidXmlException("No address element " + name + " in the message");
        }
        return false;
    }

    /**
     * Returns a hash code value for the object. This is derived from the address if it is
     * set, or our own address if not.
     * Changing the address breaks the hash code immutability rule, loses us in hash tables, etc, etc.
     *
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see java.util.Hashtable
     */
    public int hashCode() {
        if (getAddress() != null) {
            return address.hashCode();
        } else {
            return super.hashCode();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Only the address field is used in this test, and if it is  null, it is considered
     * equal to any other EPR with no address
     * <p/>
     * The <code>equals</code> method implements an equivalence relation
     * on non-null object references:
     * Note that it is generally necessary to override the <tt>hashCode</tt>
     * method whenever this method is overridden, so as to maintain the
     * general contract for the <tt>hashCode</tt> method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj
     *         argument; <code>false</code> otherwise.
     * @see #hashCode()
     * @see java.util.Hashtable
     */
    public boolean equals(Object obj) {
        AlpineEPR that = (AlpineEPR) obj;
        return address == null ? that.address == null : address.equals(that.address);

    }

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        return "EPR to " + address;
    }

    /**
     * Look up a name=value pair from the query part of a URL 
     * @param name property to search
     * @return the query or null for no match
     * @throws AlpineRuntimeException if the URL would not parse
     */
    public String lookupQuery(String name) {
        URI uri;
        try {
            uri = new URI(getAddress());
        } catch (URISyntaxException e) {
            throw new AlpineRuntimeException("Couldn't turn an addr into a URL: " +
                    toString(), e);
        }
        String query = uri.getQuery();
        return lookupQuery(query, name);
    }

    /**
     * Look up a name=value pair from the query part of a URL
     * @param name property to search
     * @return the query or null for no match
     */
    public static String lookupQuery(String query, String name) {
        if(query==null) {
            return null;
        }
        String param= name + "=";
        int index;
        if(query.startsWith(param)) {
            //first query in the string
            index= 0;
        } else {
            //this is the second query
            param= "&" + param;
            index = query.indexOf(param);
        }
        if(index<0) {
            return null;
        }
        int pl=param.length();

        //find the end of the string
        int end = query.indexOf("&", index+pl);
        if (end == -1) {
            end = query.length();
        }
        return query.substring(index+pl, end).trim();
    }


    /**
     * Create a URL from the address
     * @return a new URL
     * @throws AlpineRuntimeException for any failure to convert the address string to a URL
     */
    public URL createAddressURL() {
        if(address ==null) {
            throw new AlpineRuntimeException("No address");
        }
        try {
            return new URL(address);
        } catch (MalformedURLException e) {
            throw new AlpineRuntimeException("Cannot conver to a URL:"+address);
        }
    }
}
