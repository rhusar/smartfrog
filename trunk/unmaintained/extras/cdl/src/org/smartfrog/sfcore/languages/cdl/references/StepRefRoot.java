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

import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.dom.CdlDocument;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.dom.ToplevelList;
import org.smartfrog.sfcore.languages.cdl.ParseContext;
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import javax.xml.namespace.QName;

/**
 * A refroot step discards all that went before and moves to a new location.
 * created 06-Jan-2006 16:08:35
 */

public class StepRefRoot extends Step {

    private QName refroot;

    public StepRefRoot(QName refroot) {
        this.refroot = refroot;
    }

    public QName getRefroot() {
        return refroot;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        return "~"+refroot+"/";
    }

    /**
     * This is the operation that steps need to do, to execute a step.
     *
     * @return the result.
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException
     *          if something failed.
     */
    public StepExecutionResult execute(StepExecutionResult state) throws CdlResolutionException {
        assert state != null;
        CdlDocument owner = state.getNode().getOwner();
        assert owner!=null;
        ParseContext parseContext = owner.getParseContext();
        assert parseContext != null;
        PropertyList target = parseContext.prototypeResolve(refroot);
        if(target==null) {
            throw new CdlResolutionException("Unknown cdl:refroot \""+refroot.toString()+"\"");
        }
        assert !(target instanceof ToplevelList) : "we have gone up too far!";
        return state.next(target);
    }


    /**
     * append zero or more reference parts to the current reference chain.
     *
     * @param namespaces base to use for determining xmlns mapping
     * @param reference  reference to build up
     */
    public void appendReferenceParts(NamespaceLookup namespaces,
                                     Reference reference)
            throws CdlResolutionException {
        //go to the top
        reference.addElement(ReferencePart.root());
        //now go to, well, to a deployed thing of the specified name
        Object name=refroot;
        if(refroot.getNamespaceURI().length()==0) {
            name=refroot.getLocalPart();
        }
        reference.addElement(ReferencePart.attrib(name));
    }
}
