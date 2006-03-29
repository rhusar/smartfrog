/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.anubisdeployer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;



public class ClusterPane extends JPanel {

    class ClusterCellRenderer extends JProgressBar implements TableCellRenderer {

      Color[] colors;

      public ClusterCellRenderer() {
        super(JProgressBar.HORIZONTAL);
        setBorderPainted(false);
      }

      public ClusterCellRenderer(int min, int max) {
        super(JProgressBar.HORIZONTAL, min, max);
        setBorderPainted(false);
      }

      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        int n = 0;
        if (!(value instanceof Number)) {
          String str;
          if (value instanceof String) {
            str = (String) value;
          } else {
            str = value.toString();
          }
          try {
            n = Integer.valueOf(str).intValue();
          } catch (NumberFormatException ex) {
          }
        } else {
          n = ((Number) value).intValue();
        }
        Color color = getColor(n);
        if (color != null) {
          setForeground(color);
        }
        setValue(this.getMaximum());
        return this;
      }

      public void setColors(Color[] colors) {
        this.colors = colors;
      }

      private Color getColor(int value) {
        try {
            return this.colors[value];
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
      }
  }

  private DefaultTableModel dataModel = null;

  private static final int MAX = 10;

  private static final int MIN = 0;

  public ClusterPane() {

    setLayout(new BorderLayout());

    dataModel = new DefaultTableModel() {
        public Class getColumnClass(int col) {
            switch (col) {
                case 0:
                    // Machine
                    return String.class;
                case 1:
                    // role
                    return String.class;
                case 2:

                    // cluster
                    return Integer.class;
                default:
                    return Object.class;
            }
        }
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    dataModel.setDataVector(new Object[][] { {"","",new Integer(0)}},new Object [] {"Machine","Role","Cluster"});

    JTable table = setTable(dataModel);

    JScrollPane pane = new JScrollPane(table);

    add(pane, BorderLayout.CENTER);
  }

  /**
   * Method to change data in table
   * @param data Object[][]
   * @param headers Object[]
   */
  public void setData(Object[][] data, Object[] headers){
      dataModel.setDataVector(data, headers);
  }


  private JTable setTable(DefaultTableModel dm) {

      JTable table = new JTable(dm);

      ClusterCellRenderer renderer = new ClusterCellRenderer(MIN, MAX);
      renderer.setStringPainted(true);
      renderer.setBackground(table.getBackground());

      Color[] colors = {Color.white,
          Color.yellow,
          Color.red,
          Color.blue,
          Color.green,
          Color.gray,
          Color.darkGray,
          Color.magenta,
          Color.cyan,
          Color.orange,
          Color.black};

      renderer.setColors(colors);
      table.getColumnModel().getColumn(2).setCellRenderer(renderer);

      table.getModel().addTableModelListener(new TableModelListener() {
          public void tableChanged(TableModelEvent e) {
              if (e.getType()==TableModelEvent.UPDATE) {
                  int col = e.getColumn();
                  if (col==1) {
                      int row = e.getFirstRow();
                      TableModel model = (TableModel)e.getSource();
                      Integer value = (Integer)model.getValueAt(row, col);
                      model.setValueAt("Cluster", row, ++col);
                  }
              }
          }
      });
      return table;
  }

}

