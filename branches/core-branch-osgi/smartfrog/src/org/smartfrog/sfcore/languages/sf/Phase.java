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

package org.smartfrog.sfcore.languages.sf;

import java.util.Enumeration;
import java.util.Stack;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.reference.ApplyReference;


/**
 * Phase is a class that implements a phase on a ComponentDescritpion. It is
 * iterated over the tree by the visitor pattern implemented on
 * ComponentDescription.
 */
public class Phase implements CDVisitor {
    /** The name of the phase. */
    protected String phaseName;
    protected Stack path;

    /**
     * Construct a phase object for a specific named phase.
     *
     * @param name name of the phase
     */
    public Phase(String name) {
        phaseName = "phase." + name;
    }

    /**
     * Create a PhaseAction for a specific node for which the phase is relevant.
     *
     * @param action the object describing the phase - should be a string
     *        representing a class implementing the PhaseAction interface
     * @param cd the component description on which it is to act
     *
     * @return PhaseAction for a specific node
     *
     * @throws SmartFrogResolutionException failed to create PhaseAction
     */
    protected PhaseAction phaseAction(Object action,
                                      SFComponentDescription cd,
                                      Stack pathStack)
            throws SmartFrogResolutionException {
        try {
            ClassLoadingEnvironment env = ApplyReference.resolveEnvironmentHere(cd);
            PhaseAction p = (PhaseAction) env.loadClass(action.getClass().getName()).newInstance();
            p.forComponent(cd, phaseName, path);
            return p;
        } catch (Exception ex){ 
            String actionClass;
            if (action!=null) {
              actionClass = action.toString();
            } else {
              actionClass = "null";
            }
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(phaseName+" ("+ actionClass +", internal cause: "+ex.getMessage()+ ")", ex);
        }
    }

    /**
     * Evaluate a phase on the component description - this is the required
     * method for the visitor pattern.
     *
     * @param cd the component description on which to carry out the phase
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException failed to create PhaseAction
     */
    public void actOn(ComponentDescription cd, Stack pathStack) throws SmartFrogResolutionException {
        Context c = cd.sfContext();
        for (Enumeration e = ((Context) c.clone()).keys(); e.hasMoreElements();) {
            Object name = e.nextElement();

            if (name instanceof String) {
                String sname = (String) name;

                if (sname.startsWith(phaseName)) {
                    Object value = c.get(sname);
                    //this classcast shouldn't cause problems - but if it does, its an error anyway
                    // maybe should be prepared to provide better error message!
                    phaseAction(value, (SFComponentDescription)cd, pathStack).doit();
                }
            }
        }
    }
}
