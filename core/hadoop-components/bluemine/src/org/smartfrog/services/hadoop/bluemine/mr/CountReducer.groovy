package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent

/**
 * Reduce int count to more ints; very good for intermediate merges too.
 */
class CountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    IntWritable iw = new IntWritable()

    void reduce(Text key,
                Iterable<IntWritable> values,
                Reducer.Context context) {
        int sum = (int)(values.collect() {it.get() }.sum())
        iw.set(sum)
        context.write(key, iw);
    }

}
