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

package org.smartfrog.services.anubisdeployer;

import java.rmi.RemoteException;
import java.util.Stack;
import java.util.Date;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.CDVisitor;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 *
 */
public class ClusterDeployerImpl extends PrimProcessDeployerImpl {

    public static final String CLUSTERCOMPOUNDCLASS =
            "org.smartfrog.services.anubisdeployer.ClusterCompoundImpl";
    static String uniqueNameBase;
    public static final String ATTR_CLUSTER_NODE_MANAGEMENT = "clusterNodeManagement";
    public static final String ATTR_CLUSTER_STATUS_MONITOR = "clusterStatusMonitor";

    static {
        try {
            uniqueNameBase = "sfClusterReservation-" + SFProcess.getProcessCompound().sfCompleteName();
        } catch (RemoteException e) {
            uniqueNameBase = "sfClusterReservation.error";
        }
    }

    /**
     * Constructs the ClusetrDeployerImpl with ComponentDescription.
     *
     * @param descr target to operate on
     */
    public ClusterDeployerImpl(ComponentDescription descr) {
        super(descr);
    }

    public Prim deploy(Reference name, Prim parent, Context params)
            throws SmartFrogDeploymentException {
        target = preprocess(target);
        return super.deploy(name, parent, params);
    }

    private static ComponentDescription preprocess(ComponentDescription descr)
            throws SmartFrogDeploymentException {

        /*
        * if the attribute sfProcessHost is set - simply pass to the super-class
        * deployer - as this overrides cluster deployment
        */

        boolean alreadyAllocated = true;
        try {
            descr.sfResolve(new Reference(ReferencePart.here(ClusterCompoundImpl.ATTR_SF_RESERVATION_ID)));
        } catch (Exception e) {
            alreadyAllocated = false;
        }
        if (alreadyAllocated) {
            return descr;
        }

        /*
         * work on a copy, so as not to pollute the original just in case...
         * also this keeps a clean copy in case of errors
         */

        ComponentDescription dcopy = (ComponentDescription) descr.copy();

        /* 
         * define the data that is to be used for the various passes
         *     requirements will be used during the gather pass to collect resource requirements
         *     allocations will hold the mappings from abstract to physical nodes
         *
         */

        ComponentDescription requirements = new ComponentDescriptionImpl(null, new ContextImpl(), true);
        ComponentDescription allocations = new ComponentDescriptionImpl(null, new ContextImpl(), true);

        try {
            dcopy.visit(new Gatherer(requirements), true, false);

            //System.out.println("requirements" + requirements);

            allocations = allocate(requirements);

            if (allocations == null) {
                throw new SmartFrogDeploymentException("insufficient resources");
            } else {
                dcopy.visit(new Mapper(allocations), true, false);
            }

        } catch (Exception e) {
            dcopy = descr;
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        }
        return dcopy;
    }

    private static ComponentDescription allocate(ComponentDescription reqs) {
        //System.out.println("allocating " + reqs);
        Reference clusterManagementRef = new Reference(ReferencePart.here(ATTR_CLUSTER_NODE_MANAGEMENT));
        Reference clusterStatusRef = new Reference(ReferencePart.here(ATTR_CLUSTER_STATUS_MONITOR));

        ComponentDescription resources = null;
        ProcessCompound pc = SFProcess.getProcessCompound();

        ClusterMonitor cm;
        try {
            Prim cd = (Prim) pc.sfResolve(clusterManagementRef);
            cm = (ClusterMonitor) cd.sfResolve(clusterStatusRef);
        } catch (SmartFrogException e) {
            e.printStackTrace();
            return null; // shouldn't happen - at least don't know what to do if it does...
        } catch (RemoteException e) {
            return null; // shouldn't happen, as it is local...
        }

        try {
            resources = cm.clusterStatus();
        } catch (RemoteException e1) {
            return null; // shouldn't happen as it is local...
        }

        //System.out.println("to resources " + resources);
        ComponentDescription allocations = ClusterResourceMapper.mapNodes(resources, reqs);

        //System.out.println("with allocations " + allocations);
        return allocations;
    }

    private static class Gatherer implements CDVisitor {
        static int index = 0;
        ComponentDescription reqs;
        public static final String ATTR_NAME = "name";

        public Gatherer(ComponentDescription reqs) {
            this.reqs = reqs;
        }

        public void actOn(ComponentDescription node, Stack path) {
            /* if the node is a ClusterCompound, tested by the
             * sfClass, look to see if it is already allocated to a
             * node (sfProcessHost attribute exists) if not, then
             * gather the data into the reqs.
	         *
	         * Note that since the assumption is made that the tree is fully resolved,
	         * the path is irrelevant.
             */
            //if (!node.getEager()) return;
            Context c = node.sfContext();
            Object sfClass = c.get("sfClass");
            if (sfClass != null && sfClass.equals(CLUSTERCOMPOUNDCLASS)) {
                if (c.containsKey("sfProcessHost")) {
                    // although a host has been allocated, check that the resrvation id is set.
                    // The host may have been set to ensure the location at which the  deployment is made, but
                    // resources are still required to be reserved, just no mapping done.

                    /** @TODO fix the fact that resources still need to be checked and mapped...
                     */
                    if (!c.containsKey("sfReservationId")) {
                        c.put("sfReservationId", newUniqueName());
                    }
                } else {
                    //System.out.println("putting sfReservationId");
                    c.put("sfReservationId", newUniqueName());
                    ComponentDescription req =
                            (ComponentDescription) c.get("sfClusterNode");
                    if (req != null) {
                        Object id;
                        try {
                            id = req.sfResolve(new Reference(ReferencePart.here(ATTR_NAME)));
                        } catch (SmartFrogResolutionException e) {
                            id = newUniqueName();
                            req.sfContext().put(ATTR_NAME, id);
                        }
                        ClusterResourceMapper.accumulateRequirements(reqs, req);
                    }
                }
            }
        }

        private static String newUniqueName() {
            return uniqueNameBase + new Date().getTime() + (index++);
        }
    }

    private static class Mapper implements CDVisitor {
        Context allocs;

        public Mapper(ComponentDescription allocs) {
            this.allocs = allocs.sfContext();
        }

        public void actOn(ComponentDescription node, Stack path) {
            /* if the node is a ClusterCompound, tested by the
             * sfClass, look to see if it is already allocated to a
             * node (sfProcessHost attribute exists) if not, then add
             * the sfProcessHost mapping for the abstract node name.
	     *
	     * Note that since the assumption is made that the tree is fully resolved,
	     * the path is irrelevant.
             */
            //if (!node.getEager()) return;
            Context c = node.sfContext();
            Object sfClass = c.get("sfClass");
            if (sfClass != null && sfClass.equals(CLUSTERCOMPOUNDCLASS)) {
                if (!c.containsKey("sfProcessHost")) {
                    ComponentDescription req =
                            (ComponentDescription) c.get("sfClusterNode");
                    if (req != null) {
                        Object id;
                        try {
                            id = req.sfResolve(new Reference(ReferencePart.here("name")));
                            c.put("sfProcessHost", allocs.get(id));
                        } catch (Exception e) {
                            // shouldn't ever happen...!
                        }
                    }
                }
            }
        }
    }
}
