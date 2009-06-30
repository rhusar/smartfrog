/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.longhaul.server;

import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**

 *
 */

public abstract class EndpointBase {

    private static ProcessCompound rootProcess;

    public EndpointBase() {
        bindToRootProcess();
    }

    static synchronized void bindToRootProcess() {
        if (rootProcess == null) {
            try {
                ProcessCompound localProcess = SFProcess.getProcessCompound();
                rootProcess = localProcess;
/*
                rootProcess = SFProcess.getRootLocator().getRootProcessCompound(null,
                        ((Number) localProcess
                                .sfResolveHere(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT, false)).intValue());
*/
            } catch (Exception e) {
                //log and continue
            }
        }
    }


    /**
     * Get the root process, assuming we are bound
     *
     * @return the process
     */
    public static ProcessCompound getRootProcess() {
        return rootProcess;
    }

    /**
     * Get a list of child applications and keys
     *
     * @return a list of child applications and keys
     *
     * @throws RemoteException on network trouble
     */
    public List<ChildApplication> getApplications() throws RemoteException {
        List<ChildApplication> apps = new ArrayList<ChildApplication>();
        ProcessCompound root = getRootProcess();
        Enumeration<Liveness> children = root.sfChildren();
        while (children.hasMoreElements()) {
            Liveness liveness = children.nextElement();
            Object key = root.sfAttributeKeyFor(liveness);
            ChildApplication childApp = new ChildApplication(key, (Prim) liveness);
            apps.add(childApp);
        }
        return apps;
    }


    /**
     * Create a temporary file and register the file as an attachment
     * @param extension extension to give it
     * @return the new file
     * @throws java.io.IOException if the file wont be created.
     */
/*     public FileEntry createNewTempFile(String extension) throws IOException {
         AddedFilestore filestore = getFilestore();
         FileEntry entry = filestore.createNewFile("file", extension);
         addAttachment(entry);
         return entry;
     }*/

    /**
     * Add the attachment in the attachments table
     *
     * @param entry new file entry
     */
/*     public synchronized void addAttachment(FileEntry entry) {
         attachments.add(entry);
     }*/

    protected static class ChildApplication {
        Object key;
        String safename;
        Prim application;

        public ChildApplication(Object key, Prim application) {
            this.key = key;
            this.safename = key.toString();
            this.application = application;
        }

        public Object getKey() {
            return key;
        }

        public String getSafename() {
            return safename;
        }

        public Prim getApplication() {
            return application;
        }
    }
}
