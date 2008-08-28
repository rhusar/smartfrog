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
package org.smartfrog.services.xunit.serial;

import org.smartfrog.services.xunit.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is information about a test that is sent over the wire.
 *
 * One of the problems here is that even 'success' can be qualified, so that Prolog unit tests can
 * say 'suceeded, but with multiple solutions', or a timed test could have 'succeeded out of time'
 */

public final class TestInfo implements Serializable, Cloneable {

    /**
     * session info to use when forwarding
     * @serial
     */
    private String sessionID;

    /**
     * text from Test.toString();
     *
     * @serial
     */
    private String text;

    /**
     * A description of the test from any description attribute/metadata
     * @serial
     */
    private String description;

    /**
     * A URL to give the test; such as a link back to a defect.
     */
    private String url;

    /**
     * A list of tags to file the test
     * @serial
     */
    private ArrayList<String> tags=new ArrayList<String>();

    /**
     * name of the test
     *
     * @serial
     */
    private String name;

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
     * IP Address
     * @serial 
     */
    private String ipaddr;

    /**
     * name of the process on which the test ran
     *
     * @serial
     */
    private String processName;

    /**
     * fault information. May be null; may contain multiple nested faults
     *
     * @serial
     */
    private ThrowableTraceInfo fault;

    /**
     * an array of messages, type LogEntry
     * @serial
     */
    private ArrayList<LogEntry> messages=new ArrayList<LogEntry>();

    /**
     * The outcome of the test. This can be one of the outcomes, or
     * something else
     * @serial 
     */
    private String outcome=OUTCOME_SUCCESS;

    public static final String OUTCOME_SUCCESS = "success";
    public static final String OUTCOME_FAILURE = "failure";
    public static final String OUTCOME_ERROR = "error";
    public static final String OUTCOME_TIMEOUT = "timeout";
    public static final String OUTCOME_SKIPPED = "skipped";
    public static final String OUTCOME_INTERRUPTED = "interrupted";
    public static final String OUTCOME_UNKNOWN = "unknown";

    /**
     * empty constructor is used during deserialization
     */

    public TestInfo() {
    }

    /**
     * import the test information if something went wrong
     * This also sets up the hostname and ip addr to the local values
     * @param fault something that caused a failure
     */
    public TestInfo(Throwable fault) {
        init();
        if (fault != null) {
            addFaultInfo(fault);
        }
    }

    /**
     * initialisation extracts the hostname from the localhost
     */
    public void init() {
        hostname = Utils.getHostname();
        ipaddr = Utils.getIpAddr();
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

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public boolean isSuccess() {
        return OUTCOME_SUCCESS.equals(outcome);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
     * get the duration of the call in milliseconds
     * @return the duration (start time - end time)
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


    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
     * clone the trace info; include cloning any child entries
     *
     * @return the cloned object
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        TestInfo cloned = (TestInfo) super.clone();
        if (fault != null) {
            cloned.fault = (ThrowableTraceInfo) cloned.fault.clone();
        }
        cloned.tags = new ArrayList<String>(tags);
        cloned.messages=new ArrayList<LogEntry>(messages);
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
            //should be utterly impossible
            //but we turn into a runtime exception, just in case :)
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return getTitle();
    }

    /**
     * get the classname.text of this test
     *
     * @return the title of this test
     */
    public String getTitle() {
        return getName() + "." + getText();
    }

    /**
     * Get the list of messages, all of type LogEntry
     * @return a list
     */
    public List<LogEntry> getMessages() {
        return messages;
    }


    /**
     * add an entry to this test
     * @param entry entry to add
     */
    public void log(LogEntry entry) {
        messages.add(entry);
    }

    /**
     * Create a skipped test message with the current start time/end time
     * @param name the name of the test
     * @return a skipped test
     */
    public static TestInfo skipped(String name) {
        TestInfo info=new TestInfo();
        info.setName(name);
        info.markStartTime();
        info.setEndTime(info.getStartTime());
        info.setOutcome(OUTCOME_SKIPPED);
        return info;
    }
}
