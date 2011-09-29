/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.services.hadoop.mapreduce.terasort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * An input format that assigns ranges of longs to each mapper.
 */
@SuppressWarnings({"deprecation"})

public class TeraGenRangeInputFormat 
     implements InputFormat<LongWritable, NullWritable> {
  
  /**
   * An input split consisting of a range on numbers.
   */
  static class RangeInputSplit implements InputSplit {
    long firstRow;
    long rowCount;

    public RangeInputSplit() { }

    public RangeInputSplit(long offset, long length) {
      firstRow = offset;
      rowCount = length;
    }

    public long getLength() throws IOException {
      return 0;
    }

    public String[] getLocations() throws IOException {
      return new String[]{};
    }

    public void readFields(DataInput in) throws IOException {
      firstRow = WritableUtils.readVLong(in);
      rowCount = WritableUtils.readVLong(in);
    }

    public void write(DataOutput out) throws IOException {
      WritableUtils.writeVLong(out, firstRow);
      WritableUtils.writeVLong(out, rowCount);
    }
  }
  
  /**
   * A record reader that will generate a range of numbers.
   */
  static class RangeRecordReader 
        implements RecordReader<LongWritable, NullWritable> {
    long startRow;
    long finishedRows;
    long totalRows;

    public RangeRecordReader(RangeInputSplit split) {
      startRow = split.firstRow;
      finishedRows = 0;
      totalRows = split.rowCount;
    }

    public void close() throws IOException {
      // NOTHING
    }

    public LongWritable createKey() {
      return new LongWritable();
    }

    public NullWritable createValue() {
      return NullWritable.get();
    }

    public long getPos() throws IOException {
      return finishedRows;
    }

    public float getProgress() throws IOException {
      return finishedRows / (float) totalRows;
    }

    public boolean next(LongWritable key, 
                        NullWritable value) {
      if (finishedRows < totalRows) {
        key.set(startRow + finishedRows);
        finishedRows += 1;
        return true;
      } else {
        return false;
      }
    }
    
  }

  public RecordReader<LongWritable, NullWritable> 
    getRecordReader(InputSplit split, JobConf job,
                    Reporter reporter) throws IOException {
    return new RangeRecordReader((RangeInputSplit) split);
  }

  /**
   * Create the desired number of splits, dividing the number of rows
   * between the mappers.
   */
  public InputSplit[] getSplits(JobConf job, 
                                int numSplits) {
    long totalRows = TeraGenJob.getNumberOfRows(job);
    long rowsPerSplit = totalRows / numSplits;
    System.out.println("Generating " + totalRows + " using " + numSplits + 
                       " maps with step of " + rowsPerSplit);
    InputSplit[] splits = new InputSplit[numSplits];
    long currentRow = 0;
    for(int split=0; split < numSplits-1; ++split) {
      splits[split] = new RangeInputSplit(currentRow, rowsPerSplit);
      currentRow += rowsPerSplit;
    }
    splits[numSplits-1] = new RangeInputSplit(currentRow, 
                                              totalRows - currentRow);
    return splits;
  }

}
