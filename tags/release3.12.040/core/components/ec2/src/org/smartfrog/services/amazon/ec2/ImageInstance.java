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

import java.util.Locale;

/**
 * This is a holder class for image instances; contains more OO operations on them.
 */

public class ImageInstance {


    public ImageInstance(Jec2 binding,
                         ReservationDescription reservation,
                         ReservationDescription.Instance instance) {
        this.binding = binding;
        this.instance = instance;
        this.reservation = reservation;
    }

    private ReservationDescription.Instance instance;
    private ReservationDescription reservation;
    private Jec2 binding;

    public ReservationDescription.Instance getInstance() {
        return instance;
    }

    public ReservationDescription getReservation() {
        return reservation;
    }

    public Jec2 getBinding() {
        return binding;
    }

    public String getImageId() {
        return instance.getImageId();
    }

    public String getInstanceId() {
        return instance.getInstanceId();
    }

    public String getPrivateDnsName() {
        return instance.getPrivateDnsName();
    }

    public String getDnsName() {
        return instance.getDnsName();
    }

    public String getReason() {
        return instance.getReason();
    }

    public String getKeyName() {
        return instance.getKeyName();
    }

    /**
     * Get the state. This is always lower case in the ENGLISH locale
     *
     * @return the new state
     */
    public String getState() {
        String s = instance.getState();
        return s != null ? s.toLowerCase(Locale.ENGLISH) : null;
    }

    public boolean isRunning() {
        return EC2Instance.STATE_RUNNING.equals(getState());
    }

    public boolean isPending() {
        return EC2Instance.STATE_PENDING.equals(getState());
    }

    public boolean isShuttingDown() {
        return EC2Instance.STATE_SHUTTING_DOWN.equals(getState());
    }

    public boolean isTerminated() {
        return EC2Instance.STATE_TERMINATED.equals(getState());
    }


    /**
     * Returns a string representation of the object.
     */
    public String toString() {
        return instance.toString();
    }

    /**
     * Terminate the instance
     *
     * @return a termination record
     * @throws EC2Exception when things go wrong
     */
    public TerminatingInstanceDescription terminate() throws EC2Exception {
        String[] in = new String[1];
        in[0] = getInstanceId();
        return binding.terminateInstances(in).get(0);
    }


}
