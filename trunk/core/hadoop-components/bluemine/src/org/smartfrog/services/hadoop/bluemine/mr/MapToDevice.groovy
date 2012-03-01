package org.smartfrog.services.hadoop.bluemine.mr

import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.events.EventParser
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper

class MapToDevice extends AbstractBlueMapper {

    @Override
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        return event.device
    }

}
