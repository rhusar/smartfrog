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

package org.smartfrog.examples.orchdws.gui.graphpanel;

import java.awt.Dimension;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


/**
 * A compound to collect data from a source and convert into some other form.
 */
public class TideMeterImpl extends GraphImpl implements TideMeter, Compound, Runnable {
    /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog(this.getClass());

    // the tide mark pane
    TideMarkPanel tmp;
    int maxResetPeriod;
    int minResetPeriod;
    int lowerThresholdValue;
    int maxThreshold;
    int minThreshold;
    int countUnchangedMax = 0;
    int countUnchangedMin = 0;
    boolean firstThreshold = true;

    public TideMeterImpl() throws RemoteException {
    }

    public void collectGraphData() throws Exception {
        super.collectGraphData();
        maxResetPeriod = sfResolve(MAXRESETPERIOD, 25, false);
        minResetPeriod = sfResolve(MINRESETPERIOD, 50, false);
        lowerThresholdValue = sfResolve(LOWERTHRESHOLDVALUE, minY + 1, false);
    }

    /**
     * Create a specific graph panel to display the data from this data source
     *
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GraphPanel createGraphPanel(Dimension size) {
        tmp = new TideMarkPanel(size);

        return initPanel(tmp);
    }

    /**
     * Check mion & max each time you're accesing data source of data.
     */
    protected void getData() {
        try {
            int lastValue = source.getData();

            if (firstThreshold && (lastValue > lowerThresholdValue)) {
                tmp.bars = true;
                maxThreshold = lastValue;
                tmp.setUpperThreshold(maxThreshold);
                minThreshold = lastValue;
                tmp.setLowerThreshold(minThreshold);
                firstThreshold = false;
            }

            if (lastValue > maxThreshold) {
                maxThreshold = lastValue;
                tmp.setUpperThreshold(maxThreshold);
            } else {
                if (countUnchangedMax++ > maxResetPeriod) {
                    maxThreshold = lastValue;
                    tmp.setUpperThreshold(maxThreshold);
                    countUnchangedMax = 0;
                }
            }

            if (lastValue < minThreshold) {
                minThreshold = lastValue;
                tmp.setLowerThreshold(minThreshold);
            } else {
                if (countUnchangedMin++ > minResetPeriod) {
                    minThreshold = lastValue;
                    tmp.setLowerThreshold(minThreshold);
                    countUnchangedMin = 0;
                }
            }

            convertData(lastValue);
        } catch (Exception e) {
            if (sfLog.isErrorEnabled()) sfLog.error (e);
        }
    }
}
