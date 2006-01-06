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
package org.smartfrog.sfcore.languages.cdl.references;

import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.services.xml.java5.NamespaceUtils;
import org.smartfrog.services.xml.utils.XsdUtils;
import org.ggf.cddlm.generated.api.CddlmConstants;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

import nu.xom.ParentNode;

/**
 * A reference path
 * Nothing in here is thread safe.
 */
public class ReferencePath {
    /**
     * Error message when constructing from something that lacks a cdl:ref attribute.
     * {@value}
     */
    public static final String ERROR_NON_REFERENCE = "Trying to create a reference path from a non-reference";
    /**
     * Error message when trying to make relative something that has no toplevel node to bind against.
     * {@value}
     */
    public static final String ERROR_NO_TOPLEVEL = "There is no toplevel node in this graph; unable to make relative";

    public ReferencePath() {
    }

    /**
     * Build from a source
     *
     * @param source
     * @throws IllegalArgumentException
     */
    public ReferencePath(PropertyList source) {
        String refRootValue = source.getRefRootValue();
        String refValue = source.getRefValue();
        if (refValue == null) {
            throw new IllegalArgumentException(ERROR_NON_REFERENCE);
        }

        build(refValue);

        //extract lazy flag.
        String lazyValue=source.getAttributeValue(CddlmConstants.ATTRIBUTE_LAZY,
                CddlmConstants.XML_CDL_NAMESPACE);
        lazy=XsdUtils.isXsdBooleanTrue(lazyValue);

        if (refRootValue != null) {
            //a refroot: paste this in to the front of the path.
            QName resolved = source.resolveQName(refRootValue);
            Step step=new StepRefRoot(resolved);
            steps.add(0,step);

        } else {
            makeRelative(source);
        }
    }

    private boolean lazy;

    /**
     * the steps in the path
     */
    private List<Step> steps = new ArrayList<Step>();

    public List<Step> getSteps() {
        return steps;
    }

    /**
     * add a new step to the path
     *
     * @param step
     */
    public void append(Step step) {
        assert step != null;
        steps.add(step);
    }

    /**
     * Test for the path being empty
     *
     * @return
     */
    public boolean isEmpty() {
        return steps.isEmpty();
    }

    /**
     * This function can only be called on a non-empty path.
     *
     * @return true iff this is a relative path.
     */
    public boolean isRelative() {
        return !steps.get(0).isRoot();
    }


    /**
     * Is a path lazy?
     * @return
     */
    public boolean isLazy() {
        return lazy;
    }

    /**
     * Mark a path as lazy
     * @param lazy
     */
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }


    /**
     * Get a step in a position
     * @param position
     * @return the step
     * @throws IndexOutOfBoundsException if there is nothing at that location
     */
    public Step getStep(int position) {
        return steps.get(position);
    }

    /**
     * Returns a string representation of the object.
     * Has an extra / at the end, whether you want it or not.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        if (!isEmpty()) {
            for (Step step : steps) {
                result.append(step.toString());
                if (!step.isRoot()) {
                    result.append('/');
                }
            }
        }

        return result.toString();
    }

    /**
     * Build from a path of the spec
     * <pre>
     * . | .. | localname | prefix:localname separated by /
     * </pre>
     * This is public primarily for testing; normally the
     * {@link ReferencePath#ReferencePath(org.smartfrog.sfcore.languages.cdl.dom.PropertyList)}
     * constructor should be used, as that will make the link relative at the same time.
     *
     * @param path
     */
    public void build(String path) {
        //this is very easy to parse; no need for any complex recursive
        //parser. Even so, regexp work may make this tractable
        steps = new ArrayList<Step>();
        int start = 0;
        final int pathlength = path.length();
        if (path.startsWith("/")) {
            append(new StepRoot());
            start = 1;
            if (pathlength == 1) {
                return;
            }
        }
        //now, scan through the source looking for stuff
        boolean finished = false;

        while (!finished) {
            int slash = path.indexOf('/', start);
            String qname;
            if (slash < 0) {
                finished = true;
                qname = path.substring(start);
            } else {
                qname = path.substring(start, slash);
                start = slash + 1;
                finished = start >= pathlength;
            }
            //here qname is the current element. Extract but do not yet evaluate
            if (".".equals(qname)) {
                append(new StepHere());
            } else if ("..".equals(qname)) {
                append(new StepUp());
            } else {
                String prefix = NamespaceUtils.extractNamespacePrefix(qname);
                String localname = NamespaceUtils.extractLocalname(qname);
                StepDown step = new StepDown(prefix, localname);
                append(step);
            }
        }


    }

    /**
     * logic to turn an absolute list into a relative one. This has to be done
     * before extends processing, so is executed during initialisation.
     *
     * @param source
     */
    private void makeRelative(PropertyList source) {
        assert !isEmpty();
        Step first = steps.get(0);
        if (!first.isRoot()) {
            //no work needed
            return;
        }
        //remove the relative reference.
        steps.remove(first);
        //now we count the number of steps to a parent.
        PropertyList node = source;
        while (!node.isToplevel()) {
            //insert a new upward step
            steps.add(0, new StepUp());
            ParentNode parent = node.getParent();
            if (parent==null || !(parent instanceof PropertyList)) {
                //bad type. bail out now.
                throw new CdlRuntimeException(ERROR_NO_TOPLEVEL);
            }
            node = (PropertyList) parent;
        }
        //here we are at a toplevel node
        //nothing remains to be done
        assert isRelative();
    }

    /**
     * this operation copies a reference path.
     * a new list is created, but the contents of the
     * list are the original steps; this is not a deep clone.
     * @return a new path that looks like the original
     */
    public ReferencePath shallowCopy() {
        ReferencePath copy=new ReferencePath();
        copy.steps=new ArrayList(steps);
        return copy;
    }

    /**
     * Append a path to this.
     * Any lazy flag propagates, so the total path is lazy if the appended path is lazy,
     * and the base path was not already.
     * @param other
     */
    public void appendPath(ReferencePath other) {
        lazy |= other.isLazy();
        steps.addAll(other.getSteps());
    }
}
