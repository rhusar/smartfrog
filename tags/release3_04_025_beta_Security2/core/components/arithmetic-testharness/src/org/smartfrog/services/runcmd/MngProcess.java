package org.smartfrog.services.runcmd;

import java.util.HashMap;
import java.util.Iterator;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;

//import com.jeffguy.IniFile;

/**
 *  Title: SmartFrog CVS Description: Copyright: Copyright
 *  (c) 2001 Company: HP Labs Bristol
 *
 *@author     Serrano
 *@created    19 September 2001
 *@version    1.0
 */


public class MngProcess {

    private HashMap processes;

    // Aux variables
    private RunCommandInt runCmdProc=null;
    private String nameP=null;


    /**
     *  Constructor for the MngProcess object
     */
    public MngProcess() {
      processes=new HashMap();
    }


    void addProcess (RunCommandInt processSF, boolean overWrite){
       try{
         if (processSF!=null){
            nameP = processSF.getProcessName();
            runCmdProc = (RunCommandInt)processes.get(nameP);
            if (overWrite){
               if (runCmdProc!=null){
                  try {
                     runCmdProc.kill();
                     if (runCmdProc instanceof Prim) {
                        Prim newSFPrim = (Prim) runCmdProc;
                        newSFPrim.sfDetach();
                        newSFPrim.sfTerminate(new TerminationRecord("normal", "Process overwritten (MngProcess):" + runCmdProc.getProcessName(), null));
                     }
                  } catch (Exception e) { System.err.println(e.getMessage());};
               }
               processes.put(nameP, processSF);
            } else {
               if (runCmdProc==null){
                  processes.put(nameP, processSF);
               } else {
                  //processes.put(nameP, runCmdProc);
               }
            }
         }
      } catch (Exception e) { System.err.println(e.getMessage());};
    }

    void deleteProcess (String processName) {
       try {
         if (processes.containsKey(processName)){
            runCmdProc = ((RunCommandInt)processes.get(processName));
            processes.remove(processName);
            runCmdProc.kill();
            runCmdProc=null;
         } else {
            // Not present in hash
         }
      } catch (Exception e) { System.err.println(e.getMessage());};
    }

    void startProcess (String processName){
       try{
          if (processes.containsKey(processName)){
            runCmdProc = ((RunCommandInt)processes.get(processName));
            runCmdProc.start();
         }
      } catch (Exception e) { System.err.println(e.getMessage());};
    }

    void stopProcess (String processName){
       try {
          if (processes.containsKey(processName)){
            runCmdProc = ((RunCommandInt)processes.get(processName));
            runCmdProc.stop();
         }
      } catch (Exception e) { System.err.println(e.getMessage());};
    }

    void killProcess (String processName){
       try{
          if (processes.containsKey(processName)){
            runCmdProc = ((RunCommandInt)processes.get(processName));
            runCmdProc.kill();
          }
       } catch (Exception e) { System.err.println(e.getMessage());};
    }

    public void killAll (){
       // Special case for sfDaemon! Only one should exist a every time to do a proper clean up
       //TODO generalize this. Special case for the console.
       Object sfDaemon = null;
       if (processes.size()>0) {
            for (Iterator iterat = (processes.keySet()).iterator() ; iterat.hasNext(); ) {
                  Object key =iterat.next();
                  if (((String)key).equals("sfDaemon") ){
                    sfDaemon= key;
                  } else {
                     try{
                        ((RunCommandInt)processes.get(key)).stop();
                        ((RunCommandInt)processes.get(key)).kill();
                     } catch (Exception e) { System.err.println(e.getMessage());};
                 }
            }
            try {
              Thread.sleep(3*1000);
            }
            catch (Exception ex) {
            }
            try{
               if (sfDaemon!=null) { ((RunCommandInt)processes.get(sfDaemon)).kill();}
            } catch (Exception e) { System.err.println(e.getMessage());};
        }
    }

    public void runAll (){

       if (processes.size()>0) {
           // first the daemon
            for (Iterator iterat = (processes.keySet()).iterator() ; iterat.hasNext(); ) {
                  Object key =iterat.next();
                  if (((String)key).equals("sfDaemon") ){
                     try{
                        ((RunCommandInt)processes.get(key)).start();
                     } catch (Exception e) { System.err.println(e.getMessage());};
                 }
            }
            try {
              Thread.sleep(3*1000);
            }
            catch (Exception ex) {
            }
            // then the rest of processes
            for (Iterator iterat = (processes.keySet()).iterator() ; iterat.hasNext(); ) {
                  Object key =iterat.next();
                  if (!((String)key).equals("sfDaemon") ){
                     try{
                        ((RunCommandInt)processes.get(key)).start();
                     } catch (Exception e) { System.err.println(e.getMessage());};
                 }
            }
        }
    }

      public Object[][] getListProcesses()
      {
         RunCommandInt runCmdProcess=null;
         if (processes.size()>0) {
            Object [][] data= new Object[processes.size()][3];
            int i=0;
            try {
            for (Iterator iterat = (processes.keySet()).iterator() ; iterat.hasNext(); ) {
                  Object key =iterat.next();
                  data[i][0]= key;
                  runCmdProcess = ((RunCommandInt)processes.get(key));
                  data[i][1]= runCmdProcess.getStatus(); // Status
                  data[i][2]= runCmdProcess.getCmd(); // All Cmds
                  i++;
                 }
            } catch (Exception e) { System.err.println(e.getMessage());};
            return data;
        } else {
           Object [][] data= new Object[1][3];
           data[0][0]= ""; //"Process Name";
           data[0][1]= ""; // "Status"; // Status
           data[0][2]= ""; //"Cmd"; // All Cmds
           return data;
        }
     }

//     // ***************************************************
//     // To record the first 10 processes in a file
//
//    private IniFile iniFile = null;
//    private String iniProcessFileName = "bin/sfGuiCFG.bat";// "bin/sfGui.bat";
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
//               processName = iniFile.getSetting(iniSecProcess, iniKeyProcess+"Name"+number , "" );
//               cmdStart= iniFile.getSetting(iniSecProcess, iniKeyProcess+"CmdStart"+number , "" );
//               cmdStop=  iniFile.getSetting(iniSecProcess, iniKeyProcess+"CmdStop"+number , "" );
//               if (!processName.equals ("")){
//                  if (cmdStop.equals("")){
//                    this.addProcess( new runCmdProcess(processName, cmdStart, "."), true );
//                  } else {
//                    this.addProcess( new runCmdProcess(processName, cmdStart, " ", cmdStop, " ", ".", null), true );
//                  }
//               }
//           }
//   }
//
//
//   /**
//    *  Description of the Method
//    */
//   public void saveIniFile() {
//      // Only neccesary if we get a window to modigy the parameters ;-)
//      // example: iniFile.setSetting(iniSecRunProcess, iniKeyClasspath, classpath);
//      // update classpath used!
//      //iniFile.flush();
//    if (processes.size()>0) {
//        String cmdStart;
//        String cmdStop;
//        String nameProcess;
//        runCmdProcess runCmdProc=null;
//        int i=0;
//        for (Iterator iterat = (processes.keySet()).iterator() ; iterat.hasNext(); ) {
//            String number= (new Integer(i)).toString();
//            Object key =iterat.next();
//            nameProcess= (String) key;
//            runCmdProc = (runCmdProcess) processes.get(key);   // Attribute
//            cmdStart =  runCmdProc.getCmdStart();
//            cmdStop =  runCmdProc.getCmdStop();
//            iniFile.setSetting(iniSecProcess, iniKeyProcess+"Name"+number, nameProcess);
//            iniFile.setSetting(iniSecProcess, iniKeyProcess+"CmdStart"+number, cmdStart);
//            iniFile.setSetting(iniSecProcess, iniKeyProcess+"CmdStop"+number, cmdStop);
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
//
//   // ***************************************************


}


