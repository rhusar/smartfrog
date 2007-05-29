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

package org.smartfrog.examples.dynamicwebserver.thresholder;

import java.awt.Dimension;
import java.rmi.RemoteException;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.Graph;
import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphImpl;
import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;


/**
 * This sfgraph component will try to display the graph indicated by the
 * dataSource. It will also create an AdjustableThresholdPanel to control the
 * upper and lower threshold values of its SmartFrog parent ThresholderImpl.
 */
public class DisplayThresholderImpl extends GraphImpl implements Graph,
    DisplayThresholder, Compound {
    // the managed Thresholder
    Thresholder thresholdControl = null;
    boolean bars = false;

    /**
     * Default rmi constructor
     *
     * @throws Exception DOCUMENT ME!
     */
    public DisplayThresholderImpl() throws Exception {
    }

    /**
     * Deployment phase : get the thresholder, and initialize the graph
     *
     * @throws SmartFrogException DOCUMENT ME!
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        // the parent has to be a Thresholder,
        // Could be changed to resolve a link
        thresholdControl = (Thresholder) sfResolve(THRESHOLDER,
                new ThresholderImpl(), true);
        bars = sfResolve(BARS, false, false);

        // the deploy phase initializes the graph
        super.sfDeploy();
    }

    /**
     * Create a different graphpanel : AdjustableThresholdPanel will contain
     * two slide bars
     *
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GraphPanel createGraphPanel(Dimension size) {
        // pass the thresholder to this panel
        AdjustableThresholdPanel atp = new AdjustableThresholdPanel(size,
                thresholdControl);
	atp.bars = bars;
        return initPanel(atp);
    }
}
