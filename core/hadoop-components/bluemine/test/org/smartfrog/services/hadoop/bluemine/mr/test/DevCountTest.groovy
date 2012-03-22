package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.DeviceCountMap
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

class DevCountTest extends BluemineTestBase {


    void testDevCount() {
        runCountJob("devcount", DeviceCountMap)
    }

}
