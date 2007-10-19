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
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.rmi.RemoteException;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.ThresholdPanel;

/**
 * <p>
 * Description: AdjustableThresholdPanel.
 * </p>
 *
 */

public class AdjustableThresholdPanel extends ThresholdPanel
    implements MouseListener, MouseMotionListener, KeyListener {
    // the treshold controller managed by this gui
    Thresholder thresholdControl;
    int lowerThreshold = -1;
    int upperThreshold = -1;
    boolean grabbedUpperSlide = false;
    boolean grabbedLowerSlide = false;
    int grabbedYDelta;

    public AdjustableThresholdPanel(Dimension size,
                                            Thresholder thresholdControl) {
        super(size);
        r = (int) (getSize().getHeight() * handleSizePercent);
        xOffset = r;
        yOffset = r / 2;
        deltaSlide = 2 * r;
        initImages();
        this.thresholdControl = thresholdControl;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public synchronized int getUpperThreshold() {
        try {
            if (upperThreshold == -1) {
                upperThreshold = thresholdControl.upperThreshold();
            }
        } catch (RemoteException e) {
            upperThreshold = 0;
        }

        return upperThreshold;
    }

    public synchronized int getLowerThreshold() {
        try {
            if (lowerThreshold == -1) {
                lowerThreshold = thresholdControl.lowerThreshold();
            }
        } catch (RemoteException e) {
            lowerThreshold = 0;
        }

        return lowerThreshold;
    }

    public synchronized void setUpperThresholdF(float newThreshold) {
        upperThreshold = (int) (yMin + ((newThreshold * (yMax - yMin)) / 100));

        try {
            thresholdControl.setUpperThreshold(upperThreshold);
        } catch (RemoteException e) {
        }
    }

    public synchronized void setLowerThresholdF(float newThreshold) {
        lowerThreshold = (int) (yMin + ((newThreshold * (yMax - yMin)) / 100));

        try {
            thresholdControl.setLowerThreshold(lowerThreshold);
        } catch (RemoteException e) {
        }
    }

    public synchronized void setUpperThreshold(int newThreshold) {
        upperThreshold = newThreshold;

        try {
            thresholdControl.setUpperThreshold(upperThreshold);
        } catch (RemoteException e) {
        }
    }

    public synchronized void setLowerThreshold(int newThreshold) {
        lowerThreshold = newThreshold;

        try {
            thresholdControl.setLowerThreshold(lowerThreshold);
        } catch (RemoteException e) {
        }
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

    public void mouseReleased(MouseEvent e) {
        grabbedUpperSlide = false;
        grabbedLowerSlide = false;
        dirty = true;
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        if (grabbedUpperSlide || grabbedLowerSlide) {
            int y = e.getY() //- grabbedYDelta
                 -yOffset;
            double maxDeltaY = yMax - yMin;
            float realDelta = Math.abs(yAxisPixelLength - y);
            float newThreshold = (float)
                                (100 * (realDelta / (float) yAxisPixelLength));

            if (grabbedUpperSlide) {
                if (e.getY() < (lowerSlidePos - deltaSlide)) {
                    if (e.getY() >= yOffset) {
                        setUpperThresholdF(newThreshold);
                        upperSlidePos = e.getY();
                    }
                }

                // if the upper threshold is dragged higher than yMax
                if ((e.getY() - grabbedYDelta) < yOffset) {
                    setUpperThreshold(100); //<-- parameterize 100
                    upperSlidePos = yOffset;
                }

                // if the upper threshold is dragged lower than lowerThreshold
                if ((e.getY() - grabbedYDelta) > (lowerSlidePos - deltaSlide)) {
                    realDelta = Math.abs(yAxisPixelLength -
                            (lowerSlidePos - deltaSlide - yOffset));
                    newThreshold = (int) (100 * (realDelta / yAxisPixelLength));
                    setUpperThresholdF(newThreshold); //<-- parameterize 100
                    upperSlidePos = lowerSlidePos - deltaSlide;
                }
            }

            if (grabbedLowerSlide) {
                if (e.getY() > (upperSlidePos + deltaSlide)) {
                    if (e.getY() <= (getSize().getHeight() - yOffset)) {
                        setLowerThresholdF(newThreshold);
                        lowerSlidePos = e.getY();
                    }
                }

                if (e.getY() > getSize().getHeight()) {
                    setLowerThreshold(0); //<-- parameterize 0
                    lowerSlidePos = (int) getSize().getHeight() - yOffset;
                }

                if ((e.getY() - grabbedYDelta) < (upperSlidePos + deltaSlide)) {
                    realDelta = Math.abs(yAxisPixelLength -
                            ((upperSlidePos + deltaSlide) - yOffset));
                    newThreshold = (int) (100 * (realDelta / yAxisPixelLength));
                    setLowerThresholdF(newThreshold);
                    lowerSlidePos = upperSlidePos + deltaSlide;
                }
            }

            repaint();
        }
    }

    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        if (keysAllowed && (thresholdControl != null)) {
            switch (e.getKeyCode()) {
            case (KeyEvent.VK_S):
                handleSizePercent += 0.01;

                break;

            case (KeyEvent.VK_X):
                handleSizePercent -= 0.01;

                break;

            case (KeyEvent.VK_U):
                setUpperThreshold(getUpperThreshold() + 5);

                break;

            case (KeyEvent.VK_J):
                setUpperThreshold(getUpperThreshold() - 5);

                break;

            case (KeyEvent.VK_I):
                setLowerThreshold(getLowerThreshold() + 5);

                break;

            case (KeyEvent.VK_K):
                setLowerThreshold(getLowerThreshold() - 5);

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
