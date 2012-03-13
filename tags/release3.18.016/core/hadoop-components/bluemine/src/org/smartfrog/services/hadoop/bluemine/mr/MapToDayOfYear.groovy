package org.smartfrog.services.hadoop.bluemine.mr

import org.apache.hadoop.mapreduce.Mapper
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent

/**
 * Map to a day of the week. This mapper supports an offset day, defined by
 * {@link BluemineOptions#OPT_HOUR_DAY_STARTS}; this allows the day to begin
 * at, say 3cd for 3 am; all events before that are deemed to belong to the previous day.
 *
 */
class MapToDayOfYear extends MapToHour {

    final GregorianCalendar cal = new GregorianCalendar()

    @Override
    protected void setup(Mapper.Context context) {
        super.setup(context)
    }

    @Override
    String selectOutputKey(BlueEvent event, Mapper.Context context) {
        Date date = event.datestamp
        cal.time = date
        int dayOfYear = cal.get(GregorianCalendar.DAY_OF_YEAR)
        return dayOfYear
    }


}
