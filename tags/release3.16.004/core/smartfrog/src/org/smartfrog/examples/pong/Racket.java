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


/// NEED AN IMAGE TRACKER AND FAST !

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Point2D;

/**
 * Defines the Racket object.
 */ 
public class Racket extends Obstacle {
    /** Plane percentage. */ 
    static double planePercentage = 0.5;
    /** Image object. */ 
    private Image racketImage = null;

    //private Dimension racketSize = new Dimension(20,100);
    //private boolean dimensionHasChanged = false;
    private Color racketColor = Color.red;

    /**
     * Constructor.
     *
     * @param mass the mass of the ball
     * @param initialPosition the initial position of the ball
     * @param initialSize the dimwnsion size  
     */ 
    public Racket(int mass, Point2D initialPosition, Dimension initialSize) {
        super(mass, initialPosition, initialSize);
    }

    /**
     * Checks if elastic clooision takes palce between obstacle1 and obstacle2
     * in a given axis.
     *
     * @param obs1 obstacle object 1  
     * @param obs2 obstacle object 2
     * @param axis the axis
     */   
    public void elasticCollision(Obstacle obs1, Obstacle obs2, int axis) {
        Point2D speed1 = obs1.getSpeed();
        Point2D speed2 = obs2.getSpeed();
        double m1 = obs1.getMass();
        double m2 = obs2.getMass();

        if (axis == Util.XAxisCollision) {
            // take the height difference , measure against deflection distance
            double hdiff = obs1.getPosition().getY() -
                obs2.getPosition().getY();
            double deflectionDist = (planePercentage * this.getDimension()
                                                           .getHeight()) / 2;
            double ySpeedPercentTransmitted = 0;

            if (Math.abs(hdiff) >= deflectionDist) {
                ySpeedPercentTransmitted = (Math.abs(hdiff) - deflectionDist) /
                ((this.getDimension().getHeight() / 2) -
             deflectionDist);
            }

            double newSpeedX1 = (((2 * m2) / (m1 + m2)) * speed2.getX()) +
                (((m1 - m2) / (m2 + m1)) * speed1.getX());
            double newSpeedX2 = (((2 * m1) / (m1 + m2)) * speed1.getX()) +
                (((m2 - m1) / (m2 + m1)) * speed2.getX());
            int sign = (hdiff < 0) ? (-1) : 1;
            obs1.setSpeed(new Point2D.Double(newSpeedX1,
                    (newSpeedX1 * ySpeedPercentTransmitted * sign) +
                    obs1.getSpeed().getY()));
            obs2.setSpeed(new Point2D.Double(newSpeedX2,
                    (newSpeedX2 * ySpeedPercentTransmitted) +
                    obs2.getSpeed().getY()));
        }

        if (axis == Util.YAxisCollision) {
            double newSpeedY1 = (((2 * m2) / (m1 + m2)) * speed2.getY()) +
                (((m1 - m2) / (m2 + m1)) * speed1.getY());
            double newSpeedY2 = (((2 * m1) / (m1 + m2)) * speed1.getY()) +
                (((m2 - m1) / (m2 + m1)) * speed2.getY());
            obs1.setSpeed(new Point2D.Double(speed1.getX(), newSpeedY1));
            obs2.setSpeed(new Point2D.Double(speed2.getX(), newSpeedY2));
        }
    }
}
