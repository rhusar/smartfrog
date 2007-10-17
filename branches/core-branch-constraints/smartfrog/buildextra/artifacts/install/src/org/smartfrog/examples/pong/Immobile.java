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

import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 * Defines the Immobile object.
 */ 
public class Immobile extends Obstacle {
    /**
     * Constructor.
     *
     * @param mass the mass of the ball
     * @param initialPosition the initial position of the ball
     * @param initialSize the dimwnsion size  
     */ 
    public Immobile(int mass, Point2D initialPosition, Dimension initialSize) {
        super(mass, initialPosition, initialSize);
    }
    
    /**
     * Checks if obstacle collides or not.
     *
     * @param obs obstacle
     * @param timeWindow window time
     *
     * @return true if collided else false
     */ 
    public boolean collide(Obstacle obs, double timeWindow) {
        Point2D speed = obs.getSpeed();
        boolean collisionX = false;
        boolean collisionY = false;

        double deltaSpeedX = -speed.getX();
        double deltaSpeedY = -speed.getY();
        double collX = collideX(obs, deltaSpeedX, timeWindow);
        double collY = collideY(obs, deltaSpeedY, timeWindow);

        if (collX == 0) {
            collisionY = (collY > 0);
        } else if (collX == -1) {
        } else {
            collisionX = (collY != 0) ? (collX <= collY) : true;
        }

        if (collY == 0) {
            collisionX = (collX > 0);
        } else if (collY == -1) {
        } else {
            collisionY = (collX != 0) ? (collY < collX) : true;
        }

        if (collisionX) {
            obs.setSpeed(new Point2D.Double(-speed.getX(), speed.getY()));
        }

        if (collisionY) {
            obs.setSpeed(new Point2D.Double(speed.getX(), -speed.getY()));
        }

        return collisionX || collisionY;
    }
}
