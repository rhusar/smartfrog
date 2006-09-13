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



public class InfoProcess {

    private String workDir=null;
    private String processName=null;
    private String host = "127.0.0.1"; //$NON-NLS-1$
    private Object startCmd=null;
    private String startAtt=""; //$NON-NLS-1$
    private Object stopCmd=null;
    private String stopAtt=""; //$NON-NLS-1$
    private String[] envProp=null;
    private boolean autoReStart=false;
//    public RunProcess process=null;
    private String statusInfo="Created"; //$NON-NLS-1$


    /**
     *  Constructor for the MngProcess object
     */
    public InfoProcess(String processName, String startCmd, String workDir) {
      this.processName=processName;
      this.startCmd=startCmd;
      this.workDir=workDir;
      //System.out.println("Infoprocess New...: "+this.toString());
     }

    public InfoProcess(String processName, Object startCmd, Object stopCmd, String workDir) {
        this.processName=processName;
        this.startCmd=startCmd;
        this.stopCmd = stopCmd;
        this.workDir=workDir;
        //System.out.println("Infoprocess New...: "+this.toString());
       }
    
    public InfoProcess(String processName, String startCmd, String startAtt, String stopCmd, String stopAtt, String workDir, String[] envProp) {
      this.workDir=workDir;
      this.processName=processName;
      this.startCmd=startCmd;
      this.startAtt=startAtt;
      this.stopCmd=stopCmd;
      this.stopAtt=stopAtt;
      this.envProp=envProp;
      //System.out.println("Infoprocess New...: "+this.toString());
     }


//    private void cleanProcess (){
//       //System.out.println("process.cleanProcess:clean start -"+this.getProcessName());
//       if (process!=null){
//         if ((process.getStatus()).equals("running")){
//            process.kill();
//            //System.out.println("process.cleanProcess:clean Kill -"+this.getProcessName());
//         }
//         //process.destroy();
//         process=null;
//         //System.out.println("process.cleanProcess:clean Null -"+this.getProcessName());
//       }
//      //System.out.println("process.cleanProcess:clean emd -"+this.getProcessName());
//    }
//
//    public void start (){
//      //System.out.println("process.Start:stating -"+this.getProcessName());
//      cleanProcess();
//      process=new RunProcess(this.startCmd+" "+this.startAtt, this.workDir, this.processName, this.envProp);
//      process.setReStart(this.autoReStart);
//      process.start();
//      this.statusInfo="Started";
//      //System.out.println("process.Start:started -"+this.getProcessName());
//    }
//
//    public void stop (){
//      cleanProcess(); // Review this for not killing it! Use an auxiliary process.
//      //System.out.println("process.Stop:stopping -"+this.getProcessName()+" Cmd:"+this.stopCmd);
//      if (this.stopCmd!=null){
//         process=new RunProcess(this.stopCmd+" "+this.stopAtt, this.workDir, this.processName, this.envProp);
//         process.setReStart(this.autoReStart);
//         process.start();
//         this.statusInfo="Stopped";
//      } else {
//         //process=new RunProcess(this.startCmd+" "+this.startAtt, this.workDir, this.processName);
//         this.statusInfo="Stopped(no Stop cmd)";
//      }
//      //System.out.println("process.Stop:stop end -"+this.getProcessName());
//    }
//
//    public void kill (){
//       cleanProcess();
//       this.statusInfo="Killed";
//    }
//
    public String getProcessName(){
      return processName;
    }

    public String getCmdStart(){
      if (startCmd == null){
        return ""; //$NON-NLS-1$
      }
      return (startCmd+" "+startAtt); //$NON-NLS-1$
    }

    public String getCmdStop(){
      if (stopCmd == null){
        return ""; //$NON-NLS-1$
      }
      return (stopCmd+" "+stopAtt); //$NON-NLS-1$
    }

    public Object getCmdStopObj(){
        return stopCmd;
      }

    public String getCmd(){
      return ("StartCmd: "+startCmd+" "+startAtt+" ¦ "+"StopCmd: "+stopCmd+" "+stopAtt); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }
//
//    public String getStatus (){
//      if (process!=null){
//         return (this.statusInfo+"("+this.process.getStatus()+")");
//      }
//      return (this.statusInfo+"(Null)");
//    }
//
//    public void setAutoReStart(boolean autoReStart){
//       this.autoReStart=autoReStart;
//       if (process!=null) process.setReStart(this.autoReStart);
//    }

    public String toString(){
      return("ProcessInfo: "+processName+ //$NON-NLS-1$
//           ", Status: "  +this.getStatus()+
           ", StartCmd: "+this.startCmd+" "+this.startAtt+ //$NON-NLS-1$ //$NON-NLS-2$
           ", StopCmd: " +this.stopCmd +" "+this.startAtt+ //$NON-NLS-1$ //$NON-NLS-2$
           ", WorkDir:"  +this.workDir+ //$NON-NLS-1$
           ", EnvProp:"  +this.envProp.toString()); //$NON-NLS-1$

    }

}