package org.smartfrog.services.hadoop.bluemine.reducers

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent

/**
 * This emits an event as serialised events
 * TODO
 */
class EventEmitReducer extends Reducer<Text, BlueEvent, Text, IntWritable> {

    IntWritable iw = new IntWritable()

    void reduce(Text key,
                Iterable<BlueEvent> values,
                Reducer.Context context) {
        int sum = (int) (values.collect() {event -> 1 }.sum())
        iw.set(sum)
        context.write(key, iw);
    }

}
