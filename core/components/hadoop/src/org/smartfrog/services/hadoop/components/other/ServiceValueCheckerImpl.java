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
package org.smartfrog.services.hadoop.components.other;

import org.smartfrog.services.hadoop.components.cluster.HadoopServiceImpl;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created 21-Jan-2009 17:10:22
 */

public class ServiceValueCheckerImpl extends HadoopServiceImpl implements HadoopCluster {

    public static final String ATTR_EXPECTED_VALUES = "expectedValues";

    public ServiceValueCheckerImpl() throws RemoteException {
    }

    /**
     * validate anything
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        ManagedConfiguration conf = createConfiguration();
        Vector<Vector<String>> expectedValues = ListUtils
                .resolveStringTupleList(this, new Reference(ATTR_EXPECTED_VALUES), true);
        String dump = conf.dump();

        for (Vector<String> tuple : expectedValues) {
            String key = tuple.get(0);
            String value = tuple.get(1);
            String actual = conf.get(key);
            if (actual == null) {
                throw new SFHadoopException("no value for " + key + " in \n" + dump, this, conf);
            }
            if (!value.equals(actual)) {
                throw new SFHadoopException("Wring value for " + key
                        + " - expected \""+value+"\" got \""+actual+"\""
                        + "in \n" + dump, this, conf);
            }
        }
        new ComponentHelper(this)
                .sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                        "Values are as expected", null, null);
    }


}
