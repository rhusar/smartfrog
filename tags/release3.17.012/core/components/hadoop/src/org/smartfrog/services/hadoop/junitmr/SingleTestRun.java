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

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import org.apache.hadoop.io.ThrowableWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Result of a single test run, and the listener for the test run itself. Fields marked as transient are used during the
 * test run, and not serialized
 */
public class SingleTestRun implements Writable, TestListener {
    public boolean skipped;
    public boolean succeeded;
    public long testStarted, testFinished;
    public String name;
    public String text;
    public ThrowableWritable thrown;


    /**
     * Simple constructor
     */
    public SingleTestRun() {
    }

    /**
     * {@inheritDoc}
     *
     * @param out <code>DataOuput</code> to serialize this object into.
     * @throws IOException IO trouble
     */
    public void write(DataOutput out) throws IOException {
        out.writeUTF(name == null ? "" : name);
        out.writeBoolean(skipped);
        out.writeBoolean(succeeded);
        out.writeLong(testStarted);
        out.writeLong(testFinished);
        out.writeUTF(text == null ? "" : text);
        JUnitMRUtils.writeText(out, text);
        boolean hasThrown = thrown != null;
        out.writeBoolean(hasThrown);
        if (hasThrown) {
            thrown.write(out);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param in <code>DataInput</code> to deseriablize this object from.
     * @throws IOException IO trouble
     */
    public void readFields(DataInput in) throws IOException {
        name = in.readUTF();
        skipped = in.readBoolean();
        succeeded = in.readBoolean();
        testStarted = in.readLong();
        testFinished = in.readLong();
        text = in.readUTF();
        boolean hasThrown = in.readBoolean();
        if (hasThrown) {
            if (thrown != null) {
                thrown = new ThrowableWritable();
            }
            thrown.readFields(in);
        }
    }

    /**
     * A test started.
     */
    public void startTest(Test test) {
        name = test.toString();
        testStarted = System.currentTimeMillis();
    }

    /**
     * A test ended.
     */
    public void endTest(Test test) {
        testFinished = System.currentTimeMillis();
    }

    /**
     * An error occurred.
     */
    public void addError(Test test, Throwable t) {
        succeeded = false;

    }

    /**
     * A failure occurred.
     */
    public void addFailure(Test test, AssertionFailedError t) {
        addError(test, t);
    }

}
