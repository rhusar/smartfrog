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

package org.smartfrog.examples.dynamicwebserver.gui.graphpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import javax.swing.JPanel;

import org.smartfrog.SFSystem;

/**
 * A panel to display a graph. It uses two arrays, one for x axis, a second one
 * for the y axis.
 */
public class GraphPanel extends JPanel implements ComponentListener,
    KeyListener {
    /** Log for this class, created using class name*/
    LogSF log = LogFactory.getLog(this.getClass().getName());
    // The xAxis & yAxis dimension.
    public final static int xAxis = 0;

    // The xAxis & yAxis dimension.
    public final static int yAxis = 1;

    // graphical elements for this panel
    BufferedImage bi;
    Graphics2D graphicsHandle;
    Rectangle area;
    boolean firstTime = true;

    // does this Panel need redrawing ?
    protected boolean dirty = false;

    // the panel holding any extra button, label, etc.
    CompleteGraphPanel cgp = null;

    // variables related to the data displayed
    public int xMin = 0;

    // variables related to the data displayed
    public int xMax = 10;

    // variables related to the data displayed
    public int xStep = 1;

    // variables related to the data displayed
    public int gridStepX = 5;

    // variables related to the data displayed
    public int yMin = 0;

    // variables related to the data displayed
    public int yMax = 10;

    // variables related to the data displayed
    public int gridStepY = 5;

    // the size of the xData array
    public int sampleNumber;

    // the distance between the border of this panel and the axis
    public int xOffset;

    // the distance between the border of this panel and the axis
    public int xOffsetDefault = 10;

    // the distance between the border of this panel and the axis
    public int yOffset;

    // the distance between the border of this panel and the axis
    public int yOffsetDefault = 10;

    // the length of the axis, in pixel
    protected int xAxisPixelLength;

    // the length of the axis, in pixel
    protected int yAxisPixelLength;

    // default color used
    public Color backgroundColor = Color.black;

    // default color used
    public Color gridColor = Color.darkGray;

    // default color used
    public Color axisColor = Color.white;

    // default color used
    public Color textColor = Color.white;

    // default color used
    public Color graphColor = Color.yellow;

    // default pencils width as a % of the total height
    public float axisWidth = 0.5f;

    // default pencils width as a % of the total height
    public float graphWidth = 1.5f;

    // actual pencil widths
    public float axisPencilWidth;

    // actual pencil widths
    public float graphPencilWidth;

    // draw a grid for the axis
    public boolean drawGrid = true;

    //access to keyboard or not
    public boolean keysAllowed = false;

    // is the y axis adjustable or not ?
    public boolean adjustable = false;

    // the two data arrays. They are updated by an external user of this object
    public int[] xData;
    public int[] yData;

    // images composing the bar
    protected Image topBar=null;

    // images composing the bar
    protected Image middleBar=null;

    // images composing the bar
    protected Image bottomBar=null;

    // image for the background
    protected Image backgroundImage=null;

    boolean filledDisplay = false;
    protected int xBarOffset = 10;
    protected int yBarSize = 10;

    /**
     * Default constructor
     */
    public GraphPanel() {
        setBackground(backgroundColor);

        //    setBorder(BorderFactory.createEtchedBorder());
    }

    /**
     * The constructor to be used : Sets the size, the offsets, the pencils,
     * adds the listeners
     *
     * @param size DOCUMENT ME!
     */
    public GraphPanel(Dimension size) {
        this();
        setSize(size);
        setOffset(xOffsetDefault, yOffsetDefault);
        initImages();
        setPencils();

        // a listener for the size mods
        addComponentListener(this);
        addKeyListener(this);

        // set focusable and request focus to get key input
        setFocusable(true);
        requestFocus();
    }

    public void initImages() {
        try {
            String imagesPath = org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel.class.getPackage().getName()+".";
            imagesPath=imagesPath.replace('.','/');
//            topBar = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel.class.getResource(
//                        "top.gif"));
//            bottomBar = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel.class.getResource(
//                        "bottom.gif"));
            //middleBar = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel.class.getResource(
//                        "middle.gif"));
//            backgroundImage = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel.class.getResource(
//                        "background.gif"));
            topBar = createImage(imagesPath+"top.gif");
            bottomBar = createImage(imagesPath+"bottom.gif");
            middleBar = createImage(imagesPath+"middle.gif");
            backgroundImage = createImage(imagesPath+"background.gif");
            try {
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(topBar, 0);
                tracker.addImage(middleBar, 1);
                tracker.addImage(bottomBar, 2);
                tracker.waitForID(0, 5000);
                tracker.waitForID(1, 5000);
                tracker.waitForID(2, 5000);
            } catch (InterruptedException iex) {
                if (log.isErrorEnabled()) log.error (iex);
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) log.error (e);
        }
    }

    /**
     * If no image is found a null is returned.
     * @param SFURL
     * @return the image or null if nothing found.
     */
    public Image createImage(String SFURL) {
        try {
            byte imageData[] =  SFSystem.getByteArrayForResource(SFURL);
            Image img = java.awt.Toolkit.getDefaultToolkit().createImage(imageData, 0, imageData.length);
            return img;
        } catch (Exception e) {
            if (log.isDebugEnabled()){
               log.warn("Resulting image will be null. " + e.getMessage(),e);
            }
        }
        return null;
    }

    /**
     * Set the CompleteGraphPanel surrounding this one. Will be used to update
     * extra labels, correspond with buttons, etc.
     *
     * @param cgp DOCUMENT ME!
     */
    public void setCompleteGraphPanel(CompleteGraphPanel cgp) {
        this.cgp = cgp;
        cgp.setLabels(
        //        " MIN = "+
        new Integer(yMin) + "",
        //        " MAX = "+
        new Integer(yMax) + "");
    }

    /**
     * Set the size of the data arrays
     *
     * @param samples DOCUMENT ME!
     */
    public void setSampleNumber(int samples) {
        sampleNumber = samples;
        xData = new int[sampleNumber];
        yData = new int[sampleNumber];
    }

    public void setGraphWidth(float graphWidth) {
        this.graphWidth = graphWidth;
        setPencils();
    }

    /**
     * Set the pencils size
     */
    public void setPencils() {
        axisPencilWidth = (((float) getSize().getHeight()) * axisWidth) / 100;
        graphPencilWidth = (((float) getSize().getHeight()) * graphWidth) / 100;
    }

    /**
     * Set the axis for both dimensions
     *
     * @param xMin DOCUMENT ME!
     * @param xMax DOCUMENT ME!
     * @param xStep DOCUMENT ME!
     * @param yMin DOCUMENT ME!
     * @param yMax DOCUMENT ME!
     */
    public void setDataDimensions(int xMin, int xMax, int xStep, int yMin,
        int yMax) {
        setAxis(GraphPanel.xAxis, xMin, xMax, xStep);
        setAxis(GraphPanel.yAxis, yMin, yMax, 0);
    }

    /**
     * Set grid dimensions
     *
     * @param gridStepX DOCUMENT ME!
     * @param gridStepY DOCUMENT ME!
     */
    public void setGridDimensions(int gridStepX, int gridStepY) {
        this.gridStepX = gridStepX;
        this.gridStepY = gridStepY;
    }

    /**
     * Set border offsets. Also computes the length, in pixel, of both axis.
     *
     * @param xOffset DOCUMENT ME!
     * @param yOffset DOCUMENT ME!
     */
    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        xAxisPixelLength = (int) (getSize().getWidth() - xOffset);
        yAxisPixelLength = (int) (getSize().getHeight() - (2 * yOffset));
        dirty = true;
    }

    /**
     * Set data for one axis : max & min value represented the step is the
     * distance between two samples. It should correspond to ((xMax - xMin) /
     * sampleNumbers)
     *
     * @param axis DOCUMENT ME!
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param step DOCUMENT ME!
     */
    public void setAxis(int axis, int min, int max, int step) {
        switch (axis) {
        case GraphPanel.xAxis:
            xMax = max;
            xMin = min;
            xStep = step;

            break;

        case GraphPanel.yAxis:
            yMax = max;
            yMin = min;

            break;
        }
    }

    /**
     * Toggle a different display for the graph.
     */
    public void toggleDisplay() {
        filledDisplay = !filledDisplay;
    }

    /**
     * Toggle a different display for the graph.
     *
     * @param filled DOCUMENT ME!
     */
    public void toggleDisplay(boolean filled) {
        filledDisplay = filled;
    }

    /**
     * Toggle the grid on the graph.
     */
    public void toggleGrid() {
        drawGrid = !drawGrid;
    }

    /**
     * Set the pencil's colour
     *
     * @param pencilColour DOCUMENT ME!
     */
    public void setPencilColour(String pencilColour) {
        try {
            this.graphColor = Color.decode(pencilColour);
        } catch (Exception e) {
        }
    }

    /**
     * Set the graph's adjustability.
     *
     * @param adjustable DOCUMENT ME!
     */
    public void setAdjustable(boolean adjustable) {
        this.adjustable = adjustable;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        update(g);
    }

    public void update(Graphics g) {
        try {
            Graphics2D g2 = (Graphics2D)g;
            Dimension dim = getSize();
            int w = dim.width;
            int h = dim.height;

            // if the graph has not been drawn or the size has changed,
            // initialize the graph
            if (firstTime||dirty) {
                // create the buffered image & its graphics
                bi = (BufferedImage)createImage(w, h);
                graphicsHandle = bi.createGraphics();
                graphicsHandle.setColor(Color.black);
                graphicsHandle.setStroke(new BasicStroke(5.0f));

                // set up antialiasing
                graphicsHandle.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.
                                                VALUE_ANTIALIAS_ON);

                // drawing area
                area = new Rectangle(dim);

                // it's not first time anymore, but following functions may
                // use the dirty flag : unset it at the end of the draw phase
                firstTime = false;
            }

            // Clears the rectangle that was previously drawn.
            graphicsHandle.setColor(backgroundColor);
            graphicsHandle.clearRect(0, 0, area.width, area.height);

            if (backgroundImage!=null) {
                graphicsHandle.drawImage(backgroundImage, 0, 0, area.width,
                                         area.height, this);
            } else {
                // Draws and fills the newly positioned rectangle to the buffer.
                graphicsHandle.fillRect(0, 0, area.width, area.height);
            }

            // Draws the content of the panel on to the buffered image's graphics
            drawPanel(graphicsHandle);

            // Draws the buffered image to the screen.
            g2.drawImage(bi, 0, 0, this);

            // if dirty was true, we give a chance to any of the previous function to do some work
            dirty = false;
        } catch (Throwable ex) {
           if (log.isErrorEnabled()) log.error (ex);
        }
    }

    /**
     * Overwrite this method to draw the panel differently
     *
     * @param g2d DOCUMENT ME!
     */
    public void drawPanel(Graphics2D g2d) {
        if (drawGrid) {
            drawGrid(g2d);
        }

        drawData(g2d);
        drawAxis(g2d);
    }

    /**
     * Draw a 2d grid in the background. "gridStepX" and "gridStepY" are the spaces
     * between lines of the grid.
     *
     * @param g2d
     */
    protected void drawGrid(Graphics2D g2d) {
        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(1.0f));

        //scale the gridStepX to the screen's axis
        int scaledGridStepX = (gridStepX * xAxisPixelLength) / (xMax - xMin);
        int x = 0;

        if (scaledGridStepX > 10) {
            while (x < (xAxisPixelLength - scaledGridStepX)) {
                x += scaledGridStepX;
                g2d.drawLine(xOffset + x,
                    (int) getSize().getHeight() - yOffset, xOffset + x, yOffset);
            }
        }

        int scaledGridStepY = scaleY(gridStepY); //

        //        (int) ( gridStepY * ((double) yAxisPixelLength /(yMax - yMin)));
        int displayedY = yOffset + yAxisPixelLength;
        int valueY = yMin;

        if (scaledGridStepY > 10) {
            while (displayedY > (yOffset + scaledGridStepY)) {
                valueY += gridStepY;
                displayedY = yAxisPixelLength - scaleY(valueY) + yOffset;
                g2d.drawLine(xOffset, displayedY,
                    (int) getSize().getWidth() //- xOffset
                , displayedY);
            }
        }
    }

    /**
     * Draw the two main axis
     *
     * @param g2d DOCUMENT ME!
     */
    protected void drawAxis(Graphics2D g2d) {
        Dimension dim = getSize();
        int width = dim.width;
        int height = dim.height;
        g2d.setColor(axisColor);
        g2d.setStroke(new BasicStroke(axisPencilWidth));

        // draw the two main lines
        g2d.drawLine(xOffset, height - yOffset, width, //- xOffset,
            height - yOffset);
        g2d.drawLine(xOffset, height - yOffset, xOffset, yOffset);

        // and the corner
        g2d.setColor(Color.red);
        g2d.drawRect(xOffset - ((int) axisPencilWidth / 2),
            height - yOffset - ((int) axisPencilWidth / 2),
            (int) axisPencilWidth, (int) axisPencilWidth);
    }

    /**
     * Draw the two data arrays. We need to convert them both to fit to the
     * axis' scales, and to offset them
     *
     * @param g2d DOCUMENT ME!
     */
    protected void drawData(Graphics2D g2d) {
        int[] yDataDisplay; // the scaled y
        int[] xDataDisplay; //  the scaled x

        if ((xData != null) && (yData != null)) {
            yDataDisplay = new int[yData.length];
            xDataDisplay = new int[xData.length];

            for (int i = 0; i < yData.length; i++) {
                yDataDisplay[i] = -scaleY(yData[i]) + yAxisPixelLength +
                    yOffset; //-scaleY(yMin+0.5) ;
                xDataDisplay[i] = scaleX(xData[i]) + xOffset;
            }

            g2d.setStroke(new BasicStroke(graphPencilWidth / 2));

            if (filledDisplay) {
                // draw a rectangle per sample on the x axis
                for (int i = 0; i < xDataDisplay.length; i++) {
                    int w = 1 + ((xStep * xAxisPixelLength) / (xMax - xMin));
                    int h = scaleY(yData[i]);

                    if ((topBar != null) && (middleBar != null)) {
                        int topSizeY = (h < topBar.getHeight(this)) ? h
                                                                    : topBar.getHeight(this);
                        g2d.drawImage(topBar, xDataDisplay[i] + xBarOffset,
                            yDataDisplay[i], w - (2 * xBarOffset), topSizeY,
                            this);
                        g2d.drawImage(middleBar, xDataDisplay[i] + xBarOffset,
                            yDataDisplay[i] + topSizeY, w - (2 * xBarOffset),
                            h - topSizeY, this);
                    } else {
                        g2d.setColor(graphColor);
                        g2d.fillRect(xDataDisplay[i], yDataDisplay[i],
                            1 + ((xStep * xAxisPixelLength) / (xMax - xMin)),
                            scaleY(yData[i]));
                    }

                    //          g2d.drawImage(topBar,xDataDisplay[i] + 2,yDataDisplay[i],w -4, 10 ,this);
                    //
                    //          topBar
                    //
                    //          g2d.setStroke(new BasicStroke(graphPencilWidth/6));
                    //
                    //          g2d.setColor(textColor);
                    //          g2d.drawString(new Integer(yData[i]).toString(),xDataDisplay[i],yDataDisplay[i]);
                }
            } else {
                // draw a line
                g2d.setStroke(new BasicStroke(graphPencilWidth / 2));
                g2d.setColor(graphColor);
                g2d.drawPolyline(xDataDisplay, yDataDisplay, xData.length);
            }
        }
    }

    private void autoAdjust() {
        int newMax = yMin;

        for (int i = 0; i < yData.length; i++) {
            if (yData[i] > newMax) {
                newMax = yData[i];
            }
        }

        if (yMax != yMin) {
            yMax = newMax;
        }

        cgp.setLabels(
        //        " MIN = "+
        new Integer(yMin) + "",
        //        " MAX = "+
        new Integer(yMax) + "");
        dirty = true;
        repaint();
    }

    /**
     * Returns a scaled int between 0 and xAxisPixelLength If xData greater
     * than xMax or xData less than  xMin the return values  are 0 and
     * xAxisPixelLength respectively
     *
     * @param xDataValue DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected int scaleX(int xDataValue) {
        int result = 0;

        if (xDataValue >= xMax) {
            return xAxisPixelLength;
        }

        if (xDataValue <= xMin) {
            return 0;
        }

        if (xMin != 0) {
            double xcoeff = ((double) xAxisPixelLength) / (xMax - xMin);
            double xOr = -xcoeff * xMin;
            result = (int) ((xcoeff * xDataValue) + xOr);
        } else {
            result = (int) (xDataValue * ((double) xAxisPixelLength / xMax));
        }

        return result;
    }

    /**
     * Returns a scaled int between 0 and yAxisPixelLength If yData greater
     * than yMax or yData less than yMin the return values  are 0 and
     * yAxisPixelLength respectively
     *
     * @param yDataValue DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected int scaleY(int yDataValue) {
        int result = 0;

        if (yDataValue >= yMax) {
            if (!adjustable) {
                return yAxisPixelLength;
            } else {
                yMax = yDataValue + 1; // here the CompleteGraphPanel should also be notified
                cgp.setLabels(
                //            " MIN = "+
                new Integer(yMin) + "", // " MAX = "+
                    new Integer(yMax) + "");
                dirty = true;
                repaint();

                return yAxisPixelLength;
            }
        }

        if (yDataValue <= yMin) {
            return 0;
        }

        if (yMin != 0) {
            double ycoeff = ((double) yAxisPixelLength) / (double) (yMax -
                yMin);
            double yOr = -ycoeff * yMin;
            result = (int) ((ycoeff * yDataValue) + yOr);
        } else {
            double coeff = (((double) yAxisPixelLength) / (double) yMax);
            result = (int) (yDataValue * coeff);
        }

        return result;
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    /**
     * Called when the component has been resized
     *
     * @param e DOCUMENT ME!
     */
    public void componentResized(ComponentEvent e) {
        // reset offsets to recompute the axis' pixel length
        setOffset(this.xOffset, this.yOffset);

        // reset the pencils (their size is a % of the total height
        setPencils();
        dirty = true;
    }

    public void componentShown(ComponentEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (!keysAllowed) {
            return;
        }

        switch (e.getKeyCode()) {
        case (KeyEvent.VK_DOWN):
            graphWidth = graphWidth / 2;
            setPencils();

            break;

        case (KeyEvent.VK_UP):
            graphWidth = graphWidth * 2;
            setPencils();

            break;

        case (KeyEvent.VK_Z):
            axisWidth = axisWidth / 2;
            setPencils();

            break;

        case (KeyEvent.VK_A):
            axisWidth = axisWidth * 2;
            setPencils();

            break;

        case (KeyEvent.VK_G):
            toggleGrid();

            break;

        case (KeyEvent.VK_F):
            toggleDisplay();

            break;

        case (KeyEvent.VK_R):
            autoAdjust();

            break;

        case (KeyEvent.VK_O):
            setOffset(xOffset + 1, yOffset + 1);

            break;

        case (KeyEvent.VK_L):
            setOffset(xOffset - 1, yOffset - 1);

            break;
        }

        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
