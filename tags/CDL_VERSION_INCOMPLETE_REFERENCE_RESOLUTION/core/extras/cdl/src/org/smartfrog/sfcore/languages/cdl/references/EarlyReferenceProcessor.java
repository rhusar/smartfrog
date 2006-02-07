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
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.SystemElement;
import org.smartfrog.sfcore.languages.cdl.dom.Names;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.Constants;
import org.smartfrog.sfcore.languages.cdl.resolving.ResolveEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import nu.xom.ParsingException;
import nu.xom.Node;
import nu.xom.Attribute;

/**
 * Handle compile-time/not-late references.
 * created 04-Jan-2006 15:50:59
 */

public class EarlyReferenceProcessor implements ProcessingPhase {

    private static Log log= LogFactory.getLog(EarlyReferenceProcessor.class);

    DepthFirstOperationPhase stateInferrer;

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
     *
     * @param document the document to work on
     * @throws java.io.IOException
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     * @throws nu.xom.ParsingException
     */
    public void process(CdlDocument document) throws IOException, CdlException, ParsingException {
        //1. go through the doc and resolve references. Everywhere? or just under the system?
        //2. run through all

        int count=0;
        stateInferrer.process(document);
        ResolveEnum state= ResolveEnum.ResolvedUnknown;
        boolean finished;
        do {
            ToplevelList configuration = document.getConfiguration();
            state = resolveList(configuration);
            SystemElement system = document.getSystem();
            ResolveEnum resolvedSystem = resolveList(system);
            state.merge(resolvedSystem);
            finished = state == ResolveEnum.ResolvedComplete;

        } while(!finished && count++<Constants.RESOLUTION_SPIN_LIMIT);

        if(!finished) {
            throw new CdlResolutionException("Gave up trying to resolve this document ");
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
        resolveReferences(target,0);
        stateInferrer.apply(target);
        return target.getResolveState();
    }


    /**
     * resolve all references in a property list
     * @param target list in
     * @param depth
     * @return a (possibly new) list, or the current one with changes
     */
    private PropertyList resolveReferences(PropertyList target, int depth) throws CdlException {
        if (depth > Constants.RESOLUTION_DEPTH_LIMIT) {
            throw new CdlResolutionException(ReferencePath.ERROR_RECURSIVE_RESOLUTION
                    + target.getDescription());
        }
        ResolveEnum state = target.getResolveState();
        if (state == ResolveEnum.ResolvedComplete || state == ResolveEnum.ResolvedLazyLinksRemaining) {
            return target;
        }
        boolean shouldResolve = target.isValueReference();
        if (shouldResolve) {
            //its a reference, process it
            return resolveReferenceNode(target);
        } else {

            if (state == ResolveEnum.ResolvedIncomplete) {
                //there is something under here that needs resolving

                //resolve child references
                for (Node node : target) {
                    if (node instanceof PropertyList) {
                        //cast it
                        PropertyList template = (PropertyList) node;
                        PropertyList newTemplate = resolveReferences(template, 0);
                        if (template != newTemplate) {
                            target.replaceChild(template, newTemplate);
                        }
                    }
                }
            }
            return target;
        }
    }

    /**
     * Resolve a node that is tagged as a reference.
     * @param target
     * @return
     */
    private PropertyList resolveReferenceNode(PropertyList target) throws CdlException {
        if (log.isDebugEnabled()) {
            log.debug("processing reference " + target);
        }

        ReferencePath path;
        path=target.getReferencePath();
        assert path!=null: "Path is null on "+target.getDescription();
        StepExecutionResult result = path.execute(target);
        assert result.isFinished();
        if(result.isLazyFlagFound() && result.getNode().isValueReference()) {
            //lazy was hit. we need to mark ourselves as lazy and continue without resolving
            target.setLazy(true);
            return target;
        }
        PropertyList dest = result.getNode();
        assert dest !=null;
        if(target==dest) {
            return target;
        }
        //create a clone of the initial property list
        //using a factory specific to the type of the current target, to
        //ensure that toplevel lists get handled
        PropertyList replacement=target.getFactory().create(dest);
        if(Constants.POLICY_STRIP_ATTRIBUTES_FROM_REFERNCE_DESTINATION) {
            replacement.removeAllAttributes();
        }
        //now copy attributes from the target.
        boolean lazy=target.isLazy();
        for (Attribute attr : target.attributes()) {
            if(Constants.XMLNS_CDL.equals(attr.getNamespaceURI())) {
                String name = attr.getLocalName();
                if(!lazy && (Names.ATTR_REFROOT.equals(name)
                || Names.ATTR_REF.equals(name))) {
                    continue;
                }
            }
            Attribute cloned=new Attribute(attr);
            replacement.addAttribute(cloned);
        }
        replacement.setLocalName(target.getLocalName());
        replacement.setNamespaceURI(target.getNamespaceURI());
        replacement.setNamespacePrefix(target.getNamespacePrefix());
        return replacement;

    }


}
