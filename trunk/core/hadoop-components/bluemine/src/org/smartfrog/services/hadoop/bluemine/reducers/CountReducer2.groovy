package org.smartfrog.services.hadoop.bluemine.reducers

import org.apache.hadoop.io.IntWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer

/**
 * Reduce int count to more ints; very good for intermediate merges too.
 */
class CountReducer2 extends Reducer {
  def iw = new IntWritable()

  def reduce(Text k,
              Iterable values,
              Reducer.Context ctx) {
    def sum = values.collect() {it.get() }.sum()
    iw.set(sum)
    ctx.write(k, iw);
  }
}
