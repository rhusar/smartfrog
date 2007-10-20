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

import org.smartfrog.tools.gui.browser.util.GenParsePanel;

//@TODO: FILTERS FOR MULTILANGUAGE DON'T WORK WHEN EXTENSION IN CAPITALS!!! Should it work with capitals?

import com.jeffguy.IniFile;

import java.io.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.text.*;
import java.awt.event.*;

import java.util.Date;
import java.text.SimpleDateFormat;

// for Splash window
import javax.swing.border.*;

//import java.text.DateFormat;

//import org.smartfrog.tools.gui.browser.gui.TextAreaOutputStream;

//Took from JEdit
import org.gjt.sp.jedit.textarea.*;
import org.gjt.sp.jedit.syntax.*;
import org.smartfrog.tools.gui.browser.syntax.*;
// SF Syntax
import javax.swing.event.*;
import java.util.Vector;
import java.util.Iterator;

import java.util.Enumeration;

import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.tools.gui.browser.util.BrowseSFFilesTreePanel;
import org.smartfrog.tools.gui.browser.util.LoadSFFiles;

/**

 *@created    19 September 2001
 */
//to DO:
// - tree pane
//
// Not tested:
// - redirected keyboard fron processes!
//
/**
 *@created
 */

public class MainFrame
    extends JFrame implements ActionListener {
  /**
   *  Description of the Field
   */
  public final static String version = "v0.8 r04";
  // This has to  be done properly !!!!!!!!!!!!!!! no static. Because of crap log.
  static PrintStream msg = System.out;
  static JLabel statusBar = new JLabel();
  private static Image imageApp;

  private static int scrollTime = 2 * 1000;
  private Timer timerScroll = new Timer(scrollTime, this);
  private boolean screenScrollChanged = false;
  private boolean msgScrollChanged = false;

  private static String iniFileName = "bin/sfGuiCFG.bat";
  private static String iniKeySFFilesDir = "rem sfFilesDir";
  //cheating the bat file, used for config as well;-)
  private static String iniSecRunProcess = "SFGuiConfig";
  private static String iniKeySFSystemClass = "rem SFSystemClass";
//   private static String iniKeyClasspath = "set libs"; // Should be taken from system.property(classpath);
  private static String iniKeyCmdSFDaemon = "rem cmdSFDaemon";
  private static String iniKeySFDaemonProcessName = "rem SFDaemonProcessName";
  private static String iniKeySFDaemonDefIniFile = "rem SFDaemonDefIniFile";
  private static String iniKeySFDaemonDefSFFile = "rem SFDaemonDefSFFile";
  private static String iniKeyCmdSFStart = "rem cmdSFStart";
  private static String iniKeyCmdSFStop = "rem cmdSFStop";
  private static String iniKeyCmdAddSecurity = "rem suffixSecureScrip";
  private static String iniKeyCmdBrowserURL = "rem cmdBrowserURL";
  private static String iniKeyCmdExeBrowserWindows = "rem cmdBrowserWin";
  private static String iniKeyCmdExeBrowserLinux = "rem cmdBrowserLinux";
  private static String iniKeyLookAndFeel = "rem lookAndFeel";

  private String sfFilesDir = "./sf";
  // Default SFSystem class
  private String sfSystemClass = "org.smartfrog.SFSystem";
  //Default config for Browsers
  private String cmdExeBrowserLinux = "firefox";
  private String cmdExeBrowserWindows = "explorer";
  private String cmdBrowserURL = "http://127.0.0.1:4242/";

  // Default SF Commands ...
  private String cmdSFDaemon = "-Dorg.smartfrog.processcompound.sfProcessName=";
  private String sfDaemonProcessName = "rootProcess";
  private String sfDaemonDefIniFileProperty = "-Dorg.smartfrog.iniFile=";
  private String sfDaemonDefSFFileProperty =
      "-Dorg.smartfrog.sfcore.processcompound.sfDefault.sfDefault=";
  private String sfDaemonDefIniFile = "./bin/default.ini";
  private String sfDaemonDefSFFile = "./bin/default.sf";
  //"sfDaemon";
  private String sfDaemonFile = "./bin/daemon.sf";
  private String cmdSFStart = "DEPLOY";
  private String cmdSFStop = "TERMINATE";
  private String cmdAddSecurity = "security";
  //lookAndFeel
  private String lookAndFeel = "kunststoff";

  String panelNameParse = "SFParse";
  String panelNameBrowseComp = "Browse SF Comp";
  String panelNameSFFile = "SF File";
  String panelNameRaw = "Raw";
  String panelNameType = "Type Reso.";
  String panelNamePlace = "Placement";
  String panelNameDescription = "Description";
  String panelNameDeploy = "Deploy";
  String panelNameGenParse = "Adv.Parser";

  int autoNameCounter = 0;

  //Token markers
  org.gjt.sp.jedit.syntax.TokenMarker sfTokenMarker = new SfTokenMarker();
  org.gjt.sp.jedit.syntax.TokenMarker sf2TokenMarker = new Sf2TokenMarker();
  org.gjt.sp.jedit.syntax.TokenMarker sfXMLTokenMarker = new SfXMLTokenMarker();
  //Default TokenMarker
  org.gjt.sp.jedit.syntax.TokenMarker activeTokenMarker = sfTokenMarker;

  JPanel contentPane;
  JMenuBar jMenuBarMain = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  /*
   *  Tool Bar
   */
  JToolBar jToolBar = new JToolBar();
  /**
   *  Description of the Field
   */
  public ImageIcon imageOpen;
  ImageIcon imageSave;
  ImageIcon imageAbout;
  /**
   *  Description of the Field
   */
  public ImageIcon imageCopy;
  ImageIcon imageCut;
  ImageIcon imagePaste;
  ImageIcon imageUndo;
  ImageIcon imageRedo;

  /**
   *  Description of the Field
   */
  public ImageIcon imageParse;
  /**
   *  Description of the Field
   */
  public ImageIcon imageRun;
  ImageIcon imageStop;
  ImageIcon imagePreferences;
  ImageIcon imageBrowseSF;
  ImageIcon imageSFDaemon;
  ImageIcon imageSFStopDaemon;
  ImageIcon imageExit;
  ImageIcon imageMngConsole;

  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  JTabbedPane jTabbedPanelNorth = new JTabbedPane();
  JTabbedPane jTabbedPaneSouth = new JTabbedPane();
  JScrollPane jScrollPaneMsg = new JScrollPane();
//  JTextArea jTextAreaSFFile = new JTextArea();// !!!!!!!!!!!!!
  JMenuItem jMenuItemWin = new JMenuItem();
  JMenuItem jMenuItemMetal = new JMenuItem();
  JMenuItem jMenuItemAuto = new JMenuItem();
  JMenu jMenuLF = new JMenu();
  JMenuItem jMenuItemMotif = new JMenuItem();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuItemOpen = new JMenuItem();
  JMenu jMenuSF = new JMenu();
  JMenuItem jMenuItemRun = new JMenuItem();
  JMenuItem jMenuItemParse = new JMenuItem();
  boolean dirty = false;
  JMenuItem jMenuItemSaveAs = new JMenuItem();
  JMenuItem jMenuItemSave = new JMenuItem();
  /**
   *  Description of the Field
   */
  public JEditTextArea jTextAreaSFFile = new JEditTextArea();
  JTabbedPane jTabbedPaneParse = new JTabbedPane();
//  JTextArea jTextAreaRaw = new JTextArea();
//  JTextArea jTextAreaType = new JTextArea();
//  JTextArea jTextAreaPlace = new JTextArea();
//  JTextArea jTextAreaDeploy = new JTextArea();// True means modified text.
  JEditTextArea jTextAreaRaw = new JEditTextArea();
  JEditTextArea jTextAreaType = new JEditTextArea();
  JEditTextArea jTextAreaPlace = new JEditTextArea();
  JEditTextArea jTextAreaDeploy = new JEditTextArea();
  JEditTextArea jTextAreaDescription = new JEditTextArea();
  JTextArea screen = new JTextArea();
  JScrollPane output = new JScrollPane();

  // for Running externalprocess!
  InfoProcess auxProcess = null;
  RunProcess runCmd = null;
  RunProcess runBrowser = null;
  RunProcess runBrowserHelp = null;

  String classpath = "";

  //Files used in the application
  JFileChooser jFileChooser = new JFileChooser(sfFilesDir);
  /**
   *  Description of the Field
   */
  public String currFileName = null;
  // Full path with filename. null means new / untitled.
  String auxCurrFileName = null;
  String outputFileName = null;

  // for redirecting standard output.
  OutputStream dout = System.out;
  InputStream din = System.in;
  PrintStream out = System.out;
  JMenu jMenuEdit = new JMenu();
  JMenuItem jMenuItemCopy = new JMenuItem();
  JMenuItem jMenuItemCut = new JMenuItem();
  JMenuItem jMenuItemPaste = new JMenuItem();
  JMenuItem jMenuItemSelectAll = new JMenuItem();
  JMenuItem jMenuItemSelectNone = new JMenuItem();
  JMenuItem jMenuItemNew = new JMenuItem();
  JMenuItem jMenuItemUndo = new JMenuItem();
  JMenuItem jMenuItemRedo = new JMenuItem();
  SyntaxDocument syntaxDocumentSFFile;
  JMenuItem jMenuItemStop = new JMenuItem();
  JMenuItem jMenuItemInfo = new JMenuItem();
  JMenu jMenuTools = new JMenu();
  JMenuItem jMenuItemCleanOutput = new JMenuItem();
  JMenuItem jMenuItemReloadDescriptions = new JMenuItem();
  JMenuItem jMenuItemCleanMsg = new JMenuItem();
  JButton jButtonStop = new JButton();
  JButton jButtonRun = new JButton();
  JButton jButtonParse = new JButton();
  JButton jButtonRedo = new JButton();
  JButton jButtonUndo = new JButton();
  JButton jButtonOpen = new JButton();
  JButton jButtonSave = new JButton();
  JButton jButtonAbout = new JButton();
  JButton jButtonPreferences = new JButton();
  JButton jButtonBrowser = new JButton();
  JMenuItem jMenuItemBrowseSF = new JMenuItem();
  JMenuItem jMenuItemSaveOutput = new JMenuItem();
  JMenuItem jMenuItemInfoProp = new JMenuItem();
  JSplitPane jSplitPane2 = new JSplitPane();
  ProcessPanel processPanel = new ProcessPanel();
  JPanel jPanel1 = new JPanel();
  JTextField processNameTextField = new JTextField();
  JTextField hostNameTextField = new JTextField();
  private boolean isWindows = false;
  private boolean isWindows9x = false;
  private boolean isWindowsNT = false;
  // True means modified text.

  /**
   *  Stream to get the key input from the display's screen
   */
  private PipedInputStream pipeKeyIn;

  /**
   *  Stream to write keys pressed in the display's screen
   */
  private PipedOutputStream pipeKeyOut;

  /**
   *  Print Stream to write key characters to pipeKeyOut
   */
  private PrintStream printKey;
  // to manage Undo
  private UndoManager undo = new UndoManager();

  private int tabSize = 4;

  // Ini File
  private IniFile iniFile = null;
  JButton jButtonExit = new JButton();
  JButton jButtonSFDaemon = new JButton();
  JButton jButtonSFDaemonStop = new JButton();
  JMenuItem jMenuItem1 = new JMenuItem();
  JCheckBox securityCheckBox = new JCheckBox();
  JTextArea jTextAreaMsg = new JTextArea();
  //BrowseSFFilesPanel BrowseSFComponentPanel;
  BrowseSFFilesTreePanel BrowseSFComponentPanel;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  Document documentTextAreaMsg;
  Document documentScreen;
  GenParsePanel jPanelGenParse;
  JButton jButtonCopy = new JButton();
  JButton jButtonCut = new JButton();
  JButton jButtonPaste = new JButton();
  JMenuItem jMenuItemKuntstoff = new JMenuItem();

  /**
   *  Construct the frame
   */
  public MainFrame() {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      showLogoSequence();
      jbInit();
      customInit();
      updateCaption();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *  Construct the frame
   *
   *@param  fileName                Description of Parameter
   *@param  runAllProcessAfterBoot  Description of Parameter
   */
  public MainFrame(String fileName, boolean runAllProcessAfterBoot,
                   boolean eclipseMode) {

    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      showLogoSequence();
      jbInit();
      customInit();
      openFile(fileName);
      updateCaption();
      if (runAllProcessAfterBoot) {
        this.processPanel.runAll();
      }
      if (eclipseMode) {
        setEclipseMode();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setEclipseMode() {
    //int indexPanel = jTabbedPanelNorth.indexOfTab(panelNameSFFile);
    jTextAreaSFFile.setEditable(false);
    //jTabbedPanelNorth.remove(indexPanel);
    jButtonOpen.hide();
    jButtonSave.hide();
    jMenuItemOpen.hide();
    jMenuItemSaveAs.hide();
    jMenuItemSave.hide();
    jMenuItemNew.hide();

  }

  /**
   *  Sets the cursorOnWait attribute of the MainFrame class
   *
   *@param  comp  The new cursorOnWait value
   *@param  on    The new cursorOnWait value
   */
  public static void setCursorOnWait(Component comp, boolean on) {
    if (on) {
      comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    else {
      comp.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  /**
   *  Centres a component inside a container<p>
   *
   *  if the conatiner is null the component is centered in the screen
   *
   *@param  parent  Description of Parameter
   *@param  comp    Description of Parameter
   */
  public static void centerWindow(Container parent, Component comp) {
    int x;
    int y;
    Rectangle parentBounds;
    Dimension compSize = comp.getSize();
    // If Container is null or smaller than the component
    // then our bounding rectangle is the
    // whole screen
    if ( (parent == null) || (parent.getBounds().width < compSize.width) ||
        (parent.getBounds().height < compSize.height)) {
      parentBounds = new Rectangle(comp.getToolkit().getScreenSize());
      parentBounds.setLocation(0, 0);
    }
    // Else our bounding rectangle is the Container
    else {
      parentBounds = parent.getBounds();
    }
    // Place the component so its center is the same
    // as the center of the bounding rectangle
    x = parentBounds.x + ( (parentBounds.width / 2) - (compSize.width / 2));
    y = parentBounds.y + ( (parentBounds.height / 2) - (compSize.height / 2));
    comp.setLocation(x, y);
  }

  // This has to  be done properly !!!!!!!!!!!!!!!
  /**
   *  Description of the Method
   *
   *@param  message   Description of Parameter
   *@param  who       Description of Parameter
   *@param  severity  Description of Parameter
   *@return           Description of the Returned Value
   */
  public static int log(String message, String who, int severity) {
    /*
     *  Severity Levels: Taken from Bluestone CSF r6
     *  code.0=N;  name.0=NONE;     mask.0=0
     *  code.1=F;  name.1=FLOW;     mask.1=63
     *  code.2=D;  name.2=DEBUG;    mask.2=62
     *  code.4=I;  name.4=INFO;     mask.4=60
     *  code.8=W;  name.8=WARNING;  mask.8=56
     *  code.16=E; name.16=ERROR;   mask.16=48
     *  code.32=C; name.32=CRITICAL; mask.32=32
     */
    String[] severityMsg = {
        "NONE", "FLOW", "DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"};
    StringBuffer buffer = new StringBuffer();
    int length = buffer.length();
    buffer.append(" [");
    buffer.append(new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yy").format(new Date(
        System.currentTimeMillis())));
    buffer.append(']');
    buffer.append('[');
    //buffer.append( ( Thread.currentThread().getName() + "          " ).substring( 0, 10 ) );
    if (who == null) {
      who = "unknown";
    }
    buffer.append(who);
    buffer.append(']');
    buffer.append('[');
    buffer.append(severityMsg[severity]);
    buffer.append("]: ");
    buffer.append(message);
    //System.out.println(buffer.toString());
    msg.println(buffer.toString());
    // Last message in the status bar as well
    statusBar.setText(buffer.toString());
    statusBar.setToolTipText(buffer.toString());
    return (buffer.length() - length);
  }

  /**
   *  Description of the Method
   */
  public static void version() {
    System.out.println("SFGui " +
                       org.smartfrog.tools.gui.browser.MainFrame.version);
  }

  /**
   *  Sets the currFilePath attribute of the MainFrame object
   *
   *@param  path  The new currFilePath value
   */
  public void setCurrFilePath(String path) {
    if (path != null) {
      this.classpath = path;
      //System.out.println("Changed loded path:"+classpath);
    }
  }

  /**
   *  Sets the tabSize attribute of the MainFrame object
   *
   *@param  textArea  The new tabSize value
   *@param  tabSize   The new tabSize value
   */
  public void setTabSize(JEditTextArea textArea, int tabSize) {
    //int auxTabSize =((Integer) textArea .getDocument().getProperty(PlainDocument.tabSizeAttribute)).intValue();
    //auxTabSize = tabSize;
    textArea.getDocument().putProperty(PlainDocument.tabSizeAttribute,
                                       new Integer(tabSize));
  }

  /**
   *  Gets the currFilePath attribute of the MainFrame object
   *
   *@return    The currFilePath value
   */
  public String getCurrFilePath() {
    if (classpath != null) {
      return classpath;
    }
    else {
      return "";
    }
  }

  /**
   *  Description of the Method
   *
   *@param  message   Description of Parameter
   *@param  severity  Description of Parameter
   *@return           Description of the Returned Value
   */
  public int log(String message, int severity) {
    return log(message, null, severity);
  }

  /**
   *  File | Exit action performed
   *
   *@param  e  Description of Parameter
   */
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    this.Exit();
  }

  /**
   *  Description of the Method
   */
  private void Exit() {
    if (okToAbandon(jTextAreaSFFile.getText(), this.currFileName)) {
      this.saveIniFile();
      System.out.println("...SFGui finished.");
      //this.processPanel.mngProcess.killAll();
      this.stopSFDaemon();
      if (runCmd != null) {
        try {
          runCmd.kill();
        }
        catch (Throwable ex) {
        }
      }
      System.exit(0);
    }
  }

  /**
   *  Help | About action performed
   *
   *@param  e  Description of Parameter
   */
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    helpAbout();
  }

  /**
   *  Overridden so we can exit when window is closed
   *
   *@param  e  Description of Parameter
   */
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
      saveIniFile();
    }
  }

  /**
   *  Sets the preferences attribute of the MainFrame object
   */
  void setPreferences() {
    PreferencesDialog prefDig = new PreferencesDialog(this,
        "Preferences Dialog", true);
    centerWindow(this, prefDig);
    prefDig.show();
    saveIniFile();

  }

  /**
   *  Description of the Method
   */
  void cleanTextAreasParse() {
    jTextAreaRaw.setText("Raw");
    jTextAreaType.setText("Type Resolution");
    jTextAreaPlace.setText("Placement");
    jTextAreaDescription.setText("Description");
    jTextAreaDeploy.setText("Deploy");
  }

  /**
   *  Description of the Method
   */
  void newFile() {
    // Handle the File|New menu item.
    if (okToAbandon(jTextAreaSFFile.getText(), currFileName)) {
      // clears the text of the TextArea
      jTextAreaSFFile.setText("");
      undo.discardAllEdits();
      setTabSize(jTextAreaSFFile, tabSize);
      // clear the current filename and set the file as clean:
      currFileName = null;
      dirty = false;
      updateCaption();
      this.cleanTextAreasParse();
      this.jPanelGenParse.clean();
    }
  }

  /**
   *  Description of the Method
   */
  void fileOpen() {
    if (!okToAbandon(jTextAreaSFFile.getText(), currFileName)) {
      return;
    }
    // Use the OPEN version of the dialog, test return for Approve/Cancel
    if (JFileChooser.APPROVE_OPTION == jFileChooser.showOpenDialog(this)) {
      // Call openFile to attempt to load the text from file into TextArea
      openFile(jFileChooser.getSelectedFile().getPath());
    }
    this.repaint();
  }

  private void setTokenEditMarker(String fileName) {
    if (fileName.endsWith(".sf")) {
      this.setTokenEditTokenMarker(sfTokenMarker);
      this.jRadioButtonMenuItemSF.setSelected(true);
      this.languagejLabel.setText("  SF   ");
    }
    else if (fileName.endsWith(".sf2")) {
      this.setTokenEditTokenMarker(sf2TokenMarker);
      this.jRadioButtonMenuItemSF2.setSelected(true);
      this.languagejLabel.setText("  SF2   ");
    }
    else if (fileName.endsWith(".sfxml")) {
      this.setTokenEditTokenMarker(sfXMLTokenMarker);
      this.jRadioButtonMenuItemSFXML.setSelected(true);
      this.languagejLabel.setText("  SFXML   ");
    }
    else {
      //this.setTokenEditTokenMarker(sfTokenMarker);
      //this.jRadioButtonMenuItemSF.setSelected(true);
      //this.languagejLabel.setText ( "  SF   ");
    }
  }

  /**
   *  Description of the Method
   *
   *@param  fileName  Description of Parameter
   */
  void openFile(String fileName) {
    try {
      if ( (fileName == null) || fileName.equals("")) {
        return;
      }
      setTokenEditMarker(fileName);

      // Open a file of the given name.
      File file = new File(fileName);

      // Get the size of the opened file.
      int size = (int) file.length();

      // Set to zero a counter for counting the number of
      // characters that have been read from the file.
      int chars_read = 0;

      // Create an input reader based on the file, so we can read its data.
      // FileReader handles international character encoding conversions.
      FileReader in = new FileReader(file);

      // Create a character array of the size of the file,
      // to use as a data buffer, into which we will read
      // the text data.
      char[] data = new char[size];

      // Read all available characters into the buffer.
      while (in.ready()) {
        // Increment the count for each character read,
        // and accumulate them in the data buffer.
        chars_read += in.read(data, chars_read, size - chars_read);
      }
      in.close();

      // Create a temporary string containing the data,
      // and set the string into the JTextArea.
      jTextAreaSFFile.setText(new String(data, 0, chars_read));
      undo.discardAllEdits();
      // Cache the currently opened filename for use at save time...
      this.currFileName = fileName;
      // ...and mark the edit session as being clean
      this.dirty = false;

      // Display the name of the opened directory+file in the statusBar.
      statusBar.setText("Opened " + fileName);
      statusBar.setToolTipText("Opened " + fileName);
      this.insertPathname(fileName);
      this.cleanTextAreasParse();
      this.jPanelGenParse.clean();
      updateCaption();
      setTabSize(jTextAreaSFFile, tabSize);
    }
    catch (IOException e) {
      statusBar.setText("Error opening " + fileName);
      statusBar.setToolTipText("Error opening " + fileName);
    }
  }

  /**
   *  Description of the Method
   *
   *@param  text  Description of Parameter
   *@return       Description of the Returned Value
   */
  boolean saveFile(String text) {

    // Handle the case where we don't have a file name yet.
    if (currFileName == null) {
      return saveAsFile(text);
    }

    try {
      // Open a file of the current name.
      File file = new File(currFileName);

      // Create an output writer that will write to that file.
      // FileWriter handles international characters encoding conversions.
      FileWriter out = new FileWriter(file);
      //String text = jTextAreaSFFile.getText()
      out.write(text);
      out.close();
      this.insertPathname(currFileName);
      this.dirty = false;
      updateCaption();
      return true;
    }
    catch (IOException e) {
      statusBar.setText("Error saving " + currFileName);
      statusBar.setToolTipText("Error saving " + currFileName);
      JOptionPane.showMessageDialog(this, "Error saving " + currFileName,
                                    "Text Save", JOptionPane.ERROR_MESSAGE);
    }
    return false;
  }

  /**
   *  Description of the Method
   *
   *@param  text  Description of Parameter
   *@return       Description of the Returned Value
   */
  boolean saveAsFile(String text) {
    while (true) {
      // Use the SAVE version of the dialog, test return for Approve/Cancel
      if (JFileChooser.APPROVE_OPTION == jFileChooser.showSaveDialog(this)) {
        // Set the current file name to the user's selection,
        // then do a regular saveFile
        this.currFileName = jFileChooser.getSelectedFile().getPath();
        //repaints menu after item is selected
        this.repaint();
        //Test if the file previously existed...
        File file = new File(currFileName);
        if (file.exists()) {
          //-----------------------
          int value = JOptionPane.showConfirmDialog(this,
              "File already exist. Overwrite?",
              "Save As... - Overwrite?", JOptionPane.YES_NO_OPTION);
          switch (value) {
            case JOptionPane.YES_OPTION:

              // yes, please save changes
              this.setTokenEditMarker(currFileName);
              this.repaint();
              return saveFile(text);
            case JOptionPane.NO_OPTION:

              // no, abandon edits
              // i.e. return true without saving
              //return false;
              break;
            default:

              // cancel
              return false;
          }
          //-----------------------
          break;
        }
        else {
          return saveFile(text);
        }
      }
      else {
        this.repaint();
        return false;
      }
    }
    return false;
  }

  /**
   *  Description of the Method
   *
   *@param  text      Description of Parameter
   *@param  fileName  Description of Parameter
   *@return           Description of the Returned Value
   */
  public boolean okToAbandon(String text, String fileName) {
    if (!dirty) {
      return true;
    }

    int value = JOptionPane.showConfirmDialog(this, "Save changes?",
                                              "Text Edit",
                                              JOptionPane.YES_NO_CANCEL_OPTION);
    switch (value) {
      case JOptionPane.YES_OPTION:

        // yes, please save changes
        return saveFile(text);
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

  /**
   *  Description of the Method
   */
  void updateCaption() {
    String caption;

    if (currFileName == null) {
      // synthesize the "Untitled" name if no name yet.
      caption = "Untitled";
    }
    else {
      caption = currFileName;
    }

    // add a "*" in the caption if the file is dirty.
    if (dirty) {
      caption = "* " + caption;
    }
    caption = "SF Gui - " + caption;
    this.setTitle(caption);
  }

  /**
   *  Description of the Method
   */
  void helpAbout() {
    MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                    (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }

  /**
   *  Description of the Method
   *
   *@param  auxjTextArea  Description of Parameter
   */
  void scrollTextArea(JTextArea auxjTextArea) {
    try {
      Document d = auxjTextArea.getDocument();
      //auxjTextArea.select(d.getLength(), d.getLength());
      Rectangle r = auxjTextArea.modelToView(d.getLength());
      if (r != null) {
        auxjTextArea.scrollRectToVisible(r);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemAuto_actionPerformed(ActionEvent e) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      this.lookAndFeel = "auto";
      SwingUtilities.updateComponentTreeUI(this);
      saveIniFile();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemKuntstoff_actionPerformed(ActionEvent e) {
    try {
      UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.
                               KunststoffLookAndFeel());
      SwingUtilities.updateComponentTreeUI(this);
      this.lookAndFeel = "kunststoff";
      saveIniFile();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemMetal_actionPerformed(ActionEvent e) {
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      //Metal
      SwingUtilities.updateComponentTreeUI(this);
      this.lookAndFeel = "metal";
      saveIniFile();
      //this.repaint();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemWin_actionPerformed(ActionEvent e) {
    try {
      UIManager.setLookAndFeel(
          "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
      //Windows
      SwingUtilities.updateComponentTreeUI(this);
      this.lookAndFeel = "windows";
      saveIniFile();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemMotif_actionPerformed(ActionEvent e) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
      // Motif
      SwingUtilities.updateComponentTreeUI(this);
      this.lookAndFeel = "motif";
      saveIniFile();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemOpen_actionPerformed(ActionEvent e) {
    fileOpen();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemSaveAs_actionPerformed(ActionEvent e) {
    //Handle the File|Save As menu item.
    saveAsFile(jTextAreaSFFile.getText());
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemSave_actionPerformed(ActionEvent e) {
    //Handle the File|Save menu item.
    saveFile(jTextAreaSFFile.getText());
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonOpen_actionPerformed(ActionEvent e) {
    //Handle toolbar Open button
    fileOpen();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonSave_actionPerformed(ActionEvent e) {
    //Handle toolbar Save button
    saveFile(jTextAreaSFFile.getText());
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonAbout_actionPerformed(ActionEvent e) {
    //Handle toolbar About button
    //helpAbout();
    //info();
    try {
      java.net.URL url = new java.net.URL("file:////" +
                                          System.getProperty("user.dir") +
                                          "/help/help.htm");
      this.runBrowserHelp(url.toString());
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jFileChooser_actionPerformed(ActionEvent e) {}

  public String getActiveLanguage() {
    return this.languagejLabel.getText().toLowerCase().trim();
  }

  /**
   *  Description of the Method
   */
  void parse() {
    if (okToAbandon(jTextAreaSFFile.getText(), this.currFileName)) {
      cleanTextAreasParse();
      // Instead of passing the text content in the JTextArea, the text is saved and the real file is passed!
      if (currFileName != null) {
        InputStream is = null;
        try {
          // PreSet...
          jTextAreaRaw.setText("Error!. Not resolved.");
          jTextAreaType.setText("Error!. Not resolved.");
          jTextAreaPlace.setText("Error!. Not resolved.");
          jTextAreaDescription.setText(
              "Schema metadata not present for this SmartFrog description.");
          jTextAreaDeploy.setText("Error!. Not resolved.");

          jTextAreaDescription.setText(parsePhaseDescription(currFileName,
              this.getActiveLanguage()));

          is = new FileInputStream(currFileName);
          jTextAreaRaw.setText(this.parsePhase("raw", is,
                                               this.getActiveLanguage()));
          is.close();
          jTabbedPanelNorth.setSelectedIndex(jTabbedPanelNorth.indexOfTab(this.
              panelNameParse));

          // Parse Index.!!!
          jTabbedPaneParse.requestFocus();
          is = new FileInputStream(currFileName);
          jTextAreaDeploy.setText(this.parsePhase("all", is,
                                                  this.getActiveLanguage()));
          is.close();

          log("Standard SFParse Done.", "MenuParse", 3);
          //3 Info
        }
        catch (Throwable ex) {
          log(ex.getMessage(), "MenuParse", 5);
          // 5 Erro
          //System.err.println(ex.printStackTrace());
          ex.printStackTrace();
        }
      }
      else {
        log("No SFFile loaded", "ParseMnu", 4);
      }
    }
  }

  /**
   *  Description of the Method
   *
   *@param  phase  Description of Parameter
   *@param  is     Description of Parameter
   *@return        Description of the Returned Value
   */
  public String parsePhase(String phase, InputStream is, String language) {
    Phases top = null;
    Vector phases = null;
    try {
      top = new SFParser(language).sfParse(is);
      if (phase.equals("raw")) {
        return top.toString();
      }
    }
    catch (Throwable ex) {
      this.log(ex.getMessage(), "Parse", 5);
      ex.printStackTrace();
    }
    try {
      if (top != null) {
        phases = top.sfGetPhases();
        // Parse up to the phase selected in sfComboBox
        Vector auxphases = new Vector();

        if ( (phases != null) && (!phases.equals("all")) &&
            (!phases.equals("description"))) {
          Iterator iter = phases.iterator();
          String temp;
          while (iter.hasNext()) {
            temp = (String) (iter.next());
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
          return (top.sfAsComponentDescription().toString());
        }

        top = top.sfResolvePhases(auxphases);

        this.log("SFParse Done(" + (auxphases.lastElement()).toString() + ").",
                 "Parse", 3);
        return (top.toString());

      }
      else {
        this.log("SFParse Failed. No top.", "Parse", 5);
        return null;
        //3 Info
      }
    }
    catch (Throwable ex) {
      this.log(ex.getMessage(), "Parse", 5);
      //System.err.println(ex.printStackTrace());
      ex.printStackTrace();
    }
    return null;
  }

  /**
   *  To get textual description for a SF desc.
   *
   *@param  phase  Description of Parameter
   *@param  is     Description of Parameter
   *@return        Description of the Returned Value
   */
  public String parsePhaseDescription(String currFileName, String language) {
    if (currFileName != null) {
      InputStream is = null;
      try {
        is = new FileInputStream(currFileName);
        Phases top = new SFParser(language).sfParse(is);
        is.close();
        Vector phaseList = top.sfGetPhases();
        String phase;
        for (Enumeration e = phaseList.elements(); e.hasMoreElements(); ) {
          phase = (String) e.nextElement();
          try {
            if (! (phase.equals("predicate"))) {
              top = top.sfResolvePhase(phase);
              //System.out.println(phase + top.toString());
            }
          }
          catch (Exception ex) {
            //report.add("   "+ phase +" phase: "+ex.getMessage());
            throw ex;
          }
        }
        top.sfResolvePhase("description");
        //System.out.println("description"+top.toString());
        ComponentDescription cd = top.sfAsComponentDescription();
        return (cd.sfCompleteName().toString() + "\n" + cd.toString());
      }
      catch (Throwable ex) {
        log(ex.getMessage(), "parsePhaseDescription", 5);
        // 5 Erro
        //System.err.println(ex.printStackTrace());
        ex.printStackTrace();
      }
    }
    else {
      //log("No SFFile loaded", "parsePhaseDescription", 4);
    }
    return "hola";
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemParse_actionPerformed(ActionEvent e) {
    this.parse();
  }

  /**
   *  Description of the Method
   */
  void runBrowser() {
    //to do: Options for Linux and Windows... add properties to modify this.
    //cmdExeBrowser="netscape";
    //cmdBrowser="http:\\localhost:4242";
    try {
//         if (runBrowser != null) {
//            try {
//               runBrowser.kill();
//               runBrowser = null;
//            } catch (Throwable ex) {
//               // the user didn't stop the previous process ;-)
//            }
//         }
      String cmd = "";
      if (isWindows == false) {
        cmd = cmdExeBrowserLinux + " " + cmdBrowserURL;
        //linux
      }
      else {
        cmd = cmdExeBrowserWindows + " " + cmdBrowserURL;
        //windows
      }
//         runBrowser = new RunProcess(cmd);
//         runBrowser.start();
      this.runExe("BrowseChai", cmd, ".");
      log("Browser Chai Running: " + "" + cmd, "BROWSESFMnu", 3);
    }
    catch (Exception ex) {
      log(ex.getMessage(), "BROWSESFMnu", 5);
//         runBrowser.kill();
//         runBrowser = null;
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  url  Description of Parameter
   */
  void runBrowserHelp(String url) {
    //to do: Options for Linux and Windows... add properties to modify this.
    //cmdExeBrowser="netscape";
    //cmdBrowser="http:\\localhost:4242";
    try {
//         if (runBrowserHelp != null) {
//            try {
//               runBrowserHelp.kill();
//               runBrowserHelp = null;
//            } catch (Throwable ex) {
//               // the user didn't stop the previous process ;-)
//            }
//         }
      String cmd = "";
      if (isWindows == false) {
        cmd = cmdExeBrowserLinux + " " + url;
        //linux
      }
      else {
        cmd = cmdExeBrowserWindows + " " + url;
        //windows
      }
//         runBrowserHelp = new RunProcess(cmd);
//         runBrowserHelp.start();
      this.runExe("Help", cmd, ".");

      log("Help Running: " + "" + cmd, "HELPMnu", 3);
    }
    catch (Exception ex) {
      log(ex.getMessage(), "HELPMnu", 5);
//         runBrowserHelp.kill();
//         runBrowserHelp = null;
      ex.printStackTrace();
    }
  }

  /**
   *  Main processing commands method for the MainFrame object
   *
   */
  void runProcess() {
    // Checks if we are in the Browsing components panel.
    int indexPanel = this.jTabbedPanelNorth.getSelectedIndex();
    if (indexPanel == this.jTabbedPanelNorth.indexOfTab(panelNameBrowseComp)) {
      String sfFileURL = this.BrowseSFComponentPanel.getSelectedFile();
      if ( (sfFileURL != null) && (! (sfFileURL.equals("")))) {
        this.runSFProcess(sfFileURL);
      }
      return;
    }

    // no file loaded!
    if ( (currFileName != null) || (dirty)) {
      if (okToAbandon(jTextAreaSFFile.getText(), this.currFileName)) {
        File file = new File(currFileName);
        try {

          String osName = System.getProperty("os.name");
          String cmdGeneral = "";
          String batchDir = "bin";
          String dir = "";
          String cmdStart = cmdSFStart;
          String cmdStop = cmdSFStop;

          if (isWindows9x) {
            cmdGeneral = "command.exe /C";
            dir = ".\\" + batchDir + "\\";
            if (this.securityCheckBox.isSelected()) {
              dir = dir + cmdAddSecurity + "\\";
              cmdStop = dir + cmdAddSecurity + "\\";
            }

          } else if (isWindowsNT) {

            cmdGeneral = "cmd.exe /C";
            dir = ".\\" + batchDir + "\\";

            if (this.securityCheckBox.isSelected()) {
              dir = dir + cmdAddSecurity + "\\";
              cmdStop = dir + cmdAddSecurity + "\\";
            }

          }  else {
            cmdGeneral = "bash";
            dir = "./" + batchDir + "/";
            if (this.securityCheckBox.isSelected()) {
              dir = dir + cmdAddSecurity + "/";
              cmdStop = dir + cmdAddSecurity + "/";
            }

          }

          String switchOption = "-a";
          String sfFilePath = file.getPath();
          if (sfFilePath.endsWith(".sfcd")) {
            cmdStart = cmdGeneral + " " + dir + "smartfrog" + " -f " + "\"" +
                sfFilePath + "\"" + " -e";
          }
          else {
            if (isWindowsNT || isWindows9x) {
              String processName = this.processNameTextField.getText();
              if (this.autoNameCheckBox.isSelected()) {
                autoNameCounter++;
                processName = "ProcessName" + autoNameCounter;
                this.processNameTextField.setText(processName);
              }
              cmdStart = cmdGeneral + " " + dir + "smartfrog" + " -a "
                  + "\\\"\"" + processName + "\\\"\"" +
                  ":"
                  + cmdSFStart + ":"
                  + "\\\"\"" + sfFilePath + "\\\"\"" + ":"
                  + ":"
                  + this.hostNameTextField.getText() + ": -e";
              cmdStop = cmdGeneral + " " + dir + "smartfrog" + " -a "
                  + "\\\"\"" + processName + "\\\"\"" + ":"
                  + cmdSFStop + ":"
                  + "" + ":"
                  + ":"
                  + this.hostNameTextField.getText() + ": -e";
            }
            else {
              String processName = this.processNameTextField.getText();
              if (this.autoNameCheckBox.isSelected()) {
                autoNameCounter++;
                processName = "ProcessName" + autoNameCounter;
                this.processNameTextField.setText(processName);
              }
              //ProcessName:DEPLOY:org/smartfrog/examples/counter/examplekk.sf::127.0.0.1:
              cmdStart = cmdGeneral + " " + dir + "smartfrog" + " -a "
                  + "\"" + processName + "\"" +
                  ":"
                  + cmdSFStart + ":"
                  + "\"" + file.getPath() + "\"" + ":"
                  + ":"
                  + this.hostNameTextField.getText() + ": -e";

              cmdStop = cmdGeneral + " " + dir + "smartfrog" + " -a "
                  + "\"" + processName + "\"" + ":"
                  + cmdSFStop + ":"
                  + "" + ":"
                  + ":"
                  + this.hostNameTextField.getText() + ": -e";
              ////ProcessName:DEPLOY:org/smartfrog/examples/counter/examplekk.sf::127.0.0.1:
            }
          }

          jTabbedPanelNorth.setSelectedIndex(jTabbedPanelNorth.indexOfTab(
              "output"));
//                    // Select Output
//                    runCmd.start();
          auxProcess = new InfoProcess(this.processNameTextField.getText() + "(" +
                                       this.hostNameTextField.getText() + ")",
                                       cmdStart, " ", cmdStop, " ", ".", null);
          auxProcess.start();
          this.processPanel.mngProcess.addProcess(auxProcess, true);
          //log("Process Running: " + "\n   " + cmd, "RUNMnu", 3);
          log("Process Running: " + "" + auxProcess.getProcessName(),
              "runProcess", 3);
          this.processPanel.refresh();
          output.requestFocus();
          //runCmd = new RunProcess("java-cp smartfrog org.smartfrog.SFSystem -c "+file.toURL());
          //runCmd.run();
          //runCmd.start();
        }
        catch (Exception ex) {
          log(ex.getMessage(), "runProcess", 5);
          ex.printStackTrace();
        }
      }
    }
    else {
      log("No SFFile loaded", "runProcess", 4);
    }
  }

  /**
   *  Main processing commands method for the MainFrame object For use from
   *  BrowseSFFilesPanel TODO:Modify runProcess to use this one (passing it the
   *  path to the file to run...
   *
   *@param  sfFilePath  Description of Parameter
   */
  public void runSFProcess(String sfFilePath) {
    if (sfFilePath != null) {
      try {
        String osName = System.getProperty("os.name");
        //System.out.println("runSFProcess:osNAME:"+osName);
        String cmdGeneral = "";
        String batchDir = "bin";
        String dir = "";
        String cmdStart = cmdSFStart;
        String cmdStop = cmdSFStop;
        if (this.securityCheckBox.isSelected()) {
          cmdStart = cmdStart + this.cmdAddSecurity;
          cmdStop = cmdStop + this.cmdAddSecurity;
        }
        if (isWindowsNT) {
          cmdGeneral = "cmd.exe /C";
          dir = ".\\" + batchDir + "\\";
        }
        else if (isWindows9x) {
          cmdGeneral = "command.exe /C";
          dir = ".\\" + batchDir + "\\";
        }
        else {
          cmdGeneral = "bash";
          dir = "./" + batchDir + "/";
        }

        String switchOption = "-a";

        if (sfFilePath.endsWith(".sfcd")) {
          cmdStart = cmdGeneral + " " + dir + "smartfrog" + " -f "
              + "\"" + sfFilePath + "\"" + " -e";
        }
        else {
          String processName = this.processNameTextField.getText();
          if (this.autoNameCheckBox.isSelected()) {
            autoNameCounter++;
            processName = "ProcessName" + autoNameCounter;
            this.processNameTextField.setText(processName);
          }

          if (isWindows) {

            cmdStart = cmdGeneral + " " + dir + "smartfrog" + " -a "
                + "\\\"\"" + processName + "\\\"\"" +
                ":"
                + cmdSFStart + ":"
                + "\\\"\"" + sfFilePath + "\\\"\"" + ":"
                + ":"
                + this.hostNameTextField.getText() + ": -e";
            cmdStop = cmdGeneral + " " + dir + "smartfrog" + " -a "
                + "\\\"\"" + processName + "\\\"\"" + ":"
                + cmdSFStop + ":"
                + "" + ":"
                + ":"
                + this.hostNameTextField.getText() + ": -e";

          }
          else {
            cmdStart = cmdGeneral + " " + dir + "smartfrog" + " -a "
                + "\"" + processName + "\"" +
                ":"
                + cmdSFStart + ":"
                + "\"" + sfFilePath + "\"" + ":"
                + ":"
                + this.hostNameTextField.getText() + ": -e";

            cmdStop = cmdGeneral + " " + dir + "smartfrog" + " -a "
                + "\"" + processName + "\"" + ":"
                + cmdSFStop + ":"
                + "" + ":"
                + ":"
                + this.hostNameTextField.getText() + ": -e";
          }
        }

        jTabbedPanelNorth.setSelectedIndex(jTabbedPanelNorth.indexOfTab(
            "output"));
        //jTabbedPanelNorth.setSelectedIndex(2);
        auxProcess = new InfoProcess(this.processNameTextField.getText() + "(" +
                                     this.hostNameTextField.getText() + ")",
                                     cmdStart, " ", cmdStop, " ", ".", null);
        auxProcess.start();
        this.processPanel.mngProcess.addProcess(auxProcess, true);
        log("Process Running: " + "" + auxProcess.getProcessName(),
            "runSFProcess", 3);
        this.processPanel.refresh();
        output.requestFocus();
      }
      catch (Exception ex) {
        log(ex.getMessage(), "runProcess", 5);
        ex.printStackTrace();
      }
    }
  }

  /**
   *  Main processing commands method for the MainFrame object
   */
  void runSFDaemon() {
    String cmd = "";
    LoadSFFiles.refreshClassPath();
    String cmdStop = "java" + " "
        + "-cp \"" + classpath + "\" "
        + this.sfSystemClass + " "
        + "-a" + " "
        + this.sfDaemonProcessName + ":TERMINATE:::"
        + "" + this.hostNameTextField.getText() + ":" + " "
        + "-e";

    if (isWindows == false) {
      //linux
      cmd = "java" + " "
          + "-cp \"" + classpath + "\" "
          + sfDaemonDefIniFileProperty + "" + sfDaemonDefIniFile + "" + " "
          + sfDaemonDefSFFileProperty + "" + sfDaemonDefSFFile + "" + " "
          + cmdSFDaemon + "" + sfDaemonProcessName + " "
          + this.sfSystemClass
          + "";
    }
    else {
      // windows
      cmd = "java" + " "
          + "-cp \"" + classpath + "\" "
          + sfDaemonDefIniFileProperty + "\"" + sfDaemonDefIniFile + "\"" + " "
          + sfDaemonDefSFFileProperty + "\"" + sfDaemonDefSFFile + "\"" + " "
          + cmdSFDaemon + "" + sfDaemonProcessName + " "
          + this.sfSystemClass
          + "";
    }
    this.runExe("sfDaemon", cmd, cmdStop, ".");
  }

  /**
   *  Main processing commands method for the MainFrame object
   */
  void stopSFDaemonBtn() {
    String cmd = "";

    cmd = "java" + " "
        + "-cp \"" + classpath + "\" "
        + this.sfSystemClass + " "
        + "-a" + " "
        + this.sfDaemonProcessName + ":TERMINATE:::"
        + "" + this.hostNameTextField.getText() + ":" + " "
        + "-e";

    this.runExe("sfStopDaemon", cmd, ".");
  }

  /**
   *  Main processing commands method for the MainFrame object
   */
  void stopSFDaemon() {

    this.processPanel.mngProcess.stopProcess("sfDaemon");

//      String cmd = "";
//
//          cmd = "java" + " "
//                + "-cp \"" + classpath + "\" "
//                + this.sfSystemClass + " "
//                + "-a"+" "
//                +  this.sfDaemonProcessName + ":TERMINATE:::"
//                + "" + this.hostNameTextField.getText()+ ":"+ " "
//                + "-e";
//
//
//
//      this.runExe("sfStopDaemon", cmd, ".");
  }

  /**
   *  Main processing commands method for the MainFrame object
   *
   *@param  batchFile  Description of Parameter
   *@param  workDir    Description of Parameter
   */
  public void runBatchFile(String batchFile, String workDir) {
    try {
      String osName = System.getProperty("os.name");
      String cmdGeneral = "";
      String cmd = batchFile;
      if (isWindowsNT) {
        cmdGeneral = "cmd.exe /C";
      }
      else if (isWindows9x) {
        cmdGeneral = "command.exe /C";
      }
      else {
        // UNIX in general... (MAC not supported)
        cmdGeneral = "bash";
      }
      cmd = cmdGeneral + " " + cmd + " "
          + " ";
      auxProcess = new InfoProcess(batchFile, cmd, workDir);
      auxProcess.start();
      this.processPanel.mngProcess.addProcess(auxProcess, true);
      this.processPanel.refresh();
      log("Process Running: " + "\n  " + batchFile, "RUNBatchFile", 3);
      output.requestFocus();
    }
    catch (Exception ex) {
      log(ex.getMessage(), "RUNBatchFile(" + batchFile + ")", 5);
      ex.printStackTrace();
    }
  }

  /**
   *  Main processing commands method for the MainFrame object
   *
   *@param  name     Description of Parameter
   *@param  exeCmd   Description of Parameter
   *@param  workDir  Description of Parameter
   */
  public void runExe(String name, String exeCmd, String stopCmd, String workDir) {
    try {
      auxProcess = new InfoProcess(name, exeCmd, " ", stopCmd, " ", workDir, null);
      auxProcess.start();
      this.processPanel.mngProcess.addProcess(auxProcess, true);
      this.processPanel.refresh();
      log("Process Running: " + "" + name, "RUNExe", 3);
      output.requestFocus();
    }
    catch (Exception ex) {
      log(ex.getMessage(), "RUNExe(" + name + ")", 5);
      ex.printStackTrace();
    }
  }

  /**
   *  Main processing commands method for the MainFrame object
   *
   *@param  name     Description of Parameter
   *@param  exeCmd   Description of Parameter
   *@param  workDir  Description of Parameter
   */
  public void runExe(String name, String exeCmd, String workDir) {
    try {
      auxProcess = new InfoProcess(name, exeCmd, workDir);
      auxProcess.start();
      this.processPanel.mngProcess.addProcess(auxProcess, true);
      this.processPanel.refresh();
      log("Process Running: " + "" + name, "RUNExe", 3);
      output.requestFocus();
    }
    catch (Exception ex) {
      log(ex.getMessage(), "RUNExe(" + name + ")", 5);
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   */
  void runMngConsole() {
    try {
      //End option dialog
      int port = 3800;
      String hostName = "localhost";
      //New option dialgog
      hostName = modalOptionDialog("Management Console for ...", "HostName: ",
                                   hostName);
      try {
        port = Integer.parseInt( (modalOptionDialog(
            "Management Console for ...",
            "Port: ", "" + port)));
      }
      catch (Exception ex1) { //ignore
      }
      if (hostName == null) {
        return;
      }

      String fileSep = System.getProperty("file.separator");
      String cmd = "." + fileSep + "bin" + fileSep + "sfManagementConsole";
      cmd = cmd + " -h " + hostName;
      cmd = cmd + " -p " + port;
      this.runBatchFile(cmd, ".");
      log("Management Console running: " + "" + cmd, "RunMngConsole", 3);
    }
    catch (Exception ex) {
      log(ex.getMessage(), "RunMngConsole", 5);
      ex.printStackTrace();
    }
  }

  /**
   * Prepares option dialog box
   *
   *@param  title    title displayed on the dialog box
   *@param  message  message to be displayed
   *@param defaultValue default value
   *@return formatted string
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
    if (s == null) {
      return s; //User cancelled!
    }
    if ( (s != null) && (s.length() > 0)) {
      return s;
    }
    else {
      return defaultValue;
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemRun_actionPerformed(ActionEvent e) {
    //run();
    runProcess();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemCopy_actionPerformed(ActionEvent e) {
//      jTextAreaSFFile.copy();
//      jTextAreaSFFile.grabFocus();
    this.copyText();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemCut_actionPerformed(ActionEvent e) {
//      jTextAreaSFFile.cut();
//      jTextAreaSFFile.grabFocus();
    this.cutText();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemPaste_actionPerformed(ActionEvent e) {
//      jTextAreaSFFile.paste();
//      jTextAreaSFFile.grabFocus();
    this.pasteText();
  }

  /**
   *  Description of the Method
   */
  void copyText() {
    this.getActiveJTextArea().copy();
    this.getActiveJTextArea().grabFocus();
  }

  /**
   *  Description of the Method
   */
  void cutText() {
    int indexPanel = this.jTabbedPanelNorth.getSelectedIndex();
    if (indexPanel == this.jTabbedPanelNorth.indexOfTab(panelNameSFFile)) {
      jTextAreaSFFile.cut();
      jTextAreaSFFile.grabFocus();
    }
  }

  /**
   *  Description of the Method
   */
  void pasteText() {
    int indexPanel = this.jTabbedPanelNorth.getSelectedIndex();
    if (indexPanel == this.jTabbedPanelNorth.indexOfTab(panelNameSFFile)) {
      jTextAreaSFFile.paste();
      jTextAreaSFFile.grabFocus();
    }
  }

  /**
   *  Gets the activeJTextArea attribute of the MainFrame object
   *
   *@return    The activeJTextArea value
   */
  JEditTextArea getActiveJTextArea() {
    int indexPanel = this.jTabbedPanelNorth.getSelectedIndex();
    if (indexPanel == this.jTabbedPanelNorth.indexOfTab(panelNameSFFile)) {
      return jTextAreaSFFile;
    }
    else if (indexPanel ==
             this.jTabbedPanelNorth.indexOfTab(panelNameBrowseComp)) {
      return this.BrowseSFComponentPanel.getTextArea();
    }
    else if (indexPanel == this.jTabbedPanelNorth.indexOfTab(panelNameParse)) {
      indexPanel = this.jTabbedPaneParse.getSelectedIndex();
      if (indexPanel == this.jTabbedPaneParse.indexOfTab(panelNameRaw)) {
        return jTextAreaRaw;
      }
      else if (indexPanel == this.jTabbedPaneParse.indexOfTab(panelNameType)) {
        return jTextAreaType;
      }
      else if (indexPanel == this.jTabbedPaneParse.indexOfTab(panelNamePlace)) {
        return jTextAreaPlace;
      }
      else if (indexPanel ==
               this.jTabbedPaneParse.indexOfTab(panelNameDescription)) {
        return jTextAreaDescription;
      }
      else if (indexPanel == this.jTabbedPaneParse.indexOfTab(panelNameDeploy)) {
        return jTextAreaDeploy;
      }
      else if (indexPanel == this.jTabbedPaneParse.indexOfTab(panelNameGenParse)) {
        return jPanelGenParse.getTextArea();
      }
      else {
      }
    }
    else {
      return jTextAreaSFFile;
      // By default it is assumed that is the main editor area!
    }

    return jTextAreaSFFile;
    // By default it is assumed that is the main editor area!
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemNew_actionPerformed(ActionEvent e) {
    this.newFile();

  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void syntaxDocumentSFFile_changedUpdate(DocumentEvent e) {
    if (!dirty) {
      dirty = true;
      updateCaption();
    }
    // Change in Doc happened!
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void syntaxDocumentSFFile_insertUpdate(DocumentEvent e) {
    if (!dirty) {
      dirty = true;
      updateCaption();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void syntaxDocumentSFFile_removeUpdate(DocumentEvent e) {
    if (!dirty) {
      dirty = true;
      updateCaption();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void syntaxDocumentSFFile_undoableEditHappened(UndoableEditEvent e) {
    // Add Undo.
    undo.addEdit(e.getEdit());
  }

  /**
   *  Description of the Method
   */
  void redo() {
    this.setCursorOnWait(jTextAreaSFFile, true);
    if (undo.canRedo()) {
      undo.redo();
    }
    jTextAreaSFFile.grabFocus();
    this.setCursorOnWait(jTextAreaSFFile, false);

  }

  /**
   *  Description of the Method
   */
  void undo() {
    this.setCursorOnWait(jTextAreaSFFile, true);
    if (undo.canUndo()) {
      undo.undo();
    }
    jTextAreaSFFile.grabFocus();
    this.setCursorOnWait(jTextAreaSFFile, false);

  }

  /**
   *  Description of the Method
   */
  void selectAll() {
    jTextAreaSFFile.selectAll();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemRedo_actionPerformed(ActionEvent e) {
    this.redo();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemUndo_actionPerformed(ActionEvent e) {
    this.undo();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemSelectAll_actionPerformed(ActionEvent e) {
    this.selectAll();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemStop_actionPerformed(ActionEvent e) {
    //Stop Process !!!
    StopProcess();
  }

  /**
   *  Description of the Method
   */
  void StopProcess() {
    try {
      this.processPanel.mngProcess.stopProcess(this.processNameTextField.
                                               getText() + "(" +
                                               this.hostNameTextField.getText() +
                                               ")");
      if (runCmd != null) {
        runCmd.kill();
        runCmd = null;
        screen.requestFocus();
      }
    }
    catch (Exception ex) {
      log(ex.getMessage(), "STOPMnu", 5);
      ex.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   */
  void info() {
//    log("\nRunning: "
//           +"\n  -SF v"+org.smartfrog.Version.versionString
//           +"\n  - Java v " + System.getProperty("java.version")
//           +"\n  -Classpath: "+System.getProperty("java.class.path")
//           +"\n  -Loaded classpath: "+classpath,"INFOMnu",3);
    jTextAreaMsg.append("\nRunning: ");
    jTextAreaMsg.append("\n  -SF v" + org.smartfrog.Version.versionString());
    jTextAreaMsg.append("\n  -Java v " + System.getProperty("java.version"));
    jTextAreaMsg.append("\n  -Classpath: " +
                        System.getProperty("java.class.path"));
    jTextAreaMsg.append("\n  -Loaded classpath: " + classpath);
    jTextAreaMsg.append("\n  -OS: " + System.getProperty("os.name") + "\n");
    // to standard output a more complete info ;-)
    System.out.println("");
    System.out.println("*******************************************************");
    System.out.println("* SFGui " + version );
    System.out.println("* " + org.smartfrog.Version.versionString() );
    System.out.println("* (C)Copyright Hewlett-Packard Development Company, LP ");
    System.out.println("*******************************************************");
    System.out.println("Java Version:   " + System.getProperty("java.version"));
    System.out.println("Java Home:      " + System.getProperty("java.home"));
    System.out.println("Java Ext Dir:   " + System.getProperty("java.ext.dirs"));
    System.out.println("OS Name:        " + System.getProperty("os.name"));
    System.out.println("OS Version:     " + System.getProperty("os.version"));
    System.out.println("Is Windows:     " + isWindows);            
    System.out.println("User Name:      " + System.getProperty("user.name"));
    System.out.println("User Home:      " + System.getProperty("user.home"));
    System.out.println("User Work Dir:  " + System.getProperty("user.dir"));
    try {
      java.net.InetAddress localhost = java.net.InetAddress.getLocalHost();
      out.println("LocalHost Name: " + localhost.getHostName());
      out.println("LocalHost IP:   " + localhost.getHostAddress());
      out.println("isMulticast?    " + localhost.isMulticastAddress());
    }
    catch (Exception ex) {
      System.out.println("Exception Info:" + ex.toString());
    }
    System.out.println("* Java ClassPath: " + "\n" +
          (System.getProperty("java.class.path")).replace(
            System.getProperty("path.separator").charAt(0), '\n'));
    out.println("*****************************************************");
    System.out.println("");

  }

  /**
   *  Description of the Method
   */
  void infoProperties() {
//    log("\nRunning: "
//           +"\n  -SF v"+org.smartfrog.Version.versionString
//           +"\n  - Java v " + System.getProperty("java.version")
//           +"\n  -Classpath: "+System.getProperty("java.class.path")
//           +"\n  -Loaded classpath: "+classpath,"INFOMnu",3);
    System.out.println("\n **************************************************");
    System.out.println("   Info Properties: ");
    // to standard output a more complete info ;-)
    System.out.println("");
    java.util.Properties p = System.getProperties();
    java.util.Enumeration keys = p.keys();
    Object key = "";
    while (keys.hasMoreElements()) {
      key = keys.nextElement();
      System.out.println("* " + key + ": " + System.getProperty( (String) key));
    }
    System.out.println("");
    System.out.println(" ************************************************** \n");

  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemInfo_actionPerformed(ActionEvent e) {
    info();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jTextAreaMsg_caretUpdate(CaretEvent e) {}

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void screen_caretUpdate(CaretEvent e) {}

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jTextAreaMsg_caretPositionChanged(InputMethodEvent e) {
    // This is not the best way of calling the scroll!
    scrollTextArea(jTextAreaMsg);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void screen_caretPositionChanged(InputMethodEvent e) {
    // This is not the best way of calling the scroll!
    scrollTextArea(screen);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jTextAreaMsg_focusGained(FocusEvent e) {
    // This is not the best way of calling the scroll!
    scrollTextArea(jTextAreaMsg);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void screen_focusGained(FocusEvent e) {
    // This is not the best way of calling the scroll!
    scrollTextArea(screen);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void output_focusGained(FocusEvent e) {
    // This is not the best way of calling the scroll!
    scrollTextArea(screen);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemCleanOutput_actionPerformed(ActionEvent e) {
    int value = JOptionPane.showConfirmDialog(this, "Save Output content? ",
                                              "Clean Output",
                                              JOptionPane.YES_NO_CANCEL_OPTION);
    switch (value) {
      case JOptionPane.YES_OPTION:

        // yes, please save changes
        saveAsFileOutput(this.screen.getText());
      case JOptionPane.NO_OPTION:
        screen.setText("");
        return;
      case JOptionPane.CANCEL_OPTION:
      default:

        // cancel
        return;
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemReloadDescriptions_actionPerformed(ActionEvent e) {
     BrowseSFFilesTreePanel old = BrowseSFComponentPanel;
     LoadSFFiles.refreshClassPath();
     BrowseSFComponentPanel = new BrowseSFFilesTreePanel(this);
     jTabbedPanelNorth.remove(old);
     jTabbedPanelNorth.add(BrowseSFComponentPanel, panelNameBrowseComp);
     old=null;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemCleanMsg_actionPerformed(ActionEvent e) {
    jTextAreaMsg.setText("");
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonPreferences_actionPerformed(ActionEvent e) {
    setPreferences();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonUndo_actionPerformed(ActionEvent e) {
    undo();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonRedo_actionPerformed(ActionEvent e) {
    redo();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonParse_actionPerformed(ActionEvent e) {
    parse();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonRun_actionPerformed(ActionEvent e) {
    //run();
    runProcess();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonStop_actionPerformed(ActionEvent e) {
    StopProcess();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonBrowser_actionPerformed(ActionEvent e) {
    runBrowser();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemBrowseSF_actionPerformed(ActionEvent e) {
    runBrowser();
  }

  /**
   *  Description of the Method
   *
   *@param  text  Description of Parameter
   *@return       Description of the Returned Value
   */
  boolean saveFileOutput(String text) {
    // Handle the case where we don't have a file name yet.
    if (outputFileName == null) {
      return saveAsFileOutput(text);
    }

    try {
      // Open a file of the current name.
      File file = new File(outputFileName);

      // Create an output writer that will write to that file.
      // FileWriter handles international characters encoding conversions.
      FileWriter out = new FileWriter(file);
      //String text = jTextAreaSFFile.getText()
      out.write(text);
      out.close();
      this.dirty = false;
      updateCaption();
      this.setTokenEditMarker(currFileName);
      return true;
    }
    catch (IOException e) {
      statusBar.setText("Error saving " + outputFileName);
      statusBar.setToolTipText("Error saving " + outputFileName);
      JOptionPane.showMessageDialog(this, "Error saving " + outputFileName,
                                    "Text Save", JOptionPane.ERROR_MESSAGE);
    }
    return false;
  }

  /**
   *  Description of the Method
   *
   *@param  text  Description of Parameter
   *@return       Description of the Returned Value
   */
  boolean saveAsFileOutput(String text) {
    // Use the SAVE version of the dialog, test return for Approve/Cancel
    if (JFileChooser.APPROVE_OPTION == jFileChooser.showSaveDialog(this)) {
      // Set the current file name to the user's selection,
      // then do a regular saveFile
      this.outputFileName = jFileChooser.getSelectedFile().getPath();
      //repaints menu after item is selected
      this.setTokenEditMarker(currFileName);
      this.repaint();
      return saveFileOutput(text);
    }
    else {
      this.repaint();
      return false;
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemSaveOutput_actionPerformed(ActionEvent e) {
    this.saveAsFileOutput(this.screen.getText());
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItemInfoProp_actionPerformed(ActionEvent e) {
    infoProperties();
  }

  /**
   *  Description of the Method
   */
  private void customOS() {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Windows")) {
      isWindows = true;
      if (osName.toLowerCase().equals("Windows 95") || osName.equals("Windows 98")) {
         isWindows9x =true;
      }
      if (osName.equals("Windows 2000") || osName.startsWith("Windows NT") || osName.equals("Windows XP")|| osName.equals("Windows Vista")){
         isWindowsNT = true;                            
      }
    }
    else {
      isWindows = false;
    }
    //System.out.println("OS: "+OS);

    if (isWindows == false) {
      //iniFileName = "SFGui.bat";
      //cheating: the bat/shell file, used for config as well;-)
      //iniSecRunProcess = "RunProcess";
      //iniKeyClasspath = "CLASSPATH";
    }
  }

  private void setTokenEditTokenMarker(org.gjt.sp.jedit.syntax.TokenMarker
                                       tokenMarker) {

    jTextAreaSFFile.setTokenMarker(tokenMarker);

    jTextAreaRaw.setTokenMarker(tokenMarker);
    jTextAreaType.setTokenMarker(tokenMarker);
    jTextAreaPlace.setTokenMarker(tokenMarker);
    jTextAreaDescription.setTokenMarker(tokenMarker);
    jTextAreaDeploy.setTokenMarker(tokenMarker);
  }

  //**Custom Component initialization*/
  /**
   *  Description of the Method
   *
   *@exception  Exception  Description of Exception
   */
  private void customInit() throws Exception {
    imageApp = Toolkit.getDefaultToolkit().getImage(org.smartfrog.tools.gui.
        browser.MainFrame.class.getResource("frogred.gif"));
    this.setIconImage( (Image) imageApp);
    // Set diferences for dif OSs
    customOS();

    undo.setLimit(300);
    dout = new PrintStream(new TextAreaOutputStream(screen));
    msg = new PrintStream(new TextAreaOutputStream(jTextAreaMsg));
    // get input from user
    try {
      pipeKeyIn = new PipedInputStream();
      pipeKeyOut = new PipedOutputStream(pipeKeyIn);
      printKey = new PrintStream(pipeKeyOut);
    }
    catch (Exception e) {
      System.err.println("Error connecting pipes:\n" + e);
      e.printStackTrace();
    }
    screen.setEditable(true);
    din = pipeKeyIn;
    // end input from user

    out = new PrintStream(new TextAreaOutputStream(screen));
    // Redirecting standard output:
    System.setOut(out);
    //Need to be tested! (BufferedOutputStream)
    System.setIn(din);
    System.setErr(out);

    // Customizing JEditTextAreas: Java Color Syntax. Improve this.

    jTextAreaRaw.setFont(new java.awt.Font("DialogInput", 0, 12));
    jTextAreaType.setFont(new java.awt.Font("DialogInput", 0, 12));
    jTextAreaPlace.setFont(new java.awt.Font("DialogInput", 0, 12));
    jTextAreaDescription.setFont(new java.awt.Font("DialogInput", 0, 12));
    jTextAreaDeploy.setFont(new java.awt.Font("DialogInput", 0, 12));

    setTokenEditTokenMarker(sfTokenMarker);

    setTabSize(jTextAreaSFFile, tabSize);
    setTabSize(jTextAreaRaw, tabSize);
    setTabSize(jTextAreaType, tabSize);
    setTabSize(jTextAreaPlace, tabSize);
    setTabSize(jTextAreaDescription, tabSize);
    setTabSize(jTextAreaDeploy, tabSize);

    //jTextAreaSFFile.getGutter().setAntiAliasingEnabled(true);
    jTextAreaSFFile.getGutter().setHighlightInterval(5);
    jTextAreaSFFile.getGutter().setHighlightedForeground(Color.red);
    // ---

    loadIniFile();
    setLookAndFeel(lookAndFeel);
    //Hide msg panel
    jSplit1Position = this.jSplitPane1.getDividerLocation();
    this.jTabbedPaneSouth.setVisible(false);
    jCheckBoxMenuItemShowMsgP.setSelected(false);

    jFileChooser.setCurrentDirectory(new File(sfFilesDir));
    // Setting the classpath used for the deamon, the same used by the console
    classpath = System.getProperty("java.class.path");
    //System.out.println("Classpath: "+ classpath);
    timerScroll.start();
    statusBar.setText("      SFGui " + this.version);
    statusBar.setToolTipText("      SFGui " + this.version);

  }

  /**
   *  Sets the lookAndFeel attribute of the MainFrame object
   *
   *@param  lookAndFeel  The new lookAndFeel value
   */
  void setLookAndFeel(String lookAndFeel) {
    try {
      if (lookAndFeel.equals("kunststoff")) {
        UIManager.setLookAndFeel(new com.incors.plaf.kunststoff.
                                 KunststoffLookAndFeel());
      }
      else if (lookAndFeel.equals("auto")) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        //Automatic
      }
      else if (lookAndFeel.equals("windows")) {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        //Windows
      }
      else if (lookAndFeel.equals("motif")) {
        UIManager.setLookAndFeel(
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        // Motif
      }
      else if (lookAndFeel.equals("metal")) {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        //Metal
      }
      SwingUtilities.updateComponentTreeUI(this);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   *  Load the diferent paramenters from the ini file specified in variable
   *  iniFileName
   */
  private void loadIniFile() {

    // Read Config File
    //System.out.println("Loading Ini File!");
    iniFile = new IniFile(iniFileName, false);
    //iniFile.setDebug(false);
    // Not cached
    sfFilesDir = iniFile.getSetting(iniSecRunProcess, iniKeySFFilesDir,
                                    sfFilesDir);
    // Default SFSystem class
    sfSystemClass = iniFile.getSetting(iniSecRunProcess, iniKeySFSystemClass,
                                       sfSystemClass);
    //Default config for Browsers
    cmdExeBrowserLinux = iniFile.getSetting(iniSecRunProcess,
                                            iniKeyCmdExeBrowserLinux,
                                            cmdExeBrowserLinux);
    cmdExeBrowserWindows = iniFile.getSetting(iniSecRunProcess,
                                              iniKeyCmdExeBrowserWindows,
                                              cmdExeBrowserWindows);
    cmdBrowserURL = iniFile.getSetting(iniSecRunProcess, iniKeyCmdBrowserURL,
                                       cmdBrowserURL);
    // Default SF Commands ...
    cmdSFDaemon = iniFile.getSetting(iniSecRunProcess, iniKeyCmdSFDaemon,
                                     cmdSFDaemon);
    sfDaemonProcessName = iniFile.getSetting(iniSecRunProcess,
                                             iniKeySFDaemonProcessName,
                                             sfDaemonProcessName);
    sfDaemonDefIniFile = iniFile.getSetting(iniSecRunProcess,
                                            iniKeySFDaemonDefIniFile,
                                            sfDaemonDefIniFile);
    sfDaemonDefSFFile = iniFile.getSetting(iniSecRunProcess,
                                           iniKeySFDaemonDefSFFile,
                                           sfDaemonDefSFFile);
    cmdSFStart = iniFile.getSetting(iniSecRunProcess, iniKeyCmdSFStart,
                                    cmdSFStart);
    cmdSFStop = iniFile.getSetting(iniSecRunProcess, iniKeyCmdSFStop, cmdSFStop);
    // Subfix
    cmdAddSecurity = iniFile.getSetting(iniSecRunProcess, iniKeyCmdAddSecurity,
                                        cmdAddSecurity);
    lookAndFeel = iniFile.getSetting(iniSecRunProcess, iniKeyLookAndFeel,
                                     lookAndFeel);
    this.loadHistoryIniFile();
  }

  /**
   *  Description of the Method
   */
  private void saveIniFile() {
    //iniFile.setDebug(true);
    // Only neccesary if we get a window to modigy the parameters ;-)
    // example: iniFile.setSetting(iniSecRunProcess, iniKeyClasspath, classpath);
    iniFile.setSetting(iniSecRunProcess, iniKeyLookAndFeel, lookAndFeel);
    // update classpath used!
    iniFile.flush();
    saveHistoryIniFile();
//      this.processPanel.saveAll();
  }

  /**
   *  Component initialization
   *
   *@exception  Exception  Description of Exception
   */
  private void jbInit() throws Exception {
    //loadIcons();
    imageOpen = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Open.gif"));
    imageSave = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Save.gif"));
    imageAbout = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                               getResource("About.gif"));
    imageCopy = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Copy.gif"));
    imageCut = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                             getResource("Cut.gif"));
    imagePaste = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                               getResource("Paste.gif"));
    imageUndo = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Undo.gif"));
    imageRedo = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Redo.gif"));
    imageParse = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                               getResource("CheckAll.gif"));
    imageRun = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                             getResource("ExecuteProject.gif"));
    imageStop = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Stop.gif"));
    imageSFDaemon = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                                  getResource("sfDaemon.gif"));
    imageSFStopDaemon = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                                      getResource("sfStopDaemon.gif"));
    //        imageKill = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("Hide.gif"));
    //        imageAdd = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("UpdateRow.gif"));
    //        imageDelete = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("DeleteRow.gif"));
    //        imageRefresh = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.getResource("NewSheet.gif"));
    imagePreferences = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                                     getResource("Options.gif"));
    imageBrowseSF = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                                  getResource("World.gif"));
    imageExit = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                              getResource("Door.gif"));
    imageMngConsole = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                                    getResource("frogbluesmall.gif"));
    //setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("[Your Icon]")));

    // needs a reference to this frame!
    jPanelGenParse = new GenParsePanel(this);
    BrowseSFComponentPanel = new BrowseSFFilesTreePanel(this);

    contentPane = (JPanel)this.getContentPane();
    syntaxDocumentSFFile = jTextAreaSFFile.getDocument();
    documentTextAreaMsg = jTextAreaMsg.getDocument();
    documentScreen = screen.getDocument();
    contentPane.setLayout(borderLayout1);
    //this.setFont(new java.awt.Font("DialogInput", 0, 12));
    this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    this.setSize(new Dimension(950, 728));
    this.setTitle("SFGui");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(115,
        java.awt.event.KeyEvent.ALT_MASK, false));
    jMenuFileExit.addActionListener(new MainFrame_jMenuFileExit_ActionAdapter(this));
    jMenuFileExit.addActionListener(new MainFrame_jMenuFileExit_ActionAdapter(this));
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setBorder(null);
    jSplitPane1.setPreferredSize(new Dimension(50, 61));
    jTabbedPaneSouth.setTabPlacement(JTabbedPane.LEFT);
    //screen.addKeyListener(this);
    jMenuItemWin.setText("Windows");
    jMenuItemWin.addActionListener(new MainFrame_jMenuItemWin_actionAdapter(this));
    jMenuItemMetal.setText("Metal");
    jMenuItemMetal.addActionListener(new MainFrame_jMenuItemMetal_actionAdapter(this));
    jMenuItemAuto.setText("Auto");
    jMenuItemAuto.addActionListener(new MainFrame_jMenuItemAuto_actionAdapter(this));
    jMenuLF.setText("L&F");
    jMenuItemMotif.setText("Motif");
    jMenuItemMotif.addActionListener(new MainFrame_jMenuItemMotif_actionAdapter(this));
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new MainFrame_jMenuHelpAbout_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuItemOpen.setText("Open");
    jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(79,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemOpen.addActionListener(new MainFrame_jMenuItemOpen_actionAdapter(this));
    jMenuSF.setText("SF");
    jMenuItemRun.setText("Run");
    jMenuItemRun.setAccelerator(javax.swing.KeyStroke.getKeyStroke(82,
        java.awt.event.KeyEvent.ALT_MASK, false));
    jMenuItemRun.addActionListener(new MainFrame_jMenuItemRun_actionAdapter(this));
    jMenuItemParse.setText("Parse");
    jMenuItemParse.setAccelerator(javax.swing.KeyStroke.getKeyStroke(80,
        java.awt.event.KeyEvent.ALT_MASK, false));
    jMenuItemParse.addActionListener(new MainFrame_jMenuItemParse_actionAdapter(this));
    jMenuItemSave.setText("Save");
    jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(83,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemSave.addActionListener(new MainFrame_jMenuItemSave_actionAdapter(this));
    jMenuItemSaveAs.setToolTipText("");
    jMenuItemSaveAs.setText("Save As");
    jMenuItemSaveAs.addActionListener(new
                                      MainFrame_jMenuItemSaveAs_actionAdapter(this));
    jFileChooser.addActionListener(new MainFrame_jFileChooser_actionAdapter(this));
    jTextAreaSFFile.setToolTipText("");
    jTextAreaSFFile.setFont(new java.awt.Font("DialogInput", 0, 12));
    jTextAreaSFFile.setTokenMarker(sfTokenMarker);
    // SmartFrog Token Marker ;-)
    jTabbedPaneParse.setTabPlacement(JTabbedPane.LEFT);
    jTextAreaRaw.setText("Raw");
    jTextAreaRaw.setEditable(false);
    jTextAreaType.setText("Type Resolution");
    jTextAreaType.setEditable(false);
    jTextAreaPlace.setText("Placement");
    jTextAreaPlace.setEditable(false);
    jTextAreaDescription.setText("Description");
    jTextAreaDescription.setEditable(false);
    jTextAreaDeploy.setText("Deploy");
    jTextAreaDeploy.setEditable(false);
    screen.setForeground(SystemColor.text);
    screen.setBackground(new Color(80, 60, 120));
    screen.setFont(new java.awt.Font("DialogInput", 0, 12));
    screen.addFocusListener(new MainFrame_screen_focusAdapter(this));
    screen.addCaretListener(new MainFrame_screen_caretAdapter(this));
    screen.addInputMethodListener(new MainFrame_screen_inputMethodAdapter(this));
    output.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    output.addFocusListener(new MainFrame_output_focusAdapter(this));
    jMenuEdit.setText("Edit");
    jMenuItemCopy.setText("Copy");
    jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(67,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemCopy.addActionListener(new MainFrame_jMenuItemCopy_actionAdapter(this));
    jMenuItemCut.setText("Cut");
    jMenuItemCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(88,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemCut.addActionListener(new MainFrame_jMenuItemCut_actionAdapter(this));
    jMenuItemPaste.setText("Paste");
    jMenuItemPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(86,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemPaste.addActionListener(new MainFrame_jMenuItemPaste_actionAdapter(this));
    jMenuItemNew.setText("New");
    jMenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(78,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemNew.addActionListener(new MainFrame_jMenuItemNew_actionAdapter(this));
    jMenuItemUndo.setText("Undo");
    jMenuItemUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(90,
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemUndo.addActionListener(new MainFrame_jMenuItemUndo_actionAdapter(this));
    jMenuItemRedo.setText("Redo");
    jMenuItemRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(90,
        java.awt.event.KeyEvent.CTRL_MASK | java.awt.event.KeyEvent.SHIFT_MASK, false));
    jMenuItemRedo.addActionListener(new MainFrame_jMenuItemRedo_actionAdapter(this));
    syntaxDocumentSFFile.addDocumentListener(new
                                             MainFrame_syntaxDocumentSFFile_documentAdapter(this));
    syntaxDocumentSFFile.addUndoableEditListener(new
                                                 MainFrame_syntaxDocumentSFFile_undoableEditAdapter(this));
    jMenuItemSelectAll.setText("Select all");
    jMenuItemSelectAll.addActionListener(new
                                         MainFrame_jMenuItemSelectAll_actionAdapter(this));
    jTextAreaMsg.setEditable(false);
    jTextAreaMsg.addCaretListener(new MainFrame_jTextAreaMsg_caretAdapter(this));
    jTextAreaMsg.addInputMethodListener(new
                                        MainFrame_jTextAreaMsg_inputMethodAdapter(this));
    jMenuItemStop.setText("Stop");
    jMenuItemStop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(83,
        java.awt.event.KeyEvent.ALT_MASK, false));
    jMenuItemStop.addActionListener(new MainFrame_jMenuItemStop_actionAdapter(this));
    jMenuItemInfo.setText("Info");
    jMenuItemInfo.addActionListener(new MainFrame_jMenuItemInfo_actionAdapter(this));
    jMenuTools.setText("Tools");
    jMenuItemCleanOutput.setText("Clean Output");
    jMenuItemCleanOutput.addActionListener(new MainFrame_jMenuItemCleanOutput_actionAdapter(this));
    jMenuItemCleanOutput.setAccelerator(javax.swing.KeyStroke.getKeyStroke(79, java.awt.event.KeyEvent.ALT_MASK, false));
    jMenuItemReloadDescriptions.setText("Reload Descriptions");
    jMenuItemReloadDescriptions.addActionListener(new MainFrame_jMenuItemReloadDescription_actionAdapter(this));

    jMenuItemCleanMsg.setText("Clean Msg");
    jMenuItemCleanMsg.addActionListener(new
                                        MainFrame_jMenuItemCleanMsg_actionAdapter(this));

    jButtonRun.setBorder(null);
    jButtonRun.setToolTipText("Run");
    jButtonRun.setIcon(imageRun);
    jButtonRun.addActionListener(new MainFrame_jButtonRun_actionAdapter(this));
    jButtonUndo.setBorder(null);
    jButtonUndo.setToolTipText("Undo");
    jButtonUndo.setIcon(imageUndo);
    jButtonUndo.addActionListener(new MainFrame_jButtonUndo_actionAdapter(this));
    jButtonOpen.setIcon(imageOpen);
    jButtonOpen.addActionListener(new MainFrame_jButtonOpen_actionAdapter(this));
    jButtonOpen.setBorder(null);
    jButtonOpen.setToolTipText("Open File");
    jButtonSave.setIcon(imageSave);
    jButtonSave.addActionListener(new MainFrame_jButtonSave_actionAdapter(this));
    jButtonSave.setBorder(null);
    jButtonSave.setToolTipText("Save File");
    jButtonAbout.setIcon(imageAbout);
    jButtonAbout.addActionListener(new MainFrame_jButtonAbout_actionAdapter(this));
    jButtonAbout.setBorder(null);
    jButtonAbout.setToolTipText("Help");
    jButtonRedo.setBorder(null);
    jButtonRedo.setToolTipText("Redo");
    jButtonRedo.setIcon(imageRedo);
    jButtonRedo.addActionListener(new MainFrame_jButtonRedo_actionAdapter(this));
    jButtonCopy.setBorder(null);
    jButtonCopy.setToolTipText("Copy");
    jButtonCopy.setIcon(imageCopy);
    jButtonCopy.addActionListener(new MainFrame_jButtonCopy_actionAdapter(this));
    jButtonCut.setBorder(null);
    jButtonCut.setToolTipText("Cut");
    jButtonCut.setIcon(imageCut);
    jButtonCut.addActionListener(new MainFrame_jButtonCut_actionAdapter(this));
    jButtonPaste.setBorder(null);
    jButtonPaste.setToolTipText("Paste");
    jButtonPaste.setIcon(imagePaste);
    jButtonPaste.addActionListener(new MainFrame_jButtonPaste_actionAdapter(this));
    jButtonParse.setBorder(null);
    jButtonParse.setToolTipText("Parse");
    jButtonParse.setIcon(imageParse);
    jButtonParse.addActionListener(new MainFrame_jButtonParse_actionAdapter(this));
    jButtonStop.setBorder(null);
    jButtonStop.setToolTipText("Stop");
    jButtonStop.setIcon(imageStop);
    jButtonStop.addActionListener(new MainFrame_jButtonStop_actionAdapter(this));
    jButtonPreferences.setToolTipText("Preferences");
    jButtonPreferences.setIcon(imagePreferences);
    jButtonPreferences.addActionListener(new
                                         MainFrame_jButtonPreferences_actionAdapter(this));
    jButtonBrowser.setBorder(null);
    jButtonBrowser.setToolTipText("Browse SF Components");
    jButtonBrowser.setIcon(imageBrowseSF);
    jButtonBrowser.addActionListener(new MainFrame_jButtonBrowser_actionAdapter(this));
    jButtonExit.setBorder(null);
    jButtonExit.setToolTipText("Exit (Alt+F4)");
    jButtonExit.setIcon(imageExit);
    jButtonExit.setRolloverEnabled(true);
    jButtonExit.addActionListener(new MainFrame_jButtonExit_actionAdapter(this));
    jButtonSFDaemon.setBorder(null);
    jButtonSFDaemon.setToolTipText("sfDaemon");
    jButtonSFDaemon.setIcon(imageSFDaemon);
    jButtonSFDaemon.setRolloverEnabled(true);
    jButtonSFDaemon.addActionListener(new
                                      MainFrame_jButtonSFDaemon_actionAdapter(this));
    jButtonSFStopDaemon.setBorder(null);
    jButtonSFStopDaemon.setToolTipText("Stop sfDaemon");
    jButtonSFStopDaemon.setIcon(imageSFStopDaemon);
    jButtonSFStopDaemon.setRolloverEnabled(true);
    jButtonSFStopDaemon.addActionListener(new
                                          MainFrame_jButtonSFStopDaemon_actionAdapter(this));

    jMenuItemBrowseSF.setToolTipText("Browse SF Components");
    jMenuItemBrowseSF.setText("Browse");
    jMenuItemBrowseSF.addActionListener(new
                                        MainFrame_jMenuItemBrowseSF_actionAdapter(this));
    jMenuItemSaveOutput.setText("Save Output ...");
    jMenuItemSaveOutput.addActionListener(new
                                          MainFrame_jMenuItemSaveOutput_actionAdapter(this));
    jMenuItemInfoProp.setText("Info Prop.");
    jMenuItemInfoProp.addActionListener(new
                                        MainFrame_jMenuItemInfoProp_actionAdapter(this));
    jSplitPane2.setBorder(null);
    jSplitPane2.setLastDividerLocation(400);
    //jSplitPane2.setResizeWeight(0.3);
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setText(" ");
    processNameTextField.setMaximumSize(new Dimension(50, 2147483647));
    processNameTextField.setMinimumSize(new Dimension(4, 20));
    processNameTextField.setPreferredSize(new Dimension(30, 21));
    processNameTextField.setText("ProcessName");
    hostNameTextField.setText("127.0.0.1");
    hostNameTextField.setPreferredSize(new Dimension(30, 21));
    hostNameTextField.setMinimumSize(new Dimension(4, 20));
    hostNameTextField.setMaximumSize(new Dimension(50, 2147483647));
    jPanel1.setLayout(gridBagLayout1);
    jMenuItem1.setText("sfDaemon");
    jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(122, 0, false));
    jMenuItem1.addActionListener(new MainFrame_jMenuItem1_actionAdapter(this));
    securityCheckBox.setText("Sec On");
    jTextAreaMsg.addInputMethodListener(new
                                        MainFrame_jTextAreaMsg_inputMethodAdapter(this));
    jTextAreaMsg.addCaretListener(new MainFrame_jTextAreaMsg_caretAdapter(this));
    jTextAreaMsg.setEditable(false);
    BrowseSFComponentPanel.setToolTipText("");
    documentTextAreaMsg.addDocumentListener(new
                                            MainFrame_documentTextAreaMsg_documentAdapter(this));
    documentScreen.addDocumentListener(new
                                       MainFrame_documentScreen_documentAdapter(this));
    jPanelGenParse.setFont(new java.awt.Font("Dialog", 2, 12));
    jMenuItemKuntstoff.setText("Kunststoff");
    jMenuItemKuntstoff.addActionListener(new
                                         MainFrame_jMenuItemKuntstoff_actionAdapter(this));
    jCheckBoxMenuItemShowMsgP.setSelected(true);
    jCheckBoxMenuItemShowMsgP.setText("Show Msg P.");
    jCheckBoxMenuItemShowMsgP.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
        77,
        java.awt.event.KeyEvent.CTRL_MASK | java.awt.event.KeyEvent.ALT_MASK, false));
    jCheckBoxMenuItemShowMsgP.addActionListener(new java.awt.event.
                                                ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jCheckBoxMenuItemShowMsgP_actionPerformed(e);
      }
    });
    jRadioButtonMenuItemSF.setText("SF    language");
    jRadioButtonMenuItemSF.setSelected(true);
    jRadioButtonMenuItemSF.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButtonMenuItemSF_actionPerformed(e);
      }
    });
    jRadioButtonMenuItemSF2.setText("SF2   language");
    jRadioButtonMenuItemSF2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButtonMenuItemSF2_actionPerformed(e);
      }
    });
    jRadioButtonMenuItemSFXML.setText("SFXML language");
    jRadioButtonMenuItemSFXML.addActionListener(new java.awt.event.
                                                ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jRadioButtonMenuItemSFXML_actionPerformed(e);
      }
    });
    languagejLabel.setForeground(Color.blue);
    languagejLabel.setText("  sf   ");
    jMenuItemStopSfDaemon.setText("Stop sfDaemon");
    jMenuItemStopSfDaemon.setAccelerator(javax.swing.KeyStroke.getKeyStroke(123,
        0, false));
    jMenuItemStopSfDaemon.addActionListener(new
                                            MainFrame_jMenuItemStopSfDaemon_actionAdapter(this));
    jMenuItemSearch.setText("Search...");
    jMenuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke('F',
        java.awt.event.KeyEvent.CTRL_MASK, false));
    jMenuItemSearch.addActionListener(new
                                      MainFrame_jMenuItemSearch_actionAdapter(this));
    jMenuItemSearchNext.setText(" Search next");
    jMenuItemSearchNext.setAccelerator(javax.swing.KeyStroke.getKeyStroke(114,
        0, false)); //F3
    jMenuItemSearchNext.addActionListener(new
                                          MainFrame_jMenuItemSearchNext_actionAdapter(this));
    jButtonMngConsole.setBorder(null);
    jButtonMngConsole.setToolTipText("Management Console");
    jButtonMngConsole.setIcon(imageMngConsole);
    jButtonMngConsole.setRolloverEnabled(true);
    jButtonMngConsole.addActionListener(new
                                        MainFrame_jButtonMngConsole_actionAdapter(this));
    jMenuItemMngConsole.setText("Mng. Console");
    jMenuItemMngConsole.addActionListener(new
                                          MainFrame_jMenuItemMngConsole_actionAdapter(this));
    autoNameCheckBox.setText("Auto Name");
    buttonGroupLanguages.add(jRadioButtonMenuItemSF);
    buttonGroupLanguages.add(jRadioButtonMenuItemSF2);
    buttonGroupLanguages.add(jRadioButtonMenuItemSFXML);
    jMenuFile.add(jMenuItemNew);
    jMenuFile.add(jMenuItemOpen);
    jMenuFile.add(jMenuItemSave);
    jMenuFile.add(jMenuItemSaveAs);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuBarMain.add(jMenuFile);
    jMenuBarMain.add(jMenuEdit);
    jMenuBarMain.add(jMenuSF);
    jMenuBarMain.add(jMenuTools);
    jMenuBarMain.add(jMenuLF);
    jMenuBarMain.add(jMenuHelp);
    this.setJMenuBar(jMenuBarMain);
    contentPane.add(jToolBar, BorderLayout.NORTH);
    jToolBar.add(jButtonOpen, null);
    jToolBar.add(jButtonSave, null);
    jToolBar.addSeparator();
    jToolBar.add(jButtonCopy, null);
    jToolBar.add(jButtonCut, null);
    jToolBar.add(jButtonPaste, null);
    jToolBar.addSeparator();
    jToolBar.add(jButtonUndo, null);
    jToolBar.add(jButtonRedo, null);
    jToolBar.addSeparator();
    jToolBar.add(jButtonParse, null);
    jToolBar.add(jButtonRun, null);
    jToolBar.add(jButtonStop, null);
    jToolBar.addSeparator();
    jToolBar.add(jButtonSFDaemon, null);
    jToolBar.add(jButtonSFStopDaemon, null);
    jToolBar.add(jButtonMngConsole);
    jToolBar.addSeparator();
    jToolBar.add(jButtonBrowser, null);
    //jToolBar.add(jButtonPreferences, null); // Disabled at this time!!!
    jToolBar.addSeparator();
    jToolBar.add(jButtonAbout, null);
    jToolBar.addSeparator();
    jToolBar.addSeparator();
    jToolBar.add(jButtonExit, null);
    contentPane.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(jTabbedPanelNorth, JSplitPane.TOP);
    jTabbedPanelNorth.add(jTextAreaSFFile, panelNameSFFile);
    jTabbedPanelNorth.add(jTabbedPaneParse, panelNameParse);
    jTabbedPaneParse.add(jTextAreaRaw, panelNameRaw);
    //      jTabbedPaneParse.add(jTextAreaType, panelNameType);
    //      jTabbedPaneParse.add(jTextAreaPlace, panelNamePlace);
    jTabbedPaneParse.add(jPanelGenParse, panelNameGenParse);
    jTabbedPaneParse.add(jTextAreaDescription, panelNameDescription);
    jTabbedPaneParse.add(jTextAreaDeploy, panelNameDeploy);
    jTabbedPanelNorth.add(output, "output");
    jTabbedPanelNorth.add(BrowseSFComponentPanel, panelNameBrowseComp);
    jSplitPane1.add(jTabbedPaneSouth, JSplitPane.BOTTOM);
    jSplitPane2.add(statusBar, JSplitPane.RIGHT);
    jSplitPane2.add(jPanel1, JSplitPane.LEFT);
    jTabbedPaneSouth.add(jScrollPaneMsg, "Msg");
    jScrollPaneMsg.getViewport().add(jTextAreaMsg, null);
    jScrollPaneMsg.getViewport().add(jTextAreaMsg, null);
    jTabbedPaneSouth.add(processPanel, "Proc");
    contentPane.add(jSplitPane2, BorderLayout.SOUTH);
    output.getViewport().add(screen, null);
    jSplitPane1.setDividerLocation(425);
    jMenuLF.add(jMenuItemAuto);
    jMenuLF.add(jMenuItemMetal);
    jMenuLF.add(jMenuItemWin);
    jMenuLF.add(jMenuItemMotif);
    jMenuLF.add(jMenuItemKuntstoff);
    jMenuLF.addSeparator();
    jMenuLF.add(jCheckBoxMenuItemShowMsgP);
    jMenuHelp.add(jMenuItemInfo);
    jMenuHelp.add(jMenuItemInfoProp);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuSF.add(jMenuItemParse);
    jMenuSF.add(jMenuItemRun);
    jMenuSF.add(jMenuItemStop);
    jMenuSF.addSeparator();
    jMenuSF.add(jMenuItem1);
    jMenuSF.add(jMenuItemStopSfDaemon);
    jMenuSF.addSeparator();
    jMenuSF.add(jMenuItemBrowseSF);
    jMenuSF.add(jMenuItemMngConsole);
    jMenuEdit.add(jMenuItemUndo);
    jMenuEdit.add(jMenuItemRedo);
    jMenuEdit.addSeparator();
    jMenuEdit.add(jMenuItemCopy);
    jMenuEdit.add(jMenuItemCut);
    jMenuEdit.add(jMenuItemPaste);
    jMenuEdit.addSeparator();
    jMenuEdit.add(jMenuItemSelectAll);
    jMenuEdit.addSeparator();
    jMenuEdit.add(jMenuItemSearch);
    jMenuEdit.add(jMenuItemSearchNext);
    jMenuTools.add(jMenuItemCleanOutput);
    jMenuTools.add(jMenuItemCleanMsg);
    jMenuTools.add(jMenuItemReloadDescriptions);
    jMenuTools.add(jMenuItemSaveOutput);
    jMenuTools.addSeparator();
    jMenuTools.add(jRadioButtonMenuItemSF);
    jMenuTools.add(jRadioButtonMenuItemSF2);
    jMenuTools.add(jRadioButtonMenuItemSFXML);
    jPanel1.add(processNameTextField,
                new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0
                                       , GridBagConstraints.WEST,
                                       GridBagConstraints.HORIZONTAL,
                                       new Insets(0, 0, 0, 0), 62, 4));
    jPanel1.add(hostNameTextField, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 54, 4));
    jPanel1.add(securityCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 6, 0, 0), 1, 0));
    jPanel1.add(languagejLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(autoNameCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE,
        new Insets(0, 0, 0, 0), 0, 0));
    javax.swing.filechooser.FileFilter sfFilter = new SFFileFilter();
    jFileChooser.addChoosableFileFilter(sfFilter);
    jFileChooser.addChoosableFileFilter(new SF2FileFilter());
    jFileChooser.addChoosableFileFilter(new SFXMLFileFilter());
    jFileChooser.setFileFilter(sfFilter);
    //jFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    jFileChooser.setCurrentDirectory(new File("./sf/"));
    jSplitPane2.setDividerLocation(400);
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuFileExit_ActionAdapter
      implements ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuFileExit_ActionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuFileExit_ActionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuFileExit_actionPerformed(e);
    }
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuItemAuto_actionAdapter
      implements java.awt.event.ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuItemAuto_actionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuItemAuto_actionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItemAuto_actionPerformed(e);
    }
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuItemMetal_actionAdapter
      implements java.awt.event.ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuItemMetal_actionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuItemMetal_actionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItemMetal_actionPerformed(e);
    }
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuItemWin_actionAdapter
      implements java.awt.event.ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuItemWin_actionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuItemWin_actionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItemWin_actionPerformed(e);
    }
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuItemMotif_actionAdapter
      implements java.awt.event.ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuItemMotif_actionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuItemMotif_actionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItemMotif_actionPerformed(e);
    }
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuHelpAbout_ActionAdapter
      implements java.awt.event.ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuHelpAbout_ActionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuHelpAbout_ActionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuHelpAbout_actionPerformed(e);
    }
  }

  /**
   *  Description of the Class
   *
   *@author     julgui
   *@created    19 September 2001
   */
  class MainFrame_jMenuItemOpen_actionAdapter
      implements java.awt.event.ActionListener {
    MainFrame adaptee;

    /**
     *  Constructor for the MainFrame_jMenuItemOpen_actionAdapter object
     *
     *@param  adaptee  Description of Parameter
     */
    MainFrame_jMenuItemOpen_actionAdapter(MainFrame adaptee) {
      this.adaptee = adaptee;
    }

    /**
     *  Description of the Method
     *
     *@param  e  Description of Parameter
     */
    public void actionPerformed(ActionEvent e) {
      adaptee.jMenuItemOpen_actionPerformed(e);
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonExit_actionPerformed(ActionEvent e) {
    this.Exit();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jMenuItem1_actionPerformed(ActionEvent e) {
    this.runSFDaemon();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonSFDaemon_actionPerformed(ActionEvent e) {
    this.runSFDaemon();
  }

  //--------------------------Splash screen -------------

  /**
   *  Logo timer
   */
  protected Timer logoTimer = null;
  JWindow logoWin = null;
  ImageIcon hplogo = null;
  JCheckBoxMenuItem jCheckBoxMenuItemShowMsgP = new JCheckBoxMenuItem();
  /**
   *  It shows the given Image as a logo on a simple window
   *
   *@param  logo  Description of the Parameter
   *@return       Description of the Return Value
   */
  public JWindow showLogo(ImageIcon logo) {
    JWindow logoWin = new JWindow();
    BorderLayout boderLayout = new BorderLayout();
    JLabel jl = new JLabel(logo);
    JLabel jlTitle = new JLabel("SFGui " + version, JLabel.CENTER);
    JPanel jp = new JPanel();
    jp.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
    jp.setLayout(boderLayout);
    jp.add(jl, BorderLayout.CENTER);
    jp.add(jlTitle, BorderLayout.SOUTH);
    jp.setSize(jl.getPreferredSize());
    logoWin.setContentPane(jp);
    logoWin.setSize(jp.getPreferredSize());
    centerWindow(null, logoWin);
    logoWin.validate();
    logoWin.toFront();
    logoWin.setVisible(true);
    return logoWin;
  }

  /**
   *  Initial loading showing the logo
   */
  protected void showLogoSequence() {
    hplogo = new ImageIcon(org.smartfrog.tools.gui.browser.MainFrame.class.
                           getResource("hplogo.gif"));
    logoWin = showLogo(hplogo);
    logoTimer = new Timer(5500, this);
    logoTimer.setRepeats(false);
    logoTimer.start();
  }

  /**
   *  Hide the logo
   */
  protected void hideLogo() {
    logoWin.setVisible(false);
    logoWin.dispose();
  }

  //------------------ End Splash screen --------------------

  /**
   *  Used for timed events to scroll output and msg textAreas.
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    //System.out.println("Event");
    //this.statusBar.setText("Event:"+ e.getActionCommand());
    try {
      if (screenScrollChanged) {
        scrollScreenPane();
        screenScrollChanged = false;
      }
      if (msgScrollChanged) {
        scrollMsgPane();
        msgScrollChanged = false;
        this.processPanel.refresh();
      }
      if (e.getSource().equals(logoTimer)) {
        hideLogo();
      }
    }
    catch (Exception exc) {

    }
  }

  /**
   *  Description of the Method
   */
  void scrollMsgPane() {
    //scrollDown TextAreaMsg
    try {
      this.jScrollPaneMsg.getVerticalScrollBar().setValue(this.jTextAreaMsg.
          getHeight() - this.jTextAreaMsg.getVisibleRect().height);
    }
    catch (Exception ex) {
      //e.printStackTrace();
    }

  }

  /**
   *  Description of the Method
   */
  void scrollScreenPane() {
    try {
      this.output.getVerticalScrollBar().setValue(this.screen.getHeight() -
                                                  this.screen.getVisibleRect().
                                                  height);
    }
    catch (Exception ex) {
      //e.printStackTrace();
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void documentTextAreaMsg_insertUpdate(DocumentEvent e) {
    if ( (e.getType().toString().equals("INSERT"))) {
      msgScrollChanged = true;
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void documentScreen_insertUpdate(DocumentEvent e) {
    if ( (e.getType().toString().equals("INSERT"))) {
      screenScrollChanged = true;
    }
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonCopy_actionPerformed(ActionEvent e) {
//      jTextAreaSFFile.copy();
//      jTextAreaSFFile.grabFocus();
    this.copyText();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonCut_actionPerformed(ActionEvent e) {
//      jTextAreaSFFile.cut();
//      jTextAreaSFFile.grabFocus();
    this.cutText();
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  void jButtonPaste_actionPerformed(ActionEvent e) {
//      jTextAreaSFFile.paste();
//      jTextAreaSFFile.grabFocus();
    this.pasteText();
  }

  int jSplit1Position = 150;
  ButtonGroup buttonGroupLanguages = new ButtonGroup();
  JRadioButtonMenuItem jRadioButtonMenuItemSF = new JRadioButtonMenuItem();
  JRadioButtonMenuItem jRadioButtonMenuItemSF2 = new JRadioButtonMenuItem();
  JRadioButtonMenuItem jRadioButtonMenuItemSFXML = new JRadioButtonMenuItem();
  JLabel languagejLabel = new JLabel();

  void jCheckBoxMenuItemShowMsgP_actionPerformed(ActionEvent e) {
    if (this.jCheckBoxMenuItemShowMsgP.isSelected()) {
      this.jTabbedPaneSouth.setVisible(true);
      this.jSplitPane1.setDividerLocation(jSplit1Position);
    }
    else {
      this.jTabbedPaneSouth.setVisible(false);
      jSplit1Position = this.jSplitPane1.getDividerLocation();
    }
  }

  void jRadioButtonMenuItemSF_actionPerformed(ActionEvent e) {
    this.setTokenEditMarker(".sf");
    this.repaint();
  }

  void jRadioButtonMenuItemSF2_actionPerformed(ActionEvent e) {
    this.setTokenEditMarker(".sf2");
    this.repaint();
  }

  void jRadioButtonMenuItemSFXML_actionPerformed(ActionEvent e) {
    this.setTokenEditMarker(".sfxml");
    this.repaint();
  }

  //------------In progress -----------------
  // History files!
  /*************************************************************************
   * Some bits from:
   * http://www.javaworld.com/javaworld/javatips/jw-javatip119.html#resources
   * @author Klaus Berg                                                     *
   *************************************************************************/
  /*******************************************************************
   * Insert the last loaded pathname into the File menu if it is not  *
   * present yet. Only max pathnames are shown (the max number can be *
   * set in Jmon.ini, default is 9). Every item starts with 	      *
   * "<i>: ", where <i> is in the range [1..max].	              *
   * The loaded itemname will become item number 1 in the list.       *
   *******************************************************************/
  private final int MAX_ITEM_LEN = 50;
  private static final String FILE_SEPARATOR_STR = System.getProperty(
      "file.separator");
  private int max_itemnames = 9;
  private ArrayList pathnameHistory = new ArrayList(max_itemnames);
  private ArrayList itemnameHistory = new ArrayList(max_itemnames);

  public final void insertPathname(String pathname) {
    for (int k = 0; k < pathnameHistory.size(); k++) {
      if ( ( (String) pathnameHistory.get(k)).equals(pathname)) {
        int index = jMenuFile.getItemCount() - itemnameHistory.size() + k;
        jMenuFile.remove(index);
        pathnameHistory.remove(k);
        itemnameHistory.remove(k);
        if (itemnameHistory.isEmpty()) {
          //JSeparator is the last menu item at (index-1)
          jMenuFile.remove(index - 1);
        }
        insertPathname(pathname);
        return;
      }
    }
    if (itemnameHistory.isEmpty()) {
      jMenuFile.addSeparator();
    }
    else {
      // remove all itemname entries to prepare for re-arrangement
      for (int i = jMenuFile.getItemCount() - 1, j = 0;
           j < itemnameHistory.size(); i--, j++) {
        jMenuFile.remove(i);
      }
    }
    if (itemnameHistory.size() == max_itemnames) {
      // fileList is full: remove last entry to get space for the first item
      itemnameHistory.remove(max_itemnames - 1);
      pathnameHistory.remove(max_itemnames - 1);
    }
    itemnameHistory.add(0, getItemname(pathname));
    pathnameHistory.add(0, pathname);
    for (int i = 0; i < itemnameHistory.size(); i++) {
      MenuItemWithFixedTooltip item = new MenuItemWithFixedTooltip( (i + 1) +
          ": "
          + (String) itemnameHistory.get(i));
      item.setToolTipText( (String) pathnameHistory.get(i));
      item.addActionListener(new ItemListener(i));
      jMenuFile.add(item);
    }
  }

  /***********************************************************
   * Create a tooltip location directly over the menu item,   *
   * ie, left allign the tooltip text in "overlay" technique. *
   ***********************************************************/
  private final class MenuItemWithFixedTooltip
      extends JMenuItem {

    public MenuItemWithFixedTooltip(String text) {
      super(text);
    }

    public Point getToolTipLocation(MouseEvent e) {
      Graphics g = getGraphics();
      FontMetrics metrics = g.getFontMetrics(g.getFont());
      String prefix = itemnameHistory.size() <= 9 ? "8: " : "88: ";
      int prefixWidth = metrics.stringWidth(prefix);
      int x = JButton.TRAILING + JButton.LEADING - 1 + prefixWidth;
      return new Point(x, 0);
    }
  }

  /***********************************
   * Listen to menu item selections.  *
   ***********************************/
  private final class ItemListener
      implements ActionListener {
    int itemNbr;

    ItemListener(int itemNbr) {
      this.itemNbr = itemNbr;
    }

    public void actionPerformed(ActionEvent e) {
      MainFrame.this.openFile( (String) pathnameHistory.get(itemNbr));
      JMenuItem item = (JMenuItem) e.getSource();
      MainFrame.this.insertPathname(item.getToolTipText());
    }
  }

  /**********************************************************
   * Return the itemname (abbreviated itemname if necessary) *
   * to be shown in the file menu open item list.            *
   * A maximum of MAX_ITEM_LEN characters is used for the    *
   * itemname because we do not want to make the JMenuItem   *
   * entry too wide.                                         *
   **********************************************************/
  protected String getItemname(String pathname) {
    final char FILE_SEPARATOR = FILE_SEPARATOR_STR.charAt(0);
    final int pathnameLen = pathname.length();
    // if the pathame is short enough: return whole pathname
    if (pathnameLen <= MAX_ITEM_LEN) {
      return pathname;
    }
    // if we have only one directory: return whole pathname
    // we will not cut to MAX_ITEM_LEN here
    if (pathname.indexOf(FILE_SEPARATOR_STR) ==
        pathname.lastIndexOf(FILE_SEPARATOR_STR)) {
      return pathname;
    }
    else {
      // abbreviate pathanme: Windows OS like solution
      final int ABBREVIATED_PREFIX_LEN = 6; // e.g.: C:\..\
      final int MAX_PATHNAME_LEN = MAX_ITEM_LEN - ABBREVIATED_PREFIX_LEN;
      int firstFileSeparatorIndex = 0;
      for (int i = pathnameLen - 1; i >= (pathnameLen - MAX_PATHNAME_LEN); i--) {
        if (pathname.charAt(i) == FILE_SEPARATOR) {
          firstFileSeparatorIndex = i;
        }
      }
      if (firstFileSeparatorIndex > 0) {
        return pathname.substring(0, 3)
            + ".."
            + pathname.substring(firstFileSeparatorIndex, pathnameLen);
      }
      else {
        return pathname.substring(0, 3)
            + ".."
            + FILE_SEPARATOR_STR
            + ".."
            + pathname.substring(pathnameLen - MAX_PATHNAME_LEN, pathnameLen);
      }
    }
  }

  //-- Ini file for History file
  private String iniKeyFileHistory = "rem historyFile";
  //cheating the bat file, used for config as well;-)
  private String iniSecFileHistory = "FilesHistory";
  private JButton jButtonSFStopDaemon = new JButton();
  private JMenuItem jMenuItemStopSfDaemon = new JMenuItem();
  JMenuItem jMenuItemSearch = new JMenuItem();
  JMenuItem jMenuItemSearchNext = new JMenuItem();
  JButton jButtonMngConsole = new JButton();
  JMenuItem jMenuItemMngConsole = new JMenuItem();
  JCheckBox autoNameCheckBox = new JCheckBox();

  public void loadHistoryIniFile() {
    //this.iniFile.setDebug(true);
    // Read Config File
//      iniFile = new IniFile(iniFileName, false);
    String pathName;
    for (int i = max_itemnames - 1; i >= 0; i--) {
      String number = (new Integer(i)).toString();
      pathName = iniFile.getSetting(iniSecFileHistory,
                                    iniKeyFileHistory + "Name" + number, "");
      if (!pathName.equals("")) {
        this.insertPathname(pathName);
      }
    }
  }

  /**
   *  Save history of used files.
   */
  public void saveHistoryIniFile() {
    //this.iniFile.setDebug(true);
    if (pathnameHistory.size() > 0) {
      String pathName;
      int i = 0;
      String number;
      for (i = 0; i < this.pathnameHistory.size(); i++) {
        number = (new Integer(i)).toString();
        pathName = (String) pathnameHistory.get(i);
        //System.out.println("pathName "+i+": "+pathName);
        if (pathName != null) {
          //if (!pathName.equals("")){
          iniFile.setSetting(iniSecFileHistory,
                             iniKeyFileHistory + "Name" + number, pathName);
        }
        else {
          iniFile.setSetting(iniSecFileHistory,
                             iniKeyFileHistory + "Name" + number, "");
        }
      }
      iniFile.flush();
    }
  }

  void jButtonSFStopDaemon_actionPerformed(ActionEvent e) {
    this.stopSFDaemonBtn();
  }

  void jMenuItemStopSfDaemon_actionPerformed(ActionEvent e) {
    this.stopSFDaemon();
  }

  void jMenuItemSearch_actionPerformed(ActionEvent e) {
    this.getActiveJTextArea().search_word_define();
  }

  void jMenuItemSearchNext_actionPerformed(ActionEvent e) {
    this.getActiveJTextArea().search_word();

  }

  public void jButtonMngConsole_actionPerformed(java.awt.event.ActionEvent e) {
    this.runMngConsole();
  }

  public void jMenuItemMngConsole_actionPerformed(java.awt.event.ActionEvent e) {
    this.runMngConsole();
  }

  //-------------------------------------------


}

class MainFrame_jMenuItemMngConsole_actionAdapter
    implements java.awt.event.ActionListener {
  private MainFrame adaptee;
  MainFrame_jMenuItemMngConsole_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    adaptee.jMenuItemMngConsole_actionPerformed(e);
  }
}

class MainFrame_jButtonMngConsole_actionAdapter
    implements java.awt.event.ActionListener {
  private MainFrame adaptee;
  MainFrame_jButtonMngConsole_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {
    adaptee.jButtonMngConsole_actionPerformed(e);
  }
}

// End Main Class

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemSaveAs_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemSaveAs_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemSaveAs_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemSaveAs_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemSave_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemSave_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemSave_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemSave_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jFileChooser_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jFileChooser_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jFileChooser_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jFileChooser_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemParse_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemParse_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemParse_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemParse_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemRun_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemRun_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemRun_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemRun_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemCopy_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemCopy_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemCopy_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemCopy_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemCut_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemCut_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemCut_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemCut_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemPaste_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemPaste_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemPaste_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemPaste_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemNew_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemNew_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemNew_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemNew_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuFileExit_ActionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuFileExit_ActionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuFileExit_ActionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuFileExit_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_syntaxDocumentSFFile_documentAdapter
    implements javax.swing.event.DocumentListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_syntaxDocumentSFFile_documentAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_syntaxDocumentSFFile_documentAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void insertUpdate(DocumentEvent e) {
    adaptee.syntaxDocumentSFFile_insertUpdate(e);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void removeUpdate(DocumentEvent e) {
    adaptee.syntaxDocumentSFFile_removeUpdate(e);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void changedUpdate(DocumentEvent e) {
    adaptee.syntaxDocumentSFFile_changedUpdate(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_syntaxDocumentSFFile_undoableEditAdapter
    implements javax.swing.event.UndoableEditListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_syntaxDocumentSFFile_undoableEditAdapter
   *  object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_syntaxDocumentSFFile_undoableEditAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void undoableEditHappened(UndoableEditEvent e) {
    adaptee.syntaxDocumentSFFile_undoableEditHappened(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemRedo_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemRedo_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemRedo_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemRedo_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemUndo_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemUndo_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemUndo_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemUndo_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemSelectAll_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemSelectAll_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemSelectAll_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemSelectAll_actionPerformed(e);
  }
}

/*
 *  -Added features:
 *  Do, undo, redo, select. Editor complete enough.
 */
/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemStop_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemStop_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemStop_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemStop_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jTextAreaMsg_inputMethodAdapter
    implements java.awt.event.InputMethodListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jTextAreaMsg_inputMethodAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jTextAreaMsg_inputMethodAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void inputMethodTextChanged(InputMethodEvent e) {}

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void caretPositionChanged(InputMethodEvent e) {
    adaptee.jTextAreaMsg_caretPositionChanged(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_screen_inputMethodAdapter
    implements java.awt.event.InputMethodListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_screen_inputMethodAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_screen_inputMethodAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void inputMethodTextChanged(InputMethodEvent e) {}

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void caretPositionChanged(InputMethodEvent e) {
    adaptee.screen_caretPositionChanged(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemInfo_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemInfo_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemInfo_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemInfo_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jTextAreaMsg_caretAdapter
    implements javax.swing.event.CaretListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jTextAreaMsg_caretAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jTextAreaMsg_caretAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void caretUpdate(CaretEvent e) {
    adaptee.jTextAreaMsg_caretUpdate(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_screen_caretAdapter
    implements javax.swing.event.CaretListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_screen_caretAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_screen_caretAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void caretUpdate(CaretEvent e) {
    adaptee.screen_caretUpdate(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_screen_focusAdapter
    extends java.awt.event.FocusAdapter {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_screen_focusAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_screen_focusAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void focusGained(FocusEvent e) {
    adaptee.screen_focusGained(e);
  }
}

/**
 *  Description of the Class
 *
 *@author     julgui
 *@created    19 September 2001
 */
class MainFrame_jMenuItemCleanOutput_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemCleanOutput_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemCleanOutput_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemCleanOutput_actionPerformed(e);
  }
}

/**
 *  Description of the Class

 */
class MainFrame_jMenuItemReloadDescription_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemReloadDescription_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemReloadDescription_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemReloadDescriptions_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *
 */
class MainFrame_jMenuItemCleanMsg_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemCleanMsg_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemCleanMsg_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemCleanMsg_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_output_focusAdapter
    extends java.awt.event.FocusAdapter {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_output_focusAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_output_focusAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void focusGained(FocusEvent e) {
    adaptee.output_focusGained(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonOpen_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonOpen_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonOpen_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonOpen_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonSave_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonSave_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonSave_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonSave_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonAbout_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonAbout_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonAbout_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonAbout_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonPreferences_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonPreferences_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonPreferences_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonPreferences_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonUndo_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonUndo_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonUndo_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonUndo_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonRedo_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonRedo_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonRedo_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonRedo_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonParse_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonParse_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonParse_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonParse_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonRun_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonRun_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonRun_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonRun_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonStop_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonStop_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonStop_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonStop_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jButtonBrowser_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonBrowser_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonBrowser_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonBrowser_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jMenuItemBrowseSF_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemBrowseSF_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemBrowseSF_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemBrowseSF_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jMenuItemSaveOutput_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemSaveOutput_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemSaveOutput_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemSaveOutput_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *


 */
class MainFrame_jMenuItemInfoProp_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemInfoProp_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemInfoProp_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemInfoProp_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_jButtonExit_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonExit_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonExit_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonExit_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_jMenuItem1_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItem1_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItem1_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItem1_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_jButtonSFDaemon_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonSFDaemon_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonSFDaemon_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonSFDaemon_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_documentTextAreaMsg_documentAdapter
    implements javax.swing.event.DocumentListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_documentTextAreaMsg_documentAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_documentTextAreaMsg_documentAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void insertUpdate(DocumentEvent e) {
    adaptee.documentTextAreaMsg_insertUpdate(e);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void removeUpdate(DocumentEvent e) {}

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void changedUpdate(DocumentEvent e) {}
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_documentScreen_documentAdapter
    implements javax.swing.event.DocumentListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_documentScreen_documentAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_documentScreen_documentAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void insertUpdate(DocumentEvent e) {
    adaptee.documentScreen_insertUpdate(e);
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void removeUpdate(DocumentEvent e) {}

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void changedUpdate(DocumentEvent e) {}
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_jButtonCopy_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonCopy_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonCopy_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonCopy_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_jButtonCut_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonCut_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonCut_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonCut_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    05 December 2001
 */
class MainFrame_jButtonPaste_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jButtonPaste_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jButtonPaste_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonPaste_actionPerformed(e);
  }
}

/**
 *  Description of the Class
 *

 *@created    15 February 2002
 */
class MainFrame_jMenuItemKuntstoff_actionAdapter
    implements java.awt.event.ActionListener {

  MainFrame adaptee;

  /**
   *  Constructor for the MainFrame_jMenuItemKuntstoff_actionAdapter object
   *
   *@param  adaptee  Description of Parameter
   */
  MainFrame_jMenuItemKuntstoff_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  /**
   *  Description of the Method
   *
   *@param  e  Description of Parameter
   */
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemKuntstoff_actionPerformed(e);
  }
}

class MainFrame_jButtonSFStopDaemon_actionAdapter
    implements java.awt.event.ActionListener {
  private MainFrame adaptee;

  MainFrame_jButtonSFStopDaemon_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonSFStopDaemon_actionPerformed(e);
  }
}

class MainFrame_jMenuItemStopSfDaemon_actionAdapter
    implements java.awt.event.ActionListener {
  private MainFrame adaptee;

  MainFrame_jMenuItemStopSfDaemon_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemStopSfDaemon_actionPerformed(e);
  }
}

class MainFrame_jMenuItemSearch_actionAdapter
    implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_jMenuItemSearch_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemSearch_actionPerformed(e);
  }
}

class MainFrame_jMenuItemSearchNext_actionAdapter
    implements java.awt.event.ActionListener {
  MainFrame adaptee;

  MainFrame_jMenuItemSearchNext_actionAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemSearchNext_actionPerformed(e);
  }
}
