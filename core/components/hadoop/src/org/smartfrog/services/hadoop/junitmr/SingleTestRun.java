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

import org.apache.hadoop.io.Writable;
import junit.framework.TestListener;
import junit.framework.Test;
import junit.framework.AssertionFailedError;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;

/**
 * Result of a single test run, and the listener for the test run itself.
 * Fields marked as transient are used during the test run, and not serialized
 */
public class SingleTestRun implements Writable, TestListener {
    public boolean skipped;
    public boolean succeeded;
    public String text = "";


    public SingleTestRun() {
    }

    /**
     * Serialize the fields of this object to <code>out</code>.
     *
     * @param out <code>DataOuput</code> to serialize this object into.
     * @throws IOException
     */
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(skipped);
        out.writeBoolean(succeeded);
        JUnitMRUtils.writeText(out, text);
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
        skipped = in.readBoolean();
        succeeded = in.readBoolean();
        text = JUnitMRUtils.readText(in);
    }

    /**
     * An error occurred.
     */
    public void addError(Test test, Throwable t) {

    }

    /**
     * A failure occurred.
     */
    public void addFailure(Test test, AssertionFailedError t) {
         
    }

    /**
     * A test ended.
     */
    public void endTest(Test test) {
   

    }

    /**
     * A test started.
     */
    public void startTest(Test test) {

    }

}
