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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.geom.Rectangle2D;


public class TideMarkPanel extends ThresholdPanel {
    //   int upperThreshold , lowerThreshold ;
    public TideMarkPanel(Dimension size) {
        super(size);
    }

    public void initImages() {
        super.initImages();

        try {
            //      upperSlideImage =  java.awt.Toolkit.getDefaultToolkit().getImage(
            //          org.smartfrog.services.gui.graphpanel.TideMarkPanel.class.getResource("slideU.gif"));
            //      lowerSlideImage =  java.awt.Toolkit.getDefaultToolkit().getImage(
            //          org.smartfrog.services.gui.graphpanel.TideMarkPanel.class.getResource("slideL.gif"));
            topBar = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.TideMarkPanel.class.getResource(
                        "barTop.gif"));

            //     bottomBar =  java.awt.Toolkit.getDefaultToolkit().getImage(
            //         org.smartfrog.examples.dynamicwebserver.gui.graphpanel.GraphPanel.class.getResource("bottom.gif"));
            middleBar = java.awt.Toolkit.getDefaultToolkit().getImage(org.smartfrog.examples.dynamicwebserver.gui.graphpanel.TideMarkPanel.class.getResource(
                        "barMiddle.gif"));

            //      barUImage =  java.awt.Toolkit.getDefaultToolkit().getImage(
            //          org.smartfrog.examples.dynamicwebserver.gui.graphpanel.TideMarkPanel.class.getResource("slideUTs.gif"));
            //      barLImage =  java.awt.Toolkit.getDefaultToolkit().getImage(
            //          org.smartfrog.examples.dynamicwebserver.gui.graphpanel.TideMarkPanel.class.getResource("slideLTs.gif"));
            try {
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(topBar, 0);
                tracker.addImage(middleBar, 1);

                //        tracker.addImage(barUImage, 2);
                //        tracker.addImage(barLImage , 3);
                //        tracker.waitForID(2,5000);
                tracker.waitForID(0, 5000);
                tracker.waitForID(1, 5000);
            } catch (InterruptedException iex) {
                if (log.isErrorEnabled()) log.error (iex);
            }
        } catch (Exception e) {
            if (log.isIgnoreEnabled()) log.ignore (e);
        }
    }

    //  int barXOffset = 10;
    public void drawThresholdHandle(Graphics2D g2d, int hy, Image barImage,
        String tString, Color thresholdColor, Image slideImage) {
        if (!bars) {
            return;
        }

        if (barImage != null) {
            if (barImage == barUImage) {
                g2d.drawImage(barImage, xOffset + (int) axisPencilWidth,
                    (int) (hy - (barImage.getHeight(this) / 2)),
                    (int) getSize().getWidth() - xOffset -
                    (int) axisPencilWidth, (int) barImage.getHeight(this), this);
            }
            else {
                int barSizeY = ((getHeight() - yOffset - hy) < barImage.getHeight(this))
                    ? (getHeight() - yOffset - hy) : barImage.getHeight(this);

                if (hy < (getHeight() - yOffset)) {
                    g2d.drawImage(barImage, xOffset + (int) axisPencilWidth, //+ xBarOffset  ,

                    //                     (int) (hy + barImage.getHeight(this) / 2),
                    hy,
                        (int) getSize().getWidth() - xOffset -
                        (int) axisPencilWidth, //- 2 *xBarOffset -,
                        (int) barSizeY, this);
                }
            }
        } else {
            g2d.setColor(thresholdColor);
            g2d.drawLine(xOffset, hy, (int) getSize().getWidth(), //-xOffset,
                hy);
        }

        g2d.setFont(nf);

        Rectangle2D rlts = g2d.getFont().getStringBounds(tString,
                g2d.getFontRenderContext());
        g2d.setColor(textColor);
        g2d.drawString(tString,
            (xOffset + (getWidth() / 2)) - ((2 * (int) rlts.getWidth()) / 3),
            hy + (int) (rlts.getHeight() / 3));
    }
}
