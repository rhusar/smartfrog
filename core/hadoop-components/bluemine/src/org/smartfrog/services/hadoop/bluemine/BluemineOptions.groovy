package org.smartfrog.services.hadoop.bluemine

public interface BluemineOptions {

    String OPT_HOUR_DAY_STARTS = "bluemine.hour.day.starts"

    String OPT_DAY_WEEK_STARTS = "bluemine.day.week.starts"

    String DEFAULT_JOB_TRACKER = "localhost:54311"

    String DEFAULT_FS = "hdfs://localhost:54310"
}