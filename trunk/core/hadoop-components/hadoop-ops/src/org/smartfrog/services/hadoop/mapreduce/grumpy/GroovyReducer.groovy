package org.smartfrog.services.hadoop.mapreduce.grumpy

import org.apache.hadoop.mapreduce.Reducer

/**
 * A generic Groovy reducer. All Java generic information is stripped
 */
class GroovyReducer extends Reducer {

    @Override
    void reduce(key,
                Iterable values,
                Reducer.Context context) {
    }
}
