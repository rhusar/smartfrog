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

package org.smartfrog.services.shellscript;

import java.util.Vector;
import java.io.File;

// RumCmd class - Data holder for Process parameters

 public class Cmd {

     // Data needed for runTime Exec.

     String cmdarray[] = null;
     String envp[] = null;
     File dir = null;

     /** String name for line return. */
     String lineSeparator = System.getProperty("line.separator");

     public Cmd() {

     }

     public Cmd(String cmdarray[], String envp[], File dir) {
         this.cmdarray = cmdarray;
         this.cmdarray = cmdarray;
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

 }
