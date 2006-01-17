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
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;

/**
 * This path keeps track of the processing of a (successful) resolution.
 * created 06-Jan-2006 16:52:48
 */

public class StepExecutionResult {

    private ReferencePath path;
    private int index;
    private PropertyList node;
    private boolean lazyFlagFound;

    public StepExecutionResult(ReferencePath path, PropertyList nextNode) {
        this.path = path;
        this.node = nextNode;
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
        this.node = node;
    }

    public boolean isLazyFlagFound() {
        return lazyFlagFound;
    }

    public void setLazyFlagFound(boolean lazyFlagFound) {
        this.lazyFlagFound = lazyFlagFound;
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
     * Test for a path being finished; that is, we have indexed over it.
     * @return
     */
    public boolean isFinished() {
        return index>=path.size();
    }

    public Step getCurrentStep() {
        if(isFinished()) {
            return null;
        } else {
            return path.getStep(index);
        }
    }

    public StepExecutionResult executeCurrentStep() throws CdlException {
        Step currentStep = getCurrentStep();
        if(currentStep==null) {
            //already finished; do nothing else
            return this;
        } else {
            return currentStep.execute(this);
        }
    }
}
