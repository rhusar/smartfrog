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

package org.smartfrog.examples.dynamicwebserver.gui.progresspanel;

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * ProgressPanel
 */

public class ProgressPanel extends JPanel {

   /** Log for this class, created using class name*/
   static LogSF sfLogStatic = LogFactory.getLog(ProgressPanel.class);
    /*
      Date format
    */
    static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS yyyy/MM/dd");

    static int i = 0;
    ImageIcon redBall;
    ImageIcon grnBall;
    ImageIcon yelBall;
    Vector balls = new Vector();
    GridLayout gridLayout1 = new GridLayout();
    JPanel jPanelBalls = new JPanel();
    JLabel jLabel3 = new JLabel();
    JPanel jPanelMsg = new JPanel();
    JTextField jTextFieldMsg = new JTextField();
    JLabel jLabelMsg = new JLabel();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    JPanel jPanelLabel = new JPanel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabelTitle = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    JLabel jLabel5 = new JLabel();
    FlowLayout flowLayoutBall = new FlowLayout(FlowLayout.LEFT, 0, 0);

    //(1,0);

    /**
     * Constructor for the ProgressPanel object
     *
     * @param title Description of the Parameter
     */
    public ProgressPanel(String title) {
        try {
            jbInit();
            jLabelTitle.setText(title);
        } catch (Exception ex) {
            sfLogStatic.err(ex);
        }
    }

    /**
     * Description of the Method
     *
     * @param ball Description of the Parameter
     * @param status Description of the Parameter
     * @param message Description of the Parameter
     */
    public void updateBall(int ball, int status, String message) {
        try {
            jTextFieldMsg.setText(message);

            if ((balls.size() < ball) || (ball <= 0)) {
                return;
            } else {
                JLabel label = ((JLabel) (balls.elementAt((ball - 1))));

                if (status == 0) {
                    label.setIcon(redBall);
                } else if (status == 1) {
                    message = "[" +dateFormat.format(new Date(System.currentTimeMillis())) + "] " + message;
                    label.setToolTipText(message);
                    label.setIcon(yelBall);
                } else if (status == 2) {
                    if (label.getIcon() == this.yelBall) {
                        label.setIcon(this.grnBall);
                    }
                } else {
                    // Non existent progress status
                }
            }
        } catch (Exception ex) {
            sfLogStatic.err(ex);
        }
    }

    /**
     * Description of the Method
     *
     * @param total Description of the Parameter
     */
    void createBalls(int total) {
        for (int c = 0; c < total; c++) {
            addBall();
        }
    }

    /**
     * Adds a feature to the Ball attribute of the ProgressPanel object
     */
    void addBall() {
        JLabel ball = new JLabel(this.redBall);
        jPanelBalls.add(ball);
        balls.add(ball);
    }

    /**
     * Description of the Method
     *
     * @exception Exception Description of the Exception
     */
    void jbInit() throws Exception {
        //Load Icons
        redBall = new ImageIcon(org.smartfrog.examples.dynamicwebserver.gui.progresspanel.ProgressPanel.class.getResource(
                    "red_bullet.gif"));
        grnBall = new ImageIcon(org.smartfrog.examples.dynamicwebserver.gui.progresspanel.ProgressPanel.class.getResource(
                    "grn_bullet.gif"));
        yelBall = new ImageIcon(org.smartfrog.examples.dynamicwebserver.gui.progresspanel.ProgressPanel.class.getResource(
                    "yel_bullet.gif"));

        //
        this.setFont(new java.awt.Font("Dialog", 0, 14));
        this.setBorder(BorderFactory.createRaisedBevelBorder());
        this.setLayout(gridLayout1);
        gridLayout1.setRows(3);
        gridLayout1.setColumns(1);
        jPanelBalls.setLayout(flowLayoutBall);
        jLabel3.setText("                   ");
        jTextFieldMsg.setText(
            "Deployed                                        ");
        jLabelMsg.setText("Last Msg:");
        jPanelLabel.setLayout(borderLayout1);
        jLabel4.setText("     ");
        jLabelTitle.setFont(new java.awt.Font("Serif", 1, 14));
        jLabelTitle.setForeground(Color.blue);
        jLabelTitle.setText("Title");
        jLabel5.setText("                  ");
        jPanelMsg.setLayout(gridBagLayout1);
        jPanelMsg.add(jTextFieldMsg,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 13, 0, 16), 200, 0));
        jPanelMsg.add(jLabelMsg,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 25, 0, 0), 0, 0));
        jPanelBalls.add(jLabel5, BorderLayout.WEST);
        jPanelBalls.add(jLabel3, BorderLayout.CENTER);
        this.add(jPanelLabel, null);
        this.add(jPanelBalls, null);
        this.add(jPanelMsg, null);

        jPanelLabel.add(jLabelTitle, BorderLayout.CENTER);
        jPanelLabel.add(jLabel4, BorderLayout.WEST);
    }


}
