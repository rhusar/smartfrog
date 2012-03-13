package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.MapToDayOfWeek
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

class DayCountTest extends BluemineTestBase {

    void testHourCount() {
        runCountJob("daycount", MapToDayOfWeek.class)
    }

}
