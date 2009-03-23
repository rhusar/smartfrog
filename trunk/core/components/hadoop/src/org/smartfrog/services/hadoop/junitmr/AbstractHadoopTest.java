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
package org.smartfrog.services.hadoop.junitmr;

import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created 18-Mar-2009 15:26:31
 */

public class AbstractHadoopTest extends TestCase implements JUnitHadoopContext {


    protected Configuration configuration;
    protected static final String FS_DEFAULT_NAME = "fs.default.name";

    /**
     * Sets up the fixture, for example, open a network connection. This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected String getRequiredConfProperty(String property) {
        Configuration conf = getConfiguration();
        String value = conf.get(property);
        assertNotNull("Unset property "+property, value);
        return value;
    }

    /**
     * Create a DFS Instance and initialise it from the configuration
     *
     * @param conf configuration
     * @return a DFS
     * @throws IOException if things go wrong
     */
    public FileSystem getFilesystem() throws IOException {
        String filesystemURL = getRequiredConfProperty(FS_DEFAULT_NAME);
        return createFileSystem(filesystemURL);
    }

    /**
     * Create a DFS client instance from a given URL; initialize it from the configuration
     *
     * @param filesystemURL the URL of the filesystem
     * @return a filesystem client
     * @throws IOException if things go wrong
     */
    public FileSystem createFileSystem(String filesystemURL)
            throws IOException {
        Configuration conf = getConfiguration();
        URI uri;
        try {
            uri = new URI(filesystemURL);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid " + FS_DEFAULT_NAME
                            + " URI: " + filesystemURL + ":" + e);
        }
        FileSystem dfs = FileSystem.get(uri, conf);
        dfs.initialize(uri, conf);
        return dfs;
    }
}
