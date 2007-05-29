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



package org.smartfrog.tools.gui.browser;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 *  Table Utilities to handle some common table representation problems
 *
 */
public class TableUtilities {
   // Calculate the required width of a table column
   /**
    *  Description of the Method
    *
    *@param  table        Description of the Parameter
    *@param  columnIndex  Description of the Parameter
    *@return              Description of the Return Value
    */
   public static int calculateColumnWidth(JTable table,
         int columnIndex) {
      int width = 0;
      // The return value
      int rowCount = table.getRowCount();

      // Header width
      TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
      Component comp = renderer.getTableCellRendererComponent(
            table, table.getColumnName(columnIndex), false, false, 0, columnIndex);
      width = comp.getPreferredSize().width;

      // Cells width
      for (int i = 0; i < rowCount; i++) {
         renderer = table.getCellRenderer(i, columnIndex);
         comp = renderer.getTableCellRendererComponent(
               table, table.getValueAt(i, columnIndex),
               false, false, i, columnIndex);
         int thisWidth = comp.getPreferredSize().width;
         if (thisWidth > width) {
            width = thisWidth;
         }
      }
      return width;
   }


   // Set the widths of every column in a table
   /**
    *  Sets the columnWidths attribute of the TableUtilities class
    *
    *@param  table       The new columnWidths value
    *@param  insets      The new columnWidths value
    *@param  setMinimum  The new columnWidths value
    *@param  setMaximum  The new columnWidths value
    */
   public static void setColumnWidths(JTable table, Insets insets,
         boolean setMinimum,
         boolean setMaximum) {
      boolean done = false;
      int retries = 0;
      do {
         try {
            int columnCount = table.getColumnCount();
            TableColumnModel tcm = table.getColumnModel();
            int spare = (insets == null ? 0 : insets.left + insets.right);

            for (int i = 0; i < columnCount; i++) {
               int width = calculateColumnWidth(table, i);
               width += spare;

               int n = tcm.getColumnCount();

               TableColumn column = tcm.getColumn(i);
               column.setPreferredWidth(width);
               column.setWidth(width);

               if (setMinimum == true) {
                  column.setMinWidth(width);
               }
               if (setMaximum == true) {
                  column.setMaxWidth(width);
               }
            }
            done = true;
         } catch (Exception e) {
            // Usually this error is due to a race condition between this method
            // and some other code modifying the TableColumnModel of this table
            System.out.println("Possible race condition detected, retrying...");
            try {
               retries++;
               if (retries > 3) {
                  done = true;
               } else {
                  Thread.currentThread().sleep(100);
               }
            } catch (Exception ee) {
               System.err.println("Error retriying setColumnWidths:\n" + ee);
               ee.printStackTrace();
            }
         }
      } while (!done);
   }


   // Sort an array of integers in place
   /**
    *  Description of the Method
    *
    *@param  values  Description of the Parameter
    */
   public static void sort(int[] values) {
      int length = values.length;
      if (length > 1) {
         for (int i = 0; i < length - 1; i++) {
            for (int j = i + 1; j < length; j++) {
               if (values[j] < values[i]) {
                  int temp = values[i];
                  values[i] = values[j];
                  values[j] = temp;
               }
            }
         }
      }
   }
}



