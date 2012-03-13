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
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

/**
 * The Mapper class that given a row number, will generate the appropriate output line.
 */
@SuppressWarnings({"deprecation"})

public class TeraGenMapper extends MapReduceBase
        implements Mapper<LongWritable, NullWritable, Text, Text> {

    private Text key = new Text();
    private Text value = new Text();
    private TeraGenRandomGenerator rand;
    private byte[] keyBytes = new byte[12];
    private byte[] spaces = "          ".getBytes();
    private byte[][] filler = new byte[26][];

    {
        for (int i = 0; i < 26; ++i) {
            filler[i] = new byte[10];
            for (int j = 0; j < 10; ++j) {
                filler[i][j] = (byte) ('a' + i);
            }
        }
    }

    /**
     * Add a random key to the text
     *
     * @param rowId
     */
    private void addKey() {
        for (int i = 0; i < 3; i++) {
            long temp = rand.next() / 52;
            keyBytes[3 + 4 * i] = (byte) (' ' + (temp % 95));
            temp /= 95;
            keyBytes[2 + 4 * i] = (byte) (' ' + (temp % 95));
            temp /= 95;
            keyBytes[1 + 4 * i] = (byte) (' ' + (temp % 95));
            temp /= 95;
            keyBytes[4 * i] = (byte) (' ' + (temp % 95));
        }
        key.set(keyBytes, 0, 10);
    }

    /**
     * Add the rowid to the row.
     *
     * @param rowId
     */
    private void addRowId(long rowId) {
        byte[] rowid = Integer.toString((int) rowId).getBytes();
        int padSpace = 10 - rowid.length;
        if (padSpace > 0) {
            value.append(spaces, 0, 10 - rowid.length);
        }
        value.append(rowid, 0, Math.min(rowid.length, 10));
    }

    /**
     * Add the required filler bytes. Each row consists of 7 blocks of 10 characters and 1 block of 8 characters.
     *
     * @param rowId the current row number
     */
    private void addFiller(long rowId) {
        int base = (int) ((rowId * 8) % 26);
        for (int i = 0; i < 7; ++i) {
            value.append(filler[(base + i) % 26], 0, 10);
        }
        value.append(filler[(base + 7) % 26], 0, 8);
    }

    public void map(LongWritable row, NullWritable ignored,
                    OutputCollector<Text, Text> output,
                    Reporter reporter) throws IOException {
        long rowId = row.get();
        if (rand == null) {
            // we use 3 random numbers per a row
            rand = new TeraGenRandomGenerator(rowId * 3);
        }
        addKey();
        value.clear();
        addRowId(rowId);
        addFiller(rowId);
        output.collect(key, value);
    }

}
