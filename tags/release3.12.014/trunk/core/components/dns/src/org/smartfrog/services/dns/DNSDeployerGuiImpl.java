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

package org.smartfrog.services.dns;



import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import javax.swing.JDialog;
import javax.swing.WindowConstants;


/**
 * Triggers an interaction with the user to select a service instance.
 * Pops up a swing menu.
 * 
 * 
 */
public class DNSDeployerGuiImpl extends JPanel implements DNSDeployerGui {

    /** Enclosing frame for this gui. */
    JFrame frame;

    /** Index of the chosen service in the array. */
    int result = NOT_CHOSEN;

    /** A set of services to choose from. */
    DNSServiceInstance[]  services = null;

    /** No result yet. */ 
    public static final int NOT_CHOSEN = -1;

    /**
     * Creates a new <code>Test</code> instance.
     *
     * @param frame a <code>JFrame</code> value
     * @param services a <code>DNSServiceInstance[]</code> value
     */
    public DNSDeployerGuiImpl(JFrame frame, DNSServiceInstance[]  services ) {

        super(new BorderLayout());

        this.frame = frame;
        this.services = services;
        
        JLabel title = new JLabel("Click the Deploy button"
                                  + " after selecting a service.",
                                  JLabel.CENTER);

        //Create the components.
        JPanel choicePanel = createServicesDialogBox();
        choicePanel.setBorder(BorderFactory.createEmptyBorder(20,20,5,20));
        add(title, BorderLayout.NORTH);  
        add(choicePanel, BorderLayout.CENTER);
    }



    /**
     * Interactive function to pick up one service. Blocks until selection
     * is done.
     *
     * @return A service chosen by the user.
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public synchronized DNSServiceInstance pickOne() {
        
        while (result == NOT_CHOSEN) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
        DNSServiceInstance resultInstance =  services[result];
        // we run the clean-up asynchronously...
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   dispose();
               }
            });
        return resultInstance;
      }


    /**
     * Describe <code>setNumber</code> method here.
     *
     * @param number an <code>int</code> value
     */
    public synchronized void setResult(int number) {

        result = number;
        if (result != NOT_CHOSEN) {
            this.notifyAll();
        }
    }

    /**
     * Cleans-up.
     *
     */
    public synchronized void dispose() {

        frame.dispose();
    }


    /**
     * Creates a panel for the services choice. 
     *
     * @return A panel for the services.
     */
    JPanel createServicesDialogBox() {


        JRadioButton[] radioButtons = new JRadioButton[services.length];
        final ButtonGroup group = new ButtonGroup();

        for (int i=0; i< services.length; i++) {
            radioButtons[i] = new JRadioButton(services[i].toString());
            radioButtons[i].setActionCommand(Integer.toString(i));
        }
        for (int i=0; i< services.length; i++) {
            group.add(radioButtons[i]);
        }

        //Select the first button by default.
        radioButtons[0].setSelected(true);
        JButton deployButton = new JButton("Deploy!!");
        
        deployButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String command = group.getSelection().getActionCommand();
                    setResult(Integer.parseInt(command));
                }
            });

        return createPane("the services:", radioButtons, deployButton);

    }
                                     

    
    /**
     * Combines basic elements to form a service panel.
     *
     * @param description a <code>String</code> value
     * @param radioButtons a <code>JRadioButton[]</code> value
     * @param showButton a <code>JButton</code> value
     * @return a <code>JPanel</code> value
     */
    JPanel createPane(String description,
                      JRadioButton[] radioButtons,
                      JButton showButton) {

        int numChoices = radioButtons.length;
        JPanel box = new JPanel();
        JLabel label = new JLabel(description);

        box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
        box.add(label);

        for (int i = 0; i < numChoices; i++) {
            box.add(radioButtons[i]);
        }

        JPanel pane = new JPanel(new BorderLayout());
        pane.add(box, BorderLayout.NORTH);
        pane.add(showButton, BorderLayout.SOUTH);
        return pane;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     * @param out An array to return a GUI handler.
     * @param all a <code>DNSServiceInstance[]</code> value
     */
    private static void createAndShowGUI(DNSDeployerGui[] out,
                                         DNSServiceInstance[] all) {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        
        //Create and set up the window.
        JFrame frame = new JFrame("Select deployment service");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); 
        
        //Set up the content pane.
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(1,1));
        DNSDeployerGuiImpl gui = new DNSDeployerGuiImpl(frame, all);

        contentPane.add(gui);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        // return value...
        out[0] = gui;    
    }


    /**
     * Creates a GUI to show a collection of instances.
     *
     * @param all a <code>DNSServiceInstance[]</code> value
     * @return a <code>DNSDeployerGui</code> value
     * @exception SmartFrogDeploymentException if an error occurs
     */
    public static DNSDeployerGui getInstance(final DNSServiceInstance[] all) 
       throws SmartFrogDeploymentException {

       final DNSDeployerGui[] instance = new DNSDeployerGui[1];

       try {
           // Wait because I need to get a valid "future" before continuing
           javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                   public void run() {
                       createAndShowGUI(instance, all);
                       
                   }
               });
           return instance[0];
       } catch (Exception e) {
           throw new SmartFrogDeploymentException("Got Gui exception", e);
       }

   }
    
}
