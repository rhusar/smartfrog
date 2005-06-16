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

import nu.xom.Element;
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
            resolveChildExtends(system);
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
        return resolveExtends(target);
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
        boolean toplevel = target.isToplevel();
        QName name = target.getName();
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
        int maxsize = target.getNode().getChildCount();
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
            state = ResolveEnum.ResolvedNoWorkNeeded;
        } else {
            //something to resolve.
            ResolveResult extended;
            log.debug("Resolving " +
                    target.getName() +
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
                output = inheritElements(target, resolvedPropertyList);
            }
            state = propagate(extended.state);
        }
        result = new ResolveResult(state, target);
        return result;
    }

    /**
     * merge in all elements. public for testing we extend any elements that
     * need extending before merging.
     *
     * @param extension
     */
    private PropertyList inheritElements(PropertyList target,
            PropertyList extension)
            throws CdlException {
        Element templateElt = extension.getNode();
        //max size of our list is the sum of all children
        int maxsize = target.getNode().getChildCount() +
                templateElt.getChildCount();
        //list of elements (this retains the overall order of things)
        List<Node> newChildren = new ArrayList<Node>(maxsize);
        //this is a map that caches mappings of things
        HashMap<QName, QName> entries = new HashMap<QName, QName>(maxsize);
        for (Node node : extension.children()) {
            if (node instanceof Element) {
                //get the element
                Element element = (Element) node;
                //find the matching property list element
                PropertyList elementAsList = extension.getChildListContaining(
                        element);
                if(elementAsList==null) {
                    //this is an element, but not mapped to a PropertyList
                    //which means that it is actually a CDL special type.
                    //add it as is, and go on to the next element
                    //todo: Write some tests that play with extending expressions
                    newChildren.add(node);
                    //go on to the next element
                    continue;
                }
                assert elementAsList != null;
                QName name = elementAsList.getName();

                //merge it
                ResolveResult resolved = resolveExtends(elementAsList);
                PropertyList resolvedList = resolved.getResolvedPropertyList();

                //now, at this point we have a property list which contains
                //a resolved element. We are going to get that element out because
                //it is what we want.
                Element resolvedElement = resolvedList.getNode();

                //now, look for a match locally
                PropertyList matchedList = target.getChildListContaining(
                        resolvedElement);
                if (matchedList == null) {
                    //insert a copy of the resolved element.
                    //the copy is needed in case it gets manipulated later
                    Element copiedElement = (Element) resolvedElement.copy();
                    newChildren.add(copiedElement);
                    entries.put(name, name);
                } else {
                    //complex merge.
                    //first, pull in the attributes of the child
                    matchedList.inheritAttributes(resolvedList);
                    //then insert the element of the current list into place
                    newChildren.add(matchedList.getNode());
                    entries.put(name, name);
                }
            } else {
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
     * This method resolves and copies all elements beneath our target into a list,
     * unless their qname appears in the hashmap of known qnames. Elements
     * are added to the #map as they are propagated
     * If the hashmap is null, the mapping lookup/add is skipped,
     * returning a list of all elements, resolved when appropriate.
     * @param target
     * @param map
     * @return a list of nodes, any extended elements are resolved and @cdl:extends stripped.
     * @throws CdlException in the event of trouble
     */
    private List<Node> copyAndResolve(PropertyList target,
            HashMap<QName, QName> map)
            throws CdlException {
        int childCount = target.getNode().getChildCount();
        List<Node> newChildren = new ArrayList<Node>(childCount);
        for (Node node : target.children()) {
            if (node instanceof Element) {
                //get the element
                Element element = (Element) node;
                //find the matching property list element
                PropertyList elementAsList = target.getChildListContaining(
                        element);
                if (elementAsList == null) {
                    //special elt, so special handling.
                    //add and continue
                    newChildren.add(node);
                    continue;
                }
                QName name = elementAsList.getName();
                //merge it
                ResolveResult resolved = resolveExtends(elementAsList);
                PropertyList resolvedList = resolved.getResolvedPropertyList();

                //now, at this point we have a property list which contains
                //a resolved element. We are going to get that element out because
                //it is what we want.
                Element resolvedElement = resolvedList.getNode();
                if (map == null) {
                    newChildren.add(resolvedElement);
                } else {
                    if (map.get(name) != null) {
                        map.put(name, name);
                        newChildren.add(resolvedElement);
                    }
                }
            } else {
                //anything other than an element. Just merge it in
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
    private PropertyList replaceNode(PropertyList target, List<Node> newChildren)
            throws CdlXmlParsingException {
        //copy the target element
        Element newElement = (Element) target.getNode().copy();
        //strip its children away (a bit wasteful)
        newElement.removeChildren();
        //add the new ones in order
        for (Node sprog : newChildren) {
            sprog.detach();
            newElement.appendChild(sprog);
        }
        //here we have our new element, ready to go
        PropertyList resultTemplate = new PropertyList(newElement);
        return resultTemplate;
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
