package org.smartfrog.services.hadoop.bluemine.mr

import groovy.util.logging.Commons
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.events.EventParser

/**
 * Be aware that Groovy's semantics of inner classes is very different from Java's (esp. when it comes
 * to access to outer fields from the inner class; this can cause confusion in the compilers and IDEs
 * when it comes to referring to the nested Context object
 */
@Commons
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
        try {
            parser.parse(event, value, "at offset " + key)
        } catch (IOException ioe) {
            log.warn(ioe);
            context.getCounter("bluemine", "input errors").increment(1)
            return;
        }
        String outkey = selectOutputKey(event, context)
        if (outkey == null) {
            log.warn("Null output key parsing \"" + value + "\"");
            context.getCounter("bluemine", "key errors").increment(1)
        } else {
            outputKey.set(outkey)
            process(key, context)
        }
    }

    /**
     * When invoked , event is the current event, outputKey is set to the Text to write
     * @param key
     * @param context
     */
    void process(LongWritable key, Mapper.Context context) {
        context.write(outputKey, event)
    }

    /**
     * Select the output key
     * @param event
     * @param context
     * @return
     */
    abstract String selectOutputKey(BlueEvent event, Mapper.Context context);

    @Override
    protected void setup(Mapper.Context context) {
        super.setup(context)
    }

    @Override
    protected void cleanup(Mapper.Context context) {
        super.cleanup(context)
    }


}