package org.smartfrog.services.hadoop.bluemine.events

import org.apache.hadoop.io.Writable

/**
 * Events are parseable and writeable
 */
class BlueEvent implements Writable {

    String device
    String gate
    String name
    Date datestamp
    long duration

    /**
     * the empty date
     */
    static final Epoch = new Date(0)

    BlueEvent() {
        reset()
    }

    /**
     * Reset the event for re-use; sets everything to non null
     * empty values
     */
    void reset() {
        device = ""
        gate = ""
        name = ""
        datestamp = Epoch
        duration = 0
    }

    /**
     * This generates an extended value
     * @return
     */
    @Override
    String toString() {
        return "${gate}, ${device},$duration,${datestamp},${name},"
    }

    @Override
    void write(DataOutput out) {
        out.writeUTF(device)
        out.writeUTF(gate)
        out.writeUTF(name)
        out.writeLong(datestamp.time)
        out.writeLong(duration)
    }

    @Override
    void readFields(DataInput src) throws IOException {
        device = src.readUTF()
        gate = src.readUTF()
        name = src.readUTF()
        datestamp = new Date(src.readLong());
        duration = src.readLong()
    }

    long getEndtime() {
        duration + datestamp.time
    }
}
