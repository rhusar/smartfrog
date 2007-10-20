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
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.cdl.utils.NamespaceLookup;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import javax.xml.namespace.QName;

/**
 * This is a normal step down the tree.
 */
public class StepDown extends Step {

    private String prefix;
    private String localname;

    public StepDown() {
    }

    public StepDown(String prefix, String localname) {
        this.prefix = prefix;
        this.localname = localname;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLocalname() {
        return localname;
    }

    public void setLocalname(String localname) {
        this.localname = localname;
    }

    /**
     * Returns a string representation of the object.
     * @return [prefix:]localname
     */
    public String toString() {
        StringBuffer result=new StringBuffer();
        if(prefix!=null && prefix.length()>0) {
            result.append(prefix);
            result.append(':');
        }
        result.append(localname);
        return result.toString();
    }

    /**
     * This is the operation that steps need to do, to execute a step.
     *
     * @return the result.
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException
     *          if something failed.
     */
    public StepExecutionResult execute(StepExecutionResult state) throws CdlResolutionException {
        PropertyList node = state.getNode();
        NamespaceLookup namespaces=state.getNamespaces();
        String uri="";
        if(prefix!=null) {
            uri = namespaces.resolveNamespaceURI(prefix);
            if(uri==null) {
                throw new CdlResolutionException("Unknown prefix :"+prefix,state);
            }
        }
        PropertyList child = node.getChildTemplateMatching(uri, localname);
        if(child==null) {
            throw new CdlResolutionException("Child element not found  {" + uri+"}#"+localname, state);
        }
        return state.next(child);
    }


    /**
     * append zero or more reference parts to the current reference chain.
     *
     * @param namespaces   base to use for determining xmlns mapping
     * @param reference reference to build up
     */
    public void appendReferenceParts(NamespaceLookup namespaces,
                                     Reference reference) throws
            CdlResolutionException {
        String uri = "";
        if (prefix != null) {
            uri = namespaces.resolveNamespaceURI(prefix);
            if (uri == null) {
                throw new CdlResolutionException("Unknown prefix :" + prefix);
            }
        }

        Object refName;
        if(uri.length()==0) {
            refName=localname;
        } else {
            refName=new QName(uri,localname,prefix);
        }
        reference.addElement(ReferencePart.attrib(refName));
    }

}
