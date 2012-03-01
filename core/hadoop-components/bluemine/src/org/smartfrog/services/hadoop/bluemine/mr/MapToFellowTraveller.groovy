package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.io.LongWritable

class MapToFellowTraveller extends AbstractBlueMapper {

    Set<BlueEvent> activeEvents = new TreeSet<BlueEvent>()

    @Override
    void process(final LongWritable key, final Mapper.Context context) {
        super.process(key, context)
        //event is the curret event.
        Date started = event.datestamp
    }


    @Override
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        return event.device
    }


}
