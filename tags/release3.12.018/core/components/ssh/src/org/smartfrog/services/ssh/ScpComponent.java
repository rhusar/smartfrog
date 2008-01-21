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
package org.smartfrog.services.ssh;


/**
 * SmartFrog Interface for SCP component. Used to upload/download files 
 * to/from a remote machine securely over SSH.
 * @author Ashish Awasthi
 */
public interface ScpComponent extends SSHComponent {

    /**
     * {@value}
     */
    String LOCAL_FILES = "localFiles";
    /**
     * {@value}
     */
    String REMOTE_FILES = "remoteFiles";
    /**
     * {@value}
     */
    String TRANSFER_TYPE = "transferType";
    /**
     * {@value}
     */
    String RECURSIVE = "recursive";
}

