package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent

class MapToDevice extends AbstractBlueMapper {

    @Override
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        return event.device
    }

    /**
     * When invoked , event is the current event, outputKey is set to the Text to write
     * @param key
     * @param context
     */
    void process(LongWritable key, Mapper.Context context) {
        context.write(outputKey, event)
    }
}
