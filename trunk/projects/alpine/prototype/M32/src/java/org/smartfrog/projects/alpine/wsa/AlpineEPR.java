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

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Attribute;
import org.smartfrog.projects.alpine.om.soap11.Envelope;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Header;
import org.smartfrog.projects.alpine.om.base.ElementEx;
import org.smartfrog.projects.alpine.interfaces.Validatable;
import org.smartfrog.projects.alpine.interfaces.XomSource;
import org.smartfrog.projects.alpine.faults.ValidationException;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;
import org.smartfrog.projects.alpine.xmlutils.NodeIterator;

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
 * @see <a href="http://www.w3.org/TR/2005/CR-ws-addr-soap-20050817/">WS-A specification</a>
 </code>
 */

public final class AlpineEPR implements Validatable, AddressingConstants, XomSource {


    private String address;


    private Element metadata;

    private Element referenceParameters;

    public AlpineEPR() {
    }

    /**
     * read the address from the XML; see {@link #read(nu.xom.Element, String)}
     * @param element element to read
     * @param namespace WS-A namespace; if null is inferred from the element
     */
    public AlpineEPR(Element element,String namespace) {
        read(element, namespace);
    }

    public AlpineEPR(AlpineEPR that) {
        address=that.address;
        if(that.metadata!=null) {
            metadata=(Element) that.metadata.copy();
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
        if(getAddress()==null || getAddress().length()==0) {
            throw new ValidationException("Missing or empty "+WSA_TO +" attribute");
        }
        return true;
    }

    /**
     * Add the address to a SOAP message as the To: element. This will also replace any existing headers of the same name
     * This triggers a call to {@link #validate()} to validate the address
     * @param message   message to add to
     * @param namespace which xmlns to use
     * @param prefix prefix for elements
     * @param markReferences whether to mark references or not as references (the later specs require this)
     * @param mustUnderstand should the address + actions headers be mustUnderstand=true?
     */
    public void addToSoapMessage(MessageDocument message, String namespace, String prefix,
                                 boolean markReferences,
                                 boolean mustUnderstand) {
        validate();
        String prefixColon=prefix+":";
        Envelope env=message.getEnvelope();
        Header header=env.getHeader();
        Element to=new ElementEx(prefixColon+WSA_TO,namespace);
        Header.setMustUnderstand(to,mustUnderstand);
        to.appendChild(getAddress());
        header.addOrReplaceChild(to);
        if(referenceParameters!=null) {
            for(Node node: new NodeIterator(referenceParameters)) {
                if(node instanceof Element) {
                    Element e=(Element) node;
                    Element copy=(Element) e.copy();
                    if(markReferences) {
                        Attribute isRef=new Attribute(prefixColon+WSA_ATTR_IS_REFERENCE_PARAMETER,
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
        return toXom(WSA_ADDRESS,XMLNS_WSA_2005,"wsa");
    }

    /**
     * Convert it to a Xom element tree.
     * ISSUE: there is no namespacing of reference params or metadata; we just clone them.
     * @param localname local name of root element (e.g "To"
     * @param namespace namespace, e.g. {@link AddressingConstants#XMLNS_WSA_2005}
     * @param prefix prefix, e.g. wsa2005
     * @return an address containing all the parts of the address as children
     */
    public ElementEx toXom(String localname,String namespace, String prefix) {
        String prefixColon = prefix + ":";
        ElementEx root=new ElementEx(prefixColon+localname,namespace);
        if(address!=null) {
            Element to = new ElementEx(prefixColon + WSA_ADDRESS, namespace,getAddress());
            root.appendChild(to);
        }
        if(referenceParameters!=null) {
            ElementEx elt=new ElementEx(prefixColon + WSA_REFERENCE_PARAMETERS, namespace);
            elt.copyChildrenFrom(referenceParameters);
            root.appendChild(elt);
        }
        if (metadata != null) {
            ElementEx elt = new ElementEx(prefixColon + WSA_METADATA, namespace);
            elt.copyChildrenFrom(metadata);
        }
        return root;
    }


    /**
     * Clone by creating a new instance. this is a deep copy, all the way down.
     * @return a full deep copy of the xml
     */
    public AlpineEPR clone() {
        return new AlpineEPR(this);
    }

    /**
     * Read in the EPR from an element, cloning bits.
     * the namespace defines the namespace to look for. If null, use the xmlns of the element passed in.
     * Other elements in the same namespace are ignored; there is no post-read validation.
     *
     * @param element element to start at
     * @param namespace namespace to use
     * @throws InvalidXmlException if there was no namespace.
     *
     */
    public void read(Element element,String namespace) {
        if(namespace==null) {
            namespace=element.getNamespaceURI();
            if(namespace==null) {
                throw new InvalidXmlException("No namespace on "+element);
            }
        }
        for(Node n: new NodeIterator(element)) {
            if(n instanceof Element && namespace.equals(((Element)n).getNamespaceURI())) {
                Element elt=(Element) n;
                String text=elt.getValue();
                String localname=elt.getLocalName();
                if(WSA_ADDRESS.equals(localname)) {
                    address=text;
                } else if(WSA_REFERENCE_PARAMETERS.equals(localname)) {
                    referenceParameters=(Element) elt.copy();
                } else if(WSA_METADATA.equals(localname)) {
                    metadata=(Element) elt.copy();
                }
            }
        }
    }

}
