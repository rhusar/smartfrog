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

package org.smartfrog.examples.orchdws.stresstest;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Description of the Class
 */
public class StressManagerFrame extends JFrame {
    JSlider js;
    JTextField jtf;
    int maxValue = 100;
    int minValue = 0;
    int majorTickSpace = maxValue / 4;
    int minorTickSpace = majorTickSpace / 5;
    Hashtable loadGenerators = new Hashtable();

    //sychronize hash table.

    /**
     * Constructor for the StressManagerFrame object
     *
     * @param title Description of the Parameter
     */
    public StressManagerFrame(String title) {
        super(title);
        this.setVisible(false);
        js = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, maxValue / 2);
        js.setMajorTickSpacing(majorTickSpace);
        js.setMinorTickSpacing(minorTickSpace);
        js.setPaintTicks(true);
        js.setPaintLabels(true);
        js.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        js.setForeground(Color.black);
        js.setBorder(BorderFactory.createEtchedBorder());
        js.addChangeListener(new JSliderHandler());

        jtf = new JTextField(15);
        jtf.setEditable(false);
        jtf.setText("Rate " + js.getValue() + "%");

        JPanel p = new JPanel();
        p.add(js);

        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(jtf, BorderLayout.SOUTH);

        setBounds(100, 100, 300, 120);
    }

    public void register(String name, StressTester comp) {
        loadGenerators.put(name, comp);
    }

    public void setValue(int value) {
        this.js.setValue(value);
    }

    public void deRegister(String name) {
        loadGenerators.remove(name);
    }

    public void setFrequency(int freq) {
        if ((freq >= this.minValue) && (freq <= this.maxValue)) {
            for (Enumeration e = loadGenerators.elements();
                    e.hasMoreElements();) {
                try {
                    StressTester element = ((StressTester) e.nextElement());
                    element.setFrequency(freq);
                } catch (Exception ex) {
                    // ignore
                }
            }
        }
    }

    /**
     * The main program for the StressManagerFrame class
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        StressManagerFrame tjs = new StressManagerFrame("Drink beer!");
    }

    /**
     * Description of the Class
     */
    class JSliderHandler implements ChangeListener {
        /**
         * Description of the Method
         *
         * @param ce Description of the Parameter
         */
        public void stateChanged(ChangeEvent ce) {
            if (!(((JSlider) ce.getSource()).getValueIsAdjusting())) {
                jtf.setText("Rate " + js.getValue() + "%");

                int freq = maxValue - js.getValue();

                for (Enumeration e = loadGenerators.elements();
                        e.hasMoreElements();) {
                    try {
                        StressTester element = ((StressTester) e.nextElement());
                        element.setFrequency(freq);
                    } catch (Exception ex) {
                        // ignore this time - next time may work
                    }
                }
            }
        }
    }
}
