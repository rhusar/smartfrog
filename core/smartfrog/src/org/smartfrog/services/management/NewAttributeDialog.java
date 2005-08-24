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

package org.smartfrog.services.management;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.parser.StreamParser;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

/**
 *  Dialog to create/add/modify attributes of a SmartFrog component
 */
public class NewAttributeDialog extends JDialog {
    /** Panel. */
    private JPanel panel = new JPanel();
    /** Save button. */
    private JButton jButtonSave = new JButton();
    /** Cancel Button. */
    private JButton jButtonCancel = new JButton();
    /** Label type. */
    private JLabel jLabelType = new JLabel();
    /** Label name. */
    private JLabel jLabelName = new JLabel();
    /** Text filed. */
    private JTextField NamejTextField = new JTextField();
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
    /** Component types. */
//    private String[] componentTypes = {"AnyValue",
//        "Integer", "Boolean", "ComponentDescription", "Reference",
//        "Long", "Float", "Double", "String"
//    };
    private String[] componentTypes = {"AnyValue"};
//        "Integer", "Boolean", "ComponentDescription", "Reference",
//        "Long", "Float", "Double", "String"
//    };


    /** Integer value for string. */
    static final int STRING = 8;
    /** Integer value for integer. */
    static final int INTEGER = 1;
    /** Integer value for boolean. */
    static final int BOOLEAN = 2;
    /** Integer value for component description. */
    static final int COMPONENT_DESCRIPTION = 3;
    /** Integer value for reference. */
    static final int REFERENCE = 4;
    /** Integer value for long. */
    static final int LONG = 5;
    /** Integer value for float. */
    static final int FLOAT = 6;
    /** Integer value for double. */
    static final int DOUBLE = 7;
    /** Integer value for PRIMVALUE. */
    static final int ANYVALUE = 0;

    //final int VECTOR = 3;
    private JComboBox TypejComboBox = new JComboBox(componentTypes);

    /**
     * Constructs NewAttributeDialog with frame, title , modal and set of
     * attributes.
     *
     * @param frame frame
     * @param title title
     * @param modal modal
     * @param attribute set of attributes
     */
    public NewAttributeDialog(Frame frame, String title, boolean modal,
        Object[] attribute) {
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
                    this.ValuejTextArea.setText(attribute[1].toString());
//                    selectComboIndex(attribute[1]);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //    public Object[] NewAttributeDialog() {
    //        this(null, "", false);
    //    }
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
        jLabelType.setText("Value");
        jLabelName.setText("Name");
        NamejTextField.setNextFocusableComponent(TypejComboBox);
        NamejTextField.setText("");
        jLabelValue.setText("(use SF syntax)");
        ValuejTextArea.setNextFocusableComponent(jButtonSave);
        panel.setMinimumSize(new Dimension(550, 300));
        panel.setPreferredSize(new Dimension(550, 300));
        getContentPane().add(panel);
//        panel.add(TypejComboBox,
//            new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
//                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
//                new Insets(15, 13, 0, 23), 151, 0));
        panel.add(NamejTextField,
            new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(17, 13, 0, 23), 302, 0));
        panel.add(jLabelName,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(17, 24, 0, 0), 0, 0));
        panel.add(jLabelType,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(17, 24, 0, 8), 0, 0));
        panel.add(jLabelValue,
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(13, 24, 140, 0), 0, 0));
        panel.add(jButtonCancel,
            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(16, 22, 13, 89), 0, 0));
        panel.add(jButtonSave,
            new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(16, 85, 13, 0), 12, 0));
        panel.add(jScrollPane1,
            new GridBagConstraints(1, 2, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(19, 13, 0, 23), 300, 130));
        jScrollPane1.getViewport().add(ValuejTextArea, null);
    }

    /**
     * Interface Method.
     * @param e event
     */
    void jButtonSave_actionPerformed(ActionEvent e) {
        //Save values in parent!
        //Return with attribute value pair. Array[];
        if ((this.NamejTextField.getText() != null) &&
                (!this.NamejTextField.getText().equals(""))) {
            attribute[0] = this.NamejTextField.getText();

            //attribute[1] = (String) this.TypejComboBox.getSelectedItem();
            attribute[1] = this.createValueObject(this.TypejComboBox.
                getSelectedIndex(),(String) this.ValuejTextArea.
                getText());
        } else {
            attribute[0] = null;
            attribute[1] = null;
        }

        this.dispose();
    }

    /**
     *     final int STRING = 0;
       final int INTEGER = 1;
       final int BOOLEAN = 2;
       final int VECTOR = 3;
       final int COMPONENT_DESCRIPTION = 4;
       final int REFERENCE = 5;
       final int LONG = 6;
       final int FLOAT = 7;
       final int DOUBLE = 8;
     * static
     */
    /**
     * Creates object from type and string value.
     * @param type of the object
     * @param valueStr value in string
     * @return the object created using type and value
     */
    Object createValueObject(int type, String valueStr) {
        try {
            switch (type) {
                case ANYVALUE:
                    return parseValue(valueStr,"sf");
                case STRING:
                    return new String(valueStr);

                case INTEGER:
                    return Integer.valueOf(valueStr);

                case BOOLEAN:
                    return Boolean.valueOf(valueStr);

                case COMPONENT_DESCRIPTION:
                    return parsePhase("raw", valueStr, "sf");

                case REFERENCE:
                    try {
                        return Reference.fromString(valueStr);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());

                        //ex.printStackTrace();
                        return null;
                    }

                    case LONG:
                        return Long.valueOf(valueStr);

                case FLOAT:
                    return Float.valueOf(valueStr);
                case DOUBLE:
                    return Double.valueOf(valueStr);
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        return null;
    }
    /**
     * Selects the combo box
     * @param value of object
     */
    void selectComboIndex(Object value) {
        if (value instanceof String) {
            this.TypejComboBox.setSelectedIndex(STRING);
        } else if ((value instanceof Integer)) {
            this.TypejComboBox.setSelectedIndex(INTEGER);
        } else if ((value instanceof Boolean)) {
            this.TypejComboBox.setSelectedIndex(BOOLEAN);
        } else if ((value instanceof ComponentDescription)) {
            this.TypejComboBox.setSelectedIndex(COMPONENT_DESCRIPTION);
        } else if ((value instanceof Reference)) {
            this.TypejComboBox.setSelectedIndex(REFERENCE);
        } else if ((value instanceof Long)) {
            this.TypejComboBox.setSelectedIndex(LONG);
        } else if ((value instanceof Float)) {
            this.TypejComboBox.setSelectedIndex(FLOAT);
        } else if ((value instanceof Double)) {
            this.TypejComboBox.setSelectedIndex(DOUBLE);
        }
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
            ex.printStackTrace();
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

                //System.out.println("TOP after: "+ top.
        //sfAsComponentDescription().toString());
                //this.log("SFParse Done(" + (auxphases.lastElement()).
        //toString() + ").", "Parse", 3);
                return (top.sfAsComponentDescription());
            } else {
                //this.log("SFParse Failed. No top.", "Parse", 5);
                return null;

                //3 Info
            }
        } catch (Throwable ex) {
            //this.log(ex.getMessage(), "Parse", 5);
            System.err.println("Error: " + ex.toString());

            //ex.printStackTrace();
        }

        return null;
    }

    public Object parseValue(String textToParse, String language) {
        try {
            SFParser parser = new SFParser(language);
            return parser.sfParseAnyValue( textToParse);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }



    /**
     * Interface Method.
     * @param e event
     */
    void jButtonCancel_actionPerformed(ActionEvent e) {
        //Return null
        attribute[0] = null;
        attribute[1] = null;
        this.dispose();
    }
}
