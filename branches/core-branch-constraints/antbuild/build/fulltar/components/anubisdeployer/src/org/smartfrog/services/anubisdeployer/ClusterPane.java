
package org.smartfrog.services.anubisdeployer;

// Example from http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Insets;
import java.awt.Font;



public class ClusterPane extends JPanel {

  int fontSize = 20;
  int rowTableSize = fontSize+10;

  /* Inner classes */
  class ClusterDataModel extends DefaultTableModel implements TableModel {
      public ClusterDataModel() {
          super();
      }
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
          switch (col) {
          case 2:
            return true;
          default:
            return false;
          }
        }

        public void setValueAt(Object obj, int row, int col) {
            switch (col) {
                case 2:
                    try {
                        Integer integer = new Integer(obj.toString());
                        super.setValueAt(integer, row, col);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                    return ;
                default:
                    super.setValueAt(obj, row, col);
                    return;
            }
        }
  }

    class LocalHostCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
        String localhost;

        public LocalHostCellRenderer() {
	    try {
		localhost = InetAddress.getLocalHost().getCanonicalHostName();
	    } catch (UnknownHostException e) {
	    }
	}

	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    if (value.toString().equals(localhost)) {
		this.setForeground(Color.BLUE);
	    } else {
		this.setForeground(Color.BLACK);
	    }
	    return this;
	}        
    }

  class ClusterCellRenderer extends JProgressBar implements TableCellRenderer {

    Color[] colors;

    private int[] limitValues;

    public ClusterCellRenderer() {
      super(JProgressBar.HORIZONTAL);
      setBorderPainted(false);
      setStringPainted(false);
    }

    public ClusterCellRenderer(int min, int max) {
      super(JProgressBar.HORIZONTAL, min, max);
      setBorderPainted(false);
      this.setString("");
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
          return colors[value];
      } catch (Exception ex) {
          return null;
      }
    }

  }

  /* End inner classes */


  private DefaultTableModel dataModel = null;

  TableSorter sorter = null;

  JTable table = null;


  private static final int MAX = 10;

  private static final int MIN = 0;

  public ClusterPane() {

      dataModel = new ClusterDataModel();

      sorter = new TableSorter(dataModel);

      table = new JTable(sorter);
      table.setFont(new Font("", Font.PLAIN, fontSize));
      table.setRowHeight(rowTableSize);

      sorter.setTableHeader(table.getTableHeader());
      sorter.setSortingStatus(0, sorter.ASCENDING);
      setLayout(new BorderLayout());


    //        //Data Example
    Object[][] dataSet = new Object[0][0];
      /* Example...
    Object[][] dataSet = new Object[][]
       {
        {"cero", new Integer(0),new Integer(0)},
        {"uno", new Integer(76), new Integer(1)},
        {"dos", new Integer(2), new Integer(2)},
        {"tres", new Integer(100), new Integer(3)},
        {"cuatro", new Integer(100), new Integer(4)},
        {"cinco", new Integer(100), new Integer(5)},
        {"seis",  new Integer(100), new Integer(6)},
        {"siete", new Integer(100), new Integer(7)},
        {"ocho", new Integer(100), new Integer(8)},
        {"nueve", new Integer(100), new Integer(9)},
        {"diez", new Integer(4),new Integer(10)},
        {"once", new Integer(4), new Integer(11)}
    };
    */
    Object[] headers = new Object[] {"Machine", "Role", "Cluster"};

    setData(dataSet,headers);

    JScrollPane pane = new JScrollPane(table);

    add(pane, BorderLayout.CENTER);
  }


  Object[][] dataSetLater;
  Object[] headersLater;
 
  public synchronized void setData(Object[][] dataSet, Object[] headers){
      dataSetLater = dataSet;
      headersLater = headers;

      SwingUtilities.invokeLater(new Runnable() { public void run() {
	  ((ClusterDataModel)((TableSorter)table.getModel()).getTableModel()).setDataVector(dataSetLater, headersLater);
	  sorter.setTableHeader(table.getTableHeader());
	  sorter.setSortingStatus(0, sorter.ASCENDING);
	  table = setTableRenderer(table.getModel());
	  org.smartfrog.services.display.TableUtilities.setColumnWidths(table,new Insets(4, 4, 4, 4),false,false);
      }});
  }

  private  JTable setTableRenderer(TableModel dm) {
    ClusterCellRenderer renderer = new ClusterCellRenderer(MIN, MAX);
    renderer.setStringPainted(true);
    renderer.setBackground(table.getBackground());
    table.getColumnModel().getColumn(0).setCellRenderer(new LocalHostCellRenderer());

    // set limit value and fill color
    Color[] colors = { Color.white,
                       Color.yellow,
                       Color.red,
                       Color.green,
                       Color.magenta,
                       Color.orange,
                       Color.cyan,
                       Color.gray,
                       Color.blue,
                       Color.black};

    renderer.setColors(colors);
    table.getColumnModel().getColumn(2).setCellRenderer(renderer);

    table.getModel().addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
          int col = e.getColumn();
          if (col == 1) {
            int row = e.getFirstRow();
            TableModel model = (TableModel) e.getSource();
            Integer value = (Integer) model.getValueAt(row, col);
            model.setValueAt(value, row, ++col);
          }
        }
      }
    });

    return table;
  }

}

