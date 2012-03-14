package org.smartfrog.services.hadoop.bluemine.events

class OrderByTime implements Comparator<BlueEvent> {

    @Override
    int compare(final BlueEvent o1, final BlueEvent o2) {
        o2.starttime - o1.starttime
    }
}
