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
package org.smartfrog.services.display;

import org.smartfrog.SFLoader;

import org.smartfrog.sfcore.common.JarUtil;
import org.smartfrog.sfcore.common.Logger;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;


/**
 * Multiuse Simple display object. It is possible to start / stop the data
 * producer.
 */
public class Display extends JFrame
        implements ActionListener, KeyListener, FontSize {

    /**
     * Class log for static invocations
     */
    static final LogSF logStatic = LogFactory.getLog(Display.class);

    /**
     * Color for non editable screen.
     */
    public final Color NONEDITCOLOR = new Color(80, 60, 120);

    /**
     * Color for editable screen.
     */
    public final Color EDITCOLOR = new Color(17, 0, 132);

    /**
     * Main toolbar on window's top.
     */
    public JToolBar mainToolBar = new JToolBar();

    /**
     * Main tab panel to hold the screen (and other panels).
     */
    public JTabbedPane tabPane = new JTabbedPane();

    /**
     * Layout object.
     */
    protected GridBagLayout gridBagLayout1 = new GridBagLayout();

    /**
     * Stop / resume button in the toolbar.
     */
    protected JToggleButton stopResume = new JToggleButton();

    /**
     * Scrollpane to hold the display's screen.
     */
    protected JScrollPane output = new JScrollPane();

    /**
     * Display's screen object.
     */
    public JTextArea screen = new JTextArea();

    // N, S, E, W, NE, NW, SE, SW, C
    /**
     * Document screen.
     */
    Document documentScreen;
    /**
     * Menu bar.
     */
    public JMenuBar jMenuBarDisplay = new JMenuBar();
    /**
     * Menu file.
     */
    JMenu jMenuDisplayOptions = new JMenu();
    /**
     * Check box.
     */
    JCheckBoxMenuItem jCheckBoxMenuItemPause = new JCheckBoxMenuItem();
    /**
     * Check box AskSaveChages
     */
    JCheckBoxMenuItem jCheckBoxMenuItemAskSaveChanges = new JCheckBoxMenuItem();

    /**
     * Menu item - clean all.
     */
    JMenuItem jMenuItemCleanAll = new JMenuItem();

    /**
     * Menu item - increase font size.
     */
    JMenuItem jMenuItemIncreaseFontSize = new JMenuItem();
    /**
     * Menu item - reduce font size.
     */
    JMenuItem jMenuItemReduceFontSize = new JMenuItem();
    /**
     * Menu item - save as.
     */
    JMenuItem jMenuItemSaveAs = new JMenuItem();
    /**
     * Menu item - exit.
     */
    JMenuItem jMenuItemExit = new JMenuItem();
    /**
     * Menu help.
     */
    JMenu jMenuHelp = new JMenu();
    /**
     * Menu item - info.
     */
    JMenuItem jMenuItemInfo = new JMenuItem();
    /**
     * Menu item - infoprop.
     */
    JMenuItem jMenuItemInfoProp = new JMenuItem();
    /**
     * Menu item - processcomp.
     */
    JMenuItem jMenuItemProcessComp = new JMenuItem();
    /**
     * Menu item - about.
     */
    JMenuItem jMenuItemAbout = new JMenuItem();
    /**
     * File name.
     */
    String currFileName = null;
    /**
     * File chooser.
     */
    JFileChooser jFileChooser1 = null;
    /**
     * Timer.
     */
    Timer timerScroll = new Timer(1 * 1000, this);

    /**
     * Scroll check every n seconds.
     */
    boolean screenScrollChanged = false;

    /**
     * Stop / resume object to control.
     */
    private StopResume stopResumeObj;

    /**
     * Stream to get the key input from the display's screen.
     */
    private PipedInputStream pipeKeyIn;

    /**
     * Stream to write keys pressed in the display's screen.
     */
    private PipedOutputStream pipeKeyOut;

    /**
     * Print Stream to write key characters to pipeKeyOut.
     */
    private PrintStream printKey;

    /**
     * Stream to display messages in the display's screen.
     */
    private PrintStream out;

    /**
     * Posible positions for the frame.
     */
    private String position = "C";

    /**
     * To control content in the text area.
     */
    private boolean dirty = false;
    private Prim sfObj = null;
    private boolean systemExit = true;
    Display mngConsole = null;
    private JMenuItem jMenuItemMngConsole = new JMenuItem();
    private int fontSize = 12;

    /**
     * Constructs Display object with title.
     *
     * @param title Title of the display window
     *
     * @throws Exception if failed to construct the object
     */
    public Display(String title) throws Exception {
        this.setVisible(false);
        jbInit();
        customInit(title, null);
    }


    /**
     * Constructs Display object with a reference to a SF object to be able to
     * terminate it!
     *
     * @param title         Title of the display window
     * @param sfObj         SmartFrog component
     * @param stopResumeObj stop resume object
     *
     * @throws Exception if failed to create the Display object
     */
    public Display(String title, Prim sfObj, StopResume stopResumeObj)
            throws Exception {
        this.sfObj = sfObj;
        this.setVisible(false);
        jbInit();
        customInit(title, stopResumeObj);
    }


    /**
     * Constructs Display object for stop / resume jobs
     *
     * @param title         Title of the display window
     * @param stopResumeObj stop resume object
     *
     * @throws Exception if failed to create the Display object
     */
    public Display(String title, StopResume stopResumeObj) throws Exception {
        this.setVisible(false);
        jbInit();
        customInit(title, stopResumeObj);
    }


    /**
     * Main method used in unit testing.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Example of use
        Display display;

        try {
            // Execution
            if (logStatic.isInfoEnabled()) logStatic.info("# B # Display Executing ");

            // Display Streams
            OutputStream dout = System.out;
            InputStream din = System.in;
            PrintStream outstream = System.out;

            if (WindowUtilities.areGraphicsAvailable()) {
                display = new Display(" Output display ...", null);
                display.setVisible(false);
                WindowUtilities.setPositionWindow(null, display, 0, 1);

                //panel Tree Class. Example to extend Display ;-)
                JPanel panelTree = new JPanel();
                BorderLayout borderLayout1 = new BorderLayout();
                JTree jTree1 = new JTree();
                jTree1.setAutoscrolls(true);
                panelTree.setLayout(borderLayout1);
                panelTree.add(jTree1, BorderLayout.CENTER);
                display.tabPane.add(panelTree, "Test new panel");

                //end panelTree example
                display.setSize(750, 400);
                display.setVisible(true);
                dout = display.getOutputStream();
                din = display.getInputStream();
                outstream = display.getPrintStream();

                // Redirecting standard output:
                System.setOut(outstream);

                // System standard OUT -> screen PrintStream
                System.setErr(outstream);

                // System standard ERR -> screen PrintStream
                System.setIn(din);

                // Screen ImputStreamSystem readed -> standard IN readed
            }

            //Testing output
            System.out.println("Testing Redirection to Display: using System.out");
            outstream.println("Printing directly using PrintStream");
            System.err.println("Printing directly using System.err");

            // Testing standard imput from user:
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = "";
            System.out.println("Reading (type 'end' to finish)...");

            while (!line.equals("end")) {
                try {
                    logStatic.out("Enter something ;-) <ENTER>: ");
                    line = br.readLine();
                    if (logStatic.isInfoEnabled()) logStatic.info("Typed: " + line);
                } catch (Exception e) {
                    if (logStatic.isErrorEnabled()) logStatic.err(e);

                }

                //end catch
            }

            //         //end while
            // Timer to test autoScroll:
            Timer t = new Timer(1 * 1000,
                    new ActionListener() {
                        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");

                        public void actionPerformed(ActionEvent evt) {
                            String message = "timer..." + fmt.format(new Date());
                            if (logStatic.isInfoEnabled()) logStatic.info("Stdout:" + message);
                        }
                    });

            t.start();
        } catch (Exception e) {
            if (logStatic.isErrorEnabled()) logStatic.err("Error in 'full' execution code: " + e, e);
        }
    }


    /**
     * Sets the screen editable property
     *
     * @param editable The new screenEditable value
     */
    public void setScreenEditable(boolean editable) {
        screen.setEditable(editable);

        if (editable) {
            screen.setBackground(EDITCOLOR);
        } else {
            screen.setBackground(NONEDITCOLOR);
        }
    }


    /**
     * Sets the text for the screen
     *
     * @param text The new textScreen value
     */
    public void setTextScreen(String text) {
        if (text != null) {
            screen.setText(text);
        }
    }


    /**
     * Provides the output stream to use in order to display messages in this
     * display's screen
     *
     * @return The outputStream value
     */
    public OutputStream getOutputStream() {
        return out;
    }


    /**
     * Provides the print stream to use in order to display messages in this
     * display's screen
     *
     * @return The printStream value
     */
    public PrintStream getPrintStream() {
        return out;
    }


    /**
     * Get the keys input stream from the screen
     *
     * @return The inputStream value
     */
    public InputStream getInputStream() {
        setScreenEditable(true);

        return pipeKeyIn;
    }


    /**
     * Allows to show / hide the toolbar
     *
     * @param show boolean indicator to show toolbar or not
     */
    public void showToolbar(boolean show) {
        mainToolBar.setVisible(show);
    }


    /**
     * Cleans added panels.
     */
    public void cleanAddedPanels() {
        tabPane.removeAll();
        tabPane.add(output, "output");
    }


    /**
     * Toggles stop / resume button state internally
     */
    public void pushStopResume() {
        if (stopResumeObj == null) {
            return;
        }

        if (stopResume.getModel().isSelected()) {
            stopResume.getModel().setSelected(false);
        } else {
            stopResume.getModel().setSelected(true);
        }

        stopResume_actionPerformed(null);
    }


    /**
     * Prints the char corresponding to key
     *
     * @param ke Key Event
     */
    public void screen_newChar(KeyEvent ke) {
        screen.setCaretPosition(screen.getText().length());

        char key = ke.getKeyChar();

        //out.print(key);
        try {
            printKey.print(key);
        } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error("Error printing key: " + ex.toString(), ex);
        }
    }


    /**
     * Appends the messages to the screen
     *
     * @param msg message
     */
    public void append(String msg) {
        this.screen.append(msg);
    }


    /**
     * Performs the action corresponding to an event.
     *
     * @param e ActionEvent object
     */
    public void actionPerformed(ActionEvent e) {
        //System.out.println("Event");
        try {
            if ((this.jCheckBoxMenuItemPause.isSelected()) &&
                    screenScrollChanged) {
                scrollDownScreen();
            }

            if ((e.getActionCommand()).equals("stopResume")) {
                stopResume_actionPerformed(e);
            }
        } catch (Exception exc) {
        }
    }


    /**
     * Performs the action corresponding to key pressed event.
     *
     * @param e KeyEvent object
     */
    public void keyPressed(KeyEvent e) {
    }


    /**
     * Performs the action corresponding to key released event.
     *
     * @param e KeyEvent object
     */
    public void keyReleased(KeyEvent e) {
    }


    /**
     * Performs the action corresponding to key typed event.
     *
     * @param e KeyEvent object
     */
    public void keyTyped(KeyEvent e) {
        screen_newChar(e);
    }


    /**
     * Scrolls down the screen.
     */
    public void scrollDownScreen() {
        try {
            // Moving scrollbar to the end of the doc
            this.output.getVerticalScrollBar().setValue(this.screen.getHeight() -
                    this.screen.getVisibleRect().height);
            screenScrollChanged = false;
        } catch (Exception e) {
            //this.setTitle("Error: "+e.getMessage());
        }
    }


    /**
     * Toggles stop / resume button state
     *
     * @param e action event
     */
    void stopResume_actionPerformed(ActionEvent e) {
        if (stopResumeObj == null) {
            return;
        }

        if (stopResume.getModel().isSelected()) {
            // stop code...
            stopResumeObj.stop();

            // selected pass to unselected
            stopResume.setText("resume");
        } else {
            // resume code ...
            stopResumeObj.resume();

            // selected pass to unselected
            stopResume.setText("stop");
        }
    }


    /**
     * Checks if file is dirty. If so get user to make a "Save? yes/no/cancel"
     * decision.
     *
     * @return boolean true or false
     */
    boolean okToAbandon() {
        if ((!jCheckBoxMenuItemAskSaveChanges.isSelected())) {
            return true;
        }
        if (!dirty) {
            return true;
        }

        int value = JOptionPane.showConfirmDialog(this, "Save changes?",
                "Text Edit", JOptionPane.YES_NO_CANCEL_OPTION);

        switch (value) {
            case JOptionPane.YES_OPTION:

                // yes, please save changes
                return saveFile();
            case JOptionPane.NO_OPTION:
                // no, abandon edits
                // i.e. return true without saving
                return true;
            case JOptionPane.CANCEL_OPTION:
            default:
                // cancel
                return false;
        }
    }

    // Save current file, asking user for new destination name.
    // Report to statuBar.

    /**
     * Checks if the screen is to be saved as a file.
     *
     * @return boolean true or false depending upon the user choice
     */
    boolean saveAsFile() {
        if (jFileChooser1 == null) {
            jFileChooser1 = new JFileChooser("./");

            // Full path with filename. null means new / untitled.
        }

        // Use the SAVE version of the dialog, test return for Approve/Cancel
        if (JFileChooser.APPROVE_OPTION == jFileChooser1.showSaveDialog(this)) {
            // Set the current file name to the user's selection,
            // then do a regular saveFile
            currFileName = jFileChooser1.getSelectedFile().getPath();

            //repaints menu after item is selected
            repaint();

            return saveFile();
        } else {
            repaint();

            return false;
        }
    }


    /**
     * Saves as file.
     */
    void SaveAs() {
        if (saveAsFile()) {
            // What ever is necessary!
        }
    }

    // Save current file; handle not yet having a filename; report to statusBar.

    /**
     * Save as file.
     *
     * @return boolean true or false
     */
    boolean saveFile() {
        if (jFileChooser1 == null) {
            jFileChooser1 = new JFileChooser("./");

            // Full path with filename. null means new / untitled.
        }

        // Handle the case where we don't have a file name yet.
        if (currFileName == null) {
            return saveAsFile();
        }

        try {
            // Open a file of the current name.
            File file = new File(currFileName);

            // Create an output writer that will write to that file.
            // FileWriter handles international characters encoding conversions.
            FileWriter outstream = new FileWriter(file);
            String text = screen.getText();
            outstream.write(text);
            outstream.close();
            this.dirty = false;

            return true;
        } catch (IOException e) {
            org.smartfrog.services.display.WindowUtilities.showError(this,
                    "Error saving file!");
        }

        return false;
    }


    /**
     * Cleans the file.
     */
    void cleanAll() {
        // Handle the File|New menu item.
        if (okToAbandon()) {
            // clears the text of the TextArea
            screen.setText("");

            // clear the current filename and set the file as clean:
            currFileName = null;
            dirty = false;
        }
    }


    /**
     * Gets the fileName attribute of the Display object
     *
     * @param directory Directory
     *
     * @return The fileName value
     */
    private String getFileName(String directory) {
        if (!directory.endsWith(System.getProperty("file.separator"))) {
            directory = directory + System.getProperty("file.separator");
        }

        String fileName = this.getTitle() + ".log";
        fileName = directory + fileName.replace(' ', '_');
        fileName = directory + fileName.replace(':', '_');
        fileName = fileName.replace('\\', File.pathSeparatorChar);
        fileName = fileName.replace('/', File.pathSeparatorChar);
//      System.out.println(
//            "*************************************To write in:    " + fileName);
        return fileName;
    }


    /**
     * Cleans the screen.
     *
     * @param save      indicator to save or not
     * @param directory directory
     */
    public synchronized void cleanScreen(boolean save, String directory) {
        try {
            if (save) {
                File file = new File(getFileName(directory));

                // Create an output writer that will write to that file.
                // FileWriter handles international characters encoding conversions.
                FileWriter outstream = new FileWriter(file, true);
                String text = screen.getText();
                outstream.write(text);
                outstream.close();
                this.dirty = false;
            }

            // clears the text of the TextArea
            screen.setText("");
        } catch (java.lang.Throwable ex) {
            System.err.println("Display.cleanScreen():" + ex.getMessage());

            try {
                out.close();
            } catch (Exception exc) {
            }
        }
    }


    /**
     * Resets the screen file.
     *
     * @param directory directory
     */
    public void resetScreenFile(String directory) {
        try {
            File file = new File(getFileName(directory));

            if (file.exists()) {
                // Create an output writer that will write to that file.
                // FileWriter handles international characters encoding conversions.
                FileWriter outstream = new FileWriter(file, false);
                outstream.close();
            }
        } catch (Throwable ex) {
            System.err.println("Display.cleanScreen():" + ex.getMessage());

            try {
                out.close();
            } catch (Exception exc) {
            }
        }
    }


    /**
     * Interface method
     *
     * @param e action event
     */
    void jMenuItemExit_actionPerformed(ActionEvent e) {
        exit();
    }


    /**
     * Interface method
     *
     * @param e window event
     */
    void this_windowClosing(WindowEvent e) {
        exit();
    }


    /**
     * Exits from the screen
     */
    void exit() {
        if (okToAbandon()) {
            if ((this.sfObj != null)) {
                // Terminate root process compound
                if ((((SFDisplay) this.sfObj).terminateSFProcessOnExit) &&
                        // If the process name is in the title we assume that is being
                        // used in default.sf and that it is displaying the output of
                        // a sf process daemon!
                        (((SFDisplay) this.sfObj).sfProcessName != null)) {
                    try {
                        String prs = null;
                        prs = this.sfObj.sfResolve(SmartFrogCoreKeys.SF_PROCESS, prs, false);
                        System.out.println("Trying to terminate: " + prs);
                        java.net.InetAddress address = null;
                        address = this.sfObj.sfResolve(SmartFrogCoreKeys.SF_HOST, address,
                                false);
                        if (prs.equals(SmartFrogCoreKeys.SF_ROOT_PROCESS)) {
                            //Terminating main daemon
                            try {
                                TerminationRecord tr = TerminationRecord.normal(
                                        "sfDaemon display closed",
                                        null);
                                System.out.println("Terminating Daemon");
                                new TerminatorThread((SFProcess.
                                        getRootLocator().
                                        getRootProcessCompound(address)),
                                        tr).start();
                            } catch (Exception ex) {
                                if ((ex.getCause() instanceof java.net.
                                        SocketException)) {
                                    // Ignore.
                                } else {
                                    //log(ex);
                                }
                            }

                        } else {
                            // Find subprocess named prs where sfDisplay is running
                            Prim pr = (Prim) ((Prim) (SFProcess.getRootLocator().
                                    getRootProcessCompound(address))).
                                    sfResolveHere(prs);
                            if (pr != null) {
                                try {
                                    // Terminate SubProcess
                                    TerminationRecord tr = TerminationRecord.normal(
                                            "Display '" + prs + "' subprocess closed",
                                            null);
                                    new TerminatorThread(pr, tr).detach()
                                            .start();
                                } catch (Exception rex) {
                                    //ignore
                                }
                            } else {
                                // Terminate sfDisplayCompound
                                try {
                                    TerminationRecord tr = TerminationRecord.normal(
                                            "User termination", null);
                                    sfObj.sfDetachAndTerminate(tr);
                                } catch (Exception ex) {
                                    sfLog().error(ex);
                                }
                            }
                        }

                    } catch (SmartFrogException sfex) {
                        //log
                    } catch (RemoteException rex) {
                        //log
                    } catch (Exception ex) {
                        //log
                    }
                } else {
                    // Terminate sfDisplayCompound
                    try {
                        TerminationRecord tr = new TerminationRecord("normal", "Display close by user", null);
                        this.sfObj.sfDetachAndTerminate(tr);
                    } catch (Exception ex) {
                        if (sfLog().isErrorEnabled()) sfLog().error(ex);
                    }
                }
            } else if (systemExit) {
                sfLog().out("Exiting console");
                System.exit(0);
            }

            // Dispose consoles!
            try {
                // if a management console was displayed, dispose it!
                if (this.mngConsole != null) {
                    this.mngConsole.dispose();
                }
            } catch (Exception ex) {
            }
            // Dispose main window.
            this.dispose();
            //TOCHECK: Should it call terminate, detach and terminate? or  not?
        }
    }


    /**
     * Sets the shouldSystemExit attribute of the Display object
     *
     * @param systemExit The new shouldSystemExit value
     */
    public void setShouldSystemExit(boolean systemExit) {
        this.systemExit = systemExit;
    }


    /**
     * Interface method
     *
     * @param e Actione event
     */
    void jMenuItemAbout_actionPerformed(ActionEvent e) {
        info();
    }


    /**
     * Displays the SF system info.
     */
    void info() {
        out.println("");
        out.println("*******************************************************");
        out.println("* " + org.smartfrog.Version.versionString() +
                "\n* " + org.smartfrog.Version.copyright());
        out.println("*******************************************************");
        out.println("* cd Java Version:   " + System.getProperty("java.version"));
        out.println("* Java Home:      " + System.getProperty("java.home"));
        out.println("* Java Ext Dir:   " + System.getProperty("java.ext.dirs"));

        //out.println("* Java ClassPath: " + System.getProperty("java.class.path")
        //);
        out.println("* OS Name:        " + System.getProperty("os.name"));
        out.println("* OS Version:     " + System.getProperty("os.version"));
        out.println("* User Name:      " + System.getProperty("user.name"));
        out.println("* User Home:      " + System.getProperty("user.home"));
        out.println("* User Work Dir:  " + System.getProperty("user.dir"));

        try {
            InetAddress localhost = SFProcess.sfDeployedHost();
            out.println("* LocalHost Name: " + localhost.getHostName());
            out.println("* LocalHost Add:  " + localhost.getHostAddress());

            //out.println("* isMulticast?    " + localhost.isMulticastAddress());
        } catch (SmartFrogException ex) {
            System.out.println("Exception Info:" + ex.toString());
        }

        out.println("* Java ClassPath: " + "\n" +
                (System.getProperty("java.class.path")).replace(
                        System.getProperty("path.separator").charAt(0), '\n'));
        try {
            String[] words = Logger.testJarRepeat;
            String classpath[] = (System.getProperty("java.class.path")).split(
                    System.getProperty("path.separator"));
            StringBuffer message = Logger.getRepeatsMessage(words, classpath);
            if (message != null) {
                out.println("* Possible problem with classpath: \n" + message.toString());
            }
        } catch (Throwable thr) { /* ignore*/ }
        out.println("*****************************************************");
        out.println("");
    }


    /**
     * Displays system properties.
     */
    void infoProperties() {
        //    log("\nRunning: "
        //           +"\n  -SF v"+org.smartfrog.Version.versionString
        //           +"\n  - Java v " + System.getProperty("java.version")
        //           +"\n  -Classpath: "+System.getProperty("java.class.path")
        //           +"\n  -Loaded classpath: "+classpath,"INFOMnu",3);
        out.println("\n*****************************************************");
        out.println("*   Info Properties: ");
        out.println("*  ----------------- ");

        Properties sysprops = null;
        try {
            sysprops = System.getProperties();

            Vector<String> keysVector = new Vector<String>();
            for (Enumeration keys = sysprops.propertyNames();
                 keys.hasMoreElements();) {
                keysVector.add((String) keys.nextElement());
            }
            // Order keys
            keysVector = JarUtil.sort(keysVector);

            for (String key : keysVector) {
                String value = "";
                try {
                    if (!(key.trim().length() == 0)) {
                        value = System.getProperty(key);
                    }
                } catch (SecurityException e) {
                    value = "Access to this property blocked by a security manager";
                }
                out.println("* " + key + ": " + value);
            }
        } catch (SecurityException e) {
            out.append(
                    "Access to System.getProperties() blocked by a security manager\n");
        }
        out.println("*****************************************************\n");
    }


    /**
     * Displays information about process compound
     */
    void infoProcessCompound() {
        try {

            out.println(
                    "\n*****************************************************");
            out.println("*   Diagnostics Process Compound: ");
            out.println("*  ------------------------ ");
            out.println(SFProcess.getProcessCompound().sfDiagnosticsReport());
            out.println(
                    "*****************************************************\n");
        } catch (Exception ex) {
            out.println(" Error infoProcessCompound: " + ex.getMessage());
        }
    }


    /**
     * Starts the management console
     */
    void startMngConsole() {
        if (sfObj == null) {
            this.modalErrorDialog("startMngConsole",
                    "Couldn't start SFMngConsole. Not running as SF component.");
            return;
        }
        //End option dialog
        int port = 3800;
        String hostName = null;

        try {
            hostName = SFProcess.getProcessCompound().sfDeployedHost().getCanonicalHostName();
        } catch (RemoteException e) {
            hostName = ""; //Ignored.
        }

        //New option dialgog
        hostName = modalOptionDialog("Management Console for ...", "HostName: ", hostName);
        if (hostName == null) return;

        try {
            java.net.InetAddress.getByName(hostName);
            SFProcess.getRootLocator().getRootProcessCompound(java.net.InetAddress.getByName(hostName), port);
        } catch (java.net.UnknownHostException uex) {
            this.modalErrorDialog("startMngConsole",
                    "Couldn't start SFMngConsole for resource " + hostName +
                            ". Unknown host.");
            return;
        } catch (java.rmi.ConnectException cex) {
            this.modalErrorDialog("startMngConsole", "Couldn't start SFMngConsole for resource " + hostName + ". " + cex.getMessage());
            return;
        } catch (Exception e) {
            this.modalErrorDialog("startMngConsole", "Couldn't start SFMngConsole for resource " + hostName);
            return;
        }

        try {
            //hostName = this.sfObj.sfDeployedHost().getCanonicalHostName();
            int height = 480;
            int width = 640;
            boolean showRootProcess = false;
            boolean showCDasChild = true;
            boolean showScripting = false;
            String positionDisplay = "NE";

            try {
                port = this.sfObj.sfResolve(new Reference(SmartFrogCoreKeys.SF_ROOT_LOCATOR_PORT), port, false);
            } catch (SmartFrogResolutionException rex) {
            }
            String nameDisplay = "sfManagementConsole ";
            mngConsole = org.smartfrog.services.management.SFDeployDisplay.
                    startConsole(nameDisplay, height, width, positionDisplay, showRootProcess, showCDasChild, showScripting, hostName, port, false);
        } catch (java.net.UnknownHostException uex) {
            if (mngConsole != null) {
                mngConsole.dispose();
                mngConsole = null;
            }
            this.modalErrorDialog("startMngConsole",
                    "Couldn't start SFMngConsole for resource " + hostName +
                            ". Unknown host.");
        } catch (java.rmi.ConnectException cex) {
            if (mngConsole != null) {
                mngConsole.dispose();
                mngConsole.exit();
                mngConsole = null;
            }
            this.modalErrorDialog("startMngConsole",
                    "Couldn't start SFMngConsole for resource " + hostName + ". " +
                            cex.getMessage());
        } catch (Exception e) {
            if (mngConsole != null) {
                mngConsole.dispose();
                mngConsole = null;
            }
            this.modalErrorDialog("startMngConsole",
                    "Couldn't start SFMngConsole for resource " + hostName);
        }
    }


    /**
     * Prepares error dialog box
     *
     * @param title   Title
     * @param message message to be displayed
     */
    private void modalErrorDialog(String title, String message) {
        dialogBox(this, true, title, JOptionPane.ERROR_MESSAGE, message);
    }

    /**
     * Prepares option dialog box
     *
     * @param title        title displayed on the dialog box
     * @param message      message to be displayed
     * @param defaultValue default value
     *
     * @return formatted string
     */
    private String modalOptionDialog(String title, String message,
                                     String defaultValue) {
        String s = (String) JOptionPane.showInputDialog(
                this,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultValue);
        if (s == null) return s; //User cancelled!
        if ((s != null) && (s.length() > 0)) {
            return s;
        } else {
            return defaultValue;
        }
    }


    /**
     * Prepares a dialog box.
     *
     * @param frame      JFrame
     * @param modal      boolean indicator for modal
     * @param title      window title
     * @param windowType window type
     * @param message    Message to be displayed
     */
    private void dialogBox(final JFrame frame,
                           final boolean modal,
                           final String title,
                           final int windowType,
                           final String message) {
        JOptionPane errorPane = new JOptionPane(message, windowType);
        JDialog dialog = errorPane.createDialog(frame, title);
        dialog.setModal(modal);
        dialog.show();
    }


    /**
     * Interface method
     *
     * @param e Action event
     */
    void jMenuItemInfo_actionPerformed(ActionEvent e) {
        info();
    }


    /**
     * Interface method
     *
     * @param e Action event
     */
    void jMenuItemIncreaseFontSize_actionPerformed(ActionEvent e) {
        increaseFontSize();
    }

    /**
     * Interface method
     *
     * @param e Action event
     */
    void jMenuItemReduceFontSize_actionPerformed(ActionEvent e) {
        reduceFontSize();
    }

    /**
     * Interface method
     *
     * @param e Action event
     */
    void jMenuItemCleanAll_actionPerformed(ActionEvent e) {
        cleanAll();
    }

    /**
     * Interface method
     *
     * @param e action event
     */
    void jMenuItemSaveAs_actionPerformed(ActionEvent e) {
        SaveAs();
    }


    /**
     * Interface method
     *
     * @param e document event
     */
    void documentScreen_changedUpdate(DocumentEvent e) {
        dirty = true;
    }


    /**
     * Interface method
     *
     * @param e document event
     */
    void documentScreen_insertUpdate(DocumentEvent e) {
        dirty = true;

        //      SimpleDateFormat fmt = new SimpleDateFormat ("HH:mm:ss");
        //      this.setTitle("timer..."+ fmt.format(new Date())+e.getType().
        //      toString());
        //      System.out.println(fmt.format(new Date())+e.toString());
        if ((this.jCheckBoxMenuItemPause.isSelected()) &&
                (e.getType().toString().equals("INSERT"))) {
            //         System.out.println("Scrolling..."+fmt.format(new Date()));
            // This locks the components in SF. It seems that this is not the right
            // place to do the update.
            //scrollDownScreen();
            screenScrollChanged = true;
        }
    }


    /**
     * Interface method
     *
     * @param e document event
     */
    void documentScreen_removeUpdate(DocumentEvent e) {
        dirty = true;
    }


    /**
     * Interface method
     *
     * @param e window event
     */
    void this_windowClosed(WindowEvent e) {
    }


    /**
     * Interface method
     *
     * @param e action event
     */
    void jMenuItemInfoProp_actionPerformed(ActionEvent e) {
        this.infoProperties();
    }


    /**
     * Interface method
     *
     * @param e action event
     */
    void jMenuItemProcessComp_actionPerformed(ActionEvent e) {
        this.infoProcessCompound();
    }


    /**
     * Widjets initialization
     *
     * @throws Exception If unable to initialize
     */
    private void jbInit() throws Exception {

        String imagesPath = Display.class.getPackage().getName() + ".";
        imagesPath = imagesPath.replace('.', '/');
        //imagesPath = imagesPath + "frog.gif";
        imagesPath = imagesPath + "SplodgeGreen32.gif";
        this.setIconImage(createImage(imagesPath));

        documentScreen = screen.getDocument();
        this.getContentPane().setLayout(gridBagLayout1);
        stopResume.setText("stop");
        stopResume.setActionCommand("stopResume");
        stopResume.addActionListener(this);
        stopResume.setVisible(false);

        screen.setBackground(NONEDITCOLOR);
        screen.setForeground(SystemColor.text);
        screen.addKeyListener(this);
        screen.setFont(new Font("DialogInput", 0, fontSize));

        mainToolBar.setBorder(BorderFactory.createEtchedBorder());
        mainToolBar.setDoubleBuffered(true);

        //Menus
        //File
        jMenuDisplayOptions.setText("Display options");

        //Exit
        jMenuItemExit.setText("Exit");
        jMenuItemExit.setAccelerator(KeyStroke.getKeyStroke(115,
                KeyEvent.ALT_MASK, false));
        jMenuItemExit.addActionListener(new Display_jMenuItemExit_actionAdapter(
                this));

        //Help
        jMenuHelp.setText("Help");

        //About
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new Display_jMenuItemAbout_actionAdapter(
                this));

        //Creating comps
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new Display_this_windowAdapter(this));
        jMenuItemInfo.setText("Info");
        jMenuItemInfo.addActionListener(new Display_jMenuItemInfo_actionAdapter(
                this));

        jMenuItemIncreaseFontSize.setText("+ font size");
        jMenuItemIncreaseFontSize.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK, false));
        jMenuItemIncreaseFontSize.addActionListener(new Display_jMenuItemIncreaseFontSize_actionAdapter(
                this));

        jMenuItemReduceFontSize.setText("- font size");
        jMenuItemReduceFontSize.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK, false));
        jMenuItemReduceFontSize.addActionListener(new Display_jMenuItemReduceFontSize_actionAdapter(
                this));

        jMenuItemCleanAll.setText("Clean all");
        jMenuItemCleanAll.setAccelerator(KeyStroke.getKeyStroke(
                67, KeyEvent.ALT_MASK, false));
        jMenuItemCleanAll.addActionListener
                (new Display_jMenuItemCleanAll_actionAdapter(this));
        jMenuItemSaveAs.setText("Save as...");
        jMenuItemSaveAs.addActionListener
                (new Display_jMenuItemSaveAs_actionAdapter(this));
        documentScreen.addDocumentListener
                (new Display_documentScreen_documentAdapter(this));
        jMenuItemInfoProp.addActionListener
                (new Display_jMenuItemInfoProp_actionAdapter(this));
        jMenuItemInfoProp.setText("Info System Properties");
        jMenuItemProcessComp.setText("Diagnostics Report ");
        jMenuItemProcessComp.addActionListener
                (new Display_jMenuItemProcessComp_actionAdapter(this));
        jCheckBoxMenuItemPause.setToolTipText("Pause AutoScroll (Alt+P)");
        jCheckBoxMenuItemPause.setText("AutoScroll");
        jCheckBoxMenuItemPause.setSelected(true);
        // AutoScroll by default
        jCheckBoxMenuItemPause.setAccelerator(KeyStroke.getKeyStroke(
                80, KeyEvent.ALT_MASK, false));

        jCheckBoxMenuItemAskSaveChanges.setToolTipText("Ask to Save Changes?");
        jCheckBoxMenuItemAskSaveChanges.setText("Ask Save Changes?");
        jCheckBoxMenuItemAskSaveChanges.setSelected(true);


        jMenuItemMngConsole.setText("SF Management Console");
        jMenuItemMngConsole.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        jMenuItemMngConsole_actionPerformed(e);
                    }
                });
        jMenuBarDisplay.add(jMenuDisplayOptions);
        jMenuBarDisplay.add(jMenuHelp);
        jMenuDisplayOptions.add(jCheckBoxMenuItemPause);
        jMenuDisplayOptions.add(jCheckBoxMenuItemAskSaveChanges);
        jMenuDisplayOptions.addSeparator();
        jMenuDisplayOptions.add(jMenuItemIncreaseFontSize);
        jMenuDisplayOptions.add(jMenuItemReduceFontSize);
        jMenuDisplayOptions.addSeparator();
        jMenuDisplayOptions.add(jMenuItemCleanAll);
        jMenuDisplayOptions.add(jMenuItemSaveAs);

        jMenuDisplayOptions.addSeparator();
        jMenuDisplayOptions.add(jMenuItemExit);
        jMenuHelp.add(jMenuItemInfo);
        jMenuHelp.add(jMenuItemInfoProp);
        jMenuHelp.addSeparator();
        jMenuHelp.add(jMenuItemProcessComp);
        jMenuHelp.add(jMenuItemMngConsole);
        jMenuHelp.addSeparator();
        jMenuHelp.add(jMenuItemAbout);
        this.setJMenuBar(jMenuBarDisplay);

        //end Menus
        this.getContentPane().add(tabPane,
                new GridBagConstraints(0, 2, 2, 10, 75.0, 75.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(8, 8, 8, 8), 0, 0));
        tabPane.add(output, "output");
        output.getViewport().add(screen, null);
        this.getContentPane().add(mainToolBar,
                new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                        GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                        new Insets(0, 8, 8, 8), 1, 1));
        mainToolBar.add(stopResume, null);
    }

    public void setFontSize(int fontSize) {
        Object selected = getSelectedInTab();
        if (selected instanceof JTextArea) {
            Font font = ((JTextArea) selected).getFont();
            ((JTextArea) selected).setFont(new java.awt.Font(font.getName(), 0, screen.getFont().getSize() + 1));
        } else if (selected instanceof FontSize) {
            ((FontSize) selected).setFontSize(fontSize);
        } else {
            WindowUtilities.showError(this, "Cannot set font size, wrong component selected.\n Selected " + selected);
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Cannot set font size, wrong component selected.\n Selected " + selected);
            }
        }
    }

    public void increaseFontSize() {
        Object selected = getSelectedInTab();
        if (selected instanceof JTextArea) {
            Font font = ((JTextArea) selected).getFont();
            ((JTextArea) selected).setFont(new java.awt.Font(font.getName(), 0, screen.getFont().getSize() + 1));
        } else if (selected instanceof FontSize) {
            ((FontSize) selected).increaseFontSize();
            ;
        } else {
            WindowUtilities.showError(this, "Cannot increase font, wrong component selected.\n Selected " + selected);
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Cannot increase font, wrong component selected.\n Selected " + selected);
            }
        }
    }


    public void reduceFontSize() {
        Object selected = getSelectedInTab();
        if (selected instanceof JTextArea) {
            Font font = ((JTextArea) selected).getFont();
            if (font.getSize() > 1) {
                ((JTextArea) selected).setFont(new java.awt.Font(font.getName(), 0, screen.getFont().getSize() - 1));
            }
        } else if (selected instanceof FontSize) {
            ((FontSize) selected).reduceFontSize();
            ;
        } else {
            WindowUtilities.showError(this, "Cannot reduce font, wrong component selected.\n Selected " + selected);
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Cannot reduce font, wrong component selected.\n Selected " + selected);
            }
        }

        //---
    }

    protected Object getSelectedInTab() {
        Object selected = tabPane.getSelectedComponent();
        if (selected instanceof JScrollPane) {
            Object aux = ((JScrollPane) selected).getViewport().getComponent(0);
            if (aux != null) {
                selected = aux;
            }
        }
        return selected;
    }

    public void setAskSaveChanges(boolean askSaveChanges) {
        jCheckBoxMenuItemAskSaveChanges.setSelected(askSaveChanges);
    }

    public static Image createImage(String imagesPath) {
        try {
            byte imageData[] = SFLoader.getByteArrayForResource(imagesPath);
            Image img = java.awt.Toolkit.getDefaultToolkit().createImage(imageData, 0, imageData.length);
            return img;
        } catch (Exception e) {
            //ignore
            if (logStatic.isErrorEnabled()) {
                logStatic.error(e);
            }
        }
        return null;
    }

    /**
     * Initializes with custom details.
     *
     * @param title            Window title
     * @param stopResumeObject stop resume object
     *
     * @throws Exception if unable to initialize
     */
    private void customInit(String title, StopResume stopResumeObject)
            throws Exception {
        out = new PrintStream(new TextAreaOutputStream(screen));
        out.println("");

        setTitle(title);
        setSize(500, 400);
        validate();
        this.stopResumeObj = stopResumeObject;

        if (this.stopResumeObj == null) {
            stopResume.setVisible(false);
            showToolbar(false);
        } else {
            stopResume.setVisible(true);
            showToolbar(true);
        }

        try {
            pipeKeyIn = new PipedInputStream();
            pipeKeyOut = new PipedOutputStream(pipeKeyIn);
            printKey = new PrintStream(pipeKeyOut);
        } catch (Exception e) {
            if (sfLog().isErrorEnabled()) {
                sfLog().error("Error connecting pipes:: " + e.toString(), e);
            }
        }

        // Timer for autoScroll
        timerScroll.start();
    }


    /**
     * Interface method
     *
     * @param e action event
     */
    void jMenuItemMngConsole_actionPerformed(ActionEvent e) {
        this.startMngConsole();
    }

//End Display class methods.

    /**
     * Class Display_jMenuItemExit_actionAdapter
     */
    static class Display_jMenuItemExit_actionAdapter
            implements ActionListener {

        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs Display_jMenuItemExit_actionAdapter object with Display
         * object
         *
         * @param adaptee adaptee object
         */
        Display_jMenuItemExit_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemExit_actionPerformed(e);
        }
    }

    /**
     * Class Display_jMenuItemAbout_actionAdapter
     */
    static class Display_jMenuItemAbout_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemAbout_actionAdapter object
         *
         * @param adaptee Display Object
         */
        Display_jMenuItemAbout_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface Method
         *
         * @param e Action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemAbout_actionPerformed(e);
        }
    }

    /**
     * Display_jMenuItemInfo_actionAdapter
     */
    static class Display_jMenuItemInfo_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemInfo_actionAdapter object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemInfo_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemInfo_actionPerformed(e);
        }
    }

    /**
     * Display_jMenuItemCleanAll_actionAdapter
     */
    static class Display_jMenuItemCleanAll_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemCleanAll_actionAdapter object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemCleanAll_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemCleanAll_actionPerformed(e);
        }
    }


    /**
     * Display_jMenuItemIncreaseFontSize_actionAdapter
     */
    static class Display_jMenuItemIncreaseFontSize_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemIncreaseFontSize_actionAdapter
         * object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemIncreaseFontSize_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemIncreaseFontSize_actionPerformed(e);
        }
    }


    /**
     * Display_jMenuItemReduceFontSize_actionAdapter
     */
    static class Display_jMenuItemReduceFontSize_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemReduceFontSize_actionAdapter object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemReduceFontSize_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemReduceFontSize_actionPerformed(e);
        }
    }

    /**
     * Display_jMenuItemSaveAs_actionAdapter
     */
    static class Display_jMenuItemSaveAs_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemSaveAs_actionAdapter object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemSaveAs_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface Method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemSaveAs_actionPerformed(e);
        }
    }

    /**
     * Display_documentScreen_documentAdapter
     */
    static class Display_documentScreen_documentAdapter
            implements DocumentListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_documentScreen_documentAdapter object
         *
         * @param adaptee Display object
         */
        Display_documentScreen_documentAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface Method
         *
         * @param e document event
         */
        public void insertUpdate(DocumentEvent e) {
            adaptee.documentScreen_insertUpdate(e);
        }


        /**
         * Removes tge updated document
         *
         * @param e document event
         */
        public void removeUpdate(DocumentEvent e) {
            adaptee.documentScreen_removeUpdate(e);
        }


        /**
         * Interface Method
         *
         * @param e document event
         */
        public void changedUpdate(DocumentEvent e) {
            adaptee.documentScreen_changedUpdate(e);
        }
    }

    /**
     * Display_this_windowAdapter
     */
    static class Display_this_windowAdapter extends WindowAdapter {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_this_windowAdapter object
         *
         * @param adaptee Display object
         */
        Display_this_windowAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface Method
         *
         * @param e window event
         */
        public void windowClosing(WindowEvent e) {
            adaptee.this_windowClosing(e);
        }


        /**
         * Interface Method
         *
         * @param e Window event
         */
        public void windowClosed(WindowEvent e) {
            adaptee.this_windowClosed(e);
        }
    }

    /**
     * Display_jMenuItemInfoProp_actionAdapter
     */
    static class Display_jMenuItemInfoProp_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs for the Display_jMenuItemInfoProp_actionAdapter object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemInfoProp_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface Method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemInfoProp_actionPerformed(e);
        }
    }

    /**
     * Display_jMenuItemProcessComp_actionAdapter
     */
    static class Display_jMenuItemProcessComp_actionAdapter
            implements ActionListener {


        /**
         * Display object.
         */
        private Display adaptee;


        /**
         * Constructs the Display_jMenuItemProcessComp_actionAdapter object
         *
         * @param adaptee Display object
         */
        Display_jMenuItemProcessComp_actionAdapter(Display adaptee) {
            this.adaptee = adaptee;
        }


        /**
         * Interface Method
         *
         * @param e action event
         */
        public void actionPerformed(ActionEvent e) {
            adaptee.jMenuItemProcessComp_actionPerformed(e);
        }

    }

    public LogSF sfLog() {
        try {
            if (sfObj != null) {
                return LogFactory.getLog(sfObj);
            }
        } catch (Exception ex) {
        }
        return logStatic;
    }
}
