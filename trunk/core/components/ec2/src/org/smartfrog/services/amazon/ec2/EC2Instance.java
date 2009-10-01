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

/**
 *
 */
public interface EC2Instance extends EC2Component {


    /**
     * this is the AMI of an image. Empty string is don't care
     * {@value}
     */
    String ATTR_IMAGE_ID = "imageID";

    /**
     * {@value}
     */
    String ATTR_INSTANCE = "instance";

    /**
     * {@value}
     */
    String ATTR_SHUTDOWN = "shutdown";

    /**
     * {@value}
     */
    String ATTR_INSTANCE_TYPE = "instanceType";

    /**
     * {@value}
     */
    String ATTR_AVAILABILITY_ZONE = "availabilityZone";

    /**
     * {@value}
     */
    String ATTR_SECURITY_GROUP = "securityGroup";


    /**
     * {@value}
     */
    String ATTR_USER_DATA = "userData";

    /**
     * Key to use for SSH-ing
     */
    String ATTR_KEY_NAME = "keyName";
    
    /** string list of instances. Can be empty {@value} */
    String ATTR_INSTANCES = "instances";
    
    /** minimum number of instances {@value} */
    String ATTR_MIN_COUNT = "minCount";
    
    /** max number {@value} */
    String ATTR_MAX_COUNT = "maxCount";

    /**
     * machine state attribute {@value}
     */
    String ATTR_STATE = "state";

    /**
     * machine state {@value}
     */
    String STATE_RUNNING = "running";
    /**
     * machine state {@value}
     */
    String STATE_PENDING = "pending";
    /**
     * machine state {@value}
     */
    String STATE_SHUTTING_DOWN = "shutting-down";
    /**
     * machine state {@value}
     */
    String STATE_TERMINATED = "terminated";

}
