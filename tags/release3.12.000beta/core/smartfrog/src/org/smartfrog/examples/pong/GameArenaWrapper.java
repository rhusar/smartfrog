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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


/**
 *  A wrapper to turn the sfPong game into a SmartFrog component. Creates a
 *  Frame and gets a handle on the opponent if there is one.
 */
public class GameArenaWrapper extends EventPrimImpl implements Prim, Linker {
    /** Game Frame. */
    GameFrame gameFrame;
    /** Game Arena. */
    ConnectedGameArena gameArena;
    /** The opponent. */
    protected Linker opponent;
    /** String name for player. */
    String myName;
    /** String name for opponent. */
    String opponentName;
    /** Window size. */
    Dimension windowSize;

    /**
     * Standard constructor for SmartFrog components.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public GameArenaWrapper() throws RemoteException {
    }

    /**
     * Collect data from the description.
     * Overrides EventPrimImpl.sfDeploy.
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
        try {
            super.sfDeploy();
            if (sfLog().isInfoEnabled()) sfLog().info(" Deploying game");
            myName = (String) sfResolve("name");

            // get optional attribute opponent
            this.opponent = (Linker) sfResolve("opponent", false);

            // get mandatory attribute windowSize
            Vector dim = (Vector) sfResolve("windowSize");

            windowSize = new Dimension(((Integer) dim.elementAt(0)).intValue(),
                    ((Integer) dim.elementAt(1)).intValue());
        } catch (SmartFrogException sfex) {
            // add the context in case of failure
            sfex.put(SmartFrogCoreKeys.SF_DEPLOY_FAILURE, this.sfContext);

            // terminate component
            Reference name = sfCompleteNameSafe();
            terminateComponent(this, sfex, name);
            throw sfex;
        } catch (Exception e) {
            // trigger termination of component
            Reference name = sfCompleteNameSafe();
            terminateComponent(this, e, name);
            throw new SmartFrogDeploymentException(e, this);
        }
    }

    /**
     * Build a game frame and start the game.
     *
     * @throws SmartFrogException In case of error while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        try {
            super.sfStart();

            // create the frame, add the game arena, start the game
            if (sfLog().isInfoEnabled()) sfLog().info(" Starting game");
            gameFrame = new GameFrame(windowSize);
            gameArena = new ConnectedGameArena(windowSize, this);
            gameFrame.getContentPane().add(gameArena, BorderLayout.CENTER);

            // add a listener to catch the window closing event
            gameFrame.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        GameArenaWrapper.this.sfTerminate(new TerminationRecord(
                                "normal", "Window closed", null));
                    }
                });

            if (opponent != null) {
                gameArena.setOpponentName((String) opponent.sfResolve("name"));
            }

            gameArena.startGame();
        } catch (SmartFrogException sfex) {
            // add the context in case of failure
            sfex.put(SmartFrogCoreKeys.SF_START_FAILURE, this.sfContext);
            Reference name = sfCompleteNameSafe();
            // trigger termination of component
            terminateComponent(this, sfex, name);
            throw sfex;
        } catch (Exception e) {
            Reference name = sfCompleteNameSafe();
            // trigger termination of component
            terminateComponent(this, e, name);
            throw new SmartFrogLifecycleException(e, this);
        }
    }

    /**
     * sfTerminate.
     *
     * @param tr TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        if (sfLog().isInfoEnabled()) sfLog().info(" Ending game");
        // check if gameArena is initialized
        if (gameArena != null) {
            gameArena.stopGame();
            gameFrame.dispose();
        }
        gameFrame = null;
        super.sfTerminateWith(tr);
    }

    /**
     * Implementation of the linker interface : reset the ball position and
     * invert the horizontal speed.
     *
     * @param xPos Position in x axis
     * @param yPos Position in y axis
     * @param xSpeed Speed in x axis
     * @param ySpeed Speed in y axis
     *
     * @throws Exception error while setting position and speed
     */
    public synchronized void incomingBall(double xPos, double yPos,
        double xSpeed, double ySpeed) throws Exception {
        // treat the entry of the ball
        gameArena.ball.setPosition(xPos - 20, yPos);
        gameArena.ball.setSpeed(xSpeed, ySpeed);
    }

    /**
     * Hide the ball on this host, then hand it over to the opponent.
     *
     * @param ball a Ball object
     */
    public synchronized void ballIsLeaving(Ball ball) {
        // get the ball data.
        double xBallPos = ball.getPosition().getX();

        // get the ball data.
        double yBallPos = ball.getPosition().getY();
        double xBallSpeed = ball.getSpeed().getX();
        double yBallSpeed = ball.getSpeed().getY();

        if (opponent != null) {
            try {
                // hand it over to the opponent.
                opponent.incomingBall(xBallPos, yBallPos, xBallSpeed,
                yBallSpeed);

                //hide the ball on this host
                gameArena.ball.setSpeed(0, 0);
                gameArena.ball.setPosition(-100, -100);
            } catch (Exception e) {
                // if the opponent can't be reached, reset and do nothing
                opponent = null;
                gameArena.setOpponentName(" Yourself");
            }
        }
    }

    /**
     * Handle an incoming message.
     * If the message received indicates that a new player has been located,
     * set it as the opponent if :
     * - it is a different component
     * - and if it does not already have an opponent.
     *
     * @param event incoming message
     */
    public synchronized void handleEvent(String event) {
        // the event String could actually be picked up from the description of
    // the advertiser itself.
        if (event.equals("service:sfPongPlayer")) {
            try {
                // the 'opponent' attribute is a lazy link to the
        // resultsCollector's results vector  in the advertised case
                Vector allResults = (Vector) sfResolve("opponent");

                for (Enumeration e = allResults.elements();
                        e.hasMoreElements();) {
                    Linker discoveredPlayer = (Linker) e.nextElement();

                    // if the object discovered is not myself and if it does not
            // have an opponent, it's the one!
                    if ((!discoveredPlayer.equals(this)) &&
                            discoveredPlayer.setOpponent(this)) {
                        this.opponent = discoveredPlayer;

                        // set the name on the display
                        gameArena.setOpponentName((String) opponent.sfResolve("name"));
                        if (sfLog().isInfoEnabled()) sfLog().info ("Discovered " + ((Prim) discoveredPlayer).sfCompleteName());
                    }
                }
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error (ex);
            }
        }

        // do not treat any other event
    }

    /**
     * Set the opponent of this player to be the Linker object provided if none
     * already exists.
     * @param opponent opponent
     *
     * @throws RemoteException in case of network/rmi error
     * @return true if the operation is successful (i.e if the opponent was not
     * already set)
     */
    public synchronized boolean setOpponent(Linker opponent)
        throws RemoteException {
        if (this.opponent == null) {
            try {
                // update the display with the opponent's name.
                gameArena.setOpponentName((String) opponent.sfResolve("name"));
            } catch (Exception ex) {
            }

            this.opponent = opponent;

            // success
            return true;
        }

        return false;
    }
}
