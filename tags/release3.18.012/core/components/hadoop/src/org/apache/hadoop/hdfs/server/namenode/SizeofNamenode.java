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

package org.apache.hadoop.hdfs.server.namenode;

import org.smartfrog.services.hadoop.operations.utils.SizeOfAgent;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.fs.permission.FsPermission;

public class SizeofNamenode {

    static PermissionStatus perms = new PermissionStatus("hadoop","hadoop",
            new FsPermission((short)0));

    public static long sizeOfINodeFile() {
        return SizeOfAgent.sizeOf(new INodeFile());
    }

    public static long sizeOfINodeDirectory() {
        return SizeOfAgent.sizeOf(new INodeDirectory("/",perms));
    }
    public static long sizeOfINodeDirectoryWithQuota() {
        return SizeOfAgent.sizeOf(new INodeDirectoryWithQuota("/",perms,0,0));
    }

    public static long sizeOfBlockInfo() {
        return SizeOfAgent.sizeOf(new BlockInfo(new Block(),0));
    }

      public static long sizeOfBlockInfo(int replicas) {
          BlockInfo instance = new BlockInfo(new Block(), replicas);
          return SizeOfAgent.sizeOf(instance);
      }


}
