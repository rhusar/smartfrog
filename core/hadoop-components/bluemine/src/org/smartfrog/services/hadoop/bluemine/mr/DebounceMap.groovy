package org.smartfrog.services.hadoop.bluemine.mr

import groovy.util.logging.Commons
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.BluemineOptions
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.events.EventWindow

/**
 * sliding window debouncing of bluetooth events
 */
@Commons
class DebounceMap extends AbstractBlueMapper {

    EventWindow window

    @Override
    protected void setup(final Mapper.Context context) {
        super.setup(context)
        window = new EventWindow(windowDuration: BluemineOptions.DEBOUNCE_WINDOW)
    }
/**
 * Output key is (year, month, day,gate). For any specific day, the ordering is handled by the reducer
 * @param event event date
 * @param context context
 * @return
 */
    @Override
    String selectOutputKey(final BlueEvent event, final Mapper.Context context) {
        return dateKey(event)
    }

    protected String dateKey(BlueEvent event) {
        Date date = event.datestamp
        return date.year + "-" + date.month + "-" + date.day + "@" + event.gate
    }

    @Override
    void process(final LongWritable key, final Mapper.Context context) {
        log.debug("Event $event")
        BlueEvent ev2 = window.insert(event)
        log.debug("Inserted $event")
        List<BlueEvent> expired = window.purgeExpired(event)
        if (!expired.empty) {
            log.debug("${expired.size()} events leaving window: $expired")
        }
        expired.each {event ->
            emit(context, event)
        }
    }

    @Override
    protected void cleanup(final Mapper.Context context) {
        //end of run, emit everything left in the window
        log.debug("Cleanup - $window.size events to emit")
        window.each { event ->
            log.debug("Emitting $event")
            emit(context, event)
        }

        super.cleanup(context)
    }

    protected void emit(Mapper.Context context, BlueEvent event) {
        String key = dateKey(event)
        outputKey.set(key)
        context.write(outputKey, event)
    }


}
