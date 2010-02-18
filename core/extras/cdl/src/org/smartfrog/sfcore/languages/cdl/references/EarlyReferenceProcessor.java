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

import org.smartfrog.sfcore.languages.cdl.process.ProcessingPhase;
import org.smartfrog.sfcore.languages.cdl.process.DepthFirstOperationPhase;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.SystemElement;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.resolving.ResolveEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.Node;

/**
 * Handle compile-time/not-late references.
 * created 04-Jan-2006 15:50:59
 */

public class EarlyReferenceProcessor implements ProcessingPhase {

    private static Log log= LogFactory.getLog(EarlyReferenceProcessor.class);

    DepthFirstOperationPhase stateInferrer;
    public static final String ERROR_INCOMPLETE_RESOLUTION = "Failed to completely resolve this document";

    public EarlyReferenceProcessor() {
        stateInferrer=createStateInferrer();
    }


    /**
     * @return a string representation of the phase
     */
    public String toString() {
        return "Static Reference Processor";
    }


    /**
     * create a processor that infers our state.
     * @return
     */
    private DepthFirstOperationPhase createStateInferrer() {
        DepthFirstOperationPhase processor = new DepthFirstOperationPhase(null,
            new InferEarlyReferenceState(),
            true, true);
        return processor;
    }

    /**
     * Process a document.
     * <ol>
     * <li>Go through the doc and resolve references under the system.</li>
     * <li>For each reference, go resolve it</li>
     * <li>patch the tree</li>
     * <li>if we hit another ref in the chain, then we need to resolve it first
     *
     * @param document the document to work on
     * @throws java.io.IOException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     * @throws nu.xom.ParsingException
     */
    public void process(CdlDocument document) throws IOException, CdlException, ParsingException {

        stateInferrer.process(document);
        ResolveEnum state= ResolveEnum.ResolvedUnknown;
        boolean finished;
        SystemElement system = document.getSystem();
        ResolveEnum resolvedSystem = resolveList(system);
        state = state.merge(resolvedSystem);
        finished = state == ResolveEnum.ResolvedComplete || state == ResolveEnum.ResolvedLazyLinksRemaining;
        if(!finished) {
            throw new CdlResolutionException(ERROR_INCOMPLETE_RESOLUTION);
        }

    }

    private ResolveEnum resolveList(PropertyList target) throws CdlException, IOException {
        if(target==null) {
            return ResolveEnum.ResolvedComplete;
        }
        ResolveEnum state = target.getResolveState();
        if(state==ResolveEnum.ResolvedComplete || state==ResolveEnum.ResolvedLazyLinksRemaining) {
            return state;
        }
        resolveReferences(target);
        stateInferrer.apply(target);
        return target.getResolveState();
    }


    /**
     * resolve all references in a property list
     * @param target list in
     * @return a (possibly new) list, or the current one with changes
     */
    private PropertyList resolveReferences(PropertyList target) throws CdlException {
        ResolveEnum state = target.getResolveState();
        if (state == ResolveEnum.ResolvedComplete || state == ResolveEnum.ResolvedLazyLinksRemaining) {
            return target;
        }
        boolean shouldResolve = target.isValueReference();
        if (shouldResolve) {
            //its a reference, process it
            return target.resolveNode();
        } else {

            if (state == ResolveEnum.ResolvedIncomplete) {
                //there is something under here that needs resolving

                //resolve child references
                for (Node node : target) {
                    if (node instanceof PropertyList) {
                        //cast it
                        PropertyList template = (PropertyList) node;
                        resolveReferences(template);
                    }
                }
            }
            return target;
        }
    }

}
