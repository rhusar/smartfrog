package org.smartfrog.services.hadoop.bluemine.mr

import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer

class EventCountReducer extends Reducer<Text, BlueEvent, Text, IntWritable> {

    IntWritable iw = new IntWritable()

    void reduce(Text key,
                Iterable<BlueEvent> values,
                Reducer.Context context) {
        int sum = (values.collect() {event -> 1 }.sum())
        iw.set(sum)
        context.write(key, iw);
    }

}
