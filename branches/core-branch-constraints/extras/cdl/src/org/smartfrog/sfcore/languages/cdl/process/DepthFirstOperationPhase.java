/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.process;

import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.Node;

/**
 * Walk down the graph and apply any named operation to it
 * created 18-Jan-2006 13:11:40
 */

public class DepthFirstOperationPhase implements ProcessingPhase {

    private PropertyListOperation before;
    private PropertyListOperation after;

    private boolean applyToConfiguration=true;

    private boolean applyToSystem=true;


    public DepthFirstOperationPhase(PropertyListOperation before, PropertyListOperation after, boolean applyToConfiguration,
                                    boolean applyToSystem) {
        this.before = before;
        this.after = after;
        this.applyToConfiguration = applyToConfiguration;
        this.applyToSystem = applyToSystem;
    }


    public void process(CdlDocument document) throws IOException, CdlException, ParsingException {
        if(applyToConfiguration && document.getConfiguration()!=null) {
            apply(document.getConfiguration());
        }
        if (applyToSystem && document.getSystem() != null) {
            apply(document.getSystem());
        }
    }

    /**
     * Depth first application of before/after operations
     * @param target
     * @throws IOException
     * @throws CdlException
     */

    public void apply(PropertyList target) throws IOException, CdlException {
        if(before!=null) {
            before.apply(target);
        }

        //children
        for(Node node:target) {
            if(node instanceof PropertyList) {
                apply((PropertyList) node);
            }
        }

        if (after!=null) {
            after.apply(target);
        }

    }


    /**
     * @return a string representation of the phase
     */
    public String toString() {
        return "Depth first operation; before="+before+" after="+after;
    }

}
