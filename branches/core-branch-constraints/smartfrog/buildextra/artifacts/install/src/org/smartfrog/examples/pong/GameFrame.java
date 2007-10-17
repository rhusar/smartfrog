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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;

/**
 * Defines the Game Frame for pong game.
 */
public class GameFrame extends JFrame {

   /** Log for this class, created using class name*/
   static LogSF sfLogStatic = LogFactory.getLog(GameFrame.class);

    /** A player object. */
    public Player player = null;
    /** Default frame dimension. */
    Dimension size = new Dimension(320, 200); 

    /**
     * Constructor.
     *
     * @param size the dimension size
     */
    public GameFrame(Dimension size) {
        if (size != null) {
            this.size = size;
        }

        // add a game canvas
        try {
            FrameInit();
        } catch (Exception ex) {
            sfLogStatic.err(ex);
        }
    }

    /**
     * Initializes the frame.
     *
     * @throws Exception error while initalization
     */ 
    private void FrameInit() throws Exception {
        pack();
        setVisible(true);
        setResizable(false);

        // center in the screen
        Rectangle parentBounds = new Rectangle(this.getToolkit().
            getScreenSize());
        parentBounds.setLocation(0, 0);

        // Place the frame so its center is the same
        // as the center of the bounding rectangle
        setSize((int) size.getWidth(), (int) size.getHeight());

        int x = parentBounds.x +
            ((int) (parentBounds.width - size.getWidth()) / 2);
        int y = parentBounds.y +
            ((int) (parentBounds.height - size.getHeight()) / 2);
        setLocation(x, y);
    }

    /**
     * The main method creates a gameArena object and starts the game thread.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame(new Dimension(640, 480));

        // create a gameArena object
        GameArena gameArena = new GameArena(new Dimension(640, 480));
        gameFrame.getContentPane().add(gameArena, BorderLayout.CENTER);

        // start the game canvas' thread
        gameArena.startGame();
    }
}
