/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

package org.smartfrog.services.quartz.collector;

import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.quartz.JobImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A compound to collect data from a source and convert into some other form.
 */
public class CollectorImpl extends PrimImpl implements Prim, Collector {
    // add references for color, pencil widths
    protected DataSource source;
    int resultData;


   private boolean  shouldTerminate = true;
    String name;

    /**
     * Overwrite this method in any case : it turns the value you get into the
     * value you want. Typically this is the place to convert a value into
     * properly scaled pixels for a display, or to compute an average, etc...
     */
    int currentCollected = 0;
    String hostname="";
    Log log = LogFactory.getLog(CollectorImpl.class);

    public CollectorImpl() throws RemoteException {
    }

    /**
     * Deploy phase : deplou children, initialize graph
     *
     * @throws SmartFrogException DOCUMENT ME!
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        hostname = sfResolve("hostname", hostname, true);
        name = sfCompleteName().toString();
    }

    /**
     * Start phase : begin value collection here
     *
     * @throws SmartFrogException DOCUMENT ME!
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        log.info(name +  "starting collector");

        try {
            startCollection();
        } catch (Exception e) {
            if (!(e instanceof SmartFrogException)) {
                throw new SmartFrogException("Exception in sfStart of Collector component",
                    e, this);
            }
        }

        log.info(name +  "finished collecting");
        // check if it should terminate by itself
            if(shouldTerminate) {
                log.info("Normal termination :" + sfCompleteNameSafe());
                TerminationRecord termR = new TerminationRecord("normal",
                "Collection finished: ",sfCompleteName());
                TerminatorThread terminator = new TerminatorThread(this,termR);
                terminator.start();
            }
    }




    /**
     * Start the collection of data here : reset the vector, get the source and
     * start the polling thread.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void startCollection() throws Exception {
        // fill the value vector with zeros;
   //     for (int i = 0; i < numberOfSamples; i++) {
     //       allValues.addElement(new Integer(0));
      //  }

        // collect data source
        source = (DataSource) sfResolve(DATASOURCE, true);

        getData();

    }



    /**
     * Termination hook : close threads & windows
     *
     * @param tr DOCUMENT ME!
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {

        super.sfTerminateWith(tr);
    }

    protected void convertData(int value) {
       // allValues.removeElementAt(0);
        //values are added at the end
        JobImpl.allValues.put(hostname, new Integer(value));
        log.info("Value for source  " + source.toString() + "======" + value);
        resultData = value;

    }

    /**
     * Overwrite this function if you're accessing a given source of data.
     */
    protected void getData() {
        try {
            convertData(source.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getResult() {
        return resultData;
    }
}
