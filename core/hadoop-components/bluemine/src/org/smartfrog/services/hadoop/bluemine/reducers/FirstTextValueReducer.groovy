package org.smartfrog.services.hadoop.bluemine.reducers

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent

/**
 * Return the first text value from a list of them. Good for generic output of a single value
 * Input (key, text+) -> (key, text[0])
 */
class FirstTextValueReducer extends Reducer<Text, Text, Text, Text> {

    Text out = new Text()

    void reduce(Text key,
                Iterable<Text> values,
                Reducer.Context context) {
        Text value = values.iterator().next()
        context.write(key, value);
    }

}
