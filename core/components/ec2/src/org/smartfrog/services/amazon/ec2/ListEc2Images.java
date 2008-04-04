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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created 25-Mar-2008 13:32:19
 */

public class ListEc2Images extends EC2ComponentImpl implements EC2Component {

    /**
     * {@value}
     */
    public static final String ATTR_IMAGES = "images";
    public static final String ATTR_STATE = "state";
    public static final String ATTR_TARGET_LIST = "targetList";
    public static final String ATTR_TARGET_ATTRIBUTE = "targetAttribute";
    public static final String ATTR_TARGET = "target";
    public static final String ATTR_OWNERS = "owners";
    public static final String ATTR_MANIFEST = "manifest";
    public static final String ATTR_INCLUDEPUBLIC = "includePublic";
    public static final String ATTR_USERS = "users";

    private List<String> imageList = EMPTY_ARGUMENTS;
    private List<String> owners = EMPTY_ARGUMENTS;
    private List<String> users = EMPTY_ARGUMENTS;
    private String state;
    private Prim target;
    private String targetListAttribute;
    private String targetAttribute;
    private String manifest;
    private boolean includePublic;
    private static final Reference refImages = new Reference(ATTR_IMAGES);
    private static final Reference refOwners = new Reference(ATTR_OWNERS);
    private static final Reference refUsers = new Reference(ATTR_USERS);

    public ListEc2Images() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        imageList = ListUtils.resolveStringList(this,
                refImages,
                true);
        owners = ListUtils.resolveStringList(this,
                refOwners,
                true);
        users = ListUtils.resolveStringList(this,
                refUsers,
                true);
        state = sfResolve(ATTR_STATE, "", true);
        manifest = sfResolve(ATTR_MANIFEST, "", true);
        includePublic = sfResolve(ATTR_INCLUDEPUBLIC, true, true);
        target = sfResolve(ATTR_TARGET, (Prim) null, true);
        targetListAttribute = sfResolve(ATTR_TARGET_LIST, "", true);
        targetAttribute = sfResolve(ATTR_TARGET_ATTRIBUTE, "", true);
        Ec2Worker thread = new Ec2Worker();
        setWorker(thread);
        thread.start();
    }


    private class Ec2Worker extends WorkflowThread {

        /**
         * Create a basic thread. Notification is bound to a local notification object.
         */
        private Ec2Worker() {
            super(ListEc2Images.this, true);
        }

        /**
         * matches an expected value against an actual one, if the expected one is set
         *
         * @param actual value to test
         * @param expect expected value, can be null or ""
         * @return true if expected is unset, or it is set and it matches the actual value
         */
        private boolean contained(String actual, String expect) {
            return expect == null || expect.length() == 0 || actual.contains(
                    expect);
        }

        /**
         * do our work
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            try {
                // describe images
                List<ImageDescription> images = getEc2binding().describeImages(
                        //imageList, owners, users);
                        imageList);
                sfLog().info("Available EC2 AMI Images");
                List<String> result = new ArrayList<String>(images.size());
                for (ImageDescription img : images) {
                    String id = img.getImageId();
                    String imageState = img.getImageState();
                    String imageLocation = img.getImageLocation();
                    if (contained(imageState, state)
                            && contained(imageLocation, manifest)
                            && !img.isPublic() || includePublic) {
                        result.add(id);
                        sfLog().info(img.toString());
                    }
                }
                //finished: write back
                if (targetListAttribute.length() > 0) {
                    target.sfReplaceAttribute(targetListAttribute, result);
                }
                if (targetAttribute.length() > 0 && result.size() > 0) {
                    target.sfReplaceAttribute(targetAttribute, result.get(0));
                }
            } catch (EC2Exception e) {
                throw new SmartFrogDeploymentException(
                        "Failed to talk to EC2 as " + getId(),
                        e);
            }
        }
    }
}
