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

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;

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
     * Serialize the fields of this object to <code>out</code>.
     *
     * @param out <code>DataOuput</code> to serialize this object into.
     * @throws IOException
     */
    public void write(DataOutput out) throws IOException {
        out.writeInt(attempts);
        out.writeInt(successes);
        out.writeInt(skips);
        out.writeInt(failures);
        JUnitMRUtils.writeText(out, name);
    }


    /**
     * Deserialize the fields of this object from <code>in</code>.
     *
     * <p>For efficiency, implementations should attempt to re-use storage in the existing object where possible.</p>
     *
     * @param in <code>DataInput</code> to deseriablize this object from.
     * @throws IOException
     */
    public void readFields(DataInput in) throws IOException {
        attempts = in.readInt();
        successes = in.readInt();
        skips = in.readInt();
        failures = in.readInt();
        name = JUnitMRUtils.readText(in);
    }


    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.
     *
     * we compare on name only
     */
    public int compareTo(TestSummary that) {
        return this.name.compareTo(that.name);
    }
}
