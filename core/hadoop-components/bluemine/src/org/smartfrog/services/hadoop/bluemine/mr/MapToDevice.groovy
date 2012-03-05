package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent

class MapToDevice extends AbstractBlueMapper {

    @Override
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        return event.device
    }

    @Override
    protected void setup(Mapper.Context context) {
        super.setup(context)
        parser.parseDatestamp = false
    }


}
