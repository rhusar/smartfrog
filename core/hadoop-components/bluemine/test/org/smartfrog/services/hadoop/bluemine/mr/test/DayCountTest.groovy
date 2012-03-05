package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.MapToHour
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase
import org.smartfrog.services.hadoop.bluemine.mr.MapToDayOfWeek

class DayCountTest extends BluemineTestBase {

    void testHourCount() {
        runEventCountJob("daycount", MapToDayOfWeek.class)
    }

}
