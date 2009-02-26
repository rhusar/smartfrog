/**
 (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.rest.wrappers;

import org.smartfrog.services.rest.Restful;
import org.smartfrog.services.rest.data.ComponentStub;
import org.smartfrog.services.rest.data.ResolutionResult;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.servlets.HttpRestRequest;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

/**
 * The wrapper factory is used to generate wrapped objects when resources within a SmartFrog system do not implement the
 * Restful interface and are incapable of directly servicing HTTP requests themselves.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class RESTWrapperFactory {

    /**
     * Generates a new instance of the appropriate wrapper class for the subject and owner specified in the result.
     *
     * @param result      The result containing references to the subject and its owner.
     * @param restRequest The request object containing all the necessary information.
     *
     * @return A wrapper object implementing the Restful interface for operation upon the subject.
     *
     * @throws RestException If the subject or object cannot be wrapped by the factory.
     */
    public static Restful wrap(ResolutionResult result, HttpRestRequest restRequest) throws RestException {
        Object owner = result.getOwner();
        Object subject = result.getSubject();

        if (!((owner instanceof Prim) || (owner instanceof ComponentDescription))) {
            throw new RestException("The specified owner is not a traversable SmartFrog component.");
        }

        try {
            // Use a component wrapper if we have a subject that is a component (Prim), description
            // (ComponentDescription) or a stub object for a new component to be created (ComponentStub)
            if ((subject instanceof Prim) ||
                    (subject instanceof ComponentStub) ||
                    (subject instanceof ComponentDescription)) {
                return new ComponentWrapper(result, restRequest);
            } else {
                return new AttributeWrapper(result, restRequest);
            }
        }
        catch (Exception e) {
            throw new RestException("An exception occured whilst trying to wrap the target resource.", e);
		}
	}
}