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

import org.smartfrog.sfcore.languages.cdl.dom.attributes.ValueOfAttribute;
import org.smartfrog.sfcore.languages.cdl.CdlParsingException;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collection;

import nu.xom.Element;

/**
 * created 21-Apr-2005 14:42:51
 */

public class Expression extends DocNode {

    public Expression() {
    }

    private ValueOfAttribute valueOf;

    private HashMap<String,Variable> variables =new HashMap<String, Variable>();

    public Collection<Variable> getVariables() {
        return variables.values();
    }

    public ValueOfAttribute getValueOf() {
        return valueOf;
    }

    protected void add(Variable v) {
        variables.put(v.getNameValue(),v);
    }

    /**
     * find a variable with a give name
     * @param name
     * @return
     */
    public Variable lookupVariable(String name) {
        return variables.get(name);
    }

    public void bind(Element element) throws CdlParsingException {
        super.bind(element);
        valueOf=ValueOfAttribute.extract(element, true);
        //now run though our children, which must all be variables
        for(Element child:childElements()) {
            if(!Variable.isA(child)) {
                throw new CdlParsingException("Unexpected element "+element);
            }
            Variable v=new Variable(child);
            add(v);
        }
    }
}
