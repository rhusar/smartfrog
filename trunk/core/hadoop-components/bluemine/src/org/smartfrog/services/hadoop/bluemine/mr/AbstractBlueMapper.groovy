package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.EventParser

abstract class AbstractBlueMapper extends Mapper<LongWritable, Text, Text, BlueEvent>
            implements BluemineOptions {

    protected EventParser parser = new EventParser()
    protected Text outputKey = new Text()
    protected BlueEvent event = new BlueEvent()

    /**
     * Parse and emit events
     * @param key line #
     * @param value raw line
     * @param context ctx
     */
    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) {
        parser.parse(event, value)
        outputKey.set(selectOutputKey(event, context))
        process(key, context)
    }

    void process(LongWritable key, Mapper.Context context) {
        context.write(outputKey, event)
    }

    abstract String selectOutputKey(BlueEvent event, Mapper.Context context) ;

    @Override
    protected void setup(Mapper.Context context) {
        super.setup(context)
    }

    @Override
    protected void cleanup(Mapper.Context context) {
        super.cleanup(context)
    }


}