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
package org.smartfrog.sfcore.languages.cdl.references;

import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Something that is used during resolution to track depth and print interesting things.
 *
 * created 07-Feb-2006 16:20:00
 */

public class ReferenceResolutionContext {

    private static Log log= LogFactory.getLog(ReferenceResolutionContext.class);

    int depth=0;

    int maxDepth= Constants.RESOLUTION_DEPTH_LIMIT;

    PropertyList start;

    public ReferenceResolutionContext(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public ReferenceResolutionContext() {
    }

    /**
     * Call this on entry
     * @param node
     */
    public synchronized void beginResolveReference(PropertyList node) throws CdlResolutionException {
        ReferencePath referencePath = node.getReferencePath();
        assert referencePath!=null;
        depth++;
        if(start==null) {
            start= node;
        }
        if(depth>maxDepth) {
            throw new CdlResolutionException(ReferencePath.ERROR_RECURSIVE_RESOLUTION
                    +start.getXPathDescription()
                    +" path ="+start.getReferencePath());
        }
        if (log.isDebugEnabled()) {
            log.debug("[" + depth + "] resolve " + node.getXPathDescription());
        }
    }

    /**
     * Call this on exit
     * @param node
     * @param result
     */
    public synchronized void endResolveReference(PropertyList node,PropertyList result) {
        depth--;
        if (log.isDebugEnabled()) {
            log.debug("[" + depth + "] end resolve " + node.getXPathDescription());
        }
    }
}
