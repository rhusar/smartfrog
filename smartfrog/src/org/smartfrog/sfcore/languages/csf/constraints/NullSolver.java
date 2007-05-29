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

package org.smartfrog.sfcore.languages.csf.constraints;

import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.CSFComponentDescription;
import org.smartfrog.sfcore.languages.csf.csfcomponentdescription.FreeVar;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.util.Vector;
import java.util.Stack;
import java.util.Iterator;

/**
 * Implementation of the default minimal solver - it does not bind any variables, it simply checks that
 * no variables are present (ie there are no unbound variables).
 */
public class NullSolver extends CoreSolver implements Solver {
    protected Vector unbounds = null;
    protected CSFComponentDescription top;

    public NullSolver() {
    }

    /**
     * Implemention of the solver interface method., Solve the constraints and bind the variables.
     * In this case, no solving, but simply check that there are no free variables.
     *
     * @param cd the component description at the root of the tree
     * @throws SmartFrogResolutionException
     *
     */
    public void solve(CSFComponentDescription cd) throws SmartFrogResolutionException {
        top = cd;
        try {
            findUnbound();
        } catch (SmartFrogResolutionException e) {
           throw e;
        } catch (Exception e) {
           throw new SmartFrogResolutionException("Error: null solver updating description with variable bindings during constraint resolution", e);
        }
    }

    private class BindingMapper implements CDVisitor {
        public void actOn(ComponentDescription cd, Stack s) throws Exception {
            for (Iterator i = cd.sfAttributes(); i.hasNext();) {
                Object key = i.next();
                Object value = cd.sfResolveHere(key);
                if (value instanceof FreeVar) {
                    unbounds.add(cd.sfCompleteName().toString() + ":" + key);
                } else if (value instanceof Vector)
                    checkVarInVector((Vector) value, cd, key);
            }
        }

        private void checkVarInVector(Vector value, ComponentDescription cd, Object key) {
            for (int el = 0; el < value.size(); el++) {
                if (value.elementAt(el) instanceof FreeVar) {
                    unbounds.add(cd.sfCompleteName().toString() + ":" + key);
                } else if (value.elementAt(el) instanceof Vector) {
                    checkVarInVector((Vector) value.elementAt(el), cd, key);
                }
            }
        }
    }

    private void findUnbound() throws Exception {
        unbounds = new Vector();
        top.visit(new NullSolver.BindingMapper(), false);
        if (unbounds.size() > 0) {
            throw new SmartFrogResolutionException("Unbound variable(s) in attribute with NULL solver " + unbounds);
        }
    }

}
