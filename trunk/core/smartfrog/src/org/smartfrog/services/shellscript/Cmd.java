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

package org.smartfrog.services.shellscript;

import java.util.Vector;
import java.util.Arrays;
import java.io.File;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;
import java.io.Serializable;

// RumCmd class - Data holder for Process parameters

 public class Cmd implements Serializable{

     /** String name for optional attribute. Value {@value}. */
     public final static String ATR_CMD = "cmd";
     /** String name for optional attribute. Value {@value}. */
     public final static String ATR_ENVP = "envProperties";
     /** String name for optional attribute. Value {@value}. */
     public final static String ATR_DIR = "dir";

     /** String name for optional attribute. Value {@value}. */
     public final static String ATR_LINE_SEPARATOR = "lineSeparator";

     /** String name for optional attribute. Value {@value}. */
     public static final String ATR_ECHO_CMD = "echoCmd";
     /** String name for optional attribute. Value {@value}. */
     public static final String ATR_EXIT_ERROR_CMD = "echoErrorCmd";

     /** String name for optional attribute. Value {@value}. */
     public static final String ATR_FILTERS_OUT = "filtersOut";
     /** String name for optional attribute. Value {@value}. */
     public static final String ATR_FILTERS_ERR = "filtersErr";

     /** This indicates if the component should detach when the
      * exec finishes. String name for attribute. Value {@value}. */
     public final static String ATR_RESTART = "restart";


     /** This indicates if the component should detach when the
      * exec finishes. String name for attribute. Value {@value}. */
     public final static String ATR_DETACH = "detach";

     /** This indicates if the component should terminate when the
      * exec finishes. String name for attribute. Value {@value}. */
     public final static String ATR_TERMINATE = "terminate";


     /**
      * Decides if to pass postives to the listener.line() interface in addition to the listener.found() call
      * Value {@value}.
      */
     public final static String ATR_PASS_POSITIVES = "passPositives";

     // Data needed for runTime Exec.

     private String cmdarray[] = null;
     private String envp[] = null;
     private File dir = null;

     // Stream Filters configuration
     private FilterListener foutL = null;
     private String filtersOut[]=null;
     private FilterListener ferrL = null;
     private String filtersErr[]=null;

     /** String name for line return. */
     private String lineSeparator = System.getProperty("line.separator");

     private String echoCommand ="echo";

     private String exitErrorCommand=null; // Unix= $?, Windows "%ERRORLEVEL%";

     //Parameters for hosting Prim components


     /**
      * Host component should terminate when exec terminates
      */
     private boolean shouldTerminate = false;

     /**
      * Host component should detach when exec terminates
      */
     private boolean shouldDetach = false;

     /**
      * Host component should restart exec when it terminates
     */
     private boolean shouldRestart = false;

     /**
      * decides if to pass postives to the listener.line() interface in addition to the listener.found() call
      */
     private boolean passPositives = false;

     public Cmd() {

     }


     public Cmd(ComponentDescription cd) throws SmartFrogException {
          try {
              this.setCmdArray(cd.sfResolve(ATR_CMD, cmdarray, false));
              this.setEnvp( cd.sfResolve(ATR_ENVP, envp, false));
              this.setEchoCommand( cd.sfResolve(ATR_ECHO_CMD, echoCommand, false));
              this.setFile(cd.sfResolve(ATR_DIR, dir, false));
              this.setExitErrorCommand( cd.sfResolve(ATR_EXIT_ERROR_CMD, exitErrorCommand, false));
              this.filtersOut = cd.sfResolve(ATR_FILTERS_OUT, filtersOut, false);
              this.filtersErr = cd.sfResolve(ATR_FILTERS_ERR, filtersErr, false);
              //Host Prim component
              this.shouldTerminate = cd.sfResolve (ATR_TERMINATE,shouldTerminate,false);
              this.shouldDetach = cd.sfResolve (ATR_DETACH,shouldDetach,false);
              this.shouldRestart = cd.sfResolve (ATR_RESTART,shouldRestart,false);
              this.passPositives = cd.sfResolve (ATR_PASS_POSITIVES, passPositives,false);
          } catch (Exception ex) {
              throw SmartFrogException.forward("Failed to create CMD", ex);
          }
     }

     public Cmd(String cmdarray[], String envp[], File dir) {
         this.cmdarray = cmdarray;
         this.dir = dir;
         this.envp = envp;
     }

     public Cmd setCmdArray(String[] cmdarray) {
         this.cmdarray = cmdarray;
         return this;
     }

     public Cmd setCmdArray(Vector cmdarray) {
         this.cmdarray = new String[cmdarray.size()];
         cmdarray.copyInto(this.cmdarray);
         return this;
     }

     public Cmd setEnvp(String[] envp) {
         this.envp = envp;
         return this;
     }

     public Cmd setEnvp(Vector envp) {
         this.envp = new String[envp.size()];
         envp.copyInto(this.envp);
         return this;
     }

     public Cmd setFile(File dir) {
         this.dir = dir;
         return this;
     }

     public Cmd setLineSeparator(String lineSeparator){
        this.lineSeparator=lineSeparator;
        return this;
     }

     public Cmd setFilterOutListener(FilterListener foutL, String filtersOut[]) {
         this.foutL = foutL;
         this.filtersOut = filtersOut;
         return this;
     }

     public Cmd setFilterErrListener(FilterListener ferrL, String filtersErr[]) {
         this.ferrL = ferrL;
         this.filtersErr = filtersErr;
         return this;
     }

     public Cmd setEchoCommand(String echoCommand){
        if (echoCommand==null) {
          this.echoCommand = echoCommand;
        } else {
          this.echoCommand = echoCommand.trim();
        }
        return this;
     }

     public Cmd setExitErrorCommand(String exitErrorCommand){
        if (exitErrorCommand==null) {
          this.exitErrorCommand = exitErrorCommand;
        } else {
          this.exitErrorCommand = exitErrorCommand.trim();
        }
        return this;
     }

     public Cmd setRestart(boolean restart) {
         this.shouldRestart = restart;
         return this;
     }

     public Cmd setPassPositives(boolean passPositives) {
         this.passPositives = passPositives;
         return this;
     }

     public Cmd setDetach(boolean detach) {
         this.shouldDetach = detach;
         return this;
     }

     public Cmd setTerminate(boolean terminate) {
         this.shouldTerminate = terminate;
         return this;
     }

     public String[] getCmdArray() {
         return cmdarray;
     }

     public String[] getEnvp() {
         return envp;
     }

     public File getFile() {
         return dir;
     }

     public String getLineSeparator() {
         return this.lineSeparator;
     }

     public String[] getFiltersOut() {
              return filtersOut;
     }

     public String[] getFiltersErr() {
              return filtersErr;
     }

     public FilterListener getFilterErrListener() {
              return this.ferrL;
     }

     public FilterListener getFilterOutListener() {
              return this.foutL;
     }

     public String getEchoCommand() {
         return this.echoCommand;
     }

     public String getExitErrorCommand() {
              return this.exitErrorCommand;
     }

     public boolean detach() {
         return this.shouldDetach;
     }

     public boolean terminate() {
         return this.shouldTerminate;
     }

     public boolean restart() {
         return this.shouldRestart;
     }

     public boolean passPositives() {
         return this.passPositives;
     }

     public String toString(){
         StringBuffer str = new StringBuffer();
         str.append("Cmd: ");
         Vector<String> v = new Vector<String>(Arrays.asList(getCmdArray()));
         str.append(v.toString());
         if (envp !=null){
             str.append(", envp: ");
             str.append(getEnvp().toString());
         }

         if (dir !=null){
             str.append(", dir: ");
             str.append(getFile().toString());
         }

         if (filtersOut !=null){
             str.append(", filtersOutputStream: ");
             v = new Vector<String>(Arrays.asList(getFiltersOut()));
             str.append(v.toString());
         }

         if (filtersErr !=null){
             str.append(", filtersErrorStream: ");
             v = new Vector<String>(Arrays.asList(getFiltersErr()));
             str.append(v.toString());
         }

         if (echoCommand !=null){
             str.append(", echoCommand: ");
             str.append(echoCommand);
         }

         if (exitErrorCommand !=null){
             str.append(", exitErrorCommand: ");
             str.append(exitErrorCommand);
         }

         str.append(", detach: ");
         str.append(detach());
         str.append(", terminate: ");
         str.append(terminate());
         str.append(", restart: ");
         str.append(restart());
         str.append(", passPositives: ");
         str.append(passPositives());

         return str.toString();
     }

 }
