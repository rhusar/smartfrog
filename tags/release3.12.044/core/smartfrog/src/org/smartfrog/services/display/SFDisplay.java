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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.InetAddress;

import javax.swing.Timer;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;

/**
 *  Implements PrintMsgInt interface and prints the message remotely.
 */
public class SFDisplay extends PrimImpl implements Prim, PrintMsgInt, PrintErrMsgInt {
   /** String name for attribute autoClean. */
   private static final String AUTO_CLEAN = "autoClean";
   /** String name for attribute cleanEveryNumSec. */
   private static final String CLEAN_EVERY_NUM_SEC = "cleanEveryNumSec";
   /** String name for attribute autoSave. */
   private static final String AUTO_SAVE = "autoSave";
   /** String name for attribute directoryAutoSave. */
   private static final String DIRECTORY_AUTO_SAVE = "directoryAutoSave";
   /** String name for attribute showIP. */
   private static final String SHOW_IP = "showIP";
   /** String name for attribute showSfProcess. */
   private static final String SHOW_SFPROCESSNAME = "showSfProcess";
   /** String name for attribute terminateSFProcessOnExit. */
   private static final String TERMINATE_SFPROCESS_ON_EXIT
                                            = "terminateSFProcessOnExit";
   /** String name for attribute nameDisplay. */
   private static final String NAME_DISPLAY = "nameDisplay";
   /** String name for attribute redirectStd. */
   private static final String REDIRECT_STD = "redirectStd";
   /** String name for attribute positiondisplay. */
   private static final String POSITION_DISPLAY = "positionDisplay";
   /** String name for attribute testDisplay. */
   private static final String TEXT_DISPLAY = "textDisplay";
   /** String name for attribute heightDisplay. */
   private static final String HEIGHT_DISPLAY = "heightDisplay";
   /** String name for attribute widthDisplay. */
   private static final String WIDTH_DISPLAY = "widthDisplay";
   /** String name for attribute widthDisplay. */
   public static final String FONTSIZE_DISPLAY = "fontSize";
   /** String name for attribute formatMsg. */
   private static final String FORMAT_MSG = "formatMsg";
   /** String name for attribute externalPrinter. */
   private static final String EXTERNAL_PRINTER = "externalPrinter";
   /** String name for attribute screenEditable. */
   private static final String SCREEN_EDITABLE = "screenEditable";
    /** String name for attribute askSaveChanges. */
    private static final String ASK_SAVE_CHANGES = "askSaveChanges";

   /**
    * Definition of components attribute - display.
    */
   public Display display = null;
   /** Definition of component attribute - Output Stream. */
   private OutputStream dout = System.out;
   /** Definition of component attribute - Input Stream. */
   private InputStream din = System.in;
   /** Definition of component attribute - Print Stream. */
   private PrintStream out = System.out;
   /** Definition of component attribute - nameDisplay. */
   private String nameDisplay = "";
   /** Definition of component attribute - positionDisplay. */
   private String positionDisplay = "C";
   /** Definition of component attribute - text. */
   private String text = "";
   /** Definition of component attribute - height. */
   private int height = 400;
   /** Definition of component attribute - width. */
   private int width = 500;
   /** Definition of component attribute - fontSize @value. */
   private int fontSize = 12;
   /** Definition of component attribute - PrintnMsgInt. */
   private PrintMsgInt printMsgImp = null;
   /** Definition of component attribute - havePrinter. */
   private boolean havePrinter = false;
   /** Definition of component attribute - rdirectStd. */
   private boolean redirectStd = false;
   /** Definition of component attribute - formatMsg. */
   private boolean formatMsg = false;
   /** Definition of component attribute - screenEditable. @value*/
   private boolean screenEditable = true;
   /** Definition of component attribute - terminateSFProcessOnExit. @value*/
   boolean terminateSFProcessOnExit = false;
    /** Definition of component attribute - ask save changes. Default: @value */
   private boolean askSaveChanges = false;

   /** Should clean the screen every "cleanEveryNumSec"?. */
   private boolean autoClean = true;

   /** Default value: clean every 15 minutes. */
   private int cleanEveryNumSec = 15 * 60;

   /** If auto clean, should save screen content? */
   private boolean autoSave = true;
   /** String valuse for directory. */
   private String directoryAutoSave = "./";
   /** Flag indicating to show IP or not. */
   private boolean showIP = false;
   /** Flag indicating to show sfPrcessName or not. */
   private boolean showSfProcessName = false;
   /** String name for sfProcessName. */
   public String sfProcessName = null;

   //Keep System streams:
   /** Output stream. */
   private PrintStream sysOut;
   /** Error stream. */
   private PrintStream sysErr;
   /** Input stream. */
   private InputStream sysIn;
   /** Timer object. */
   private Timer timerAutoClean = null;


   /**
    * Constructs SFDisplay object
    *
    *@throws  RemoteException  If RMI or network error
    */
   public SFDisplay() throws RemoteException {
      super();
   }


   /**
    * Deploys the component and reads the components attributes.
    *
    *@throws  SmartFrogException  if there is any error during reading
    *attributes or deploying the components
    *@throws  RemoteException     if there is any rmi or network error
    */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
        super.sfDeploy();
        try {
            // read components attributes
            readSFAttributes();
            if (showIP) {
                InetAddress hostName = SFProcess.sfDeployedHost();
                nameDisplay = nameDisplay + " [" + hostName + "]";
                nameDisplay = nameDisplay.replace('\\', '_');
                nameDisplay = nameDisplay.replace('/', '_');
            }
            if (showSfProcessName) {
                sfProcessName = sfResolve(SmartFrogCoreKeys.SF_PROCESS, sfProcessName, false);

                if (sfProcessName == null) {
                     nameDisplay = "[" + "" + "] " + nameDisplay;
                } else {
                     nameDisplay = "[" + sfProcessName + "] " + nameDisplay;
                }
                nameDisplay = nameDisplay.replace('\\', '_');
                nameDisplay = nameDisplay.replace('/', '_');
            }
            if (WindowUtilities.areGraphicsAvailable()) {
               display = new Display(nameDisplay, this, null);
               display.setVisible(false);
               display.setSize(width, height);
               display.setFontSize(fontSize);
               display.setTextScreen(text);
               setPositionDisplay(positionDisplay, display);
               display.setVisible(true);
               display.setAskSaveChanges(askSaveChanges);

               // Redirecting standard output:
               // TODO: redirect to other objects here???
               if (screenEditable) {
                  sysIn = System.in;
                  din = display.getInputStream();
                  System.setIn(din);
               }
               if (redirectStd) {
                  //To preserve system streams
                  sysErr = System.err;
                  sysOut = System.out;
                  out = display.getPrintStream();
                  System.setOut(out);
                  System.setErr(out);
               }
               if (autoClean) {
                timerAutoClean =
                    new Timer(cleanEveryNumSec * 1000,
                        new ActionListener() {
                          String storedMsg = "";
                          public void actionPerformed(ActionEvent evt) {
                             storedMsg = "[" +
                                   (new SimpleDateFormat(
                                   "HH:mm:ss.SSS dd/MM/yy").format(new Date(
                                   System.currentTimeMillis()))) +
                                   "] ";
                             display.append("------------ Stored: " +
                                   storedMsg + " ------------\n");
                             display.cleanScreen(autoSave, directoryAutoSave);
                          }
                        });
                timerAutoClean.start();
                if (autoSave) {
                    display.resetScreenFile(directoryAutoSave);
                }
              }
            }

        } catch (Exception e) {
            restoreOutputStreams();
             // TODO: Get the message from message bundle
             System.err.println("Error in SFDisplay.sfDeploy():" + e);
             throw new SmartFrogDeploymentException(e, this);
        }
   }

    private synchronized void restoreOutputStreams() {
        if(sysErr!=null) {
            System.setErr(sysErr);
            sysErr=null;

        }
        if(sysOut!=null) {
            System.setOut(sysOut);
            sysOut=null;
        }
    }


    /**
    *Reads attributes defined in SF description. All attributes are optional.
    *
    *@throws SmartFrogResolutionException if it fails to read an attribute
    *@throws RemoteException if it fails to read an attribute
    */
   private void readSFAttributes() throws SmartFrogResolutionException, RemoteException {
       String attribToRead = null;
       try {
            attribToRead = NAME_DISPLAY;
            nameDisplay = sfResolve(NAME_DISPLAY, nameDisplay, false);

            attribToRead = REDIRECT_STD;
            redirectStd = sfResolve(REDIRECT_STD,redirectStd, false);

            attribToRead = POSITION_DISPLAY;
            positionDisplay = sfResolve(POSITION_DISPLAY,
                                                      positionDisplay, false);
            attribToRead = TEXT_DISPLAY;
            text =  sfResolve(TEXT_DISPLAY,text, false);

            attribToRead = HEIGHT_DISPLAY;
            height = sfResolve(HEIGHT_DISPLAY,height, false);

            attribToRead = WIDTH_DISPLAY;
            width = sfResolve(WIDTH_DISPLAY, width, false);

            attribToRead = FONTSIZE_DISPLAY;
            fontSize = sfResolve(FONTSIZE_DISPLAY, fontSize, false);

            attribToRead = SCREEN_EDITABLE;
            screenEditable = sfResolve(SCREEN_EDITABLE,screenEditable, false);

            attribToRead = FORMAT_MSG;
            formatMsg = sfResolve(FORMAT_MSG, formatMsg, false);

            attribToRead = EXTERNAL_PRINTER;
            havePrinter = sfResolve(EXTERNAL_PRINTER,havePrinter, false);

            attribToRead = ASK_SAVE_CHANGES;
            askSaveChanges = sfResolve(ASK_SAVE_CHANGES,askSaveChanges, false);

            attribToRead = AUTO_CLEAN;
            autoClean = sfResolve(AUTO_CLEAN, autoClean, false);

            attribToRead = AUTO_SAVE;
            autoSave = sfResolve(AUTO_SAVE, autoSave, false);

            attribToRead = CLEAN_EVERY_NUM_SEC;
            cleanEveryNumSec = sfResolve(CLEAN_EVERY_NUM_SEC,
                                            cleanEveryNumSec, false);

            attribToRead = DIRECTORY_AUTO_SAVE;
            directoryAutoSave = sfResolve(DIRECTORY_AUTO_SAVE,
                                            directoryAutoSave, false);
            attribToRead = SHOW_IP;
            showIP = sfResolve(SHOW_IP, showIP, false);

            attribToRead = SHOW_SFPROCESSNAME;
            showSfProcessName = sfResolve(SHOW_SFPROCESSNAME,
                                            showSfProcessName, false);

            attribToRead = TERMINATE_SFPROCESS_ON_EXIT;
            terminateSFProcessOnExit = sfResolve(TERMINATE_SFPROCESS_ON_EXIT,
                 terminateSFProcessOnExit, false);
        } catch (SmartFrogResolutionException e) {
            System.err.println("Failed to read optional attribute: "
                    +attribToRead + "Exception:"+ e.getMessage());
            throw e;
        } catch (RemoteException e) {
           System.err.println("Failed to read optional attribute: "
                   + attribToRead + "Exception:" + e.getMessage());
           throw e;
       }
   }

   /**
    * Terminates the component
    *
    *@param  t Object having termination description
    */
   public synchronized void sfTerminateWith(TerminationRecord t) {
      try {
          restoreOutputStreams();

         if (screenEditable) {
            System.setIn(sysIn);
         }
      } catch (Exception e) {
         if (sfLog().isErrorEnabled()) sfLog().error("Error in SFDisplay.sfTerminateWith():" + e,e);
      }

      try {
          if (display != null) {
              display.dispose();
              display = null;
          }
      } catch (Exception e) {
      }

      super.sfTerminateWith(t);
   }

   /**
    *  Sets the positionDisplay attribute of the SFDisplay object
    *
    *@param  positionDisplay  The new positionDisplay value
    *@param  window           The new positionDisplay value
    */
   private void setPositionDisplay(String positionDisplay, Component window) {
      Container parent = null;
      org.smartfrog.services.display.WindowUtilities.setPositionDisplay(parent,
            window, positionDisplay);
   }


   /**
    * Interface method from interface PrintMsgInt.
    *
    *@param  msg  Message to be displayed
    */
   public void printMsg(String msg) {
      if (formatMsg) {
         msg = formatMsg(msg);
      }

      if (display != null) {
         display.append("" + msg + "\n");
      } else {
         System.out.println("" + msg);
      }

      if (havePrinter) {
         try {
            printMsgImp.printMsg(msg + "");
         } catch (Exception ex) {
             if (sfLog().isErrorEnabled()) sfLog().error(ex);
         }
      } else {
         //System.out.println("" + msg);
      }
   }


   /**
    * Prefixes token "ERR" with the message.
    *
    *@param  msg  Error Message
    */
   public synchronized void printErrMsg(String msg) {
      if (formatMsg) {
         msg = "[ERR]" + formatMsg(msg);
      }

      if (display != null) {
         display.append("" + msg + "\n");
      } else {
         System.out.println("" + msg);
      }

      if (havePrinter) {
         try {
            printMsgImp.printMsg(msg + "");
         } catch (Exception ex) {
            if (sfLog().isErrorEnabled()) sfLog().error(ex);
         }
      } else {
         //System.out.println("" + msg);
      }
   }


   /**
    * Formats the message by putting time stamp in HH:mm:ss.SSS dd/MM/yy format
    * before the message.
    *
    *@param  msg  Message
    *@return  Formatted message
    */
   private String formatMsg(String msg) {
      //msg = msg + ", [" + (new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yy").
      //format(new Date(System.currentTimeMillis()))) + "]";
      msg = "[" +
            (new SimpleDateFormat("HH:mm:ss.SSS dd/MM/yy").format(new Date(
            System.currentTimeMillis()))) + "] " + msg;

      //msg = msg + " / " + getSfProcessName();
      //msg = msg + " / " + localhost.toString();
      return msg;
   }


   // Implementing StreamIntf

   /**
    * Gets the outputStream attribute of the SFDisplay object
    *
    *@return    The outputStream value
    */
   public OutputStream getOutputStream() {
      if (display != null) {
         return display.getOutputStream();
      } else {
         return null;
      }
   }


   /**
    *  Gets the errorStream attribute of the SFDisplay object
    *
    *@return    The errorStream value
    */
   public OutputStream getErrorStream() {
      System.err.println("Info: Not Error Stream for Displays!");

      return null;
   }


   /**
    *  Gets the inputStream attribute of the SFDisplay object
    *
    *@return    The inputStream value
    */
   public InputStream getInputStream() {
      if (display != null) {
         return display.getInputStream();
      } else {
         return null;
      }
   }
}
