/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.dns;

import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.util.Stack;




/**
 * An abstract class to implement the component description visitor pattern.
 *
 * 
 * 
 */
public abstract class DNSVisitor implements CDVisitor {

    /** An array with possible results from the visitor pattern. */
    Object[] result = null;


    /**
     * Creates a new <code>DNSVisitor</code> instance.
     *
     */
    public DNSVisitor() {

        result =  new Object[0];
    }

    /**
     * Creates a new <code>DNSVisitor</code> instance.
     *
     * @param result an <code>Object[]</code> value
     */
    public DNSVisitor(Object[] result) {

        if (result.length == 0) {
            throw new IllegalArgumentException("invalid result arg");
        }
        this.result = result;
    }

    /**
     * method which is invoked on each node.
     *
     * @param node tree node on which to act
     *
     * @exception Exception error thrown during action
     */
    public abstract void actOn(ComponentDescription node, Stack path);





    /**
     * Gets  An array with results from the visitor pattern.
     *
     * @return  An array with  results from the visitor pattern. 
     */
    public Object[] getResult() {

        return result;
    }

}
