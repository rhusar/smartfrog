package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.MapToHour
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

class HourCountTest extends BluemineTestBase {

    void testHourCount() {
        runCountJob("hourcount", MapToHour.class)
    }

}
