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
package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentServiceFactory;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * Helper methods for cluster farmers
 */

public class ClusterFarmerUtils {
    /**
     * {@value}
     */
    public static final String WRONG_MACHINE_COUNT
            = "The maximum number of machines requested was less than the minimum";
    /**
     * {@value}
     */
    public static final String NEGATIVE_VALUES_NOT_SUPPORTED = "Negative values not supported";
    private static final String ERROR_NO_FACTORY = "No Deployment Factory is defined for this farmer ";

    /**
     * check the min and max arguments
     *
     * @param min minimum number of nodes desired
     * @param max maximumum number  desired
     * @throws SmartFrogDeploymentException if the parameters are somehow invalid
     */
    public static void validateClusterRange(int min, int max) throws SmartFrogDeploymentException {
        if (max < min) {
            throw new SmartFrogDeploymentException(WRONG_MACHINE_COUNT);
        }
        if (min < 0) {
            throw new SmartFrogDeploymentException(NEGATIVE_VALUES_NOT_SUPPORTED);
        }
    }

    /**
     * Get the diagnostics text. If the request for diagnostics fails, that exception's string value is returned
     * instead
     *
     * @param farmer farmer to query
     * @return some diagnostics
     */
    public static String getDiagnosticsTextRobustly(ClusterFarmer farmer) {
        String text;
        try {
            text = farmer.getDiagnosticsText();
        } catch (IOException e) {
            text = e.toString();
        } catch (SmartFrogException e) {
            text = e.toString();
        }
        return text;
    }

    /**
     * Get the deployment service for a factory.
     *
     * @param farmer  farmer
     * @param node    node to get a deployment service for
     * @param factory the factory, can be null
     * @return a deployment factory
     * @throws IOException        IO/network problems
     * @throws SmartFrogException anything else, including the factory parameter being null
     */
    public static NodeDeploymentService createNodeDeploymentService(ClusterFarmer farmer,
                                                                    ClusterNode node,
                                                                    NodeDeploymentServiceFactory factory)
            throws IOException, SmartFrogException {
        if (factory == null) {
            String text = getDiagnosticsTextRobustly(farmer);
            throw new SmartFrogDeploymentException(ERROR_NO_FACTORY + text);
        }
        NodeDeploymentService instance = factory.createInstance(node);
        return instance;
    }

    /**
     * Check deployment is supported. Requires factory to not be null, and for it to support deployment
     *
     * @param factory service factory to work with
     * @return true if deployment is allowed
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    public static boolean isDeploymentServiceAvailable(NodeDeploymentServiceFactory factory)
            throws IOException, SmartFrogException {
        return factory != null && factory.isNodeDeploymentSupported();
    }


    /**
     * Get the diagnostics for a factory
     *
     * @param factory  service factory to work with
     * @return the text
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    public static String getNodeDeploymentServiceDiagnostics(NodeDeploymentServiceFactory factory)
            throws SmartFrogException, IOException {
        if (factory == null) {
            return "No Deployment Service defined";
        }
        return factory.getDiagnosticsText();
    }
}
