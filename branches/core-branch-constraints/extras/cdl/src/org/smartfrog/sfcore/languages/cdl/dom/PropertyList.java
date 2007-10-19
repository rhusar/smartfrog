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
import org.smartfrog.services.cddlm.cdl.cmp.CmpCompoundImpl;
import org.smartfrog.services.xml.java5.NamespaceUtils;
import org.smartfrog.services.xml.utils.XsdUtils;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlInvalidValueReferenceException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.generate.DescriptorSource;
import org.smartfrog.sfcore.languages.cdl.generate.TypeMapper;
import org.smartfrog.sfcore.languages.cdl.references.ReferencePath;
import org.smartfrog.sfcore.languages.cdl.references.ReferenceResolutionContext;
import org.smartfrog.sfcore.languages.cdl.references.StepExecutionResult;
import org.smartfrog.sfcore.languages.cdl.resolving.ResolveEnum;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.languages.cdl.utils.Namespaces;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.reference.Reference;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;

/**
 * This represents a template in the CDL
 * created 21-Apr-2005 14:26:55
 */

public class PropertyList extends DocNode implements DescriptorSource {

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
     * boolean to say whether or not we are a root node. A root node is where references stop.
     */

    protected boolean root;

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


    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
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

    }

    /**
     * This is called to pull reference info out of the system
     */
    public void extractReferenceInformation() {
        //check for having a refroot and us not already being bound
        if (getRefValue() != null) {
            if (referencePath == null) {
                //and create a reference path if so
                //this will make a relative one absolute in the process, incidentally.
                referencePath = new ReferencePath(this);
            }

            if (getChildCount() > 0) {
                boolean childElements = getChildElements().size() > 0;
                if (Constants.POLICY_NESTED_NODES_FORBIDDEN_IN_REFERENCES || childElements) {
                    throw new CdlInvalidValueReferenceException(
                            CdlInvalidValueReferenceException.ERROR_CHILDREN_IN_REFERENCE
                                    + getDescription());
                }
            }

        }
    }


    /**
     * Copy the element and local state.
     * <p/>
     * Subclassers must subclass override the {@link #newList(String, String)} operation
     * to return their subclass, and then hand off to this superclass the act of creation
     * and initialisation. They may also want to override {@link #propagateFieldValuesToShallowCopy(PropertyList)}
     * to add extra attribute copying.
     *
     * @return a shallow copy of the original.
     */
    protected Element shallowCopy() {
        PropertyList copy = newList(getQualifiedName(), getNamespaceURI());
        propagateFieldValuesToShallowCopy(copy);
        return copy;
    }

    /**
     * This is here for subclasses to play with.
     *
     * @param copy
     */
    protected void propagateFieldValuesToShallowCopy(PropertyList copy) {
        copy.setResolveState(getResolveState());
        copy.setExtendsName(getExtendsName());
        copy.setTemplate(isTemplate());
        copy.setRoot(isRoot());
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
        if (refValue != null) {
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
        if (isLazy()) {
            buffer.append(" cdl:lazy=\"true\"");
        }
        buffer.append(">");
        return buffer.toString();
    }

    /**
     * Describe a node by something like its XPath value. Good for diagnostics.
     * @return
     */
    public String getXPathDescription() {
        StringBuffer buffer=new StringBuffer();
        if(getParent()!=null) {
            if(getParent() instanceof PropertyList) {
                PropertyList parent = (PropertyList) getParent();
                buffer.append(parent.getXPathDescription());
            } else {

            }
            buffer.append('/');
        }
        String prefix=getQName().getPrefix();
        if(prefix!=null) {
            buffer.append(prefix);
            buffer.append(';');
            buffer.append(getQName().getLocalPart());
        } else {
            buffer.append(getQName().toString());
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
//        assert extension.getQName().equals(extendsName);
        //now apply the rules of the CDL spec, section 7.2.2
        inheritAttributes(extension, Constants.POLICY_STRIP_ALL_CDL_ATTRIBUTES_FROM_MERGED_CHILDREN);
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
     * @param excludeCDL
     */
    public void inheritAttributes(PropertyList extension, boolean excludeCDL) {
        //this is where we start to work at the XOM level.
        for (Attribute extAttr : extension.attributes()) {
            String namespace = extAttr.getNamespaceURI();
            String local = extAttr.getLocalName();
            boolean shouldExclude = excludeCDL && CDL_NAMESPACE.equals(namespace);
            if (!hasAttribute(namespace, local) && !shouldExclude) {
                //no match; no exclude: copy the attribute
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
        for (Node node : this) {
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
     * logic to extract a command or a java classname from
     * the command. Crude and ugly.
     * 1. anything that begins with an @ is a reference
     * 2. anything that begins with a normal char is not.
     * TODO use the arguments to split it properly, add inclusion
     *
     * @param out
     * @return
     */
/*    protected String getBaseComponent(GenerateContext out) {
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
    }*/


    /**
     * Add a new description
     *
     * @param parent node: add attribute or children
     * @throws java.rmi.RemoteException
     */
    public void exportDescription(CdlComponentDescription parent) throws RemoteException, SmartFrogException {

        QName name = this.getQName();
        String text = getTextValue();
        if (isValueReference() && isLazy()) {
            //export a lazy reference
            try {
                Reference reference=getReferencePath().generateReference();
                parent.replace(name, reference);
            } catch (CdlResolutionException e) {
                throw SmartFrogException.forward(e);
            }
        } else {
            TypeMapper typeMapper = getParseContext().getTypeMapper();

            if(typeMapper.isEmptyOptionalNode(this)) {
                //skip out if we are nil
                return;
            }

            Object contents=typeMapper.map(this);
            if(contents!=null) {
                //specially mapped things
                parent.replace(name, contents);
            } else {
                //normal composite type
                CdlComponentDescriptionImpl description = new CdlComponentDescriptionImpl(name, parent);
                description.registerWithParent();
                int exported=exportChildren(description);
                //namespaces
                final Namespaces namespaces = getNamespaces();
                namespaces.exportDescription(description);
                //finally, if there is no child element in the description with the classname, we
                //register our classname as the default
                if (description.sfResolve(SmartFrogCoreKeys.SF_CLASS, false) == null) {
                    addDefaultSFClass(description);
                }

                //sanity check: force validate here and now
                assert null != description.sfResolve(SmartFrogCoreKeys.SF_CLASS, true);
                //only do the text if there were no children
                if(text!=null &&
                        (Constants.POLICY_ALWAYS_EXPORT_TEXT_NODES || exported>0)) {
                    description.sfReplaceAttribute(CmpCompoundImpl.ATTR_TEXT,text);
                }
            }
        }
    }

    /**
     * Override point: add the sf class to a component.
     *
     * @param description
     * @throws SmartFrogRuntimeException
     */
    protected void addDefaultSFClass(CdlComponentDescriptionImpl description) throws SmartFrogRuntimeException {
        description.sfReplaceAttribute(SmartFrogCoreKeys.SF_CLASS, Constants.CDL_COMPONENT_CLASSNAME);
    }

    /**
     * export all our children
     *
     * @param parent
     * @return
     * @throws RemoteException
     */
    private int exportChildren(CdlComponentDescription parent)
            throws RemoteException, SmartFrogException {
        int exported=0;
        for (Node node : this) {
            if (node instanceof DescriptorSource) {
                exported++;
                DescriptorSource source = (DescriptorSource) node;
                source.exportDescription(parent);
            }
        }
        return exported;
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
        for (Node n : this) {
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
        referencePath = null;
        removeOptionalCdlAttribute(ATTR_REF);
        removeOptionalCdlAttribute(ATTR_REFROOT);
        removeOptionalCdlAttribute(ATTR_LAZY);
    }


    /**
     * remove a CDL attr iff it is present
     */
    public void removeOptionalCdlAttribute(String name) {
        Attribute attribute = getAttribute(name,CDL_NAMESPACE);
        if(attribute!=null) {
            removeAttribute(attribute);
        }
    }

    /**
     * Get the reference path of a node
     *
     * @return the reference path or null
     */
    public ReferencePath getReferencePath() {
        return referencePath;
    }

    /**
     * remove an attribute
     *
     * @param localname local attr name
     * @param namespace
     */
    private boolean removeAttribute(String localname, String namespace) {
        Attribute attribute = getAttribute(localname, namespace);
        if (attribute == null) {
            return false;
        }
        removeAttribute(attribute);
        return true;
    }

    /**
     * Test for a node being a value references
     *
     * @return true if this is a valid value reference, fault if not (though its
     *         children may be)
     * @throws CdlInvalidValueReferenceException
     *          if the node is inconsistent
     */
    public boolean isValueReference() throws CdlInvalidValueReferenceException {
        String ref = getRefValue();
        if (ref == null) {
            return false;
        }
        return true;
    }

    /**
     * Test for a node having a lazy attribute
     *
     * @return returns true if cdl:lazy is present; the value is unimportant
     */
    public boolean isLazy() {
        String lazyValue = getAttributeValue(ATTR_LAZY, CDL_NAMESPACE);
        boolean lazy = lazyValue != null && XsdUtils.isXsdBooleanTrue(lazyValue);
        return lazy;
    }

    /**
     * Test for being a lazy reference
     *
     * @return true iff we are a reference with cdl:lazy=true
     */
    public boolean isLazyReference() {
        return isValueReference() && isLazy();
    }

    /**
     * Set the lazy flag/attribute.
     * When a list is lazy it has the attribute cdl:lazy set; when it is not, it is removed
     *
     * @param lazy
     */
    public void setLazy(boolean lazy) {
        removeAttribute(ATTR_LAZY, CDL_NAMESPACE);
        if (lazy) {
            addAttribute(new Attribute("cdl:" + ATTR_LAZY, CDL_NAMESPACE, XsdUtils.TRUE));
        }
    }

    /**
     * test for being a toplevel list.
     * The relevant subclass overrides it to return true
     *
     * @return true iff this is a ToplevelList.
     */
    public boolean isToplevel() {
        return false;
    }

    /**
     * Get a factory for this particular instance/subclass of PropertyList.
     *
     * @return a factory that can be used to create new objects of this type.
     */
    public PropertyListFactory getFactory() {
        return new PropertyListFactory();
    }

    /**
     * What is our local resolution state
     *
     * @return our current state. {@link ResolveEnum#ResolvedIncomplete}
     *         or {@link ResolveEnum#ResolvedIncomplete} for references, {@link ResolveEnum#ResolvedComplete}
     *         for nothing left to do. And {@link ResolveEnum#ResolvedUnknown} if there are child elements,
     *         so our state is effectively unknown.
     * @throws CdlInvalidValueReferenceException
     *
     */
    public ResolveEnum inferLocalResolutionState() throws CdlInvalidValueReferenceException {
        if (isLazy()) {
            //its a lazy link, bail out
            return ResolveEnum.ResolvedLazyLinksRemaining;
        }
        if (isValueReference()) {
            //its a (non-lazy) link
            return ResolveEnum.ResolvedIncomplete;
        } else {
            ResolveEnum state;
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
            return state;
        }
    }

    /**
     * Remove all attributes from a node
     */
    public void removeAllAttributes() {
        for(Attribute attr:attributes()) {
            removeAttribute(attr);
        }
    }

    /**
     * Replace a target reference with a clone of the destination node.
     *
     * @param dest   destination reference
     * @return a property list that represents the resolved node. It may be the same as this,
     * or it will be something new
     */
    public PropertyList replaceByReference(PropertyList dest) {
        assert dest != null;
        if (this == dest) {
            return this;
        }
        //create a clone of the initial property list
        //using a factory specific to the type of the current target, to
        //ensure that toplevel lists get handled
        PropertyList replacement = getFactory().create(dest);
        if (Constants.POLICY_STRIP_ATTRIBUTES_FROM_REFERENCE_DESTINATION) {
            replacement.removeAllAttributes();
        }
        //now copy attributes from the target.
        boolean lazy = isLazy();
        for (Attribute attr : attributes()) {
            if (Constants.XMLNS_CDL.equals(attr.getNamespaceURI())) {
                String name = attr.getLocalName();
                if (!lazy && (Names.ATTR_REFROOT.equals(name)
                        || Names.ATTR_REF.equals(name))) {
                    continue;
                }
            }
            Attribute cloned = new Attribute(attr);
            replacement.addAttribute(cloned);
        }
        replacement.setLocalName(getLocalName());
        replacement.setNamespaceURI(getNamespaceURI());
        replacement.setNamespacePrefix(getNamespacePrefix());
        return replacement;
    }

    /**
     * Resolve a node
     *
     * @param context current resolution context (essentially a history)
     * @return this node or whatever replaced it
     * @throws CdlException if resolution failed
     */
    public PropertyList resolveNode(ReferenceResolutionContext context) throws CdlException {
        if(referencePath==null) {
            //nothing to resolve; we are here
            return this;
        }
        if(isLazyReference()) {
            //todo: lazy stuff
            throw new CdlException("Not implemented: lazy reference on "
                    +this.getXPathDescription());
        } else {
            StepExecutionResult result = referencePath.execute(this,context);
            assert result.isFinished();
            PropertyList dest = result.getNode();
            if (result.isLazyFlagFound() && dest.isValueReference()) {
                //lazy was hit. we need to mark ourselves as lazy and continue without resolving
                throw new CdlException("Not implemented: lazy reference on "
                        + this.getXPathDescription());
//                setLazy(true);
//                return this;

            }
            //patch up everything
            //do the copy (will be a no-op for dest==this)
            PropertyList replacement = replaceByReference(dest);
            //then patch our parent (will be a no-op for dest==this)
            getParent().replaceChild(this,replacement);
            resolveState=replacement.inferLocalResolutionState();
            return replacement;
        }

    }

    /**
     * resolve a node
     * @return this node or whatever replaced it
     * @throws CdlException if resolution failed
     */
    public PropertyList resolveNode() throws CdlException {
        ReferenceResolutionContext context = new ReferenceResolutionContext();
        return resolveNode(context);
    }
}
