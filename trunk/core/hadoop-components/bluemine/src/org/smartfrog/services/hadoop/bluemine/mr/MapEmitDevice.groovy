package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text

class MapEmitDevice extends MapToDevice {


    @Override
    void process(final LongWritable key, final Mapper.Context context) {
        context.write(outputKey, event)
    }

    static Class keyClass() { Text }
    static Class valueClass() { BlueEvent }

}
