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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
/**
 * Defines the mass object.
 */
public class Mass {
    /** Mass. */    
    private int mass;
    /** Position. */    
    private Point2D position;
    /** Speed. */   
    private Point2D speed;
    /** Acceleration. */    
    private Point2D accel;
    /** Maximum speed. */   
    private double maxSpeed = 500;

    /**
     * Constructor.
     */
    public Mass() {
    }

    /**
     * Constructor.
     *
     * @param initialMass the mass of the ball
     * @param initialPosition the initial position of the ball
     */
    public Mass(int initialMass, Point2D initialPosition) {
        this.position = initialPosition;
        this.mass = initialMass;
        this.speed = new Point2D.Double(0, 0); // no initial speed
        this.accel = new Point2D.Double(0, 0); // no initial speed
    }

    /**
     * Gets the acceleration.
     *
     * @return the acceleration
     *
     * @see #setAccel
     */
    public final Point2D getAccel() {
        return (Point2D) accel.clone();
    }

    /**
     * Gets the speed.
     *
     * @return the speed
     *
     * @see #setSpeed
     */
    public final Point2D getSpeed() {
        return (Point2D) speed.clone();
    }

    /**
     * Gets the initial position.
     *
     * @return the initial position
     *
     * @see #setPosition
     */
    public final Point2D getPosition() {
        return (Point2D) position.clone();
    }

    /**
     * Gets the initial mass.
     *
     * @return the initial mass
     *
     * @see #setMass
     */
    public final int getMass() {
        return mass;
    }

    /**
     * Sets the mass.
     *
     * @param mass the mass
     *
     * @see #getMass
     */
    protected void setMass(int mass) {
        this.mass = mass;
    }

    /**
     * Sets the acceleration.
     *
     * @param accelX the xpos
     * @param accelY the ypos
     *
     * @see #getAccel
     */
    protected void setAccel(double accelX, double accelY) {
        this.accel.setLocation(accelX, accelY);
    }

    /**
     * Sets the speed.
     *
     * @param speed the speed
     * @see #getSpeed
     */
    protected void setSpeed(Point2D speed) {
        this.setSpeed(speed.getX(), speed.getY());
    }

    /**
     * Sets the speed.
     *
     * @param xspeed the x speed
     * @param yspeed the y speed
     * @see #getSpeed
     */
    protected void setSpeed(double xspeed, double yspeed) {
        xspeed = (xspeed > maxSpeed) ? maxSpeed
                                     : ((xspeed < -maxSpeed) ? (-maxSpeed)
                                                             : xspeed);
        yspeed = (yspeed > maxSpeed) ? maxSpeed
                                     : ((yspeed < -maxSpeed) ? (-maxSpeed)
                                                             : yspeed);
        this.speed.setLocation(xspeed, yspeed);
    }

    /**
     * Sets the position.
     *
     * @param position the position
     *
     * @see #getPosition
     */
    protected void setPosition(Point2D position) {
        this.position.setLocation(position);
    }

    /**
     * Sets the position.
     *
     * @param xpos the x position
     * @param ypos the y position
     *
     * @see #getPosition
     */
    protected void setPosition(double xpos, double ypos) {
        this.position.setLocation(xpos, ypos);
    }

    /**
     * Updates the speed.
     *
     * @param elapsed the updating factor
     */
    protected void updateSpeed(double elapsed) {
        double dvx = elapsed * accel.getX();
        double dvy = elapsed * accel.getY();
        speed.setLocation(dvx + speed.getX(), dvy + speed.getY());
    }

    /**
     * Updates the position.
     *
     * @param elapsed the updating factor
     */
    protected void updatePosition(double elapsed) {
        double dx = elapsed * speed.getX();
        double dy = elapsed * speed.getY();
        position.setLocation(dx + position.getX(), dy + position.getY());
    }

    /**
     * Draws the graphics object.
     *
     * @param g2d the graphics object
     */
    protected void draw(Graphics2D g2d) {
    }

    /**
     * Returns the textual representation.
     *
     * @return textual representation
     */ 
    public String toString() {
        return "Mass " + new Double(mass).intValue() + " \n is at: " +
        position.toString() + " \n at speed: " + speed.toString();
    }
}
