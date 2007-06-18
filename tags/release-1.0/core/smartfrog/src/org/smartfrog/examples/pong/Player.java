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

import java.awt.geom.Point2D;

/**
 * Defines the pong player component.
 */
public class Player {
    /** Initial Position of Racket. */
    Point2D racketInitialPos;

    /** Racket controlled by the player. */
    Racket racket;

    /** The acceleration the player can give to the racket. */
    int stepAccel = 400;

    /**
     * Constructor a raclet object.
     *
     * @param racket the racket object
     */
    public Player(Racket racket) {
        setRacket(racket);
    }

    /**
     * Specifies the racket position.
     *
     * @param racket racket object
     */
    public void setRacket(Racket racket) {
        this.racket = racket;
        racketInitialPos = racket.getPosition();
    }

    /**
     * Accelerates the racket in down direction.
     */
    public void keyDown() {
        this.racket.setAccel(racket.getAccel().getX(), stepAccel);
    }

    /**
     * Accelerates the racket in up direction.
     */
    public void keyUp() {
        this.racket.setAccel(racket.getAccel().getX(), -stepAccel);
    }

    /**
     * Accelerates the racket in left direction.
     */
    public void keyLeft() {
        this.racket.setAccel(-stepAccel, racket.getAccel().getY());
    }

    /**
     * Accelerates the racket in right direction.
     */
    public void keyRight() {
        this.racket.setAccel(stepAccel, racket.getAccel().getY());
    }

    /**
     * Sets the acceleration in down direction.
     */
    public void keyDownReleased() {
        this.racket.setAccel(racket.getAccel().getX(), 0);
    }

    /**
     * Sets the acceleration in up direction.
     */
    public void keyUpReleased() {
        this.racket.setAccel(racket.getAccel().getX(), 0);
    }

    /**
     * Sets the acceleration in left direction.
     */
    public void keyLeftReleased() {
        this.racket.setAccel(0, racket.getAccel().getY());
    }

    /**
     * Sets the acceleration in right direction.
     */
    public void keyRightReleased() {
        this.racket.setAccel(0, racket.getAccel().getY());
    }

    /**
     * Sets the racket position when space key is pressed.
     */
    public void keySpace() {
        this.racket.setPosition(racketInitialPos);
        this.racket.setSpeed(0, 0);
    }
}
