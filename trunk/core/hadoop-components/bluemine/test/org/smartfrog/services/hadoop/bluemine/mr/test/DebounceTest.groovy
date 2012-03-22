package org.smartfrog.services.hadoop.bluemine.mr.test

import org.smartfrog.services.hadoop.bluemine.mr.MapToDayOfWeek
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase
import org.smartfrog.services.hadoop.bluemine.output.ExtTextOutputFormat
import org.apache.hadoop.io.IntWritable
import org.smartfrog.services.hadoop.bluemine.reducers.CountReducer
import org.smartfrog.services.hadoop.grumpy.GrumpyJob
import org.apache.hadoop.io.Text
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.mr.DebounceMap
import org.smartfrog.services.hadoop.bluemine.reducers.EventCSVEmitReducer

class DebounceTest extends BluemineTestBase {

    void testDebounceSmall() {
        GrumpyJob job
        File outDir
        (job, outDir) = createMRJobNoDataset("debounce-small",
                DebounceMap,
                EventCSVEmitReducer)
        addDataset(job, GATE1_SMALL)
        job.outputFormatClass = ExtTextOutputFormat
        job.mapOutputKeyClass = Text
        job.mapOutputValueClass = BlueEvent
        runJob(job)
        dumpDir(LOG, outDir)
        outDir
    }
    void testDebounceJob() {
        GrumpyJob job
        File outDir
        (job, outDir) = createMRJob("debounce",
                DebounceMap,
                EventCSVEmitReducer)
        job.outputFormatClass = ExtTextOutputFormat
        job.mapOutputKeyClass = Text
        job.mapOutputValueClass = BlueEvent
        runJob(job)
        dumpDir(LOG, outDir)
        outDir
    }
}
