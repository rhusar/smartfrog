/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.amazon.ec2;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.TerminatingInstanceDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created 02-Apr-2008 15:18:26
 */

public class InstanceList extends ArrayList<ImageInstance> {


    public static InstanceList EMPTY_LIST = new InstanceList(0);
    private static final ArrayList<TerminatingInstanceDescription> EMPTY_TERMINATED_INSTANCE_LIST = new ArrayList<TerminatingInstanceDescription>(
            0);

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     *
     * @throws IllegalArgumentException if the specified initial capacity is
     * negative
     */
    public InstanceList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public InstanceList() {
    }


    /**
     * Create a new list of instance IDs
     *
     * @return a new list
     */
    public Vector<String> listInstanceIDs() {
        int size = size();
        Vector<String> instanceIDs = new Vector<String>(size);
        if (size == 0) {
            return new Vector<String>(0);
        }
        for (ImageInstance instance : this) {
            instanceIDs.add(instance.getInstanceId());
        }
        return instanceIDs;
    }

    /**
     * Bulk terminate all instances that aren't listed as terminated/shutting
     * down
     *
     * @return the list of outcomes
     *
     * @throws EC2Exception for problems on the way
     */
    public List<TerminatingInstanceDescription> terminate()
            throws EC2Exception {
        int size = size();
        List<String> instanceIDs = new ArrayList<String>(size);
        if (size == 0) {
            return EMPTY_TERMINATED_INSTANCE_LIST;
        }
        for (ImageInstance instance : this) {
            if (!instance.isTerminated() && !instance.isShuttingDown()) {
                instanceIDs.add(instance.getInstanceId());
            }
        }
        return get(0).getBinding().terminateInstances(instanceIDs);
    }


    /**
     * Build a list of images
     *
     * @param ec2binding   the EC2 binding
     * @param reservations a list of reservations
     *
     * @return the list of active instances
     */
    public static InstanceList listInstances(Jec2 ec2binding,
                                             List<ReservationDescription> reservations) {
        InstanceList instances = new InstanceList();
        for (ReservationDescription res : reservations) {
            List<ReservationDescription.Instance> ilist = res.getInstances();
            for (ReservationDescription.Instance i : ilist) {
                instances.add(new ImageInstance(ec2binding, res, i));
            }
        }
        return instances;
    }

    /**
     * Build a list of images
     *
     * @param ec2binding  the EC2 binding
     * @param reservation a single reservations
     *
     * @return the list of active instances
     */
    public static InstanceList listInstances(Jec2 ec2binding,
                                             ReservationDescription reservation) {
        InstanceList instances = new InstanceList();
        List<ReservationDescription.Instance> ilist = reservation.getInstances();
        for (ReservationDescription.Instance i : ilist) {
            instances.add(new ImageInstance(ec2binding, reservation, i));
        }
        return instances;
    }


    /**
     * Get a list of images from the server, pass in a (possibly empty) list of
     * instances
     *
     * @param binding     the EC2 binding
     * @param instanceIDs a (possibly empty) list of instances
     *
     * @return the list of active instances
     *
     * @throws EC2Exception when things go wrong
     */
    public static InstanceList describeInstances(Jec2 binding,
                                                 List<String> instanceIDs)
            throws EC2Exception {
        List<ReservationDescription> reservations = binding.describeInstances(
                instanceIDs);
        return listInstances(binding, reservations);
    }

    /**
     * Get a list of images from the server, pass in a (possibly empty) list of
     * instances
     *
     * @param binding     the EC2 binding
     * @param instanceIDs a (possibly empty) list of instances
     * @param ami         the image ID to look for. Empty string means ALL
     *                    IMAGES.
     * @param state       the state to filter. Empty string means ALL IMAGES.
     *
     * @return the list of active instances
     *
     * @throws EC2Exception when things go wrong
     */
    public static InstanceList describeInstances(Jec2 binding,
                                                 List<String> instanceIDs,
                                                 String ami,
                                                 String state)
            throws EC2Exception {
        InstanceList instances = describeInstances(binding, instanceIDs);
        instances = filterByAMI(instances, ami);
        instances = filterByState(instances, state);
        return instances;
    }


    /**
     * Filter the list of instances to get only those with a specific image ID.
     *
     * @param instances instances to filter
     * @param ami       the image ID to look for. Empty string means ALL
     *                  IMAGES.
     *
     * @return the (possibly shorter) list.
     */
    public static InstanceList filterByAMI(InstanceList instances, String ami) {
        if (ami.length() == 0) {
            return instances;
        }
        InstanceList result = new InstanceList(instances.size());
        for (ImageInstance instance : instances) {
            if (instance.getImageId().equals(ami)) {
                result.add(instance);
            }
        }
        return result;
    }


    /**
     * Filter the list of instances to get only those in a specific state.
     *
     * @param instances instances to filter
     * @param state     the state to filter. Empty string means ALL IMAGES.
     *
     * @return the (possibly shorter) list.
     */
    public static InstanceList filterByState(InstanceList instances,
                                             String state) {
        if (state.length() == 0) {
            return instances;
        }
        InstanceList result = new InstanceList(instances.size());
        for (ImageInstance instance : instances) {
            if (instance.getState().equals(state)) {
                result.add(instance);
            }
        }
        return result;
    }

    /**
     * Returns a string representation of this collection.
     *
     * @return a string representation of this collection
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ImageInstance instance : this) {
            builder.append(instance.toString());
            builder.append('\n');
        }
        return builder.toString();
    }
}
