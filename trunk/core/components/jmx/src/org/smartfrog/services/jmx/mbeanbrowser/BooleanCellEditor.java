/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jmx.mbeanbrowser;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.EventObject;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class BooleanCellEditor extends DefaultCellEditor {
    /**
     *  Description of the Field
     */
    protected JComboBox boolComboBox;
    /**
     *  Description of the Field
     */
    protected EditorDelegate comboDelegate;
    /**
     *  Description of the Field
     */
    protected int row = -1;
    /**
     *  Description of the Field
     */
    protected boolean[] displayBoolComboBox;


    /**
     *  Constructor for the BooleanCellEditor object
     *
     *@param  textField  Description of the Parameter
     *@param  flags      Description of the Parameter
     */
    public BooleanCellEditor(final JTextField textField, boolean[] flags) {
        super(textField);
        displayBoolComboBox = flags;
        super.setClickCountToStart(1);
    }


    /**
     *  Constructor for the BooleanCellEditor object
     *
     *@param  checkBox  Description of the Parameter
     *@param  flags     Description of the Parameter
     */
    public BooleanCellEditor(final JCheckBox checkBox, boolean[] flags) {
        super(checkBox);
        displayBoolComboBox = flags;
        super.setClickCountToStart(1);
    }


    /**
     *  Constructor for the BooleanCellEditor object
     *
     *@param  comboBox  Description of the Parameter
     *@param  flags     Description of the Parameter
     */
    public BooleanCellEditor(final JComboBox comboBox, boolean[] flags) {
        super(comboBox);
        displayBoolComboBox = flags;
        super.setClickCountToStart(1);
    }


    /**
     *  Description of the Method
     */
    protected void buildBoolComboBox() {
        boolComboBox = new JComboBox();
        boolComboBox.addItem("True");
        boolComboBox.addItem("False");
        boolComboBox.setSelectedIndex(0);
        boolComboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
        comboDelegate =
            new EditorDelegate() {
                public void setValue(Object value) {
                    boolComboBox.setSelectedItem(value);
                }


                public Object getCellEditorValue() {
                    return boolComboBox.getSelectedItem();
                }


                public boolean shouldSelectCell(EventObject anEvent) {
                    if (anEvent instanceof MouseEvent) {
                        MouseEvent e = (MouseEvent) anEvent;
                        return e.getID() != MouseEvent.MOUSE_DRAGGED;
                    }
                    return true;
                }
            };
        boolComboBox.addActionListener(delegate);
    }


    /**
     *  Gets the tableCellEditorComponent attribute of the BooleanCellEditor
     *  object
     *
     *@param  table       Description of the Parameter
     *@param  value       Description of the Parameter
     *@param  isSelected  Description of the Parameter
     *@param  row         Description of the Parameter
     *@param  column      Description of the Parameter
     *@return             The tableCellEditorComponent value
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected,
            int row, int column) {

        this.row = row;
        if (displayBoolComboBox[row]) {
            if (boolComboBox == null) {
                buildBoolComboBox();
            }
            comboDelegate.setValue(value);
            return boolComboBox;
        } else {
            delegate.setValue(value);
            return editorComponent;
        }
    }


    /**
     *  Gets the component attribute of the BooleanCellEditor object
     *
     *@return    The component value
     */
    public Component getComponent() {
        if (displayBoolComboBox[row]) {
            return boolComboBox;
        } else {
            return editorComponent;
        }
    }


    //
    //  Override the implementations of the superclass, forwarding all methods
    //  from the CellEditor interface to our delegate.
    //

    /**
     *  Gets the cellEditorValue attribute of the BooleanCellEditor object
     *
     *@return    The cellEditorValue value
     */
    public Object getCellEditorValue() {
        if (displayBoolComboBox[row]) {
            return comboDelegate.getCellEditorValue();
        } else {
            return delegate.getCellEditorValue();
        }
    }


//  public boolean isCellEditable(EventObject anEvent) {
//    delegate.isCellEditable(anEvent);
//  }

    /**
     *  Description of the Method
     *
     *@param  anEvent  Description of the Parameter
     *@return          Description of the Return Value
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        if (displayBoolComboBox[row]) {
            return comboDelegate.shouldSelectCell(anEvent);
        } else {
            return delegate.shouldSelectCell(anEvent);
        }
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public boolean stopCellEditing() {
        if (displayBoolComboBox[row]) {
            return comboDelegate.stopCellEditing();
        } else {
            return delegate.stopCellEditing();
        }
    }


    /**
     *  Description of the Method
     */
    public void cancelCellEditing() {
        if (displayBoolComboBox[row]) {
            comboDelegate.cancelCellEditing();
        } else {
            delegate.cancelCellEditing();
        }
    }

}
