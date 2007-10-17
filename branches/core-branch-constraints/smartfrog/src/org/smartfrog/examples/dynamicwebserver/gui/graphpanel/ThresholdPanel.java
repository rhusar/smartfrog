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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;


//n.b. : to be rewritten with proper coordinate spaces
//1
public class ThresholdPanel extends GraphPanel implements KeyListener {
    // colors for lower and upper threshold lines
    Color upperThresholdColor = Color.RED;

    // colors for lower and upper threshold lines
    Color lowerThresholdColor = Color.blue;
    public boolean bars = false;

    // the current sliders positions in window space (pixels)
    protected int upperSlidePos;

    // the current sliders positions in window space (pixels)
    protected int lowerSlidePos;

    // the minimal distance between sliders in
    protected int deltaSlide;

    // the size of the slides handle
    protected int r = 20;
    public float handleSizePercent = 0.07f;

    // the font
    public String fontName;
    protected Font nf;
    protected Image upperSlideImage;
    protected Image lowerSlideImage;
    protected Image barUImage;
    protected Image barLImage;
    int upperThreshold;
    int lowerThreshold;
    boolean firsttime = true;
    boolean grabbedUpperSlide = false;
    boolean grabbedLowerSlide = false;
    int grabbedYDelta;

    public ThresholdPanel(Dimension size) {
        super(size);
        r = (int) (getSize().getHeight() * handleSizePercent);
        xOffset = r;
        yOffset = r / 2;
        deltaSlide = 2 * r;
        initImages();
    }

    public void setCompleteGraphPanel(CompleteGraphPanel cgp) {
        super.setCompleteGraphPanel(cgp);
    }

    public void initImages() {
        super.initImages();

        try {
            upperSlideImage = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.ThresholdPanel.class.getResource(
                        "upperSlider.gif"));
            lowerSlideImage = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.ThresholdPanel.class.getResource(
                        "lowerSlider.gif"));
            barUImage = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.ThresholdPanel.class.getResource(
                        "barUp.gif"));
            barLImage = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.ThresholdPanel.class.getResource(
                        "barLo.gif"));

            try {
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(upperSlideImage, 0);
                tracker.addImage(lowerSlideImage, 1);
                tracker.waitForID(0, 5000);
                tracker.waitForID(1, 5000);
            } catch (InterruptedException iex) {
                if (log.isErrorEnabled()) log.error (iex);
            }
        } catch (Exception e) {
            if (log.isIgnoreEnabled()) log.ignore (e);
        }
    }

    public void setUpperThreshold(int upperThreshold) {
        this.upperThreshold = upperThreshold;
        dirty = true;
    }

    public void setLowerThreshold(int lowerThreshold) {
        this.lowerThreshold = lowerThreshold;
        dirty = true;
    }

    public int getUpperThreshold() {
        return upperThreshold;
    }

    public int getLowerThreshold() {
        return lowerThreshold;
    }

    public void drawPanel(Graphics2D g2d) {
        super.drawPanel(g2d);
        drawBars(g2d);
    }

    public void drawBars(Graphics2D g2d) {
        if (dirty || firsttime) {
            if (firsttime) {
            }

            r = (int) (getSize().getHeight() * handleSizePercent);
            deltaSlide = 2 * r;

            int thup = scaleY(getUpperThreshold());
            int thlo = scaleY(getLowerThreshold());
            upperSlidePos = yAxisPixelLength - thup + yOffset;
            lowerSlidePos = yAxisPixelLength - thlo + yOffset;

            if (upperSlidePos != lowerSlidePos) {
                firsttime = false;
            }
        }

        // draw the lower threshold
        int loTy = lowerSlidePos;

        try {
            nf = new Font(fontName, Font.BOLD, r);
        } catch (Exception e) {
            nf = new Font(g2d.getFont().getFontName(), Font.BOLD, r);
        }

        // bar or not bar
        String lowerTString = new Integer(getLowerThreshold()).toString();
        drawThresholdHandle(g2d, loTy, barLImage, lowerTString,
            lowerThresholdColor, lowerSlideImage);

        // draw the upper threshold
        int upTy = upperSlidePos;
        String upperTString = new Integer(getUpperThreshold()).toString();
        drawThresholdHandle(g2d, upTy, barUImage, upperTString,
            upperThresholdColor, upperSlideImage);
    }

    public void drawThresholdHandle(Graphics2D g2d, int hy, Image barImage,
        String tString, Color thresholdColor, Image slideImage) {
        if ((barImage != null) && bars) {
            g2d.drawImage(barImage, xOffset, (int) (hy - 2),
                (int) getSize().getWidth(), //-xOffset,
                4, this);
        } else {
            g2d.setColor(thresholdColor);
            g2d.drawLine(xOffset, hy, (int) getSize().getWidth(), //-xOffset,
                hy);
        }

        int half_size = (r > xOffset) ? xOffset : r;

        if (slideImage != null) {
            g2d.drawImage(slideImage, xOffset - half_size, hy - r,
                2 * half_size, 2 * r, this);
        } else {
            g2d.fillOval(xOffset - r, hy - r, 2 * r, 2 * r);
        }

        g2d.setFont(nf);

        Rectangle2D rlts = g2d.getFont().getStringBounds(tString,
                g2d.getFontRenderContext());
        g2d.setColor(textColor);
        g2d.drawString(tString, xOffset - ((int) rlts.getWidth() / 2),
            hy + (int) (rlts.getHeight() / 3));
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();

        if ((p.distance(xOffset, upperSlidePos) < r) ||
                (Math.abs(p.getY() - upperSlidePos) < r)) {
            grabbedUpperSlide = true;
            grabbedYDelta = (int) p.getY() - upperSlidePos;
        } else {
            if ((p.distance(xOffset, lowerSlidePos) < r) ||
                    (Math.abs(p.getY() - lowerSlidePos) < r)) {
                grabbedLowerSlide = true;
                grabbedYDelta = (int) p.getY() - lowerSlidePos;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (keysAllowed) {
            switch (e.getKeyCode()) {
            case (KeyEvent.VK_S):
                handleSizePercent += 0.01;

                break;

            case (KeyEvent.VK_X):
                handleSizePercent -= 0.01;

                break;

            case (KeyEvent.VK_B):
                bars = !bars;

                break;
            }
        }

        dirty = true;
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
