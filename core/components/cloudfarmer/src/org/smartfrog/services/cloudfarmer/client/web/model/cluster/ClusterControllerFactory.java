/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import org.smartfrog.services.cloudfarmer.client.web.exceptions.UnimplementedException;
import org.smartfrog.sfcore.common.SmartFrogException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This is the factory that creates cluster controllers.
 *
 * It knows about the available options, and bridges to various forms and such like
 */

public class ClusterControllerFactory {
    /**
     * Default path for cluster components. This should really be driven from some config parameter
     * {@value}
     */
    public static final String DEFAULT_FARMER_URL = "http://localhost/farmer";

    /**
     * Read in the preferences and create cluster controller from them. If loading fails, the default controller and
     * settings are used
     *
     * @param request action request
     * @return a new cluster controller
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    public ClusterController createClusterController(HttpServletRequest request)
            throws IOException, SmartFrogException {
        ClusterControllerBinding binding = new ClusterControllerBinding();
        binding.loadBinding(request.getSession().getServletContext());
        return createClusterController(binding);
    }

    public ClusterController createClusterController(ClusterControllerBinding binding)
            throws IOException, SmartFrogException {
        switch (binding.getController()) {
            case PHYSICAL_CONTROLLER:
                return new PhysicalClusterController();

            case SMARTFROG_CONTROLLER:
                return new DynamicSmartFrogClusterController(binding.getURL());

            default:
                throw new UnimplementedException("Not implemented: controller for "
                        + binding.getController());
        }
    }


    /**
     * controller option value ={@value}
     */
    public static final int PHYSICAL_CONTROLLER = 0;
    /**
     * controller option value ={@value}
     */
    public static final int SMARTFROG_CONTROLLER = 1;

    public static final String[] CONTROLLER_NAMES = {
            "Manually managed",
            "SmartFrog"
    };

    /**
     * Return the default URL for the specific controller
     *
     * @param controllerID integer controller value
     * @return the URL to use if none is given in a form
     */

    public static String getDefaultURL(int controllerID) {
        switch (controllerID) {
            case PHYSICAL_CONTROLLER:
                return DEFAULT_FARMER_URL;

            case SMARTFROG_CONTROLLER:
                return DEFAULT_FARMER_URL;

            default:
                return "";
        }
    }
}
