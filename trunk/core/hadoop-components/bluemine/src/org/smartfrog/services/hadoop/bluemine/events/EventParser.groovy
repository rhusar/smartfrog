package org.smartfrog.services.hadoop.bluemine.events

import java.text.SimpleDateFormat
import org.apache.hadoop.io.Text

/**
 * Parse strings like
 * <pre>
 *   Gate, dev, ?, day, time, id
 *   gate1,02e73779c77fcd4e9f90a193c4f3e7ff,,2006-10-30,16:06:43,
 *   gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:06:54,Smiley
 *   gate1,f1191b79236083ce59981e049d863604,,2006-10-30,16:06:57,vklaptop
 * </pre>
 */
class EventParser {
    static final int FIELD_GATE = 0
    static final int FIELD_DEV = 1
    static final int FIELD_DURATION = 2
    static final int FIELD_DATE = 3
    static final int FIELD_TIME = 4
    static final int FIELD_NAME = 5
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'Z'HH:mm:ss",
            Locale.ENGLISH)

    boolean parseDatestamp = true;

    /**
     * Parse a text line
     * @param line
     * @return
     */
    BlueEvent parse(Text line) {
        return parse(line.toString())
    }

    BlueEvent parse(String line) {
        BlueEvent event = new BlueEvent();
        parse(event, line, "")
        return event;
    }

    /**
     * Parse a line into a blueevent, 
     * @param event
     * @param line
     */
    public void parse(BlueEvent event, String line, String context) {
        String[] fields = line.split(",")
        if (fields.length < 5) {
            throw new IOException("Only ${fields.length} fields in \"${line}\" $context")
        }
        try {
            event.device = trim(fields[FIELD_DEV]);
            event.gate = trim(fields[FIELD_GATE]);
            event.datestamp = buildDate(fields[FIELD_DATE], fields[FIELD_TIME])
            String durationField = trim(fields[FIELD_DURATION]);
            event.duration = durationField != null ? Integer.parseInt(durationField) : 0
            event.name = fields.length > FIELD_NAME ? trim(fields[FIELD_NAME]) : ""
        } catch (Exception e) {
            throw new IOException("When parsing \"${line}\": $e $context", e)
        }
    }

    void parse(BlueEvent event, Text line, String context) {
        parse(event, line.toString(), context)
    }



    static String trim(String src) {
        return src ? src.trim() : null;
    }


    Date buildDate(String day, String time) {
        return parseDatestamp ? dateFormat.parse(trim(day) + "Z" + trim(time)) : null
    }


}
