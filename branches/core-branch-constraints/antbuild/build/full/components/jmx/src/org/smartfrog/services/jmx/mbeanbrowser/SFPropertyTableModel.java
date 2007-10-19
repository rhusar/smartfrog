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

import java.security.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.management.*;
import org.smartfrog.services.jmx.common.*;
import org.smartfrog.services.jmx.communication.RuntimeConnectionException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class SFPropertyTableModel extends AbstractTableModel {

    /**
     *  Description of the Field
     */
    public final static String CLASS = PropertyTableModel.class.getName();

    private MainFrame m_browser = null;

    private ObjectName m_mbeanName = null;

    /**
     *  TODO JavaDoc field m_sfAttributes
     */
    private ArrayList m_sfAttributes = new ArrayList();

    /**
     *  The node which represents a SF component in the application
     */
    private SFNode m_sfNode = null;

    /**
     *  TODO JavaDoc field m_componentPath
     */
    private String m_componentPath = null;


    /**
     *  Constructor for the SFPropertyTableModel object
     *
     *@param  browser    Description of the Parameter
     *@param  mbeanName  Description of the Parameter
     *@param  node       Description of the Parameter
     */
    public SFPropertyTableModel(MainFrame browser, ObjectName mbeanName, SFNode node) {
        TreeSet sortedSet;

        m_browser = browser;
        m_mbeanName = mbeanName;
        m_sfNode = node;
        m_componentPath = m_sfNode.getPath();

        sortedSet = new TreeSet(new SFAttributeComparator());

        Iterator sfAttributes = m_sfNode.getBasicAttributes();
        while (sfAttributes.hasNext()) {
            SFAttribute a = (SFAttribute) sfAttributes.next();
            sortedSet.add(a);
        }
        m_sfAttributes.addAll(sortedSet);
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
     *  Gets the columnCount attribute of the SFPropertyTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount() {
        return 3;
    }


    /**
     *  Gets the valueAt attribute of the SFPropertyTableModel object
     *
     *@param  row  Description of the Parameter
     *@param  col  Description of the Parameter
     *@return      The valueAt value
     */
    public Object getValueAt(int row, int col) {
        switch (col) {
            case 0:
                if (row > -1 && row < m_sfAttributes.size()) {
                    return ((SFAttribute) m_sfAttributes.get(row)).getName();
                }
                break;
            case 1:
                if (row > -1 && row < m_sfAttributes.size()) {
                    return ((SFAttribute) m_sfAttributes.get(row)).getValue();
                }
                break;
            case 2:
                if (row > -1 && row < m_sfAttributes.size()) {
                    return ((SFAttribute) m_sfAttributes.get(row)).getClazz();
//          Object value = ((SFAttribute)m_sfAttributes.get(row)).getValue();
//          if (value != null) return value.getClass().getName();
//          else return "";
                }
            default:
        }
        return "";
    }


    /**
     *  TODO JavaDoc method setValueAt
     *
     *@param  obj
     *@param  row
     *@param  col
     */
    public void setValueAt(Object obj, int row, int col) {
        SFAttribute attribute;
        if (col == 1) {
            try {
                attribute = (SFAttribute) this.m_sfAttributes.get(row);
                m_browser.doAction(new SetAttributeAction(attribute, obj));
                attribute.setValue(obj);
                //m_sfAttributes.set(row, new SFAttribute(attribute.getName(), obj, attribute.getSFType(), attribute.isWritable());
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
                JOptionPane.showMessageDialog(m_browser, rootCause, "Error", JOptionPane.ERROR_MESSAGE);
                //rootCause.printStackTrace();
            }
        }
    }


    /**
     *  Gets the rowCount attribute of the SFPropertyTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount() {
        return m_sfAttributes.size();
    }


    /**
     *  TODO JavaDoc method isCellEditable
     *
     *@param  row
     *@param  col
     *@return boolean
     */
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case 0:
                return false;
            case 1:
                if (row > -1 && row < m_sfAttributes.size()) {
                    return ((SFAttribute) m_sfAttributes.get(row)).isWritable();
                }
            default:
                return false;
        }
    }


    /**
     *  Refresh the value in the specified row
     *
     *@param  row  Description of the Parameter
     */
    public void refresh(int row) {
        try {
            m_sfNode.refreshBasicAttribute(row);
            SFAttribute attr = m_sfNode.getBasicAttribute(row);
            m_sfAttributes.set(row, attr);
            this.fireTableChanged(new TableModelEvent(this, row));
        } catch (RuntimeConnectionException ce) {
            //ce.printStackTrace();
            JOptionPane.showMessageDialog(this.m_browser, ce
            /*
             *  .getLocalizedMessage()
             */
                    );
            m_browser.getMBeanServer().disconnect();
            m_browser.clear();
            m_sfNode.m_parent.m_children = null;
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
            JOptionPane.showMessageDialog(m_browser, rootCause, "Error", JOptionPane.ERROR_MESSAGE);
            //rootCause.printStackTrace();
        }
    }


    /**
     *  Remove the specified row
     *
     *@param  row  Description of the Parameter
     */
    public void removeRow(int row) {
        try {
            m_sfNode.removeBasicAttribute(row);
            m_sfAttributes.remove(row);
            this.fireTableRowsDeleted(row, row);
        } catch (RuntimeConnectionException ce) {
            //ce.printStackTrace();
            JOptionPane.showMessageDialog(this.m_browser, ce
            /*
             *  .getLocalizedMessage()
             */
                    );
            m_browser.getMBeanServer().disconnect();
            m_browser.clear();
            m_sfNode.m_parent.m_children = null;
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
            JOptionPane.showMessageDialog(m_browser, rootCause, "Error", JOptionPane.ERROR_MESSAGE);
            //rootCause.printStackTrace();
        }
    }


    /**
     *  TODO method getDescription
     *
     *@param  row  Description of the Parameter
     *@return      The description value
     */
    public String getDescription(int row) {
        if (row > -1 && row < m_sfAttributes.size()) {
            SFAttribute attribute = (SFAttribute) m_sfAttributes.get(row);
            return attribute.getDescription();
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
    private class SetAttributeAction implements PrivilegedExceptionAction {
        private SFAttribute m_attribute;
        private Object m_value;
        private String m_type;


        /**
         *  Constructor for the SetAttributeAction object
         *
         *@param  attribute  Description of the Parameter
         *@param  value      Description of the Parameter
         */
        public SetAttributeAction(SFAttribute attribute, Object value) {
            m_attribute = attribute;
            m_value = value;
            if (m_attribute.getValue() != null) {
                m_type = m_attribute.getValue().getClass().getName();
            }
        }


        /**
         *  Main processing method for the SetAttributeAction object
         *
         *@return                Description of the Return Value
         *@exception  Exception  Description of the Exception
         */
        public Object run() throws Exception {
            m_browser.getMBeanServer().invoke(
                    m_mbeanName,
            // object name
                    "sfSetAttribute",
            // method name
                    new Object[]{m_componentPath + ":" + m_attribute.getName(), Utilities.objectFromString(m_type, m_value)},
            // parameters
                    new String[]{"java.lang.String", "java.lang.Object"}
            // signature
                    );
            return (null);
        }
    }

}
