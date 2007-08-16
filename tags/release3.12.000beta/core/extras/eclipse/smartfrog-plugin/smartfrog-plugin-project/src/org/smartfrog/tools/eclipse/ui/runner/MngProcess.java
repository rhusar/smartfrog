
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


package org.smartfrog.tools.eclipse.ui.runner;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Manage smartFrog processes launched by this tools
 * 
 */
public class MngProcess
{
    private static HashMap processes;

    private static MngProcess instance = null;

    // Aux variables
    //    private InfoProcess infoProc=null;
    //    private String nameP=null;

    public static MngProcess getInstance()
    {
        if (null == instance) {
            instance = new MngProcess();
        }

        return instance;
    }

    /**
     * Constructor for the MngProcess object
     */
    private MngProcess()
    {
        processes = new HashMap();
    }

    public void addProcess(InfoProcess process, boolean overWrite)
    {
        String nameP = process.getProcessName();
        InfoProcess infoProc = ( (InfoProcess)processes.get(nameP) );

        if (overWrite) {
            if (infoProc != null) {
                //            infoProc.kill(); //TODO, need to kill prvious process
            }

            processes.put(nameP, process);
        } else {
            if (infoProc == null) {
                processes.put(nameP, process);
            } else {
                //processes.put(nameP, infoProc);
            }
        }
    }

    public InfoProcess deleteProcess(String processName)
    {
        InfoProcess infoProc = null;
        if (processes.containsKey(processName)) {
            infoProc = (InfoProcess)processes.get(processName);
            processes.remove(processName);

            //         infoProc.kill(); //TODO
//            infoProc = null;
        } else {
            // Not present in hash
        }
        return infoProc;
    }

    //    void startProcess (String processName){
    //       if (processes.containsKey(processName)){
    //         infoProc = (InfoProcess)processes.get(processName);
    //         infoProc.start();
    //      }
    //    }

    //    void stopProcess (String processName){
    //       if (processes.containsKey(processName)){
    //         infoProc = (InfoProcess)processes.get(processName);
    //         infoProc.stop();
    //      }
    //    }
    //
    //    void killProcess (String processName){
    //       if (processes.containsKey(processName)){
    //         infoProc = (InfoProcess)processes.get(processName);
    //         infoProc.kill();
    //       }
    //    }

    //    public void killAll (){
    //       // Special case for sfDaemon! Only one should exist a every time to do a
    // proper clean up
    //       //TODO generalize this. Special case for the console.
    //       Object sfDaemon = null;
    //       if (processes.size()>0) {
    //            for (Iterator iterat = (processes.keySet()).iterator() ;
    // iterat.hasNext(); ) {
    //                  Object key =iterat.next();
    //                  if (((String)key).equals("sfDaemon") ){
    //                    sfDaemon= key;
    //                  } else {
    //                     ((InfoProcess) processes.get(key)).stop();
    //                     ((InfoProcess) processes.get(key)).kill();
    //                 }
    //            }
    //            try {
    //              Thread.sleep(3*1000);
    //            }
    //            catch (Exception ex) {
    //            }
    //            if (sfDaemon!=null) { ((InfoProcess) processes.get(sfDaemon)).kill();}
    //        }
    //    }
    //
    //    public void runAll (){
    //
    //       if (processes.size()>0) {
    //           // first the daemon
    //            for (Iterator iterat = (processes.keySet()).iterator() ;
    // iterat.hasNext(); ) {
    //                  Object key =iterat.next();
    //                  if (((String)key).equals("sfDaemon") ){
    //                     ((InfoProcess) processes.get(key)).start();
    //                 }
    //            }
    //            try {
    //              Thread.sleep(3*1000);
    //            }
    //            catch (Exception ex) {
    //            }
    //            // then the rest of processes
    //            for (Iterator iterat = (processes.keySet()).iterator() ;
    // iterat.hasNext(); ) {
    //                  Object key =iterat.next();
    //                  if (!((String)key).equals("sfDaemon") ){
    //                     ((InfoProcess) processes.get(key)).start();
    //                 }
    //            }
    //        }
    //    }

    public Object[][] getListProcesses()
    {
        if (processes.size() > 0) {
            Object[][] data = new Object[ processes.size() ][ 3 ];
            int i = 0;

            for (Iterator iterat = ( processes.keySet() ).iterator();
                    iterat.hasNext();) {
                Object key = iterat.next();
                data[ i ][ 0 ] = key;

                InfoProcess infoProc = (InfoProcess)processes.get(key); // Attribute
                //                  data[i][1]= infoProc.getStatus(); // Status
                data[ i ][ 1 ] = infoProc.getCmd(); // All Cmds
                i++;
            }

            return data;
        } else {
            Object[][] data = new Object[ 1 ][ 3 ];
            data[ 0 ][ 0 ] = ""; //"Process Name"; //$NON-NLS-1$
            data[ 0 ][ 1 ] = ""; // "Status"; // Status //$NON-NLS-1$
            data[ 0 ][ 2 ] = ""; //"Cmd"; // All Cmds //$NON-NLS-1$

            return data;
        }
    }

    // ***************************************************
    // To record the first 10 processes in a file

    //    private IniFile iniFile = null;
    //    private String iniProcessFileName = "bin/sfGuiCFG.bat";//
    // "bin/sfGui.bat";
    //    private String iniKeyProcess = "rem process";
    //   //cheating the bat file, used for config as well;-)
    //    private String iniSecProcess = "Processes";
    //    int numberStoredProcess = 10;
    //
    //
    //    public void loadIniFile() {
    //      // Read Config File
    //      iniFile = new IniFile(iniProcessFileName, false);
    //      String cmdStart;
    //      String cmdStop;
    //      String processName;
    //           for (int i=0 ; i<numberStoredProcess ; i++) {
    //               String number= (new Integer(i)).toString();
    //               processName = iniFile.getSetting(iniSecProcess,
    // iniKeyProcess+"Name"+number , "" );
    //               cmdStart= iniFile.getSetting(iniSecProcess,
    // iniKeyProcess+"CmdStart"+number , "" );
    //               cmdStop= iniFile.getSetting(iniSecProcess, iniKeyProcess+"CmdStop"+number
    // , "" );
    //               if (!processName.equals ("")){
    //                  if (cmdStop.equals("")){
    //                    this.addProcess( new InfoProcess(processName, cmdStart, "."), true );
    //                  } else {
    //                    this.addProcess( new InfoProcess(processName, cmdStart, " ", cmdStop, "
    // ", ".", null), true );
    //                  }
    //               }
    //           }
    //   }
    //
    //
    //   /**
    //    * Description of the Method
    //    */
    //   public void saveIniFile() {
    //      // Only neccesary if we get a window to modigy the parameters ;-)
    //      // example: iniFile.setSetting(iniSecRunProcess, iniKeyClasspath,
    // classpath);
    //      // update classpath used!
    //      //iniFile.flush();
    //    if (processes.size()>0) {
    //        String cmdStart;
    //        String cmdStop;
    //        String nameProcess;
    //        InfoProcess infoProc=null;
    //        int i=0;
    //        for (Iterator iterat = (processes.keySet()).iterator() ;
    // iterat.hasNext(); ) {
    //            String number= (new Integer(i)).toString();
    //            Object key =iterat.next();
    //            nameProcess= (String) key;
    //            infoProc = (InfoProcess) processes.get(key); // Attribute
    //            cmdStart = infoProc.getCmdStart();
    //            cmdStop = infoProc.getCmdStop();
    //            iniFile.setSetting(iniSecProcess, iniKeyProcess+"Name"+number,
    // nameProcess);
    //            iniFile.setSetting(iniSecProcess, iniKeyProcess+"CmdStart"+number,
    // cmdStart);
    //            iniFile.setSetting(iniSecProcess, iniKeyProcess+"CmdStop"+number,
    // cmdStop);
    //            i++;
    //        }
    //        // add blank lines to all the others
    //        for (; i< numberStoredProcess; i++ ) {
    //            String number= (new Integer(i)).toString();
    //            iniFile.setSetting(iniSecProcess, iniKeyProcess+"Name"+number, "");
    //            iniFile.setSetting(iniSecProcess, iniKeyProcess+"CmdStart"+number, "");
    //            iniFile.setSetting(iniSecProcess, iniKeyProcess+"CmdStop"+number, "");
    //        }
    //      iniFile.flush();
    //   }
    //  }

    // ***************************************************

}
