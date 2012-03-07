package org.smartfrog.services.hadoop.bluemine.utils

import org.smartfrog.services.hadoop.grumpy.GrumpyJob

class JobKiller implements Runnable {

    private GrumpyJob target


    @Override
    void run() {
        target.kill()
    }

    static JobKiller targetForTermination(GrumpyJob job) {
        JobKiller killer = new JobKiller(target:job)
        killer;
    }

}
