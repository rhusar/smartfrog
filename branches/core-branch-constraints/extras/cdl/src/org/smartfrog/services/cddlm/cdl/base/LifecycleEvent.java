package org.smartfrog.services.cddlm.cdl.base;

import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple class used to pass events around. Extra information may be
 * added to the data map.
 */
public final class LifecycleEvent implements Serializable,Cloneable {

    private Date timestamp;

    private LifecycleStateEnum state=LifecycleStateEnum.undefined;

    private String info;

    private TerminationRecord record;

    private HashMap<String, Serializable> data=new HashMap<String, Serializable>();

    public LifecycleEvent() {
    }

    public LifecycleEvent(LifecycleStateEnum state, String info) {
        this.state = state;
        this.info = info;
        timestamp = new Date();
    }


    public LifecycleEvent(TerminationRecord record) {
        state=LifecycleStateEnum.terminated;
        info="";
        timestamp = new Date();
        this.record = record;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public LifecycleStateEnum getState() {
        return state;
    }

    public void setState(LifecycleStateEnum state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public TerminationRecord getRecord() {
        return record;
    }

    public void setRecord(TerminationRecord record) {
        this.record = record;
    }


    public HashMap<String, Serializable> getData() {
        return data;
    }

    public void setData(HashMap<String, Serializable> data) {
        this.data = data;
    }

    /**
     * shallow clone; the hashmap of data is not cloned
     * @return a cloned object
     * @throws CloneNotSupportedException
     */
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    public String toString() {
        return timestamp.toString()+": "+state+": "
                +(info!=null?info:"")+"\n"
                +(record!=null?record.toString():"");
    }
}
