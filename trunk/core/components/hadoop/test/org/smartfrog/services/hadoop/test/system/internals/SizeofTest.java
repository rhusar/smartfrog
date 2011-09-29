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

package org.smartfrog.services.hadoop.test.system.internals;

import junit.framework.TestCase;
import org.smartfrog.services.hadoop.operations.utils.SizeOfAgent;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hdfs.server.namenode.SizeofNamenode;
import org.apache.hadoop.hdfs.server.namenode.DatanodeDescriptor;

public class SizeofTest extends TestCase {

    static final Log LOG = LogFactory.getLog(SizeofTest.class);

    private boolean hasSizeof() {
        return SizeOfAgent.hasSizeof();
    }

    private void printSizeof(String name, Object instance) {
        long size = SizeOfAgent.sizeOf(instance);
        printSizeof(name, size);
    }

    private void printSizeof(String name, long size) {
        if(size>=0) {
            LOG.info("sizeof("+name+") =" + size);
        } else {
            LOG.info("sizeof("+name+") not known: sizeof agent is not installed");
        }
    }

    public void testSizeof() throws Throwable {
        printSizeof("BlockInfo", SizeofNamenode.sizeOfBlockInfo());
        printSizeof("INode", SizeofNamenode.sizeOfINodeFile());
        printSizeof("INodeDirectory", SizeofNamenode.sizeOfINodeDirectory());
        printSizeof("INodeDirectorywithQuota", SizeofNamenode.sizeOfINodeDirectoryWithQuota());
        printSizeof("DatanodeDescriptor",new DatanodeDescriptor());
    }
}
