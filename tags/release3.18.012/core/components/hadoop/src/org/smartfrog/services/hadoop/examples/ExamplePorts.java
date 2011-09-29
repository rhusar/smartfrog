/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.examples;

/**
 * Created 13-Feb-2009 14:24:20
 */


public interface ExamplePorts {
    int NAMENODE_HTTP_PORT = 8020;
    int NAMENODE_IPC_PORT = 8021;
    int DATANODE_HTTP_PORT = 8022;
    int DATANODE_HTTPS_PORT = 8023;
    int DATANODE_IPC_PORT = 8024;
    int JOBTRACKER_HTTP_PORT = 50030;
    int JOBTRACKER_IPC_PORT = 8012;
    int TASKTRACKER_HTTP_PORT = 50060;
}
