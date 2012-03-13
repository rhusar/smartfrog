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
package org.smartfrog.services.hadoop.operations.test.system.conf;

import org.apache.hadoop.fs.FileSystem;
import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl;
import org.smartfrog.services.hadoop.operations.utils.DfsUtils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * Created 19-Jan-2009 14:27:02
 */

public class ManagedConfTestPrimImpl extends HadoopComponentImpl {
    private static final String P1 = "prop1";
    private static final String V1 = "value1";
    private static final String P2 = "prop2";
    private static final String V2 = "value2";

    public ManagedConfTestPrimImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        ManagedConfiguration conf = createConfiguration();
        String dump = conf.dump();
        sfLog().info(dump);
        assertTrue("conf is empty ", conf.size() > 0);
        assertTrue("no sfClass in\n" + dump, conf.get("sfClass", null) != null);
        assertTrue(P1 + "is set in\n" + dump, conf.get(P1, null) == null);
        conf.set(P1, V1);
        dump = conf.dump();
        String value1 = conf.get(P1, null);
        assertTrue(P1 + "==" + value1 + " in\n" + dump, value1 != null);
        assertTrue(P1 + "!=" + V1 + " -it is " + value1 + " in\n" + dump, V1.equals(value1));
        conf.set(P2, V2);
        dump = conf.dump();
        String value2 = conf.get(P2, null);
        assertTrue(P2 + " is null in\n" + dump, value2 != null);
        assertTrue(P2 + " = " + value2 + " in\n" + dump, V2.equals(value2));

        //trigger a reload, and see that things are still set
        conf.reloadConfiguration();
        dump = conf.dump();
        value2 = conf.get(P2, null);
        assertTrue(P2 + " is null in\n" + dump, value2 != null);
        assertTrue(P2 + " = " + value2 + " in\n" + dump, V2.equals(value2));

        //now see that we can find some filesystems
        assertFileSystemExists(conf, "hdfs://localhost:50070");
        assertFileSystemExists(conf, "ftp://localhost:50070");
        new ComponentHelper(this).targetForTermination();
    }

    private void assertFileSystemExists(final ManagedConfiguration conf, final String url)
            throws SmartFrogException {
        try {
            URI uri = new URI(url);
            String fskey = "fs." + uri.getScheme() + ".impl";
            String classname = conf.get(fskey, null);
            assertTrue("No entry in the configuration for " + fskey + " in " + conf.dump(),
                    classname != null);
            Class<?> clazz = conf.getClass(fskey, null);
            assertTrue("Unable to load class " + classname, clazz != null);
            FileSystem fileSystem = DfsUtils.loadFS(conf, uri);
        } catch (URISyntaxException e) {
            throw SmartFrogException.forward("loading " + url + ": " + e, e);
        } catch (IOException e) {
            throw SmartFrogException.forward("loading " + url + ": " + e, e);
        }
    }

    /**
     * Assert that a fact is true
     *
     * @param cause cause for it being false
     * @param fact  the fact that must hold
     * @throws SmartFrogException if it is not true
     */
    public void assertTrue(String cause, boolean fact) throws SmartFrogException {
        if (!fact) {
            throw new SmartFrogException(cause);
        }
    }
}
