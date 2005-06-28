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
package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.generate.GenerateContext;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.utils.XmlUtils;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.logging.Log;
import org.ggf.cddlm.generated.api.CddlmConstants;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * This represents a template in the CDL
 * created 21-Apr-2005 14:26:55
 */

public class PropertyList extends DocNode {

    /**
     * this flag is set if we are toplevel, something that
     * can be extended
     * 
     */ 
    protected boolean template = false;

    /**
     * Name of the template that we extend. Null if we do not extend anything
     */
    protected QName extendsName;

    /**
     * And the resolved extension Null if extendsName==null;
     */
    public PropertyList extendsResolved;

    /**
     * a log
     */
    protected Log log = ClassLogger.getLog(this);

    public PropertyList(String name) {
        super(name);
    }

    public PropertyList(String name, String uri) {
        super(name, uri);
    }

    public PropertyList(Element element) {
        super(element);
    }


    public QName getExtendsName() {
        return extendsName;
    }

    public void setExtendsName(QName extendsName) {
        this.extendsName = extendsName;
    }

    /**
     * Parse from XML
     *
     * @throws CdlXmlParsingException
     */
    @Override
    public void bind() throws CdlXmlParsingException {
        //parent
        super.bind();

        //what are we extending?
        Attribute extendsAttr = getExtendsAttribute();
        if (extendsAttr != null) {
            String rawextension = extendsAttr.getValue();
            //now split that into namespace
            String prefix = XmlUtils.extractNamespacePrefix(rawextension);
            String local = XmlUtils.extractLocalname(rawextension);
            String namespace = null;
            if (prefix != null) {
                namespace = getNamespaceURI(prefix);
                if (namespace == null) {
                    throw new CdlXmlParsingException(
                            ErrorMessages.ERROR_UNKNOWN_NAMESPACE + prefix);
                }
            }
            setExtendsName(XmlUtils.makeQName(namespace, local, prefix));
        }
    }

    @Override
    protected Element shallowCopy() {
        return new PropertyList(getQualifiedName(), getNamespaceURI());
    }

    /**
     * flag set to true if we are an addressable toplevel template
     *
     * @return
     */
    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    /**
     * stringify for debugging: shows local and extends name only
     *
     * @return
     */
    public String getDescription() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Prototype : ");
        buffer.append(getQName());
        if (extendsName != null) {
            buffer.append(" extends ");
            buffer.append(extendsName);
        }
        if (getOwner() != null) {
            buffer.append(" from ");
            buffer.append(getOwner());
        }
        return buffer.toString();
    }

    /**
     * Merge with another context, by inherting attributes and stripping
     * extension information
     *
     * @param extension
     */
    public void mergeAttributes(PropertyList extension) {
        //sanity check: we are merging ourselves
        assert extension.getQName().equals(extendsName);
        //now apply the rules of the CDL spec, section 7.2.2
        inheritAttributes(extension);
        //clear our extendsname, as we are now merged. no more extending for us.
        extendsName = null;
        //strip @cdl:extends
        Attribute extendsAttr = getExtendsAttribute();
        if (extendsAttr != null) {
            removeAttribute(extendsAttr);
        }
    }

    /**
     * Get the extends attribute of the element. Prints a warning if there is an
     * attribute called extends that is not in the cdl: namespace.
     *
     * @return
     */
    private Attribute getExtendsAttribute() {
        Attribute extendsAttr = getAttribute(ATTR_EXTENDS, CDL_NAMESPACE);
        if (extendsAttr == null) {
            if (getAttribute(ATTR_EXTENDS) != null) {
                //this is here because I always get this wrong myself, and wanted
                //some extra diagnostics. SteveL.
                log.warn("Template " +
                        getDescription() +
                        " has an extends attribute, but it is " +
                        "not in the CDL namespace. This may be an error.");
            }
        }
        return extendsAttr;
    }

    /**
     * merge in all attributes. public for testing
     *
     * @param extension
     */
    public void inheritAttributes(PropertyList extension) {
        //this is where we start to work at the XOM level.
        for (Attribute extAttr : extension.attributes()) {
            String namespace = extAttr.getNamespaceURI();
            String local = extAttr.getLocalName();
            if (!hasAttribute(namespace, local)) {
                //no match: copy the attribute
                addAttribute((Attribute) extAttr.copy());
            }
        }
    }

    /**
     * Get the first child element whose name matches that of the source
     *
     * @param source
     * @return
     */
    public Element getFirstChildElement(Element source) {
        return getFirstChildElement(source.getNamespaceURI(),
                source.getLocalName());
    }

    /**
     * convert from an element to a property list
     *
     * @param element element to convert
     * @return the propertly list or null for no match
     */
    public PropertyList mapToPropertyList(Element element) {
        if (element instanceof PropertyList) {
            return (PropertyList) element;
        } else {
            return null;
        }
    }

    /**
     * look up the child property list node whose qname matches that of the one
     * passed in
     *
     * @param that property list to look for
     * @return the propertly list or null for no match
     * @see #getChildTemplateMatching(QName)
     */
    public PropertyList getChildTemplateMatching(PropertyList that) {
        QName thatName = that.getQName();
        return getChildTemplateMatching(thatName);
    }


    /**
     * look up the child property list node whose qname matches that of the one
     * passed in
     *
     * @param name qname to search on
     * @return the propertly list or null for no match
     */
    public PropertyList getChildTemplateMatching(QName name) {
        for (Node node : nodes()) {
            if (node instanceof PropertyList) {
                PropertyList child = (PropertyList) node;
                if (child.isNamed(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * look up the child property list node whose (namespaceURI,localname)
     * matches the params
     *
     * @param namespaceURI
     * @param localname
     * @return
     */
    public PropertyList getChildTemplateMatching(String namespaceURI,
            String localname) {
        return getChildTemplateMatching(new QName(namespaceURI, localname));
    }

    /**
     * Write something to a smartfrog file. Parent elements should delegate to
     * their children as appropriate.
     * <p/>
     * The Base class delegates to children and otherwise does nothing
     *
     * @param out
     * @throws java.io.IOException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     */
    @Override
    public void toSmartFrog(GenerateContext out) throws IOException,
            CdlException {
        //printNodeAsSFComment(out);
        String name = getSfName(out);
        out.enter(name,getBaseComponent(out));
        printValueToSF(out);
        printAttributesToSmartFrog(out);
        printChildrenToSmartFrog(out);
        out.leave();
    }

    protected String getSfName(GenerateContext out) {
        return out.convertElementName(this);
    }

    /** 
     * logic to extract a command or a java classname from 
     * the command. Crude and ugly.
     * 1. anything that begins with an @ is a reference
     * 2. anything that begins with a normal char is not.
     * TODO use the arguments to split it properly, add inclusion 
     * @param out
     * @return
     */
    protected String getBaseComponent(GenerateContext out) {
        String parent = out.getDefaultBaseComponent();
        ElementEx commandPath = (ElementEx) getFirstChildElement(Constants.CMP_NAMESPACE,
                        CddlmConstants.CMP_ELEMENT_COMMAND_PATH);
        if (commandPath!=null) {
            //we have a new extensor.
            String command = commandPath.getTextValue().trim();
            if(command.startsWith("@")) {
                parent=command.substring(1);
            }
        }
        return parent;
    }
}
