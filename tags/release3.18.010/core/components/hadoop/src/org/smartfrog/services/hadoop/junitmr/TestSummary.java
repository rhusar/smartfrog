/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.hadoop.junitmr;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created 17-Mar-2009 14:00:23
 */
public class TestSummary implements WritableComparable<TestSummary> {

    public int attempts;
    public int successes;
    public int skips;
    public int failures;
    public String name;


    /**
     * {@inheritDoc}
     *
     * @param out <code>DataOuput</code> to serialize this object into.
     * @throws IOException IO trouble
     */
    public void write(DataOutput out) throws IOException {
        out.writeInt(attempts);
        out.writeInt(successes);
        out.writeInt(skips);
        out.writeInt(failures);
        out.writeUTF(name);
    }


    /**
     * {@inheritDoc}
     *
     * @param in <code>DataInput</code> to deseriablize this object from.
     * @throws IOException IO trouble
     */
    public void readFields(DataInput in) throws IOException {
        attempts = in.readInt();
        successes = in.readInt();
        skips = in.readInt();
        failures = in.readInt();
        name = in.readUTF();
    }


    /**
     * {@inheritDoc} we compare on name only
     */
    public int compareTo(TestSummary that) {
        return this.name.compareTo(that.name);
    }
}
