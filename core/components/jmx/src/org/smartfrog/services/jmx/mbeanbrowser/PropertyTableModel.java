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

import java.util.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.security.*;
import javax.management.*;

import org.smartfrog.services.jmx.common.Utilities;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class PropertyTableModel extends AbstractTableModel {
    /**
     *  Description of the Field
     */
    public final static String CLASS = PropertyTableModel.class.getName();

    private MainFrame m_main = null;

    /**
     *  TODO JavaDoc field m_objectName
     */
    private ObjectName m_objectName = null;

    /**
     *  TODO JavaDoc field m_propVals
     */
    //private ArrayList m_propVals = new ArrayList();
    private AttributeList m_propVals = new AttributeList();

    /**
     *  TODO JavaDoc field m_propInfo
     */
    //private ArrayList m_propInfo = new ArrayList();

    /**
     *  TODO JavaDoc field m_attributes[]
     */
    private MBeanAttributeInfo m_attributes[] = null;


    /**
     *  Constructor for the PropertyTableModel object
     *
     *@param  main        Description of the Parameter
     *@param  objectName  Description of the Parameter
     */
    public PropertyTableModel(MainFrame main, ObjectName objectName) {
        MBeanInfo mbeanInfo;
        TreeSet sortedSet;
        //MBeanAttributeInfo attribute;
        //Iterator iterator;
        //Object   value;
        //int index;

        m_main = main;
        m_objectName = objectName;
        try {
            mbeanInfo = m_main.getMBeanServer().getMBeanInfo(m_objectName);
            m_attributes = mbeanInfo.getAttributes();
            sortedSet = new TreeSet(new AttributeComparator());
            for (int i = 0; i < m_attributes.length; i++) {
                sortedSet.add(m_attributes[i]);
            }

            // Instantiated again because sometimes there are repeated attributes that the sortedSet eliminates
            m_attributes = new MBeanAttributeInfo[sortedSet.size()];

            sortedSet.toArray(m_attributes);

            for (int i = 0; i < m_attributes.length; i++) {
                if (m_attributes[i].isReadable()) {
                    Object value = m_main.doAction( new GetAttributeAction(m_attributes[i].getName()) );
                    m_propVals.add(new Attribute(m_attributes[i].getName(), value));
                }
                else m_propVals.add(new Attribute(m_attributes[i].getName(), null));
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Throwable rootCause = null;
            if (throwable instanceof MBeanException) {
                rootCause = ((MBeanException) throwable).getTargetException();
            } else if (throwable instanceof InvocationTargetException) {
                rootCause = ((InvocationTargetException) throwable).getTargetException();
            } else if (throwable instanceof PrivilegedActionException) {
                rootCause = ((PrivilegedActionException) throwable).getException();
                if (rootCause instanceof MBeanException) {
                    rootCause = ((MBeanException) rootCause).getTargetException();
                }
                if (rootCause instanceof InvocationTargetException) {
                    rootCause = ((InvocationTargetException) rootCause).getTargetException();
                }
            }
            if (rootCause == null) {
                rootCause = throwable;
            }
            JOptionPane.showMessageDialog(
                    m_main,
                    rootCause,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        }
    }


    /**
     *  TODO JavaDoc method getColumnName
     *
     *@param  columnIndex
     *@return String Column Name (Attribute, Value, Type, ?)
     */
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case (0):
                return ("Attribute");
            case (1):
                return ("Value");
            case (2):
                return ("Type");
            default:
                return ("?");
        }
    }


    /**
     *  Gets the columnCount attribute of the PropertyTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount() {
        return 3;
    }


    /**
     *  Gets the valueAt attribute of the PropertyTableModel object
     *
     *@param  row  Description of the Parameter
     *@param  col  Description of the Parameter
     *@return      The valueAt value
     */
    public Object getValueAt(int row, int col) {
        MBeanAttributeInfo info;
        switch (col) {
            case 0:
                if (row > -1 && row < m_attributes.length) {
                    info = m_attributes[row];
                    return info.getName();
                }
                break;
            case 1:
                if (row > -1 && row < m_propVals.size()) {
                    return ((Attribute) m_propVals.get(row)).getValue();
                }
                break;
            case 2:
                if (row > -1 && row < m_attributes.length) {
                    info = (MBeanAttributeInfo) m_attributes[row];
                    return info.getType();
                }
            default:
        }
        return null;
    }


    /**
     *  TODO JavaDoc method setValueAt
     *
     *@param  obj
     *@param  row
     *@param  col
     */
    public void setValueAt(Object obj, int row, int col) {
        MBeanAttributeInfo info;

        if (col == 1) {
            try {
                info = m_attributes[row];
                m_main.doAction(new SetAttributeAction(info, obj));
                //m_server.setAttribute( m_objectName, new Attribute( info.getName(), MBeanBrowserUtils.objectFromString( info.getType(), obj ) ) );
                if (info.isReadable()) {
                    m_propVals.set(row, new Attribute(info.getName(), obj));
                }
            } catch (Throwable throwable) {
                Throwable rootCause = null;
                if (throwable instanceof MBeanException) {
                    rootCause = ((MBeanException) throwable).getTargetException();
                } else if (throwable instanceof InvocationTargetException) {
                    rootCause = ((InvocationTargetException) throwable).getTargetException();
                } else if (throwable instanceof PrivilegedActionException) {
                    rootCause = ((PrivilegedActionException) throwable).getException();
                    if (rootCause instanceof MBeanException) {
                        rootCause = ((MBeanException) rootCause).getTargetException();
                    }
                    if (rootCause instanceof InvocationTargetException) {
                        rootCause = ((InvocationTargetException) rootCause).getTargetException();
                    }
                }
                if (rootCause == null) {
                    rootCause = throwable;
                }
                JOptionPane.showMessageDialog(m_main, rootCause, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     *  Gets the rowCount attribute of the PropertyTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount() {
        return m_attributes.length;
    }


    /**
     *  TODO JavaDoc method isCellEditable
     *
     *@param  row
     *@param  col
     *@return true/false
     */
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case 0:
                return false;
            case 1:
                return m_attributes[row].isWritable();
            default:
                return false;
        }
    }


    /**
     *  Refresh the value in the specified row
     *
     *@param  row            Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    public void refresh(int row) throws Exception {
        String name = (String) getValueAt(row, 0);
        Object result = m_main.doAction(new GetAttributeAction(name));
        m_propVals.set(row, new Attribute(name, result));
        this.fireTableChanged(new TableModelEvent(this, row));
    }


    /**
     *  TODO method getDescription
     *
     *@param  row  Description of the Parameter
     *@return      The description value
     */
    public String getDescription(int row) {
        if (row > -1 && row < m_propVals.size()) {
            MBeanAttributeInfo info = m_attributes[row];
            return info.getDescription();
        }
        return "";
    }


    /**
     *  Description of the Class
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    private class GetAttributeAction implements PrivilegedExceptionAction {
        private String m_attribute;


        /**
         *  Constructor for the GetAttributeAction object
         *
         *@param  attribute  Description of the Parameter
         */
        public GetAttributeAction(String attribute) {
            m_attribute = attribute;
        }


        /**
         *  Main processing method for the GetAttributeAction object
         *
         *@return                Description of the Return Value
         *@exception  Exception  Description of the Exception
         */
        public Object run() throws Exception {
            return (m_main.getMBeanServer().getAttribute(m_objectName, m_attribute));
        }
    }


    /**
     *  Description of the Class
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    private class SetAttributeAction implements PrivilegedExceptionAction {
        private MBeanAttributeInfo m_info;
        private Object m_value;


        /**
         *  Constructor for the SetAttributeAction object
         *
         *@param  info   Description of the Parameter
         *@param  value  Description of the Parameter
         */
        public SetAttributeAction(MBeanAttributeInfo info, Object value) {
            m_info = info;
            m_value = value;
        }


        /**
         *  Main processing method for the SetAttributeAction object
         *
         *@return                Description of the Return Value
         *@exception  Exception  Description of the Exception
         */
        public Object run() throws Exception {
            m_main.getMBeanServer().setAttribute(
                    m_objectName,
                    new Attribute(m_info.getName(), Utilities.objectFromString(m_info.getType(), m_value)));
            return (null);
        }
    }


    /**
     *  TODO JavaDoc class AttributeComparator
     *
     *          sfJMX
     *   JMX-based Management Framework for SmartFrog Applications
     *       Hewlett Packard
 *
     *@version        1.0
     */
    private class AttributeComparator implements Comparator {
        /**
         *  TODO JavaDoc method compare
         *
         *@param  o1
         *@param  o2
         *@return int
         */
        public int compare(Object o1, Object o2) {
            MBeanAttributeInfo a1 = (MBeanAttributeInfo) o1;
            MBeanAttributeInfo a2 = (MBeanAttributeInfo) o2;
            return (a1.getName().compareTo(a2.getName()));
        }
    }

}
