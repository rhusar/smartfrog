package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.BluemineOptions
import org.smartfrog.services.hadoop.bluemine.events.EventParser
import groovy.util.logging.Commons

/**
 * sliding window debouncing of bluetooth events
 */
@Commons
class DebounceMap extends AbstractBlueMapper {

    protected EventParser parser = new EventParser()
    protected Text outputKey = new Text()
    protected BlueEvent event = new BlueEvent()
    protected Text inputLine
    long windowDuration = 60

    //use a linked list here on account of the many deletions off the head that are planned.
    List<BlueEvent> window = new LinkedList<BlueEvent>()

    private BlueEvent eventInWindow(BlueEvent event) {
        String devID = event.device
        String gate = event.gate
        BlueEvent found = window.find { it.device == devID && it.gate == gate }
        return found
    }

    /**
     * Output key is (year, month, day,gate). For any specific day, the ordering is handled by the reducer
     * @param event event date
     * @param context context
     * @return
     */
    @Override
    String selectOutputKey(final BlueEvent event, final Mapper.Context context) {
        Date date = event.datestamp
        return date.year + "-" + date.month + "-" + date.day + "@" + event.gate
    }

    @Override
    void process(final LongWritable key, final Mapper.Context context) {
        BlueEvent liveEvent = eventInWindow(event)
        if (liveEvent) {
            //event in the window
            //add its duration to the current event
            liveEvent.merge(event)
        } else {
            //no ongoing event, add a clone of it (remember, events get re-used, so a clone is mandatory)
            window.add(event.clone());
        }

        //now go through the window and emit then purge those that are out of range, that is their end time falls before
        //the window duration
        long closingtime = event.endtime - windowDuration
        ListIterator<BlueEvent> windowView = window.listIterator()
        while(windowView.hasNext()) {
            BlueEvent windowEvent = windowView.next()
            if (windowEvent.endtime < closingtime) {
                //exiting the window: write it then remove it
                context.write(outputKey, windowEvent)
                windowView.remove()
            }
        }
    }

    @Override
    protected void cleanup(final Mapper<LongWritable, Text, Text, BlueEvent>.Context context) {
        //end of run, emit everything left in the window with the last output key
        if (outputKey!=null) {
            window.each {
                context.write(outputKey, it)
            }
        }

        super.cleanup(context)
    }


}
