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

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Defines the Game Arena for pong game.
 */
public class GameArena extends Canvas implements KeyListener {
     /** Log for this class, created using class name*/
    LogSF log = LogFactory.getLog(this.getClass());

    /** Image object. */
    static Image offScreenImage = null;
    /** Graphics object. */
    static Graphics2D graphics_handle;
    /** The Gravity constant. */
    public static int GravityConstant = 100;

    /** Display data - dimension size. */
    Dimension size;
    /** Display data - game frame. */
    GameFrame gameFrame;
    /** The highest score for the pong game. */
    public int score = 10;

    /** Animation thread data - Thread object. */
    Thread circus = null;
    /** Flag indicating whether heart beating is on or not. */
    boolean beating = false;
    /** Flag indicating whether game is started or not. */
    boolean started = false;
    /** Flag indicating whether game is lost or not. */
    boolean lost = false;
    /** Animation thread data - heartbest. */
    int heartbeat = 40; //ms
    /** Animation thread data - last frame time. */
    long lastFrameTime = 0;

    /** Game object - player. */
    Player player;
    /** Game object - ball. */
    Ball ball;
    /** Game object - racket. */
    Racket racket;
    /** String name for opponent. */
    String opponentName = "Yourself";

    /** Vector for Immobile objects. */
    Vector walls = new Vector();
    /** Immobile object - wallEast. */
    Immobile wallEast;
    /** Immobile object - wallWest. */
    Immobile wallWest;
    /** Immobile object - wallNorth. */
    Immobile wallNorth;
    /** Immobile object - wallSouth. */
    Immobile wallSouth;

    /**
     * Constructor.
     *
     * @param size the dimension size.
     */
    public GameArena(Dimension size) {
        this.size = size;
        this.setSize(size);
        this.addKeyListener(this);
        initGameArena();
    }

    /**
     * Starts the pong game.
     */
    protected void startGame() {
        beating = true;

        if (circus == null) {
            circus = new Thread(new Runnable() {
                        public void run() {
                            while (beating) {
                                try {
                                    if (!lost) {
                                        Thread.sleep(heartbeat);
                                        updateGameArena();
                                    }

                                    repaint();
                                } catch (Exception e) {
                                    if (log.isErrorEnabled()) log.error (e);
                                    beating = false;
                                }
                            }
                        }
                    });
        }

        circus.start();
    }

    /**
     * Stops the pong game.
     */
    protected void stopGame() {
        beating = false;
    }

    /**
     * Gets the dimension size.
     *
     * @return the dimension size
     */
    public Dimension getDimension() {
        return this.getSize();
    }

    /**
     * Sets the opponent name.
     *
     * @param name the opponent name
     */
    public void setOpponentName(String name) {
        opponentName = name;
    }

    /**
     * Starts all the collisions.
     *
     * @param elapsed the elapsed time
     */
    public void allCollisions(double elapsed) {
        racket.collide(ball, elapsed);

        for (Enumeration e = walls.elements(); e.hasMoreElements();) {
            Immobile wall = (Immobile) e.nextElement();
            racket.collide(wall, elapsed);

            if (ball.collide(wall, elapsed)) {
                wallHasBeenHit(wall);
            }
        }
    }

    /**
     * Checks if wall has been hit or not.
     *
     * @param wall an Immobile object
     */
    protected void wallHasBeenHit(Immobile wall) {
        if (wall == wallWest) {
            score--;
        }
    }

    /**
     * Updates the game arena for pong game.
     */
    public void updateGameArena() {
        double elapsed = 0;

        if (lastFrameTime != 0) {
            elapsed = new Double((System.currentTimeMillis() - lastFrameTime)).
            doubleValue() / 1000; // might not be the right place
        }

        lastFrameTime = System.currentTimeMillis();

        allCollisions(elapsed);
        racket.updateSpeed(elapsed);

        // and finish with normal movement
        racket.updatePosition(elapsed); //should be conditional to collision
        ball.updatePosition(elapsed);
    }

    /**
     * Initiates the game arena for pong game by adding players and objects.
     */
    public void initGameArena() {
        // first create or get racket, player, and ball
        racket = new Racket(100,
                new Point2D.Double(50, getDimension().getHeight() / 2),
                new Dimension(20, 100));

        // add players // probably Player objects already deployed, launched
    // through SF and
        player = new Player(racket);

        // add ball
        ball = new Ball(20,
                new Point2D.Double(getDimension().getWidth() / 2,
                    (getDimension().getHeight() / 2) - 50),
                new Dimension(25, 25));

        // add walls
        wallNorth = new Immobile(100,
                new Point2D.Double(size.getWidth() / 2, -10),
                new Dimension((int) size.getWidth(), 20));
        wallSouth = new Immobile(100,
                new Point2D.Double(size.getWidth() / 2, size.getHeight() - 15),
                new Dimension((int) size.getWidth(), 20));
        wallEast = new Immobile(100,
                new Point2D.Double(size.getWidth() + 5, size.getHeight() / 2),
                new Dimension(10, (int) size.getHeight()));
        wallWest = new Immobile(100,
                new Point2D.Double(-5, size.getHeight() / 2),
                new Dimension(10, (int) size.getHeight()));
        walls.addElement(wallNorth);
        walls.addElement(wallSouth);
        walls.addElement(wallEast);
        walls.addElement(wallWest);
    }

    /**
     * KeyTyped event.
     *
     * @param e keyevent
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * KeyPressed event.
     *
     * @param e keyevent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case (KeyEvent.VK_DOWN):
            player.keyDown();

            break;

        case (KeyEvent.VK_UP):
            player.keyUp();

            break;

        case (KeyEvent.VK_LEFT):
            player.keyLeft();

            break;

        case (KeyEvent.VK_RIGHT):
            player.keyRight();

            break;

        case (KeyEvent.VK_SPACE):

            if (lost) {
                score = 10;
                ball.setPosition(getDimension().getWidth() / 2,
                    (getDimension().getHeight() / 2) - 50);
                ball.setSpeed(0, 0);
                lost = false;
            }

            player.keySpace();

            break;
        }
    }

    /**
     * KeyReleased event.
     *
     * @param e keyevent
     */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        case (KeyEvent.VK_DOWN):
            player.keyDownReleased();

            break;

        case (KeyEvent.VK_UP):
            player.keyUpReleased();

            break;

        case (KeyEvent.VK_LEFT):
            player.keyLeftReleased();

            break;

        case (KeyEvent.VK_RIGHT):
            player.keyRightReleased();

            break;
        }
    }

    /**
     * Graphics update.
     * Overrides "Canvas.update" method.
     *
     * @param g the graphics object
     */
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Paint method.
     *
     * @param g the graphics object
     */
    public void paint(Graphics g) {
        paintCanvas(g);
    }

    /**
     * Paint method.
     *
     * @param g the graphics object
     */
    public void paintCanvas(Graphics g) {
        // System.out.println("My paint"+Util.trace++);
        if (offScreenImage == null) {
            offScreenImage = createImage(this.getSize().width,
                    this.getSize().height);
        }

        Graphics2D g2d = getImageHandle();

        if (g2d != null) {
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, this.getSize().width, this.getSize().height);
            racket.draw(g2d);
            ball.draw(g2d);

            // walls
            for (Enumeration e = walls.elements(); e.hasMoreElements();) {
                ((Obstacle) e.nextElement()).draw(g2d);
            }

            // score
            if (score > 0) {
                g2d.drawString("SCORE :" + score, 20,
                    (int) size.getHeight() - 40);
            } else {
                g2d.drawString("YOU LOSE", (int) size.getWidth() / 2,
                    (int) size.getHeight() / 2);
                lost = true;
            }

            g2d.drawString("You are playing against :" + opponentName, 120,
                (int) size.getHeight() - 40);
            g.drawImage(offScreenImage, 0, 0, null);
        }

        lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Graphics handle.
     *
     * @return Graphics object
     */
    final static Graphics2D getImageHandle() {
        if (offScreenImage != null) {
            graphics_handle = (Graphics2D) offScreenImage.getGraphics();
        }

        return graphics_handle;
    }
}
