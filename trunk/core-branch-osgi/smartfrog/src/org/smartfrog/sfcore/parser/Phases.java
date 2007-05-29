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

package org.smartfrog.sfcore.parser;

import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;


/**
 * Defines the Phases interface. Objects that implement this interface are
 * created by the parser. The phases may then be invoked and the finally the
 * resultant Phases instance may be converted to a simple class implementing
 * only ComponentDescritpion for handing to the SmartFrog deployment engine.
 *
 */
public interface Phases extends ComponentDescription {
    /**
     * Evaluate all the phases required of the description implementing the
     * interface. The list of phases is defined as a default for the language
     * used, or defined somehow as an attribute.
     *
     * @return An instance of Phases that is the result of applying all the
     * defined phases
     *
     * @throws SmartFrogException error evaluating phases
     * */
    Phases sfResolvePhases() throws SmartFrogException;

    /**
     * Evaluate the phase given in the parameter.
     *
     * @param phase the phase to apply
     *
     * @return An instance of Phases that is the result of applying the phase.
     *
     * @throws SmartFrogException error evaluating phases
     */
    Phases sfResolvePhase(String phase)
        throws SmartFrogException;

    /**
     * Evaluate the phases given in the parameter.
     *
     * @param phases the phases to apply
     *
     * @return An instance of Phases that is the result of applying the phase.
     *
     * @throws SmartFrogException error evaluating phases
     */
    Phases sfResolvePhases(Vector phases)
        throws SmartFrogException;

    /**
     * Return the phases required to be evaluated.
     *
     * @return the phases to apply
     */
    Vector sfGetPhases();

    /**
     * Convert the Phases (resulting from applying the phases) to a
     * ComponentDescription ready for the SmartFrog deployment engine.
     *
     * @return the convertion to a component description
     *
     * @throws SmartFrogCompilationException error converting phases to a
     * componentdescription
     */
    ComponentDescription sfAsComponentDescription() throws SmartFrogCompilationException;
}
