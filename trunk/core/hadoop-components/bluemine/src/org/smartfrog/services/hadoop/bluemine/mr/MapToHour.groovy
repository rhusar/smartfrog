package org.smartfrog.services.hadoop.bluemine.mr

import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.mapreduce.Mapper

class MapToHour extends MapToDevice {
    @Override
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        Date date = event.datestamp
        return date.hours.toString()
    }


}
