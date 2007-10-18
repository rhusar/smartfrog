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
 * Defines the obstacle object.
 */
public class Obstacle extends Mass {
    /** The dimension size. */
    protected Dimension size;

    /**
     * Constructor.
     *
     * @param mass the mass of the ball
     * @param initialPosition the initial position of the ball
     * @param initialSize the dimension size
     */
    public Obstacle(int mass, Point2D initialPosition, Dimension initialSize) {
        super(mass, initialPosition);
        this.setDimension(initialSize);
    }

    /**
     * Sets the dimension.
     *
     * @param size the dimension size
     *
     * @see #getDimension
     */
    protected void setDimension(Dimension size) {
        this.size = new Dimension(size);
    }

    /**
     * Sets the dimension.
     *
     * @param w the width
     * @param h the height
     *
     * @see #getDimension
     */
    protected void setDimension(int w, int h) {
        this.size = new Dimension(w, h);
    }

    /**
     * Gets the dimension size.
     *
     * @return the dimension size
     *
     * @see #setDimension
     */
    public Dimension getDimension() {
        return size; // clone() ???
    }

    /**
     * Checks if collision occurs in x direction.
     *
     * @param obs obstacle
     * @param deltaSpeedX speed in x direction
     * @param timeWindow window time
     *
     * @return status about the collision
     */ 
    double collideX(Obstacle obs, double deltaSpeedX, double timeWindow) {
        double initialDeltaPosLX = getLowerRightBound().getX() -
            obs.getUpperLeftBound().getX();
        double xCollTime = -1;

        if (initialDeltaPosLX <= 0) { // obs is on the left of this obstacle
            xCollTime = -initialDeltaPosLX / deltaSpeedX;

            if ((xCollTime <= timeWindow) && (xCollTime >= 0)) {
                return xCollTime;
            } else {
                return -1;
            }
        }

        double initialDeltaPosRX = getUpperLeftBound().getX() -
            obs.getLowerRightBound().getX();

        if (initialDeltaPosRX >= 0) { // obs is on the right of this obstacle
            xCollTime = -initialDeltaPosRX / deltaSpeedX;

            if ((xCollTime <= timeWindow) && (xCollTime >= 0)) {
                return xCollTime;
            } else {
                return -1;
            }
        }

        return 0;
    }

    /**
     * Checks if collision occurs in y direction.
     *
     * @param obs obstacle
     * @param deltaSpeedY speed in y direction
     * @param timeWindow window time
     *
     * @return status about the collision
     */ 
    double collideY(Obstacle obs, double deltaSpeedY, double timeWindow) {
        double initialDeltaPosUY = getLowerRightBound().getY() -
            obs.getUpperLeftBound().getY();
        double yCollTime = -1;

        if (initialDeltaPosUY <= 0) { 
        // obs is 'under' this obstacle --> lower on the screen
            yCollTime = -initialDeltaPosUY / deltaSpeedY;

            if ((yCollTime <= timeWindow) && (yCollTime >= 0)) {
                return yCollTime;
            } else {
                return -1;
            }
        }

        double initialDeltaPosOY = getUpperLeftBound().getY() -
            obs.getLowerRightBound().getY();

        if (initialDeltaPosOY >= 0) { 
        // obs is 'over' this obstacle --> higher on the screen
            yCollTime = -initialDeltaPosOY / deltaSpeedY;

            if ((yCollTime <= timeWindow) && (yCollTime >= 0)) {
                return yCollTime;
            } else {
                return -1;
            }
        }

        return 0;
    }

    /**
     * Collides the obstacle.
     *
     * @param wall the wall object
     * @param timeWindow collision time with window wall
     *
     * @return true if collison occurs else false
     */
    public boolean collide(Immobile wall, double timeWindow) {
        return wall.collide(this, timeWindow);
    }

    /**
     * Collides the obstacle.
     *
     * @param obs the obstacle object
     * @param timeWindow collision time with window
     *
     * @return true if collison occurs else false
     */
    public boolean collide(Obstacle obs, double timeWindow) {
        Point2D position = this.getPosition();
        Point2D speed = this.getSpeed();
        Point2D obstaclePosition = obs.getPosition();
        Point2D obstacleSpeed = obs.getSpeed();
        boolean collisionX = false;
        boolean collisionY = false;

        double deltaSpeedX = speed.getX() - obstacleSpeed.getX();
        double deltaSpeedY = speed.getY() - obstacleSpeed.getY();

        double collX = collideX(obs, deltaSpeedX, timeWindow);
        double collY = collideY(obs, deltaSpeedY, timeWindow);

        if (collX == 0) {
            collisionY = (collY >= 0);
        } else if (collX != -1) {
            collisionX = (collY != 0) ? (collX <= collY) : true;
        }

        if (collY == 0) {
            collisionX = (collX >= 0);
        } else if (collY != -1) {
            collisionY = (collX != 0) ? (collY < collX) : true;
        }

        if (collisionX) {
            Point2D collisionPosition = new Point2D.Double(position.getX() +
                    (collX * speed.getX()), position.getY());
            this.setPosition(collisionPosition);

            Point2D newObstaclePosition = new Point2D.Double(obstaclePosition.
                getX() + (collX * obstacleSpeed.getX()), 
                obstaclePosition.getY());
            obs.setPosition(newObstaclePosition);

            double posdiff = getPosition().getX() - obs.getPosition().getX();
            double dimdiff = (getDimension().getWidth() / 2) +
                (obs.getDimension().getWidth() / 2);

            elasticCollision(this, obs, Util.XAxisCollision);
        } else if (collisionY) {
            Point2D collisionPosition = new Point2D.Double(position.getX(),
                    position.getY() + (collY * speed.getY()));
            this.setPosition(collisionPosition);

            Point2D newObstaclePosition = new Point2D.Double(obstaclePosition.
                getX(), obstaclePosition.getY() + (collY * 
                    obstacleSpeed.getY()));
            obs.setPosition(newObstaclePosition);
            elasticCollision(this, obs, Util.YAxisCollision);
        }

        return (collisionX || collisionY);
    }

    /**
     * Elastic collision of obstacles.
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
            double newSpeedX1 = (((2 * m2) / (m1 + m2)) * speed2.getX()) +
                (((m1 - m2) / (m2 + m1)) * speed1.getX());
            double newSpeedX2 = (((2 * m1) / (m1 + m2)) * speed1.getX()) +
                (((m2 - m1) / (m2 + m1)) * speed2.getX());
            obs1.setSpeed(new Point2D.Double(newSpeedX1, speed1.getY()));
            obs2.setSpeed(new Point2D.Double(newSpeedX2, speed2.getY()));
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

    /**
     * Gets the lower right bound.
     *
     * @return the lower right bound.
     */
    public Point2D getLowerRightBound() {
        return new Point2D.Double(getPosition().getX() +
            (getDimension().getWidth() / 2),
            getPosition().getY() + (getDimension().getHeight() / 2));
    }

    /**
     * Gets the upper left bound.
     *
     * @return the upper left bound.
     */
    public Point2D getUpperLeftBound() {
        return new Point2D.Double(getPosition().getX() -
            (getDimension().getWidth() / 2),
            getPosition().getY() - (getDimension().getHeight() / 2));
    }

    /**
     * Draws the graphics object.
     *
     * @param g2d graphics object
     */ 
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
        g2d.setColor(Color.yellow);

        double w = this.getDimension().getWidth();
        double h = this.getDimension().getHeight();

        g2d.drawRect((int) (this.getPosition().getX() - (w / 2)),
            (int) (this.getPosition().getY() - (h / 2)), (int) w, (int) h);
    }
}
