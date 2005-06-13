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
import org.smartfrog.sfcore.languages.cdl.dom.attributes.ValueOfAttribute;
import org.smartfrog.sfcore.languages.cdl.faults.CdlXmlParsingException;

import java.util.Collection;
import java.util.HashMap;

/**
 * created 21-Apr-2005 14:42:51
 */

public class Expression extends DocNode {
    public static final String ERROR_UNEXPECTED_ELEMENT_IN_EXPRESSION = "Unexpected element ";
    public static final String ERROR_DUPLICATE_VALUE = "Duplicate variable in expression: ";

    private ValueOfAttribute valueOf;

    private HashMap<String, Variable> variables = new HashMap<String, Variable>();

    public Expression() {
    }

    public Expression(Element node) throws CdlXmlParsingException {
        super();
        bind(node);
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
    protected void add(Variable variable) throws CdlXmlParsingException {
        String nameValue = variable.getNameValue();
        if (lookupVariable(nameValue) != null) {
            throw new CdlXmlParsingException(getNode(),
                    ERROR_DUPLICATE_VALUE + nameValue);
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

    public void bind(Element element) throws CdlXmlParsingException {
        super.bind(element);
        valueOf = ValueOfAttribute.extract(element, true);
        //now run though our children, which must all be variables
        for (Node child : children()) {

            if (child instanceof Element) {
                Element childElement = (Element) child;
                if (!Variable.isA(childElement)) {
                    throw new CdlXmlParsingException(getNode(),
                            ERROR_UNEXPECTED_ELEMENT_IN_EXPRESSION + element);
                }
                Variable v = new Variable(childElement);
                add(v);
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
     * test that a node is of the right type
     *
     * @param element
     * @return true if the element namespace and localname match what we handle
     */
    static boolean isA(Element element) {
        return isNode(element, ELEMENT_EXPRESSION);
    }
}
