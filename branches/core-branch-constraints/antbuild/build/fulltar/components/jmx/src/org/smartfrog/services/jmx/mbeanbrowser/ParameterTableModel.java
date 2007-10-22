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
import javax.swing.*;
import javax.swing.table.*;
import javax.management.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class ParameterTableModel extends AbstractTableModel {

    /**
     *  TODO JavaDoc field m_propNames
     */
    private ArrayList m_propNames = new ArrayList();

    /**
     *  TODO JavaDoc field m_propVals
     */
    private ArrayList m_propVals = new ArrayList();

    /**
     *  TODO JavaDoc field m_opInfo
     */
    private MBeanOperationInfo m_opInfo;

    /**
     *  TODO JavaDoc field params[]
     */
    private MBeanParameterInfo params[];


    /**
     *  TODO JavaDoc constructor ParameterTableModel
     *
     *@param  opInfo
     */
    public ParameterTableModel(MBeanOperationInfo opInfo) {
        m_opInfo = opInfo;
        MBeanParameterInfo param = null;

        try {
            params = m_opInfo.getSignature();
            int s = params.length;
            for (int i = 0; i < s; i++) {
                param = params[i];
                if ((param.getName() == null) || (param.getName().length() == 0)) {
                    m_propNames.add("p" + i);
                } else {
                    m_propNames.add(param.getName());
                }
                m_propVals.add(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *  Gets the columnCount attribute of the ParameterTableModel object
     *
     *@return    The columnCount value
     */
    public int getColumnCount() {

        return 3;
    }


    /**
     *  Gets the valueAt attribute of the ParameterTableModel object
     *
     *@param  row  Description of the Parameter
     *@param  col  Description of the Parameter
     *@return      The valueAt value
     */
    public Object getValueAt(int row, int col) {

        if (col == 0) {
            return params[row].getType();
        }
        if (col == 1) {
            return m_propNames.get(row);
        } else if (col == 2) {
            return m_propVals.get(row);
        } else {
            return null;
        }
    }


    /**
     *  Gets the rowCount attribute of the ParameterTableModel object
     *
     *@return    The rowCount value
     */
    public int getRowCount() {

        return m_propNames.size();
    }


    /**
     *  TODO JavaDoc method getColumnName
     *
     *@param  columnIndex
     *@return String Column Name (Type, Parameter, Value, ?)
     */
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case (0):
                return ("Type");
            case (1):
                return ("Parameter");
            case (2):
                return ("Value");
            default:
                return ("?");
        }
    }


    /**
     *  TODO JavaDoc method setValueAt
     *
     *@param  obj
     *@param  row
     *@param  col
     */
    public void setValueAt(Object obj, int row, int col) {
        if (col == 2) {
            try {
                m_propVals.set(row, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  TODO JavaDoc method isCellEditable
     *
     *@param  row
     *@param  col
     *@return boolean true/false
     */
    public boolean isCellEditable(int row, int col) {
        switch (col) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return true;
            default:
                return false;
        }
    }

}
