/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.projects.alpine.om.soap11;

import nu.xom.Element;
import nu.xom.Attribute;
import nu.xom.Elements;
import nu.xom.Node;
import org.smartfrog.projects.alpine.om.base.ElementEx;
import org.smartfrog.projects.alpine.faults.InvalidXmlException;
import org.smartfrog.projects.alpine.faults.FaultConstants;

import javax.xml.namespace.QName;

/**
 
 */
public class Fault extends Soap11Element {
    public static final String ERROR_TOO_MANY_ELEMENTS = "More than one element of type ";

    /**
     * an empty fault is given an empty code and stub details element
     */ 
    public Fault() {
        this(ELEMENT_FAULT,NS_URI_SOAP11);
        setFaultCode("");
        setFaultString("");
        demandCreateFaultDetail();
    }    
    
    public Fault(String name) {
        super(name);
    }

    public Fault(String name, String uri) {
        super(name, uri);
    }

    public Fault(Element element) {
        super(element);
    }
    
    /**
     * duplicate ourselves
     *
     * @return a copy of ourselves
     */
    protected Element shallowCopy() {
        return new Fault(getQualifiedName(), getNamespaceURI());
    }    
    
    public String getFaultCode() {
        Element e = getFaultCodeElement();
        return extractValue(e);
    }

    protected Element getFaultCodeElement() {
        Element e=getFirstChildElement(FAULT_CODE);
        return e;
    }

    public String getFaultActor() {
        Element e = getFaultActorElement();
        return extractValue(e);
    }

    protected Element getFaultActorElement() {
        Element e = getFirstChildElement(FAULT_ACTOR);
        return e;
    }

    public void setFaultCode(String value) {
        Element newFaultCode=new Element(FAULT_CODE);
        newFaultCode.appendChild(value);
        replace(newFaultCode);
    }

    public String getFaultString() {
        Element e = getFaultStringElement();
        return extractValue(e);
    }

    /**
     * Get the value of an element if it is not null
     * @param element
     * @return the element value or null for none.
     */ 
    protected String extractValue(Element element) {
        if (element == null) {
            return null;
        }
        return element.getValue();
    }

    protected Element getFaultStringElement() {
        Element e = getFirstChildElement(FAULT_STRING);
        return e;
    }
    
    public void setFaultString(String value) {
        Element elt = new Element(FAULT_STRING);
        elt.appendChild(value);
        replace(elt);
    }
    
    public void setFaultActor(String value) {
        Element actor = new Element(FAULT_ACTOR);
        actor.appendChild(value);
        replace(actor);
    }

    /**
     * replace the first child of the same qname with this updated version
     * @param replacement
     */ 
    protected void replace(Element replacement) {
        Element old=getFirstChildElement(replacement.getLocalName(),replacement.getNamespaceURI());
        if(old!=null) {
            removeChild(old);
        }
        appendChild(replacement);
    }

    /**
     * get the fault detail, creating it if needed.
     * @return the fault detail.
     */ 
    protected ElementEx demandCreateFaultDetail() {
        ElementEx faultDetail = getFaultDetail();
        if(faultDetail==null) {
            faultDetail=new ElementEx(FAULT_DETAIL);
            appendChild(faultDetail);
        }
        return faultDetail;
    }
    
    /**
     * Get our current fault detail
     * @return
     */ 
    public ElementEx getFaultDetail() {
        return (ElementEx)getFirstChildElement(FAULT_DETAIL);
    }

    /**
     * replace any existing fault detail with a new one
     * @param detail
     */ 
    public void setFaultDetail(ElementEx detail) {
        if(detail==null) {
            replace(new ElementEx(FAULT_DETAIL));
        } else {
            replace(detail);
        }
    }

    /**
     * Append information to the fault detail element. 
     * Everything that is there is left alone.
     * @param node
     */ 
    public void appendToFaultDetail(Node node) {
        demandCreateFaultDetail().appendChild(node);
    }
    
    /**
     * Verify that the #of child elements of that name is <= 0
     *
     * @throws InvalidXmlException if needed
     */
    private void validateChildCount(String elementName) {
        Elements elements=getChildElements(elementName);
        if(elements!=null) {
            if(elements.size()>1) {
                throw new InvalidXmlException(this,ERROR_TOO_MANY_ELEMENTS+elementName);
            }
        }
    }
    
    /**
     * Validate the Xml. Throw {@link InvalidXmlException} if invalid.
     * @throws InvalidXmlException if needed
     */
    public void validateXml() {
        super.validateXml();
        validateChildCount(FAULT_CODE);
        validateChildCount(FAULT_ACTOR);
        validateChildCount(FAULT_DETAIL);
    }

    /**
     * Using the apache fault information, add stuff to the fault
     * @param thrown the exception (must not be null) to work with
     */ 
    public void addException(Throwable thrown) {
        setFaultString(thrown.getMessage());
        QName qname = FaultConstants.QNAME_FAULTDETAIL_EXCEPTIONNAME;
        String text = thrown.getClass().getName();
        addFaultDetail(qname, text);
        StackTraceElement[] stack=thrown.getStackTrace();
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<stack.length;i++) {
            StackTraceElement frame;
            frame=stack[i];
            buffer.append("  at ");
            buffer.append(frame.getClassName());
            buffer.append(".");
            buffer.append(frame.getMethodName());
            buffer.append("(");
            buffer.append(frame.getFileName());
            buffer.append(":");
            buffer.append(frame.getLineNumber());
            buffer.append(")");
        }
        addFaultDetail(FaultConstants.QNAME_FAULTDETAIL_STACKTRACE, buffer.toString());
    }

    /**
     * Create an element of the given qname and add fault details to it
     * @param qname element
     * @param text message
     */ 
    public void addFaultDetail(QName qname, String text) {
        ElementEx element = new ElementEx(qname);
        element.appendChild(text);
        appendToFaultDetail(element);
    }
    
    public void addFaultDetail(QName qname, Node node) {
        ElementEx element = new ElementEx(qname);
        element.appendChild(node);
        appendToFaultDetail(element);
    }
    
    /**
     * look up fault detail by qname
     * @param qname
     * @return
     */ 
    public Element getFirstFaultDetailChild(QName qname) {
        return demandCreateFaultDetail().getFirstChildElement(qname);
    }
}
