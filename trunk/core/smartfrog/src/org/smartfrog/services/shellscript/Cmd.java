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

     // Stream Filters configuration
     FilterListener foutL = null;
     String filtersOut[]=null;
     FilterListener ferrL = null;
     String filtersErr[]=null;

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

     public String toString(){
         StringBuffer str = new StringBuffer();
         str.append("Cmd: ");
         Vector v = new Vector(java.util.Arrays.asList(getCmdArray()));
         str.append(v.toString());
         if (envp !=null){
             str.append(", envp: ");
             str.append(this.getEnvp().toString());
         }
         if (dir !=null){
             str.append(", dir: ");
             str.append(this.getFile().toString());
         }
         if (filtersOut !=null){
             str.append(", filtersOutputStrem: ");
             v = new Vector(java.util.Arrays.asList(getFiltersOut()));
             str.append(v.toString());
         }
         if (filtersErr !=null){
             str.append(", filtersErrorStrem: ");
             v = new Vector(java.util.Arrays.asList(getFiltersErr()));
             str.append(v.toString());
         }

         return str.toString();
     }

 }
