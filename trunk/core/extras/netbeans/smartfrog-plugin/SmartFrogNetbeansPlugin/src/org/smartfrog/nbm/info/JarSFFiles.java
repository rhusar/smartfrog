/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.nbm.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Iterator;

public class JarSFFiles {
    
    // global list of all smartfrog files holding instances of SfFileInfo
    private static TreeMap<String,SfFileInfo> globalSFList = new TreeMap<String,SfFileInfo>();
    
    // list of smartfrog include files found in jar
    private ArrayList<String> matches = null;
    
    /** Creates a new instance of JarSFFiles */
    public JarSFFiles(File f) {
        matches = new ArrayList<String>();
        getSFFiles(f);
    }
    
    public JarSFFiles(String cp) {
        matches = new ArrayList<String>();
        String[] fileList = cp.split(File.pathSeparator);
        for (int i=0; i<fileList.length; i++) {
            getSFFiles(new File(fileList[i]));
        }
    }
    
    public ArrayList<String> getSFFiles(File f) {
        if (f.exists()) {
            try {
                JarFile jf = new JarFile(f);
                Enumeration<JarEntry> jEntries = jf.entries();

                while (jEntries.hasMoreElements()) {
                    JarEntry je = jEntries.nextElement();
                    String name = je.getName();
                    if (name.endsWith(".sf")) {
                        matches.add(name);
                        SfFileInfo sfInfo = new SfFileInfo(name);

                        BufferedReader br = null;
                        try {
                            br = new BufferedReader(new InputStreamReader(jf.getInputStream(je)));
                            String line = br.readLine();
                            while (line != null) {

                                // check for extends which identifies a component
                                int idx = line.indexOf("extends");
                                if (idx >= 0) {
                                    idx--;
                                    // find lastcharacter past prior whitespace
                                    boolean stop = false;
                                    while (!stop && idx >= 0) {
                                        if (Character.isWhitespace(line.charAt(idx))) {
                                            idx--;
                                        } else {
                                            stop = true;
                                        }
                                    }

                                    // find next whitespace working backwords
                                    stop = false;
                                    int idx2 = idx - 1;
                                    while (!stop && idx2 >= 0) {
                                        if (Character.isWhitespace(line.charAt(idx2))) {
                                            stop = true;
                                        } else {
                                            idx2--;
                                        }
                                    }
                                    String componentName = line.substring(idx2 + 1, idx + 1);
                                    if (Character.isLetterOrDigit(componentName.charAt(0))) {
                                        sfInfo.addComponent(componentName);
                                    }
                                }

                                // check for include which identifies an include statement
                                idx = line.indexOf("include");
                                if (idx >= 0) {
                                    idx++;
                                    // find first double quote prior whitespace
                                    boolean stop = false;
                                    while (!stop && idx < line.length()) {
                                        if (line.charAt(idx) != '\"') {
                                            idx++;
                                        } else {
                                            stop = true;
                                        }
                                    }
                                    int firstQuote = idx + 1;
                                    idx++;
                                    // find trailing double quote
                                    stop = false;
                                    while (!stop && idx < line.length()) {
                                        if (line.charAt(idx) == '\"') {
                                            stop = true;
                                        } else {
                                            idx++;
                                        }
                                    }
                                    if ((firstQuote > 0 && firstQuote < line.length()) && (idx > 0 && idx < line.length())) {
                                        String dependentIncludeName = line.substring(firstQuote, idx);
                                        sfInfo.addDependentInclude(dependentIncludeName);
                                    } else {
                                        String problem = "line: " + line + ",," + firstQuote + ",," + idx;
                                    }
                                }
                                globalSFList.put(name, sfInfo);

                                line = br.readLine();
                            }
                        } finally {
                            if (br != null) {
                                br.close();
                            }
                        }
                    }
                }
                return (matches);
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
    
    public TreeSet<String> nextLevelNames(String ref) {
        TreeSet<String> res = null;
        if (ref == null) {
            res = new TreeSet<String>();
            Iterator<String> iter = matches.iterator();
            while (iter.hasNext()) {
                String el = iter.next();
                int idx = el.indexOf("/");
                res.add(el.substring(0,idx));
            }
        } else {
            if (ref == null || ref.length() <=0) {
                return null;
            }
            
            boolean refEndsWithSlash= false;
            
            if (ref.charAt(ref.length()-1)=='/') {
                refEndsWithSlash = true;
            }
            
            res = new TreeSet<String>();
            Iterator<String> iter = matches.iterator();
            while (iter.hasNext()) {
                String el = iter.next();
                int idx = el.indexOf(ref);
                if ( (idx >= 0) && ( !ref.equals(el) ) ){
                    
                    int nextLoc = ref.length();
                    int idx2 = el.indexOf("/",nextLoc);
                    if (idx2 >= 0) {
                        res.add(el.substring(nextLoc,idx2));
                    } else {
                        res.add(el.substring(nextLoc));
                    }
                }
            }
        }
        return res;
    }
    
    public SfFileInfo getSfFileInfo(String name) {
        return globalSFList.get(name);
    }
    
    public ArrayList<String> getAllSFFilesInCP() {
        ArrayList<String> res = new ArrayList<String>();
        Collection<SfFileInfo> c = globalSFList.values();
        Iterator<SfFileInfo> iter = c.iterator();
        while (iter.hasNext()) {
            SfFileInfo sfInfo = iter.next();
            res.add(sfInfo.getName());
        }
        return res;
    }
    
}
