package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.GroovyLineCountMapper
import org.smartfrog.services.hadoop.bluemine.mr.GroovyValueCountReducer
import org.smartfrog.services.hadoop.grumpy.GrumpyJob
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

/**
 *
 */
class LineCountTest extends BluemineTestBase {


    void testLineCount() {
        GrumpyJob job
        File outDir
        (job, outDir) = createMRJob("linecount",
                                    GroovyLineCountMapper.class,
                                    GroovyValueCountReducer.class)
        runJob(job)
        dumpDir(LOG, outDir)
    }


}
