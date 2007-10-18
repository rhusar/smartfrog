/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.management;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;


import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.services.display.WindowUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.StringWriter;
import java.io.IOException;

/**
 *  Dialog to create/add/modify attributes of a SmartFrog component
 */
public class NewAttributeDialog extends JDialog {
    /** Log for this class, created using class name*/
    LogSF sfLog = LogFactory.getLog("sfManagementConsole");
    /** Panel. */
    private JPanel panel = new JPanel();
    /** Save button. */
    private JButton jButtonSave = new JButton();
    /** Cancel Button. */
    private JButton jButtonCancel = new JButton();

    /** Label name. */
    private JLabel jLabelName = new JLabel();
    /** Text filed. */
    private JTextField NamejTextField = new JTextField();
    /** Label name. */
    private JLabel jLabelTags = new JLabel();
    /** Text filed. */
    private JTextField tagsjTextField = new JTextField();
    /** Label value. */
    private JLabel jLabelValue = new JLabel();
    /** Scrool pane. */
    private JScrollPane jScrollPane1 = new JScrollPane();
    /** Text area. */
    private JTextArea ValuejTextArea = new JTextArea();
    /** Gird baglayout. */
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    /** Set of attributes. */
    private Object[] attribute = null;

    JLabel jLabel1 = new JLabel();

    /**
     * Constructs NewAttributeDialog with frame, title , modal and set of
     * attributes.
     *
     * @param frame frame
     * @param title title
     * @param modal modal
     * @param attribute set of attributes
     */
    public NewAttributeDialog(Frame frame, String title, boolean modal, Object[] attribute) {
        super(frame, title, modal);
        this.attribute = attribute;

        try {
            jbInit();
            pack();

            if (attribute != null) {
                if (attribute[0] != null) {
                    this.NamejTextField.setText(attribute[0].toString());
                }

                if (attribute[1] != null) {
                      String value = ContextImpl.getBasicValueFor(attribute[1]);
                    if  (attribute[1] instanceof ComponentDescription) {
                       StringWriter sw = new StringWriter();
                        try {
                            ((ComponentDescription)attribute[1]).writeOn(sw,0);
                        } catch (IOException ioex) {
                            // ignore should not happen
                            if (sfLog().isIgnoreEnabled()) sfLog().ignore (ioex);
                        }
                        value = sw.toString();
                    }
                    this.ValuejTextArea.setText(value);
                }
                if (attribute[2] != null) {
                    this.tagsjTextField.setText(attribute[2].toString());
                }
            }
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }
    }

    /**
     * Initializes the UI.
     * @throws Exception if there is any error during initialization
     */
    private void jbInit() throws Exception {
        panel.setLayout(gridBagLayout1);
        jButtonSave.setNextFocusableComponent(jButtonCancel);
        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jButtonSave_actionPerformed(e);
                }
            });
        jButtonCancel.setNextFocusableComponent(NamejTextField);
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jButtonCancel_actionPerformed(e);
                }
            });

        jLabelName.setText("Name");
        NamejTextField.setNextFocusableComponent(tagsjTextField);
        NamejTextField.setText("");

        jLabelTags.setText("Tags");
        tagsjTextField.setNextFocusableComponent(ValuejTextArea);
        tagsjTextField.setText("");

        jLabelValue.setText("Value");
        ValuejTextArea.setNextFocusableComponent(jButtonSave);
        panel.setMinimumSize(new Dimension(550, 300));
        panel.setPreferredSize(new Dimension(550, 300));
        jLabel1.setText("(use SF syntax)");
        getContentPane().add(panel);
        //Name
        panel.add(NamejTextField,
                new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(17, 13, 0, 23), 302, 0));
        panel.add(jLabelName,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(17, 24, 0, 0), 0, 0));
        //Tags
        panel.add(jLabelTags,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(17, 24, 0, 8), 0, 0));
        panel.add(tagsjTextField,
                new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(15, 13, 0, 23), 151, 0));
        //Buttons
        panel.add(jButtonCancel,
                new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 22, 13, 89), 0, 0));
        panel.add(jButtonSave,
                new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 85, 13, 0), 12, 0));
         //Value
         panel.add(jScrollPane1,
                  new GridBagConstraints(1, 2, 2, 2, 1.0, 1.0
                  , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(19, 13, 0, 23), 300, 130));
          panel.add(jLabelValue, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                  , GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(13, 24, 0, 0), 0, 0));
          panel.add(jLabel1, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                  , GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

          jScrollPane1.getViewport().add(ValuejTextArea, null);

    }

    /**
     * Interface Method.
     * @param e event
     */
    void jButtonSave_actionPerformed(ActionEvent e) {
        //Save values in parent!
        //Return with attribute value pair. Array[];
        try {
            if ((this.NamejTextField.getText() != null) &&
                    (!this.NamejTextField.getText().equals(""))) {
                attribute[0] = this.NamejTextField.getText();
                attribute[1] = parseValue(ValuejTextArea.getText(),"sf");
                attribute[2] = parseTags(tagsjTextField.getText(),"sf");
            } else {
                attribute[0] = null;
                attribute[1] = null;
                attribute[2] = null;
            }
        } catch (Exception e1) {
             if (sfLog().isErrorEnabled()) sfLog().error (e1);
             WindowUtilities.showError(this,"Failed to modify attribute '"+attribute.toString()+"'. \n"+e1.toString());
        }

        this.dispose();
    }

    /**
     * Parses the phases.
     * @param phase parsing phase
     * @param textToParse text to parse
     * @param language language
     * @return Compoent Description
     */
    public ComponentDescription parsePhase(String phase, String textToParse,
        String language) {
        Phases top = null;
        Vector phases = null;

        try {
            top = new SFParser(language).sfParse(textToParse);

            //System.out.println("TOP: "+ top.sfAsComponentDescription().toString());
            if (phase.equals("raw")) {
                return top.sfAsComponentDescription();
            }
        } catch (Throwable ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }

        try {
            if (top != null) {
                phases = top.sfGetPhases();

                // Parse up to the phase selected in sfComboBox
                Vector auxphases = new Vector();

                if ((phases != null) && (!phase.equals("all"))) {
                    Iterator iter = phases.iterator();
                    String temp;

                    while (iter.hasNext()) {
                        temp = (String) (iter.next());

                        //System.out.println("Temp: "+temp);
                        auxphases.add(temp);

                        if (temp.equals(phase)) {
                            break;
                        }

                        // end while
                    }

                    //end phases
                }

                if (phase.equals("all")) {
                    auxphases = (Vector) (phases.clone());

                    //for multilanguage!
                    top = top.sfResolvePhases();

                    return (top.sfAsComponentDescription());
                }

                //System.out.println("Phases: "+ auxphases.toString());
                top = top.sfResolvePhases(auxphases);

                return (top.sfAsComponentDescription());
            } else {
                //this.log("SFParse Failed. No top.", "Parse", 5);
                return null;

                //3 Info
            }
        } catch (Throwable ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }

        return null;
    }

    /**
     * Parse
     * @param textToParse  text to be parsed
     * @param language language
     * @return Object
     */
    public Object parseValue(String textToParse, String language) {
        try {
            SFParser parser = new SFParser(language);
            return parser.sfParseAnyValue( textToParse);
        } catch (Throwable ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }
        return null;
    }

    /**
     * Parse
     * @param textToParse  text to be parsed
     * @param language language
     * @return Object
     */
    public Object parseTags(String textToParse, String language) {
        try {
            SFParser parser = new SFParser(language);
            return parser.sfParseTags( textToParse);
        } catch (Throwable ex) {
            if (sfLog().isErrorEnabled()) sfLog().error (ex);
        }
        return null;
    }

    /**
     * Interface Method.
     * @param e event
     */
    void jButtonCancel_actionPerformed(ActionEvent e) {
        try {
            attribute[0] = null;
            attribute[1] = null;
            attribute[2] = null;
        } catch (Exception e1) {
            if (sfLog().isErrorEnabled()) sfLog().error (e1);
        }
        this.dispose();
    }

    private LogSF sfLog(){
        return sfLog;
    }
}
