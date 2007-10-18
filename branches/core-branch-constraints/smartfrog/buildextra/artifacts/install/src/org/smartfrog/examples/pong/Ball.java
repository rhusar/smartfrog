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

package org.smartfrog.examples.pong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * Defines the utility methods for Ball object.
 */  
public class Ball extends Obstacle {
    /** Color of the ball. */
    Color ballColor = Color.white;
    
    /**
     * Constructor.
     *
     * @param mass the mass of the ball
     * @param initialPosition the initial position of the ball
     * @param initialSize the dimension size  
     */ 
    public Ball(int mass, Point2D initialPosition, Dimension initialSize) {
        super(mass, initialPosition, initialSize);
    }

    /** 
     * Draws the Ball object.
     *
     * @param  g2d the graphics object
     */ 
    public void draw(Graphics2D g2d) {
        g2d.fillOval((int) (this.getPosition().getX() - (size.getWidth() / 2)),
            (int) (this.getPosition().getY() - (size.getHeight() / 2)),
            (int) size.getWidth(), (int) size.getHeight());
    }
}
