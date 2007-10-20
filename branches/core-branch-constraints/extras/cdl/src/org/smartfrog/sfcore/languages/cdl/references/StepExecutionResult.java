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
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;

/**
 * This path keeps track of the processing of a (successful) resolution.
 * created 06-Jan-2006 16:52:48
 */

public class StepExecutionResult implements NamespaceLookup {

    private ReferenceResolutionContext resolutionContext;
    private ReferencePath path;
    private int index;
    private PropertyList node;
    private boolean lazyFlagFound;

    /**
     * create a result tracker
     * @param path path to use
     * @param node node to start on
     * @param resolutionContext context for tracking resolution
     */
    public StepExecutionResult(ReferencePath path, PropertyList node,
                               ReferenceResolutionContext resolutionContext) {
        this.path = path;
        this.node = node;
        this.resolutionContext = resolutionContext;
    }

    public ReferencePath getPath() {
        return path;
    }

    /**
     * Set the new path/position
     * @param newpath
     * @param position
     */
    public void setPath(ReferencePath newpath,int position) {
        this.path = newpath;
        this.index=position;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PropertyList getNode() {
        return node;
    }

    public void setNode(PropertyList node) {
        assert !(node instanceof ToplevelList): "trying to move to a root node "+node;
        this.node = node;
    }

    public boolean isLazyFlagFound() {
        return lazyFlagFound;
    }

    public void setLazyFlagFound(boolean lazyFlagFound) {
        this.lazyFlagFound = lazyFlagFound;
    }

    public ReferenceResolutionContext getResolutionContext() {
        return resolutionContext;
    }

    /**
     * Move to the next element in the graph;
     * return the updated state
     * @return
     */
    public StepExecutionResult next() {
        index++;
        return this;
    }

    /**
     * Move to the next element in the graph;
     * return the updated state
     * @param node the new node to move to
     * @return
     */
    public StepExecutionResult next(PropertyList node) {
        setNode(node);
        return next();
    }


    /**
     * Resolve the next node. This can trigger recursive resolution,
     * and may change the shape of the tree
     * @throws CdlException if something went wrong
     */
    public void resolveNextNode() throws CdlException {
        if(node.getReferencePath()!=null) {
            node=node.resolveNode(resolutionContext);
        }
    }

    /**
     * Test for a path being finished; that is, we have indexed over it.
     * @return
     */
    public boolean isFinished() {
        return index>=path.size();
    }

    /**
     * Get the current standard
     * @return the current step or null
     */
    public Step getCurrentStep() {
        if(isFinished()) {
            return null;
        } else {
            return path.getStep(index);
        }
    }

    /**
     * if we are finished; return ourselves. If not, execute the next step
     * @return the next step
     * @throws CdlException
     */
    public StepExecutionResult executeCurrentStep() throws CdlException {
        Step currentStep = getCurrentStep();
        if(currentStep==null) {
            //already finished; do nothing else
            return this;
        } else {
            return currentStep.execute(this);
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        Step currentStep = getCurrentStep();
        if (currentStep == null) {
            //already finished; do nothing else
            return "(finished)";
        } else {
            return "@"+currentStep.toString();
        }
    }

    /**
     * Get the URI of a namespace
     *
     * @param prefix the prefix
     * @return the URI or null for none.
     */
    public String resolveNamespaceURI(String prefix) {
        return path.resolveNamespaceURI(prefix);
    }
    
    public NamespaceLookup getNamespaces() {
        return path.getNamespaces();
    }
}
