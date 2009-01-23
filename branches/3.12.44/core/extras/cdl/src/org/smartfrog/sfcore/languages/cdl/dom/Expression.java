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
package org.smartfrog.sfcore.languages.cdl.dom;

import nu.xom.Element;
import nu.xom.Node;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescription;
import org.smartfrog.sfcore.languages.cdl.components.CdlComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.cdl.dom.attributes.ValueOfAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;
import org.smartfrog.sfcore.languages.cdl.generate.DescriptorSource;

import javax.xml.namespace.QName;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;

/**
 * created 21-Apr-2005 14:42:51
 */

public class Expression extends DocNode implements DescriptorSource {

    private ValueOfAttribute valueOf;

    private HashMap<String, Variable> variables = new HashMap<String, Variable>();

    public Expression(String name) {
        super(name);
    }

    public Expression(String name, String uri) {
        super(name, uri);
    }

    protected Element shallowCopy() {
        return new Expression(getQualifiedName(), getNamespaceURI());
    }

    public Expression(Element node) {
        super(node);
    }


    public Collection<Variable> getVariables() {
        return variables.values();
    }

    public ValueOfAttribute getValueOf() {
        return valueOf;
    }

    /**
     * add a variable
     *
     * @param variable variable to add
     * @throws CdlXmlParsingException if it already exists
     */
    protected void addVariable(Variable variable) throws CdlXmlParsingException {
        String nameValue = variable.getNameValue();
        if (lookupVariable(nameValue) != null) {
            throw new CdlXmlParsingException(this,
                    ErrorMessages.ERROR_DUPLICATE_VALUE + nameValue);
        }
        variables.put(nameValue, variable);

    }

    /**
     * find a variable with a give name
     *
     * @param name
     * @return
     */
    public Variable lookupVariable(String name) {
        return variables.get(name);
    }

    public void bind() throws CdlXmlParsingException {
        super.bind();
        variables = new HashMap<String, Variable>();
        valueOf = ValueOfAttribute.extract(this, true);
        //now run though our children
        for (Node child : this) {

            if (child instanceof Variable) {
                //add variables to our list of vars
                addVariable((Variable) child);
            } else {
                if (child instanceof Element && !(child instanceof Documentation)) {
                    //everything that is not documentation is rejected
                    //the XSD should prevent this, anyway
                    throw new CdlXmlParsingException(this,
                            ErrorMessages.ERROR_UNEXPECTED_ELEMENT_IN_EXPRESSION + child);
                }
            }
        }
    }

    /**
     * Evaluate an expression
     */
    public void evaluate() {
        //TODO
        throw new RuntimeException("Not implemented");
    }



    /**
     * Test that a (namespace,localname) pair matches our type
     *
     * @param namespace
     * @param localname
     *
     * @return true for a match
     */
    public static boolean isA(String namespace, String localname) {
        return isNode(namespace, localname, ELEMENT_EXPRESSION);
    }

    /**
     * Add a new description
     *
     * @param parent node: add attribute or children
     * @throws java.rmi.RemoteException
     */
    public void exportDescription(CdlComponentDescription parent) throws RemoteException, SmartFrogException {
        QName name=getQName();
        CdlComponentDescriptionImpl node=new CdlComponentDescriptionImpl(name,parent);
        node.registerWithParent();
        node.sfReplaceAttribute("expression","TODO");
    }
}
