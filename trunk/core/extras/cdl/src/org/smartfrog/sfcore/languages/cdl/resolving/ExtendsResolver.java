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
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
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

    ExtendsContext stack = new ExtendsContext();

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
     * @return true iff there was a system element needing resolving
     * @throws CdlResolutionException
     */
    public boolean resolveExtends(CdlDocument document)
            throws CdlException {
        ToplevelList system = document.getSystem();
        if (system != null) {
            PropertyList newSystem = resolveChildExtends(system);
            document.setSystem((ToplevelList) newSystem);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Resolve the extends for a single node. The algorithm for resolution is
     * defined in the CDL document specification.
     *
     * @param targetName name of the target
     * @return
     * @throws CdlResolutionException
     */
    public ResolveResult resolveExtends(QName targetName)
            throws CdlException {
        PropertyList target = lookup(targetName);
        if (target == null) {
            throw new CdlResolutionException(ERROR_UNKNOWN_TEMPLATE +
                    targetName);
        }
        //resolve the reference, the thing we are extending
        ResolveResult resolveResult = resolveExtends(target);
        //now patch the context so that it is updated.
        parseContext.prototypeUpdate(resolveResult.getResolvedPropertyList());
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
    public ResolveResult resolveExtends(PropertyList target)
            throws CdlException {
        boolean toplevel = target.isTemplate();
        QName name = target.getQName();
        assert name != null;
        if (toplevel) {
            //only track names on entry and exit when we are toplevel.
            //after the first resolve, this case will always hold, but
            //the initial resolve may not be so extended
            stack.enter(name);
        }
        ResolveResult result;
        try {
            result = innerResolve(target);
        } finally {
            if (toplevel) {
                stack.exit(name);
            }
        }
        return result;
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
     * @return
     * @throws CdlResolutionException
     */
    private ResolveResult innerResolve(PropertyList target)
            throws CdlException {
        ResolveResult result;
        PropertyList output = target;
        ResolveEnum state = ResolveEnum.ResolvedIncomplete;
        //do the work
        QName extending = target.getExtendsName();
        if (extending == null) {
            //we are not extending anything...but our children might be.
            output = resolveChildExtends(target);
            state = ResolveEnum.ResolvedComplete;
        } else {
            //something to resolve.
            ResolveResult extended;
            log.debug("Resolving " +
                    target.getQName() +
                    " extends " +
                    extending);
            extended = resolveExtends(extending);
            if (extended.state == ResolveEnum.ResolvedIncomplete) {
                //if there is something that is unfinished at this level,
                //leave off it for now. though this state should be
                //impossible to reach here.
                log.debug("extended state=" + extended.state);
                //propagate it
            } else {
                //we have now resolved our parent.
                //get on with it
                PropertyList resolvedPropertyList = extended.getResolvedPropertyList();
                //copy attributes
                target.mergeAttributes(resolvedPropertyList);
                //now do the element inheritance
                output = inheritChildren(target, resolvedPropertyList);
            }
            state = propagate(extended.state);
        }
        result = new ResolveResult(state, output);
        return result;
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
        for (Node node : extension.nodes()) {
            if (node instanceof PropertyList) {
                //cast it
                PropertyList template = (PropertyList) node;
                QName name = template.getQName();

                //merge it
                ResolveResult resolved = resolveExtends(template);
                PropertyList resolvedList = resolved.getResolvedPropertyList();

                //now, at this point we have a property list which contains
                //a resolved element. We are going to get that element out because
                //it is what we want.

                //now, look for a match locally
                PropertyList matchedList = target.mapToPropertyList(
                        resolvedList);
                if (matchedList == null) {
                    //insert a copy of the resolved element.
                    //the copy is needed in case it gets manipulated later
                    PropertyList copiedList = (PropertyList) resolvedList.copy();
                    newChildren.add(copiedList);
                    //register in the cache of mapped things.
                    entries.put(name, name);
                } else {
                    //complex merge.
                    //first, pull in the attributes of the child
                    matchedList.inheritAttributes(resolvedList);
                    //then insert the children of the current list into place
                    newChildren.add(matchedList);
                    entries.put(name, name);
                }
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
     *         @cdl:extends stripped.
     * @throws CdlException in the event of trouble
     */
    private List<Node> copyAndResolve(PropertyList target,
            HashMap<QName, QName> map)
            throws CdlException {
        int childCount = target.getChildCount();
        List<Node> newChildren = new ArrayList<Node>(childCount);
        for (Node node : target.nodes()) {
            if (node instanceof PropertyList) {

                //find the matching property list element
                PropertyList entry = (PropertyList) node;
                QName name = entry.getQName();
                //merge it
                ResolveResult resolved = resolveExtends(entry);
                PropertyList resolvedList = resolved.getResolvedPropertyList();

                //now, at this point we have a resolved property list.
                //we add this to our children
                if (map == null) {
                    //when not mapping, we add everything
                    newChildren.add(resolvedList);
                } else {
                    //when mapping, we do a lookup
                    if (map.get(name) != null) {
                        //and only add unique things
                        map.put(name, name);
                        newChildren.add(resolvedList);
                    }
                }
            } else {
                //anything other than a PropertyList. Just merge it in
                newChildren.add(node);
            }
        }
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

    /**
     * Propagate resolution
     *
     * @param parent
     * @return
     */
    private ResolveEnum propagate(ResolveEnum parent) {
        if (parent == ResolveEnum.ResolvedComplete) {
            return ResolveEnum.ResolvedComplete;
        }
        if (parent == ResolveEnum.ResolvedIncomplete) {
            return ResolveEnum.ResolvedIncomplete;
        }
        if (parent == ResolveEnum.ResolvedNoWorkNeeded) {
            return ResolveEnum.ResolvedComplete;
        }
        return ResolveEnum.ResolvedLazyLinksRemaining;
    }


}
