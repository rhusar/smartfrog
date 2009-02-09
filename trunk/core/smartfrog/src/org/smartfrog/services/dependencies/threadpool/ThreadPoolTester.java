package org.smartfrog.services.dependencies.threadpool;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: pcg
 * Date: 23-Nov-2005
 * Time: 14:50:40
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPoolTester extends PrimImpl implements Prim, Runnable {
    public ThreadPoolTester() throws RemoteException {
    }

    ThreadPool tp;
    Thread t;
    int count = 0;

    private class Job implements Runnable {
        int i;
        public Job(int i){
            this.i = i;
        }

        public void run() {
            System.out.println("job " + i + " running");
            try{
                Thread.sleep(2000);
            } catch (Exception e){
            }
            System.out.println("job " + i + " done");
        }
    }

    public void sfDeploy() throws RemoteException, SmartFrogException {
        super.sfDeploy();
        tp = (ThreadPool) sfResolve("threadPool");
    }

    public void sfStart() throws RemoteException, SmartFrogException {
        super.sfStart();
        t = new Thread(this);
        t.start();
    }

    public void sfTerminateWith(TerminationRecord tr) {
        count = 1000;
        t.interrupt();
    }

    public void run() {
        try {
            int i = 0;
            while (i++ < 10) {
                int j = 0;
                while (j++ < 30) {
                    Thread.sleep(100);
                    tp.addToQueue(new Job(count++));
                }
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            // done
        }
    }
}
