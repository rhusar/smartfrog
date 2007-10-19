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
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.*;
import javax.swing.event.*;
import javax.management.*;
import org.smartfrog.services.jmx.notification.NotificationListenerWrapper;
import org.smartfrog.services.jmx.communication.ConnectionFactory;
import org.smartfrog.services.jmx.communication.ConnectorClient;
import org.smartfrog.services.jmx.communication.ServerAddress;
import org.smartfrog.services.jmx.communication.RuntimeConnectionException;
import org.smartfrog.services.jmx.communication.rmi.RmiServerAddress;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class MainFrame extends JFrame {

    private boolean debug = false;

    public boolean systemExit = false;

    private ConnectorClient connectorClient = null;
    private ServerAddress serverAddress = null;
    private int period = 0;
    // By default, no check is carried out
    private int retries = 0;
    // By default, no retries are carried out
    private ListenerImpl listener = null;
    private NotificationListener listenerWrapper = null;
    private boolean isListenerRegistered = false;
    private NotificationFrame notifFrame = null;

    boolean isConnectionLost = false;
    // Indicate if the connection has been lost when disconnect() method is invoked

    JPanel contentPane;

    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem jMenuFileExit = new JMenuItem();
    JMenu jMenuServer = new JMenu();
    JMenuItem jMenuServerSettings = new JMenuItem();
    JMenuItem jMenuServerConnect = new JMenuItem();
    JMenuItem jMenuServerDisconnect = new JMenuItem();
    JMenu jMenuHelp = new JMenu();
    JMenuItem jMenuHelpAbout = new JMenuItem();
    JMenu jMenuLF = new JMenu();
    JMenuItem jMenuILFAuto = new JMenuItem();
    JMenuItem jMenuLFWin = new JMenuItem();
    JMenuItem jMenuLFMetal = new JMenuItem();
    JMenuItem jMenuLFMotif = new JMenuItem();
    JMenu jMenuHeartBeat = new JMenu();
    JMenuItem jMenuPeriod = new JMenuItem();
    JMenuItem jMenuRetries = new JMenuItem();

    JToolBar jToolBar = new JToolBar();
    JButton jHelpButton = new JButton();
    JButton jConnectButton = new JButton();

    ImageIcon helpImage;
    ImageIcon connectImage;
    ImageIcon disconnectImage;
    ImageIcon settingImage;

    BorderLayout borderLayout1 = new BorderLayout();
    JLabel statusBar = new JLabel();
    JSplitPane mainSplitter = new JSplitPane();
    JSplitPane browserSplitter = new JSplitPane();
    JTabbedPane tabs = new JTabbedPane();

    QueryPanel queryPanel = new QueryPanel(this);
    PropertyPanel propertyPanel = new PropertyPanel(this);
    OperationPanel operationPanel = new OperationPanel(this);
    TreePanel treePanel = new TreePanel(this);

    JButton jDisconnectButton = new JButton();
    JButton jSettingButton = new JButton();
    JMenu jMenu1 = new JMenu();
    JMenuItem jMenuTree = new JMenuItem();
    JMenuItem jMenuINotifications = new JMenuItem();
    JMenuItem jMenuAdmin = new JMenuItem();




    /**
     *  Construct the frame
     *
     *@param  url  Description of the Parameter
     */
    public MainFrame(ServerAddress url) {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
	    System.out.println("Creating MainFrame");
            jbInit();
            serverAddress = url;
            notifFrame = new NotificationFrame(null);
            listener = new ListenerImpl();
            listenerWrapper = NotificationListenerWrapper.createWrapper(listener);
            connectorClient = ConnectionFactory.createConnectorClient(url.getProtocol());
            // CREATION
            // Registration for HeartBeat Notification
            connectorClient.addHeartBeatNotificationListener(listener, null, null);
            listener.addNotificationViewer(notifFrame);
            this.jSettingButton.setToolTipText("Settings ["+serverAddress+"]");
            this.setTitle(this.getTitle() + " ["+serverAddress.toString()+"]");
        } catch (Exception e) {
            //if (debug) {e.printStackTrace();}
            //else {System.err.println(e.toString());}
	    e.printStackTrace();
        }
    }


    /**
     *  Constructor for the MainFrame object
     */
    public MainFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
            connectorClient = ConnectionFactory.createConnectorClient("rmi");
            // CREATION
            serverAddress = new RmiServerAddress();
            notifFrame = new NotificationFrame(null);
            listener = new ListenerImpl();
            listenerWrapper = NotificationListenerWrapper.createWrapper(listener);
            // Registration for HeartBeat Notification
            connectorClient.addHeartBeatNotificationListener(listener, null, null);
            listener.addNotificationViewer(notifFrame);
            this.jSettingButton.setToolTipText("Settings ["+serverAddress.toString()+"]");
            this.setTitle(this.getTitle() + " ["+serverAddress.toString()+"]");
        } catch (Exception e) {
            if (debug) {e.printStackTrace();}
            else {System.err.println(e.toString());}
        }
    }


    /**
     *  Component initialization
     *
     *@exception  Exception  Description of the Exception
     */
    private void jbInit() throws Exception {
        // Loading icons
        //frogImage = Toolkit.getDefaultToolkit().getImage(com.hp.SmartJMX.mbeanbrowser.MainFrame.class.getResource("Frog.gif"));
        helpImage = new ImageIcon(MainFrame.class.getResource("About.gif"));
        connectImage = new ImageIcon(MainFrame.class.getResource("ExecuteProject.gif"));
        disconnectImage = new ImageIcon(MainFrame.class.getResource("Stop.gif"));
        settingImage = new ImageIcon(MainFrame.class.getResource("Options.gif"));

        setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("Frog.gif")));

        // Configuring MainFrame
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(550, 500));
        this.setTitle("JMX Browser");
        statusBar.setText(" ");

        // Configuring Menus for MenuBar
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuFileExit_actionPerformed(e);
                }
            });
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuHelpAbout_actionPerformed(e);
                }
            });
        jMenuLF.setText("L&F");
        jMenuILFAuto.setText("Auto");
        jMenuILFAuto.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuILFAuto_actionPerformed(e);
                }
            });
        jMenuLFWin.setText("Windows");
        jMenuLFWin.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLFWin_actionPerformed(e);
                }
            });
        jMenuLFMetal.setText("Metal");
        jMenuLFMetal.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLFMetal_actionPerformed(e);
                }
            });
        jMenuLFMotif.setText("Motif");
        jMenuLFMotif.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuLFMotif_actionPerformed(e);
                }
            });

        // Configuring Buttons for ToolBar
        jHelpButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jHelpButton_actionPerformed(e);
                }
            });
        jHelpButton.setToolTipText("About");
        jHelpButton.setIcon(helpImage);

        // Configuring SplitPanes
        mainSplitter.setDividerSize(10);
        mainSplitter.setDividerLocation(200);
        browserSplitter.setOrientation(JSplitPane.VERTICAL_SPLIT);
        browserSplitter.setDividerLocation(225);

        // Building Frame
        jConnectButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jConnectButton_actionPerformed(e);
                }
            });
        jConnectButton.setToolTipText("Connect");
        jConnectButton.setIcon(connectImage);
        jDisconnectButton.setToolTipText("Disconnect");
        jDisconnectButton.setIcon(disconnectImage);
        jDisconnectButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jDisconnectButton_actionPerformed(e);
                }
            });
        jSettingButton.setToolTipText("Settings");
        jSettingButton.setIcon(settingImage);
        jSettingButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jSettingButton_actionPerformed(e);
                }
            });
        jMenuServer.setText("Server");
        jMenuServerSettings.setText("Settings");
        jMenuServerSettings.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuServerSettings_actionPerformed(e);
                }
            });
        jMenuServerConnect.setText("Connect");
        jMenuServerConnect.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuServerConnect_actionPerformed(e);
                }
            });
        jMenuServerDisconnect.setText("Disconnect");
        jMenuServerDisconnect.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuServerDisconnect_actionPerformed(e);
                }
            });
        tabs.addChangeListener(
            new javax.swing.event.ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    tabs_stateChanged(e);
                }
            });
        jMenuHeartBeat.setText("Heart Beat");
        jMenuPeriod.setText("Period");
        jMenuPeriod.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuPeriod_actionPerformed(e);
                }
            });
        jMenuRetries.setText("Retries");
        jMenuRetries.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuRetries_actionPerformed(e);
                }
            });
        jMenu1.setText("View");
        jMenuTree.setText("SF Tree");
        jMenuTree.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuTree_actionPerformed(e);
                }
            });
        jMenuINotifications.setText("Notifications");
        jMenuINotifications.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuINotifications_actionPerformed(e);
                }
            });
        jMenuAdmin.setText("Admin");
        jMenuAdmin.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jMenuAdmin_actionPerformed(e);
                }
            });
        jToolBar.add(jSettingButton, null);
        jToolBar.add(jConnectButton, null);
        jToolBar.add(jDisconnectButton, null);
        jToolBar.add(jHelpButton);

        jMenuFile.add(jMenuFileExit);
        jMenuHelp.add(jMenuHelpAbout);
        jMenuLF.add(jMenuILFAuto);
        jMenuLF.add(jMenuLFWin);
        jMenuLF.add(jMenuLFMetal);
        jMenuLF.add(jMenuLFMotif);
        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(jMenuServer);
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(jMenuLF);
        jMenuBar1.add(jMenuHelp);
        this.setJMenuBar(jMenuBar1);

        contentPane.add(jToolBar, BorderLayout.NORTH);

        contentPane.add(mainSplitter, BorderLayout.CENTER);
        mainSplitter.add(browserSplitter, JSplitPane.LEFT);
        mainSplitter.add(tabs, JSplitPane.RIGHT);
        tabs.add(propertyPanel, "Properties");
        tabs.add(operationPanel, "Operations");
        contentPane.add(statusBar, BorderLayout.SOUTH);
        browserSplitter.add(queryPanel, JSplitPane.TOP);
        browserSplitter.add(treePanel, JSplitPane.BOTTOM);
        jMenuServer.add(jMenuServerSettings);
        jMenuServer.add(jMenuServerConnect);
        jMenuServer.add(jMenuServerDisconnect);
        jMenuServer.addSeparator();
        jMenuServer.add(jMenuHeartBeat);
        jMenuServer.addSeparator();
        jMenuServer.add(jMenuAdmin);
        jMenuHeartBeat.add(jMenuPeriod);
        jMenuHeartBeat.add(jMenuRetries);
        jMenu1.add(jMenuTree);
        jMenu1.add(jMenuINotifications);

    }


    /**
     *  Other methods *
     *
     *@exception  Exception  Description of the Exception
     */

    /**
     *  Register this object as a notification listener to all the MBean in the
     *  MBeanServer
     *
     *@exception  Exception  Description of the Exception
     */
    public void registerAsListener() throws Exception {
        if (connectorClient == null || !connectorClient.isActive()) {
            return;
        }
        if (isListenerRegistered) {
            unregisterAsListener();
        }

        Set mbeanSet = null;
        Object mbeanArray[] = null;

        // Registration for the Other Notifications
        mbeanSet = connectorClient.queryMBeans(new ObjectName("*:*"), null);
        mbeanArray = mbeanSet.toArray();

        listener.setFrame(null);
        // We make sure
        listener.addNotificationViewer(null);
        // Register for all the current notifications in all the MBeans
        for (int i = 0; i < mbeanArray.length; i++) {
            ObjectName objectname = ((ObjectInstance) mbeanArray[i]).getObjectName();
            registerForMBean(objectname);
        }
        // We set this frame into the listener afterwards because during registration the RMI client
        // tries to serialize the whole listener along with all its objects, this frame included
        listener.addNotificationViewer(notifFrame);
        listener.setFrame(this);

        isListenerRegistered = true;
    }


    /**
     *  Description of the Method
     *
     *@param  mbeanName  Description of the Parameter
     */
    public void registerForMBean(ObjectName mbeanName) {
        try {
            ObjectInstance objectInstance = connectorClient.getObjectInstance(mbeanName);
            Class mbeanClass = Class.forName(objectInstance.getClassName());

            // Register for all the notification in a normal mbean
            Class broadcasterInterface = Class.forName("javax.management.NotificationBroadcaster");
            if (broadcasterInterface.isAssignableFrom(mbeanClass)) {
                connectorClient.addNotificationListener(mbeanName, listener, null, null);
            }

            // Register for all the attribute change notifications in SFModelMBean
            Class sfMBeanClass = Class.forName("javax.management.modelmbean.ModelMBeanNotificationBroadcaster");
            if (sfMBeanClass.isAssignableFrom(mbeanClass)) {
                connectorClient.invoke(
                        mbeanName, "addAttributeChangeNotificationListener",
                        new Object[]{listenerWrapper, null, null},
                // If the attribute is null, we listen to all the attributes
                        new String[]{"javax.management.NotificationListener", "java.lang.String", "java.lang.Object"}
                        );
            }
        } catch (Exception exception) {
            // It doesn't matter. Probably the MBean isn't a Broadcaster
            if (debug) {exception.printStackTrace();}
            else {System.err.println(exception.toString());}

        }
    }


    /**
     *  Revokes previous registration for notifications from all the MBean in
     *  the MBeanServer
     */
    public void unregisterAsListener() {
        if (!isListenerRegistered || connectorClient == null || !connectorClient.isActive()) {
            return;
        }
        Set mbeanSet = null;
        Object mbeanArray[] = null;

        isListenerRegistered = false;
        try {
            mbeanSet = connectorClient.queryMBeans(new ObjectName("*:*"), null);
            mbeanArray = mbeanSet.toArray();
        } catch (Exception exc) {
            //exc.printStackTrace();
            return;
        }
        for (int i = 0; i < mbeanArray.length; i++) {
            try {
                // Unregister from all the MBeans
                ObjectName objectname = ((ObjectInstance) mbeanArray[i]).getObjectName();
                connectorClient.removeNotificationListener(objectname, listener);

                // Unregister for all the attribute change notifications in SFModelMBeans
                ObjectInstance objectInstance = connectorClient.getObjectInstance(objectname);
                Class sfMBeanClass = Class.forName("javax.management.modelmbean.ModelMBeanNotificationBroadcaster");
                Class mbeanClass = Class.forName(objectInstance.getClassName());
                if (sfMBeanClass.isAssignableFrom(mbeanClass)) {
                    connectorClient.invoke(
                            objectname, "removeAttributeChangeNotificationListener",
                            new Object[]{listenerWrapper, null},
                    // If the attribute is null, we remove for all the attributes
                            new String[]{"javax.management.NotificationListener", "java.lang.String"}
                            );
                }
            } catch (Exception exception) {
                isListenerRegistered = true;
                // We couldn't unregister for all the MBeans
                //exception.printStackTrace();
            }
        }
    }


    /**
     *  Description of the Method
     */
    public void connect() {
        try {
            try {
                connectorClient.connect(this.serverAddress);
            } catch (IllegalArgumentException iae) {
                // It can be thrown if the JMX Agent has been shut down and restarted again
                connectorClient = ConnectionFactory.createConnectorClient(serverAddress.getProtocol());
                // CREATION
                connectorClient.addHeartBeatNotificationListener(listener, null, null);
                connectorClient.setHeartBeatPeriod(period);
                connectorClient.setHeartBeatRetries(retries);
                connectorClient.connect(this.serverAddress);
                isConnectionLost = false;
            }
            registerAsListener();
            // If it is registered, this method will unregister first
            queryPanel.requery();

        } catch (java.net.UnknownHostException uex){
               String message = "unknown host "+serverAddress.getHost();
               JOptionPane.showMessageDialog(this, message);
               //System.err.println(message);
        } catch (java.net.ConnectException cex) {
            String message = "Error connecting to ["+serverAddress.toString()+"]";
            JOptionPane.showMessageDialog(this, message);
            message = ("" + cex.getMessage()+ "\n"
           + "        Possible cause: rmi connector not deployed ["+serverAddress.toString()+"]");
            System.err.println (message);
        } catch (java.rmi.NotBoundException nex){
               String message = "not found "+serverAddress.getResource()+" ["+serverAddress.toString()+"]";
               JOptionPane.showMessageDialog(this, message);
               //System.err.println(message);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            if (debug) {ex.printStackTrace();}
            else {System.err.println(ex.toString());}
        }
    }


    /**
     *  Description of the Method
     */
    public void disconnect() {
        if (!isConnectionLost) {
            unregisterAsListener();
        }
        connectorClient.disconnect();
        clear();
        setTitle("JMX Browser");
    }


    /**
     *  Getter method for the RMI Connector Address. Returns the current RMI
     *  Address.
     *
     *@return    The serverAddress value
     */
    public ServerAddress getServerAddress() {
        return serverAddress;
    }


    /**
     *  Setter method for the ServerAddress. Set a new ServerAddress.
     *
     *@param  address  The new connectorAddress value
     */
    public void setConnectorAddress(ServerAddress address) {
        serverAddress = address;
    }


    /**
     *  Gets the mBeanServer attribute of the MainFrame object
     *
     *@return    The mBeanServer value
     */
    public ConnectorClient getMBeanServer() {
        return this.connectorClient;
    }


    /**
     *  Description of the Method
     *
     *@param  action         Description of the Parameter
     *@return                Description of the Return Value
     *@exception  Exception  Description of the Exception
     */
    public Object doAction(PrivilegedExceptionAction action) throws Exception {
        return action.run();
    }


    /**
     *  Set the MBean name in all the panels of which this frame consists
     *
     *@param  mbeanName
     */
    public void setMBean(ObjectName mbeanName) {
        String title = "";
System.out.println("MainFrame Name-=====" + mbeanName.toString());
        try {
            if (mbeanName != null) {
                connectorClient.getObjectInstance(mbeanName);
                title = " - [" + mbeanName.toString() + "]";
            }
            queryPanel.setMBean(mbeanName);
            propertyPanel.setMBean(mbeanName);
            operationPanel.setMBean(mbeanName);
System.out.println("Setting Tree Panel Name-=====" + mbeanName.toString());
            treePanel.setMBean(mbeanName);
        } catch (InstanceNotFoundException e) {
            queryPanel.requery();
            queryPanel.setMBean(null);
            propertyPanel.setMBean(null);
            operationPanel.setMBean(null);
            treePanel.setMBean(null);
            title = "";
        } catch (RuntimeConnectionException ce) {
            JOptionPane.showMessageDialog(this, ce);
            this.disconnect();
            this.clear();
            title = "";
        }
        this.setTitle("JMX Browser" + title);
    }


    /**
     *  Sets the sFComponent attribute of the MainFrame object
     *
     *@param  node  The new sFComponent value
     */
    public void setSFComponent(SFNode node) {
        String title;
        try {
            connectorClient.getMBeanCount();
            // Just to check if the server has not been shut down
            if (node != null) {
                title = " - [" + node.toString() + "]";
            } else {
                title = "";
            }
            if (connectorClient.isActive()) {
                propertyPanel.setSFComponent(node);
                operationPanel.setSFComponent(node);
            } else {
                JOptionPane.showMessageDialog(this, "RMI Connector Client not connected");
                this.jDisconnectButton_actionPerformed(null);
                title = "";
            }
        } catch (RuntimeConnectionException ce) {
            JOptionPane.showMessageDialog(this, ce);
            this.jDisconnectButton_actionPerformed(null);
            title = "";
        }
        this.setTitle("JMX Browser" + title);
    }


    /**
     *  Set the period with which the RMI Connector Client check the connection
     *  with the RMI Connector Server @ param per
     *
     *@param  per  The new heartBeatPeriod value
     */
    public void setHeartBeatPeriod(int per) {
        period = per;
        if (connectorClient != null) {
            connectorClient.setHeartBeatPeriod(period);
        }
    }


    /**
     *  Get the period with which the RMI Connector Client check the connection
     *  with the RMI Connector Server
     *
     *@return    The heartBeatPeriod value
     */
    public int getHeartBeatPeriod() {
        return period;
    }


    /**
     *  Set the number of retries before considering a connection with server
     *  lost @ param ret
     *
     *@param  ret  The new heartBeatRetries value
     */
    public void setHeartBeatRetries(int ret) {
        retries = ret;
        if (connectorClient != null) {
            connectorClient.setHeartBeatRetries(retries);
        }
    }


    /**
     *  Get the number of retries before considering a connection with server
     *  lost
     *
     *@return    The heartBeatRetries value
     */
    public int getHeartBeatRetries() {
        return retries;
    }


    /**
     *  Description of the Method
     */
    public void clear() {
        queryPanel.clear();
        propertyPanel.clear();
        operationPanel.clear();
        treePanel.clear();
        this.setTitle("JMX Browser");
        statusBar.setText("");
    }


    /**
     *  File | Exit action performed
     *
     *@param  e  Description of the Parameter
     */
    public void jMenuFileExit_actionPerformed(ActionEvent e) {
        //System.exit(0);
        this.disconnect();
        this.dispose();
        if (systemExit) System.exit(0);
    }


    /**
     *  Help | About action performed
     *
     *@param  e  Description of the Parameter
     */
    public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
        MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.show();
    }


    /**
     *  Overridden so we can exit when window is closed
     *
     *@param  e  Description of the Parameter
     */
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            jMenuFileExit_actionPerformed(null);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuILFAuto_actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            if (debug) {ex.printStackTrace();}
            else {System.err.println(ex.toString());}
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuLFWin_actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            if (debug) {ex.printStackTrace();}
            else {System.err.println(ex.toString());}
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuLFMetal_actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            if (debug) {ex.printStackTrace();}
            else {System.err.println(ex.toString());}
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuLFMotif_actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            if (debug) {ex.printStackTrace();}
            else {System.err.println(ex.toString());}
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jHelpButton_actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jConnectButton_actionPerformed(ActionEvent e) {
        connect();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jDisconnectButton_actionPerformed(ActionEvent e) {
        disconnect();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jSettingButton_actionPerformed(ActionEvent e) {
//    JOptionPane.showInputDialog(this, "Enter information", "Connection settings", JOptionPane.PLAIN_MESSAGE);
        SettingDialog settingDlg = new SettingDialog(this, "Connection settings", true);
        settingDlg.show();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuServerSettings_actionPerformed(ActionEvent e) {
        jSettingButton_actionPerformed(e);
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuServerConnect_actionPerformed(ActionEvent e) {
        connect();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuServerDisconnect_actionPerformed(ActionEvent e) {
        disconnect();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void tabs_stateChanged(ChangeEvent e) {
        if (connectorClient != null && connectorClient.isActive()) {
            ((ListSelectionListener) tabs.getSelectedComponent()).valueChanged(null);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuPeriod_actionPerformed(ActionEvent e) {
        HeartBeatDialog hbDlg = new HeartBeatDialog(this, HeartBeatDialog.PERIOD, true);
        hbDlg.show();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuRetries_actionPerformed(ActionEvent e) {
        HeartBeatDialog hbDlg = new HeartBeatDialog(this, HeartBeatDialog.RETRIES, true);
        hbDlg.show();
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuTree_actionPerformed(ActionEvent e) {
        if (treePanel.isVisible()) {
            treePanel.setVisible(false);
        } else {
            treePanel.setVisible(true);
            browserSplitter.setDividerLocation(225);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuINotifications_actionPerformed(ActionEvent e) {
        notifFrame.setSize(new Dimension(625, 325));
        notifFrame.setVisible(true);
    }


    /**
     *  Description of the Method
     *
     *@param  e  Description of the Parameter
     */
    void jMenuAdmin_actionPerformed(ActionEvent e) {
        AdminDialog adminDlg = new AdminDialog(this, "Admin", true);
        adminDlg.show();
    }

}
