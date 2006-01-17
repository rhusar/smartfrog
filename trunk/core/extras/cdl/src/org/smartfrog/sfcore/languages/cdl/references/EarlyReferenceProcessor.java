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
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
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
        ToplevelList system = document.getSystem();
        if (system != null) {
            PropertyList newPropertyList = resolveReferences(system);
            if(newPropertyList!=system) {
                ToplevelList toplevel =(ToplevelList) newPropertyList;
                document.replaceSystem(toplevel);
            }
        }
    }


    /**
     * resolve all references in a property list
     * @param target list in
     * @return a (possibly new) list, or the current one with changes
     */
    private PropertyList resolveReferences(PropertyList target) throws CdlException {
        boolean shouldResolve=target.isValueReference();
        PropertyList result=target;
        if(shouldResolve) {
            if(target.isLazy()) {
                //this is lazy, skip until later.
                if(log.isDebugEnabled()) {
                    log.debug("skipping lazy reference "+target);
                }
            } else {
                //its a reference, process it
                result=resolveReferenceNode(target);
            }
        } else {
            //resolve child references
            for (Node node : target.nodes()) {
                if (node instanceof PropertyList) {
                    //cast it
                    PropertyList template = (PropertyList) node;
                    PropertyList newTemplate=resolveReferences(template);
                    if(template!=newTemplate) {
                        target.replaceChild(template,newTemplate);
                    }
                }
            }
        }
        return result;
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
        path=new ReferencePath(target);
        StepExecutionResult result = path.execute(target);
        assert result.isFinished();
        if(result.isLazyFlagFound()) {
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

        replacement.setLocalName(target.getLocalName());
        replacement.setNamespaceURI(target.getNamespaceURI());
        replacement.setNamespacePrefix(target.getNamespacePrefix());
        return target;

    }


}
