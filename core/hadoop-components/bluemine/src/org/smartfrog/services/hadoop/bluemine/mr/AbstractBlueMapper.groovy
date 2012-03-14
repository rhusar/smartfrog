package org.smartfrog.services.hadoop.bluemine.mr

import groovy.util.logging.Commons
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.BluemineOptions
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

    protected Text inputLine

    /**
     * Parse and emit events
     * @param key line #
     * @param value raw line
     * @param context ctx
     */
    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) {
        inputLine = value
        try {
            parser.parse(event, value, "at offset " + key)
        } catch (IOException ioe) {
            log.warn(ioe);
            context.getCounter("bluemine", "input errors").increment(1)
            return;
        }
        process(context, key, value, event)
    }

    /**
     * Base process operation
     * @param context context
     * @param lineNo line number
     * @param line ext itself
     * @param event the already parsed event
     */
    void process(Mapper.Context context, LongWritable lineNo, Text line, BlueEvent event) {
        String outkey = selectOutputKey(event, context)
        if (outkey == null) {
            log.warn("Null output key parsing \"" + line + "\"");
            context.getCounter("bluemine", "key errors").increment(1)
        } else {
            outputKey.set(outkey)
            process(lineNo, context)
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
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        return event.device
    }

}