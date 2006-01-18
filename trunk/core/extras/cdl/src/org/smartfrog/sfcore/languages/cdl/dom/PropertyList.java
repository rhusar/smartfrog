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
import nu.xom.Elements;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.services.xml.java5.NamespaceUtils;
import org.smartfrog.services.xml.utils.XsdUtils;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.references.ReferencePath;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlInvalidValueReferenceException;
import org.smartfrog.sfcore.languages.cdl.generate.GenerateContext;
import org.smartfrog.sfcore.languages.cdl.resolving.ResolveEnum;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

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
     */
    private boolean template = false;

    /**
     * Name of the template that we extend. Null if we do not extend anything
     */
    protected QName extendsName;


    /**
     * What is the state of resolution
     */
    private ResolveEnum resolveState = ResolveEnum.ResolvedUnknown;

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * a reference path; will be null if not supplied; set at bind time.
     */
    private ReferencePath referencePath;

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

    public ResolveEnum getResolveState() {
        return resolveState;
    }

    public void setResolveState(ResolveEnum resolveState) {
        this.resolveState = resolveState;
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
            String prefix = NamespaceUtils.extractNamespacePrefix(rawextension);
            String local = NamespaceUtils.extractLocalname(rawextension);
            String namespace = null;
            if (prefix != null) {
                namespace = getNamespaceURI(prefix);
                if (namespace == null) {
                    throw new CdlXmlParsingException(
                            ErrorMessages.ERROR_UNKNOWN_NAMESPACE + prefix);
                }
            }
            setExtendsName(NamespaceUtils.makeQName(namespace, local, prefix));
        }

        //check for having a refroot and us not already being bound
        if(getRefValue()!=null && referencePath==null) {
            //and create a reference path if so
            //this will make a relative one absolute in the process, incidentally.
            referencePath=new ReferencePath(this);
        }
    }


    /**
     * Copy the element and local state.
     *
     * Subclassers must subclass override the {@link #newList(String, String)} operation
     * to return their subclass, and then hand off to this superclass the act of creation
     * and initialisation. They may also want to override {@link #propagateFieldValuesToShallowCopy(PropertyList)}
     * to add extra attribute copying.
     * @return a shallow copy of the original.
     */
    protected Element shallowCopy() {
        PropertyList copy = newList(getQualifiedName(), getNamespaceURI());
        propagateFieldValuesToShallowCopy(copy);
        return copy;
    }

    /**
     * This is here for subclasses to play with.
     * @param copy
     */
    protected void propagateFieldValuesToShallowCopy(PropertyList copy) {
        copy.setResolveState(getResolveState());
        copy.setExtendsName(getExtendsName());
        copy.setTemplate(isTemplate());
        if (referencePath != null) {
            copy.referencePath = referencePath.shallowCopy();
        }
    }


    /**
     * this is an override point, part of a shallowCopy.
     *
     * @return a new PropertyList or a subclass, with
     */
    protected PropertyList newList(String name, String namespace) {
        return new PropertyList(name, namespace);
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
        buffer.append("<");
        buffer.append(getQName());
        if (extendsName != null) {
            buffer.append(" cdl:extends ");
            buffer.append(extendsName);
        }
        String refValue = getRefValue();
        if(refValue!=null) {
            buffer.append(" cdl:ref=\"");
            buffer.append(refValue);
            buffer.append('"');
        }
        String refRootValue = getRefRootValue();
        if (refRootValue != null) {
            buffer.append(" cdl:refroot=\"");
            buffer.append(refRootValue);
            buffer.append('"');
        }
        if(isLazy()) {
            buffer.append(" cdl:lazy=\"true\"");
        }
        buffer.append(">");
/*
        if (getOwner() != null) {
            buffer.append(" from ");
            buffer.append(getOwner());
        }
*/
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
        out.enter(name, getBaseComponent(out));
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
     *
     * @param out
     * @return
     */
    protected String getBaseComponent(GenerateContext out) {
        String parent = out.getDefaultBaseComponent();
        ElementEx commandPath = (ElementEx) getFirstChildElement(
                CddlmConstants.CMP_ELEMENT_COMMAND_PATH, Constants.CMP_NAMESPACE);
        if (commandPath != null) {
            //we have a new extensor.
            String command = commandPath.getTextValue().trim();
            if (command.startsWith("@")) {
                parent = command.substring(1);
            }
        }
        return parent;
    }

    /**
     * Run through the list and update the entire aggregate resolution state
     * This will set all children to their appropriate values, using
     * the priority logic of {@link ResolveEnum#merge(ResolveEnum)}
     *
     * @return the new state of the tree.
     */
    public ResolveEnum aggregateResolutionState() {
        ResolveEnum state = getResolveState();
        for (Node n : nodes()) {
            if (n instanceof PropertyList) {
                PropertyList child = (PropertyList) n;
                ResolveEnum childState = child.aggregateResolutionState();
                state = state.merge(childState);
            }
        }
        setResolveState(state);
        return state;
    }

    /**
     * get the refroot value
     *
     * @return refroot or null
     */
    public String getRefRootValue() {
        return getAttributeValue(ATTR_REFROOT, CDL_NAMESPACE);
    }


    /**
     * Get the reference
     *
     * @return the value of any reference. null means 'no reference'
     */
    public String getRefValue() {
        return getAttributeValue(ATTR_REF, CDL_NAMESPACE);
    }

    /**
     * Mark a reference as being resolved.
     * The reference path is removed at the same time.
     */
    protected void markReferenceResolved() {
        removeAttribute(ATTR_REF, CDL_NAMESPACE);
        referencePath=null;
    }

    /**
     * Get the reference path of a node
     * @return the reference path or null
     */
    public ReferencePath getReferencePath() {
        return referencePath;
    }

    /**
     * remove an attribute
     * @param localname local attr name
     * @param namespace
     */
    private boolean removeAttribute(String localname, String namespace) {
        Attribute attribute = getAttribute(localname, namespace);
        if(attribute==null) {
            return false;
        }
        removeAttribute(attribute);
        return true;
    }

    /**
     * Test for a node being a value references
     * @return true if this is a valid value reference, fault if not (though its
     * children may be)
     * @throws CdlInvalidValueReferenceException if the node is inconsistent
     */
    public boolean isValueReference() throws CdlInvalidValueReferenceException {
        String ref= getRefValue();
        if(ref==null) {
            return false;
        }
        Elements childElements = getChildElements();
        if(childElements.size()>0) {
            throw new CdlInvalidValueReferenceException(
                    CdlInvalidValueReferenceException.ERROR_CHILD_ELEMENTS_IN_REFERENCE
            +getDescription());
        }
        return true;
    }

    /**
     * Test for a node having a lazy attribute
     * @return returns true if cdl:lazy is present; the value is unimportant
     */
    public boolean isLazy() {
        String lazyValue = getAttributeValue(ATTR_LAZY, CDL_NAMESPACE);
        boolean lazy = lazyValue!=null && XsdUtils.isXsdBooleanTrue(lazyValue);
        return lazy;
    }

    /**
     * Set the lazy flag/attribute.
     * When a list is lazy it has the attribute cdl:lazy set; when it is not, it is removed
     * @param lazy
     */
    public void setLazy(boolean lazy) {
        removeAttribute(ATTR_LAZY, CDL_NAMESPACE);
        if (lazy) {
            addAttribute(new Attribute("cdl:"+ATTR_LAZY, CDL_NAMESPACE, XsdUtils.TRUE));
        }
    }

    /**
     * test for being a toplevel list.
     * The relevant subclass overrides it to return true
     * @return true iff this is a ToplevelList.
     */
    public boolean isToplevel() {
        return false;
    }

    /**
     * Get a factory for this particular instance/subclass of PropertyList.
     * @return a factory that can be used to create new objects of this type.
     */
    public PropertyListFactory getFactory() {
        return new PropertyListFactory();
    }

    /**
     * What is our local resolution state
     * @return our current state. {@link ResolveEnum#ResolvedIncomplete}
     * or {@link ResolveEnum#ResolvedIncomplete} for references, {@link ResolveEnum#ResolvedComplete}
     * for nothing left to do. And {@link ResolveEnum#ResolvedUnknown} if there are child elements,
     * so our state is effectively unknown.
     * @throws CdlInvalidValueReferenceException
     */
    public ResolveEnum inferLocalResolutionState() throws CdlInvalidValueReferenceException {
        ResolveEnum state;
        if (isValueReference()) {
            //its a link
            state = ResolveEnum.ResolvedIncomplete;
            if (isLazy()) {
                //its a lazy link
                state = ResolveEnum.ResolvedLazyLinksRemaining;
            }
        } else {
            //start with complete state
            state = ResolveEnum.ResolvedComplete;
            //then look for property nodes
            for (Node node : this) {
                if (node instanceof PropertyList) {
                    //if there is one or more property list child
                    //then state is unknown
                    state = ResolveEnum.ResolvedUnknown;
                    break;
                }
            }
        }
        return state;
    }

}
