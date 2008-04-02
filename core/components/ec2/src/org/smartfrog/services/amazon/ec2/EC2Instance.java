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

    String ATTR_IMAGEID = "imageID";
    String ATTR_INSTANCE = "instance";
    String ATTR_SHUTDOWN = "autoShutdown";
    String ATTR_INSTANCETYPE = "instanceType";

    String ATTR_USER_DATA = "userData";
    //this is the AMI of an image. Empty string is don't care
    //string list of instances. Can be empty
    String ATTR_INSTANCES="instances";
    //minimum number of instances
    String ATTR_MIN_COUNT="minCount";
    //max number
    String ATTR_MAX_COUNT="maxCount";

    String ATTR_STATE="state";

    String STATE_RUNNING = "running";
    String STATE_PENDING = "pending";
    String STATE_SHUTTING_DOWN = "shutting-down";
    String STATE_TERMINATED = "terminated";

}
