/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit;

import junit.framework.Test;

import java.io.Serializable;

/**
 * This is information about a test that is sent over the wire. created
 * 15-Apr-2004 13:15:32
 */

public class TestInfo implements Serializable, Cloneable {

    /**
     * session info to use when forwarding
     */
    private String sessionID;

    /**
     * text from Test.toString();
     *
     * @serial
     */
    private String text;

    /**
     * classname of the test
     *
     * @serial
     */
    private String classname;

    /**
     * UTC timestamp of when the test started
     *
     * @serial
     */
    private long startTime;

    /**
     * UTC timestamp of when the test ended
     *
     * @serial
     */
    private long endTime;

    /**
     * name of the host on which the test ran
     *
     * @serial
     */
    private String hostname;

    /**
     * fault information. May be null; may contain multiple nested faults
     *
     * @serial
     */
    private ThrowableTraceInfo fault;

    /**
     * empty constructor is used during deserialization
     */
    public TestInfo() {
    }

    /**
     * import the test information from a test
     *
     * @param test
     */
    public TestInfo(Test test) {
        init();
        extractTestInfo(test);
    }

    /**
     * import the test information if something went wrong
     *
     * @param test  a test that has run
     * @param fault something that caused a failure
     */
    public TestInfo(Test test, Throwable fault) {
        init();
        extractTestInfo(test);
        addFaultInfo(fault);
    }

    /**
     * initialisation extracts the hostname from the localhost
     */
    protected void init() {
        hostname = Utils.getHostname();
    }


    /**
     * fill in classname and text fields from a test class
     *
     * @param test
     */
    protected void extractTestInfo(Test test) {
        classname = test.getClass().getName();
        text = test.toString();
    }

    /**
     * extract the fault(s) from a throwable
     *
     * @param thrown something that was thrown
     */
    public void addFaultInfo(Throwable thrown) {
        assert thrown != null;
        fault = new ThrowableTraceInfo(thrown);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public void markStartTime() {
        setStartTime(System.currentTimeMillis());
    }

    public void markEndTime() {
        setEndTime(System.currentTimeMillis());
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * get the duration of the call
     */
    public long getDuration() {
        return endTime - startTime;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public ThrowableTraceInfo getFault() {
        return fault;
    }

    public void setFault(ThrowableTraceInfo fault) {
        this.fault = fault;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * test for a fault being contained;
     *
     * @return true if getFault returns a fault.
     */
    public boolean hasFault() {
        return getFault() != null;
    }

    /**
     * clone the trace info; include cloning any fault
     *
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone() throws CloneNotSupportedException {
        TestInfo cloned = (TestInfo) super.clone();
        if (fault != null) {
            cloned.fault = (ThrowableTraceInfo) cloned.fault.clone();
        }
        return cloned;
    }

    /**
     * cloning, without the possiblity of failing
     *
     * @return a duplicate instance
     */
    public TestInfo duplicate() {
        try {
            TestInfo cloned = (TestInfo) clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            //impossible
            //but we turn into a runtime exception
            throw new RuntimeException(e);
        }
    }

}
