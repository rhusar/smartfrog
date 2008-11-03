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
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;


/**
 * A compound to collect data from a source and convert into some other form.
 */
public class GraphImpl extends CompoundImpl implements Graph, Compound,
    Runnable {
    // add references for color, pencil widths
    protected DataSource source;
    protected String dataDisplayed = ""; // a title for the data displayed
    protected int minY;
    protected int minX;
    protected int maxX;
    protected int maxY;
    protected int stepX;
    protected int gridStepX;
    protected int gridStepY;
    protected int xOffset;
    protected int yOffset;
    protected int panelWidth;
    protected int panelHeight;
    protected String pencilColour;
    protected String frameTitle = "";
    float graphPencilWidth = 0.05f;

    // do we want to display the data ?
    protected boolean display = false;

    // do we want the graph to resize itself ?
    protected boolean adjust = false;

    // do we want bars or lines ?
    protected boolean histogram = false;

    boolean keysAllowed = false;

    // the frame to hold the display for this graph
    //  GraphFrame targetDisplay;
    public JFrame targetDisplay;

    // the position of this frame on the screen
    public String positionDisplay;

    // the panel to display this graph
    public GraphPanel gp;
    boolean collecting = false;

    // the collecting thread and its period.
    Thread collector = null;
    int pollingPeriod;

    // a vector to store the values
    public Vector allValues = new Vector();

    // the number of values
    protected int numberOfSamples;

    /**
     * Overwrite this method in any case : it turns the value you get into the
     * value you want. Typically this is the place to convert a value into
     * properly scaled pixels for a display, or to compute an average, etc...
     */
    int currentCollected = 0;

    public GraphImpl() throws RemoteException {
    }

    /**
     * Deploy phase : deplou children, initialize graph
     *
     * @throws SmartFrogException DOCUMENT ME!
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        try {
            initGraph();
        } catch (Exception e) {
            if (!(e instanceof SmartFrogException)) {
                throw new SmartFrogException("Exception in sfDeply of Graph component", e, this);
            }
        }
        if (sfLog().isDebugEnabled()) sfLog().debug("finished graph deployment");
    }

    /**
     * If a display has been requested, collect the variables and initialize
     * the graph panel
     *
     * @throws Exception DOCUMENT ME!
     */
    public void initGraph() throws Exception {
        display = sfResolve(DISPLAY, true, false);

        if (display) {
            collectGraphData();
            initializeGraph();
        }
    }

    /**
     * Start phase : begin value collection here
     *
     * @throws SmartFrogException DOCUMENT ME!
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (sfLog().isDebugEnabled()) sfLog().debug("starting graph");

        try {
            startCollection();
        } catch (Exception e) {
            if (!(e instanceof SmartFrogException)) {
                throw new SmartFrogException("Exception in sfStart of Graph component", e, this);
            }
        }
        if (sfLog().isDebugEnabled()) sfLog().debug("finished starting graph");
    }

    /**
     * Collect all data relevant to the graph display: number of samples to
     * display, axis min & max values, grid distances, panel size
     *
     * @throws Exception DOCUMENT ME!
     */
    public void collectGraphData() throws Exception {
        dataDisplayed = sfResolve(DATADISPLAYED, "", false);
        minX = sfResolve(MINX, 0, false);
        maxX = sfResolve(MAXX, 100, false);
        stepX = sfResolve(STEPX, 1, false);
        gridStepX = sfResolve(GRIDSTEPX, 10, false);
        minY = sfResolve(MINY, 0, false);
        maxY = sfResolve(MAXY, 100, false);
        gridStepY = sfResolve(GRIDSTEPY, 10, false);
        xOffset = sfResolve(XOFFSET, 10, false);
        yOffset = sfResolve(YOFFSET, 10, false);
        adjust = sfResolve(ADJUST, false, false);
        keysAllowed = sfResolve(KEYSALLOWED, false, false);
        panelWidth = sfResolve(PANELWIDTH, 300, false);
        panelHeight = sfResolve(PANELHEIGHT, 200, false);
        histogram = sfResolve(HISTOGRAM, false, false);
        pencilColour = sfResolve(PENCILCOLOUR, "0xFFFF00", false);
        positionDisplay = sfResolve(POSITIONDISPLAY, "NW", false);
        graphPencilWidth = sfResolve(GRAPHPENCILWIDTH, 5, false);
        frameTitle = sfResolve(FRAMETITLE, "Graph", false);
    }

    /**
     * Initialize all graphical data here
     */
    public void initializeGraph() {
        // create the target display frame and add the inner container panel
        targetDisplay = new JFrame(); //frameTitle);

        CompleteGraphPanel cgp = new CompleteGraphPanel(dataDisplayed,
                new Integer(minY).toString(), new Integer(maxY).toString());
        targetDisplay.getContentPane().add(cgp);

        // create the graph panel and initialize it properly
        gp = createGraphPanel(new Dimension(panelWidth, panelHeight));

        // add this panel to the  display frame
        cgp.addGraphPanel(gp);
        cgp.setSize(gp.getSize());
        targetDisplay.setSize(cgp.getSize());
        org.smartfrog.services.display.WindowUtilities.setPositionDisplay(null,
            targetDisplay, positionDisplay);

        //    targetDisplay.repaint();
        targetDisplay.setVisible(true);

        // the title needs to be set at the end other wise things are not what they should be (size, pos on screen...)
        targetDisplay.setTitle(frameTitle);
    }

    /**
     * Start the collection of data here : reset the vector, get the source and
     * start the polling thread.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void startCollection() throws Exception {
        // fill the value vector with zeros;
        for (int i = 0; i < numberOfSamples; i++) {
            allValues.addElement(new Integer(0));
        }

        // collect data source
        source = (DataSource) sfResolve(DATASOURCE, true);

        // set up the collector thread
        pollingPeriod = sfResolve(POLLINGPERIOD, 5, false);
        collecting = true;
        collector = new Thread(this);
        if (sfLog().isDebugEnabled()) sfLog().debug("starting polling thread");
        collector.start();
        if (sfLog().isInfoEnabled()) sfLog().info("polling thread started");
    }

    /**
     * Create a specific graph panel to display the data from this data source
     *
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GraphPanel createGraphPanel(Dimension size) {
        return initPanel(new GraphPanel(size));
    }

    protected GraphPanel initPanel(GraphPanel panel) {
        panel.setAdjustable(adjust);
        panel.setOffset(xOffset, yOffset);
        panel.setDataDimensions(minX, maxX, stepX, minY, maxY);
        panel.setGridDimensions(gridStepX, gridStepY);
        panel.setGraphWidth(graphPencilWidth);
        numberOfSamples = (int) ((maxX - minX) / stepX);
        panel.setSampleNumber(numberOfSamples);
        panel.keysAllowed = keysAllowed;
        panel.toggleDisplay(histogram);
        panel.setPencilColour(pencilColour);

        return panel;
    }

    /**
     * The collecting thread's run method...
     */
    public void run() {
        while (collecting) {
            getData();

            try {
                Thread.sleep(1000 * pollingPeriod);
                if (sfLog().isDebugEnabled()) sfLog().debug( "polling thread running");
            } catch (Exception e) {
            }
        }
    }

    /**
     * Termination hook : close threads & windows
     *
     * @param tr DOCUMENT ME!
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        // stop the collecting thread if there was one.
        collecting = false;

        // remove the targetDisplay
        targetDisplay.dispose();
        super.sfTerminateWith(tr);
    }

    protected void convertData(int value) {
        if (sfLog().isDebugEnabled()) sfLog().debug("new value: "+value);
        allValues.removeElementAt(0);
        allValues.addElement(new Integer(value)); //values are added at the end

        int i = 0;

        for (Enumeration e = allValues.elements(); e.hasMoreElements();) {
            gp.yData[i] = ((Integer) e.nextElement()).intValue();
            gp.xData[i] = minX + (stepX * i++);
        }

        // ask the panel to repaint itself
        gp.repaint();
    }

    /**
     * Overwrite this function if you're accessing a given source of data.
     */
    protected void getData() {
        try {
            convertData(source.getData());
        } catch (Exception e) {
            LogSF sfLog = LogFactory.getLog(this.getClass());
            if (sfLog.isErrorEnabled()) sfLog.error (e);
        }
    }
}
