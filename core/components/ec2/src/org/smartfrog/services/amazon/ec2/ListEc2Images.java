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
import com.xerox.amazonws.ec2.ImageDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created 25-Mar-2008 13:32:19
 *
 */

public class ListEc2Images extends EC2ComponentImpl implements EC2Component {

    public ListEc2Images() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            // describe images
            List<ImageDescription> images = getEc2binding().describeImages(EMPTY_ARGUMENTS);
            sfLog().info("Available EC2 AMI Images");
            for (ImageDescription img : images) {
                if ("available".equals(img.getImageState())) {
                    sfLog().info(img.getImageId() + '\t' + img.getImageLocation() + '\t' + img.getImageOwnerId());
                }
            }
        } catch (EC2Exception e) {
            throw new SmartFrogDeploymentException("Failed to talk to EC2 as "+getId(),e);
        }

    }
}
