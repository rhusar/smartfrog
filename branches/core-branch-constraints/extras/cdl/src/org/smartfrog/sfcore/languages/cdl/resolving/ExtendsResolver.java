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
package org.smartfrog.sfcore.languages.cdl.resolving;

import nu.xom.Node;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.dom.SystemElement;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlInternalErrorException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implement "extends" semantics. This could be implemented in the property list
 * itself, but is kept separate to let us play with alternate
 * algorithms/implementations created 10-Jun-2005 16:28:47
 */

public class ExtendsResolver {

    private ExtendsContext stack = new ExtendsContext();

    /**
     * a log
     */
    private Log log = ClassLogger.getLog(this);

    /**
     * parsing context
     */
    private ParseContext parseContext;
    public static final String ERROR_UNKNOWN_TEMPLATE = "Unknown Template :";

    /**
     * Extends support has a parse context
     *
     * @param context
     */
    public ExtendsResolver(ParseContext context) {
        assert context != null;
        this.parseContext = context;
    }


    /**
     * Resolve the extends for an entire document
     *
     * @param document
     * @throws CdlResolutionException
     */
    public void  resolveExtends(CdlDocument document)
            throws CdlException {
        ToplevelList config = document.getConfiguration();
        if (Constants.POLICY_ALWAYS_EXTEND_CONFIGURATION && config != null) {
            ToplevelList newConfig;
            newConfig = resolveToplevel(config);
            document.replaceConfiguration(newConfig);
        }

        SystemElement system = document.getSystem();
        if (system != null) {
            SystemElement newSystem;
            newSystem = resolveSystem(system);
            document.replaceSystem(newSystem);
        }

    }

    /**
     * This does two things.
     * It resolves the node and then checks that extension is complete; if not it bails out
     * drastically
     * @param target toplevel list to process
     * @return the resolved list
     * @throws CdlException
     */
    private ToplevelList resolveToplevel(ToplevelList target) throws CdlException {
        ToplevelList newList;
        newList = (ToplevelList) resolveChildExtends(target);
        verifyCompletelyExtended(newList);
        return newList;
    }

    private void verifyCompletelyExtended(PropertyList newSystem) throws CdlInternalErrorException {
        ResolveEnum state = newSystem.aggregateResolutionState();
        if (!state.isParseTimeResolutionComplete()) {
            throw new CdlInternalErrorException("Incomplete parse time resolution");
        }
    }

    private SystemElement resolveSystem(SystemElement system) throws CdlException {
        SystemElement newList;
        newList = (SystemElement) resolveChildExtends(system);
        verifyCompletelyExtended(newList);
        return newList;
    }
    /**
     * Resolve the extends for a single node. The algorithm for resolution is
     * defined in the CDL document specification.
     *
     * @param targetName name of the target
     * @return
     * @throws CdlResolutionException
     */
    public PropertyList resolveExtendsTarget(QName targetName)
            throws CdlException {
        PropertyList target = lookup(targetName);
        if (target == null) {
            throw new CdlResolutionException(ERROR_UNKNOWN_TEMPLATE +
                    targetName);
        }
        //resolve the reference, the thing we are extending
        PropertyList resolveResult = resolveExtends(target);
        //now patch the context so that it is updated.
        parseContext.prototypeUpdate(resolveResult);
        return resolveResult;
    }

    /**
     * Resolve the extends for a single node. The algorithm for resolution is
     * defined in the CDL document specification.
     *
     * @param target
     * @return
     * @throws CdlResolutionException
     */
    public PropertyList resolveExtends(PropertyList target)
            throws CdlException {
        boolean toplevel = target.isTemplate();
        if (toplevel) {
            return resolveToplevelTemplate(target);
        } else {
            return innerResolve(target);
        }
    }

    /**
     * resolve a toplevel template by pushing the name onto the stack,
     * popping it when it is exited
     *
     * @param target
     * @return
     * @throws CdlException
     */
    private PropertyList resolveToplevelTemplate(PropertyList target) throws CdlException {
        QName name = target.getQName();
        assert name != null;
        stack.enter(name);
        try {
            final PropertyList resolved = innerResolve(target);
            boolean isProto=parseContext.hasPrototypeNamed(target.getQName());
            if(isProto) {
                parseContext.prototypeUpdate(resolved);
            }
            return resolved;
        } finally {
            stack.exit(name);
        }
    }

    /**
     * (recursively) resolve the extends attributes of all our child nodes but
     * not this node itself.
     *
     * @param target node to work on.
     */
    public PropertyList resolveChildExtends(PropertyList target)
            throws CdlException {
        //list of elements (this retains the overall order of things)
        List<Node> newChildren;
        newChildren = copyAndResolve(target, null);

        return replaceNode(target, newChildren);
    }


    /**
     * Inner resolve is for child elements; we do not save our name on the stack
     * as we do not need to.
     *
     * @param target
     * @return the resolved target, which may be different
     * @throws CdlResolutionException
     */
    private PropertyList innerResolve(PropertyList target)
            throws CdlException {
        ResolveResult result;
        PropertyList output = target;
        ResolveEnum state = ResolveEnum.ResolvedIncomplete;
        ResolveEnum resolveState = target.getResolveState();
        if (resolveState.isParseTimeResolutionComplete()) {
            //we are in a state where no parse time resolution is required.
            return target;
        }
        //do the work
        QName extending = target.getExtendsName();
        if (extending == null) {
            //we are not extending anything...but our children might be.
            output = resolveChildExtends(target);
            state = ResolveEnum.ResolvedComplete;
        } else {
            //something to resolve.
            PropertyList extended;
            log.debug("Resolving " +
                    target.getQName() +
                    " extends " +
                    extending);
            extended = resolveExtendsTarget(extending);
            if (extended.getResolveState() == ResolveEnum.ResolvedIncomplete) {
                //if there is something that is unfinished at this level,
                //leave off it for now. though this state should be
                //impossible to reach here.
                log.debug("extended state=" + extended.getResolveState());
                //propagate it
                output=extended;
            } else {
                //we have now resolved our parent.
                //get on with it
                PropertyList resolvedPropertyList = extended;
                //copy attributes
                target.mergeAttributes(resolvedPropertyList);
                //now do the element inheritance
                output = inheritChildren(target, resolvedPropertyList);
            }
            state = extended.getResolveState();
        }
        output.setResolveState(state);
        result = new ResolveResult(output);
        return output;
    }

    /**
     * merge in all children.
     *
     * @param extension
     */
    private PropertyList inheritChildren(PropertyList target,
                                         PropertyList extension)
            throws CdlException {
        //max size of our list is the sum of all children
        int maxsize = target.getChildCount() +
                extension.getChildCount();
        //list of elements (this retains the overall order of things)
        List<Node> newChildren = new ArrayList<Node>(maxsize);
        //this is a map that caches mappings of things
        HashMap<QName, QName> entries = new HashMap<QName, QName>(maxsize);
        for (Node node : extension) {
            if (node instanceof PropertyList) {
                //cast it
                PropertyList template = (PropertyList) node;
                QName name = template.getQName();

                //resolve it
                PropertyList resolvedList = resolveExtends(template);

                //now, at this point we have a property list which contains
                //a resolved element. We are going to get that element out because
                //it is what we want.

                //now, look for a match locally
                PropertyList matchedList = target. getChildTemplateMatching(
                        resolvedList);
                PropertyList copiedList;
                if (matchedList == null) {
                    //insert a copy of the resolved element.
                    //the copy is needed in case it gets manipulated later
                    copiedList = (PropertyList) resolvedList.copy();
                } else {
                    //complex merge.
                    //extend the document
                    PropertyList result = resolveExtends(matchedList);
                    assert result.getResolveState().isParseTimeResolutionComplete();
                    //clone it
                    copiedList = (PropertyList) result.copy();
                    //pull in the attributes of the child
                    copiedList.inheritAttributes(resolvedList,
                            Constants.POLICY_STRIP_ALL_CDL_ATTRIBUTES_FROM_MERGED_CHILDREN);
                    //then insert the children of the current list into place
                }
                newChildren.add(copiedList);
                //register in the cache of mapped things.
                entries.put(name, name);
            } else {
                //something other than Element; could be Text
                newChildren.add(node);
            }
        }

        //now we run through the local list, and if there is any
        // element not found in the remote, add it to the end
        // all nodes get added too.
        List<Node> leftovers = copyAndResolve(target, entries);
        newChildren.addAll(leftovers);

        //as this point our element list is updated, and the mappings list contains
        //a mapping for every element that we have used. So, what do we do now.
        //two options:
        // 1: create a new parent element, set up the children and delegate to ourselves
        // 2: patch stuff together by hand.

        return replaceNode(target, newChildren);
    }

    /**
     * This method resolves and copies all elements beneath our target into a
     * list, unless their qname appears in the hashmap of known qnames. Elements
     * are added to the #map as they are propagated If the hashmap is null, the
     * mapping lookup/add is skipped, returning a list of all elements, resolved
     * when appropriate.
     *
     * @param target
     * @param map
     * @return a list of nodes, any extended elements are resolved and
     * @throws CdlException in the event of trouble
     * @cdl:extends stripped.
     */
    private List<Node> copyAndResolve(PropertyList target,
                                      HashMap<QName, QName> map)
            throws CdlException {
        int childCount = target.getChildCount();
        List<Node> newChildren = new ArrayList<Node>(childCount);
        //our goal state.
        ResolveEnum state = ResolveEnum.ResolvedComplete;
        for (Node node : target) {
            if (node instanceof PropertyList) {

                //find the matching property list element
                PropertyList entry = (PropertyList) node;
                QName name = entry.getQName();
                //merge it
                PropertyList resolvedList = resolveExtends(entry);
                assert name.equals(resolvedList.getQName());
                //now, at this point we have a resolved property list.
                //we add this to our children
                boolean add = false;
                if (map == null) {
                    //when not mapping, we add everything
                    add = true;
                } else {
                    //when mapping, we do a lookup
                    if (map.get(name) == null) {
                        //and only add unique things
                        map.put(name, name);
                        add = true;
                    }
                }
                if (add) {
                    //add if told to
                    state = state.merge(resolvedList.getResolveState());
                    newChildren.add(resolvedList);
                }
            } else {
                //anything other than a PropertyList. Just merge it in
                newChildren.add(node);
            }
        }
        target.setResolveState(state);
        return newChildren;
    }

    /**
     * Take a propertly list and a list of elements, and create a new node that
     * can be dropped in in place of the existing one
     *
     * @param target
     * @param newChildren
     * @return a new node with the attributes of target and containing
     *         newChildren as elements
     * @throws CdlXmlParsingException
     */
    private PropertyList replaceNode(PropertyList target,
                                     List<Node> newChildren)
            throws CdlXmlParsingException {
        //copy the target element
        PropertyList replacement = (PropertyList) target.copy();

        //strip its children away (a bit wasteful)
        replacement.removeChildren();
        //add the new ones in order
        for (Node sprog : newChildren) {
            sprog.detach();
            replacement.appendChild(sprog);
        }
        replacement.bind();
        return replacement;
    }

    /**
     * lookup a property in our context
     *
     * @param nodeName
     * @return
     */
    private PropertyList lookup(QName nodeName) {
        return parseContext.prototypeResolve(nodeName);
    }


}
