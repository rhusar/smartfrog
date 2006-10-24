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

package org.smartfrog.examples.dynamicwebserver.gui.graphpanel;

import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class CompleteGraphPanel extends JPanel { // implements KeyListener{

    private TitledBorder titledBorder1;
    private BorderLayout borderLayout1 = new BorderLayout();
    private Border border1;
    private JPanel allGraphPanel = new JPanel();
    private BorderLayout borderLayout4 = new BorderLayout();
    protected JPanel graphCenterPanel = new JPanel();
    private JPanel yAxisScalePanel = new JPanel();
    private JPanel yMinyMaxPanel = new JPanel();
    private BorderLayout borderLayout3 = new BorderLayout();
    private JLabel yMinLabel = new JLabel();
    private BorderLayout borderLayout2 = new BorderLayout();
    private JLabel yMaxLabel = new JLabel();
    private BorderLayout borderLayout5 = new BorderLayout();
    private Border border2;
    public JPanel extraPanel = new JPanel();
    private BorderLayout borderLayout6 = new BorderLayout();
    private Border border3;

    // A title
    public String title = "A graph";
    public String yMin = "";
    public String yMax = "";
    GraphPanel gp = null;

    public CompleteGraphPanel() {
        try {
            jbInit();
        } catch (Exception e) {
           LogSF sfLog = LogFactory.getLog(this.getClass());
           if (sfLog.isErrorEnabled()) sfLog.error (e);
        }
    }

    /**
     * Create a graph panel with two labels indicating yMin & yMax
     *
     * @param title : the string indicating the type of data displayed
     * @param yMin : the string displayed by the lower label
     * @param yMax : the string displayed by the upper label
     */
    public CompleteGraphPanel(String title, String yMin, String yMax) {
        this.title = title;
        this.yMin = yMin;
        this.yMax = yMax;

        try {
            jbInit();
        } catch (Exception e) {
            LogSF sfLog = LogFactory.getLog(title+"_CompleteGraphPanel");
            if (sfLog.isErrorEnabled()) sfLog.error (e);
        }

        setVisible(true);
    }

    private void jbInit() throws Exception {
        // set up panels, labels & their text and borders
        titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
                    new Color(182, 182, 182), new Color(89, 89, 89)), title);
        border1 = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(
                    Color.white, new Color(148, 145, 140)),
                BorderFactory.createEmptyBorder(2, 2, 2, 2));
        border2 = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        border3 = BorderFactory.createEmptyBorder(10, 5, 10, 5);
        this.setBackground(Color.lightGray);
        this.setBorder(titledBorder1);
        this.setLayout(borderLayout1);

        allGraphPanel.setLayout(borderLayout4);
        yAxisScalePanel.setBackground(Color.black);
        yAxisScalePanel.setLayout(borderLayout2);
        yMinyMaxPanel.setBackground(Color.black);
        yMinyMaxPanel.setBorder(border2);
        yMinyMaxPanel.setLayout(borderLayout3);

        setLabels(yMin, yMax);
        allGraphPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        graphCenterPanel.setLayout(borderLayout5);
        extraPanel.setBackground(Color.black);
        extraPanel.setBorder(border3);
        extraPanel.setLayout(borderLayout6);
        add(allGraphPanel, BorderLayout.CENTER);
        allGraphPanel.add(graphCenterPanel, BorderLayout.CENTER);
        allGraphPanel.add(yAxisScalePanel, BorderLayout.WEST);
        yAxisScalePanel.add(yMinyMaxPanel, BorderLayout.CENTER);
        yMinyMaxPanel.add(yMaxLabel, BorderLayout.NORTH);
        yMinyMaxPanel.add(yMinLabel, BorderLayout.SOUTH);
        yMinyMaxPanel.add(extraPanel, BorderLayout.CENTER);
    }

    /**
     * Add a GraphPanel to this panel
     *
     * @param gp DOCUMENT ME!
     */
    public void addGraphPanel(GraphPanel gp) {
        this.gp = gp;
        graphCenterPanel.add(gp, BorderLayout.CENTER);
        gp.setCompleteGraphPanel(this);
    }

    public void setLabels(String yMin, String yMax) {
        yMinLabel.setForeground(Color.white);
        yMinLabel.setText(yMin);
        yMaxLabel.setForeground(Color.white);
        yMaxLabel.setText(yMax);
    }
}
