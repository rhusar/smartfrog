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

package org.smartfrog.nbm;

import org.smartfrog.nbm.documents.SFChangeListener;
import org.smartfrog.nbm.info.RuntimeJarFiles;
import org.smartfrog.nbm.info.SfFileInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;
import org.openide.text.NbDocument;


public class IncludeCompletionProvider implements CompletionProvider{
    RuntimeJarFiles rjf = null;
    private static final String includeStringMatch = "#include \"";
    private static final String extendsStringMatch = "extends ";
    private int processingType = -1;
    
    /** Creates a new instance of IncludeCompletionProvider */
    public IncludeCompletionProvider() {
        rjf = new RuntimeJarFiles();
    }
    
    public CompletionTask createTask(int type, JTextComponent jTextComponent) {
        
        if (type != CompletionProvider.COMPLETION_QUERY_TYPE) {
            return null;
        }
        
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            
            protected boolean canFilter(JTextComponent component) {
                StyledDocument bDoc = (StyledDocument)component.getDocument();
                int caretPosition = component.getCaretPosition();
                int lineStartOffset=0;
                String line="";
                
                // find the start of this line
                try {
                    lineStartOffset = getRowFirstNonWhite(bDoc, caretPosition);
                } catch (BadLocationException ex) {
                    lineStartOffset = 0;
                    ex.printStackTrace();
                }
                
                // acquire the line
                try {
                    line = new String(component.getText(lineStartOffset, caretPosition - lineStartOffset));
                    
                    if (line.indexOf("include")>=0) {
                        // check to see if the include has been quoted. two quotes represent this condition
                        if (lineStartOffset > -1 && caretPosition>lineStartOffset) {
                            int t2 = line.indexOf("\"");
                            if (t2 >= 0) {
                                int t3 = line.indexOf("\"",t2+1);
                                if (t3>=t2) {
                                    Completion.get().hideAll();
                                }
                            }
                        }
                    } else if (line.indexOf(extendsStringMatch)>= 0) {
                        // hide complete if extends is given a full word or ends with '{'
                        if (lineStartOffset > -1 && caretPosition>lineStartOffset) {
                            int t2 = line.indexOf(extendsStringMatch) + extendsStringMatch.length();
                            
                            while (t2 < line.length() && Character.isWhitespace(line.charAt(t2))) {
                                t2++;
                            }
                            if (t2 < line.length()) { // found starting character after extends
                                while (t2 < line.length() && !Character.isWhitespace(line.charAt(t2))) {
                                    if ( (line.charAt(t2) == '{') || (line.charAt(t2) == ';') ) {
                                        Completion.get().hideAll();
                                    }
                                    t2++;
                                }
                                if (t2<line.length() && line.charAt(t2)==' ') {
                                    Completion.get().hideAll();
                                }
                            }
                            
                        }
                    }
                    
                    if ( (line.indexOf(includeStringMatch)<0) && (line.indexOf(extendsStringMatch)<0) ) {
                        Completion.get().hideAll();
                    }
                    
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                
                return false;
            }
            
            
            protected void query(final CompletionResultSet completionResultSet, Document document, final int caretOffset) {
                final StyledDocument bDoc = (StyledDocument)document;
                int startOffset = -1;
                
                class Operation implements Runnable {
                    String filter = null;
                    int startOffset = caretOffset-1;
                    int processingType = 0;
                    
                    public void run() {
                        try {
                            final int lineStartOffset = getRowFirstNonWhite(bDoc, caretOffset);
                            if (lineStartOffset > -1 && caretOffset>lineStartOffset) {
                                final char[] line = bDoc.getText(lineStartOffset, caretOffset-lineStartOffset).toCharArray();
                                final int whiteOffset = indexOfWhite(line);
                                String completeLine = new String(line);
                                if (completeLine.indexOf(includeStringMatch)>=0) {
                                    processingType = 1;
                                    int idx = completeLine.indexOf(includeStringMatch);
                                    if (idx>=0) {
                                        filter = completeLine.substring(idx+includeStringMatch.length());
                                    }
                                } else if (completeLine.indexOf(extendsStringMatch)>=0) {
                                    processingType = 2;
                                    int idx = completeLine.indexOf(extendsStringMatch);
                                    if (idx>=0) {
                                        int startOffset = idx + extendsStringMatch.length();
                                        if (startOffset < completeLine.length()) {
                                            filter = completeLine.substring(startOffset).trim();
                                            if (filter.length()==0) {
                                                filter=null;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (BadLocationException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                    
                    public void finishWork() {
                        if (startOffset > -1 && caretOffset > startOffset) {
                            TreeSet<String> ts = new TreeSet<String>();
                            if (processingType == 1) {
                                if (filter != null && filter.length()>0) {
                                    ts = rjf.getJarSFFiles().nextLevelNames(filter);
                                } else {
                                    ts = rjf.getJarSFFiles().nextLevelNames(null);
                                }
                            } else if (processingType == 2) {
                                boolean restrictToIncludes = SmartFrogSvcUtil.getSFRestrictToIncludes();
                                ArrayList<String> myIncludes = new ArrayList<String>();
                                if (restrictToIncludes) {
                                    String myText=null;
                                    try {
                                        myText = bDoc.getText(0, bDoc.getLength());
                                    } catch (BadLocationException ex) {
                                        ex.printStackTrace();
                                    }
                                    boolean done = false;
                                    int idx = 0;
                                    
                                    while (!done && myText != null) {
                                        idx = myText.indexOf("include",idx);
                                        if (idx >= 0) {
                                            try {
                                                // check to see if line has comments
                                                int lineStartOffset = getRowFirstNonWhite(bDoc, idx);
                                                final char[] line = bDoc.getText(lineStartOffset, idx-lineStartOffset).toCharArray();
                                                String completeLine = new String(line);
                                                if (completeLine.indexOf("//")<0) { // line did not start with a comment so add to list
                                                    // extract includename into arraylist
                                                    String thisInclude = findInclude(idx,myText);
                                                    myIncludes.add(thisInclude);
                                                    idx = idx + 2 + thisInclude.length();
                                                } else {
                                                    idx+= 7;
                                                }
                                            } catch (BadLocationException ex) {
                                                idx += 7;
                                            }
                                            
                                            
                                            
                                        } else {
                                            done = true;
                                        }
                                    }
                                } else {
                                    myIncludes = rjf.getJarSFFiles().getAllSFFilesInCP();
                                }
                                
                                Iterator<String> iter = myIncludes.iterator();
                                while (iter.hasNext()) {
                                    String includeFile = iter.next();
                                    SfFileInfo sfInfo = rjf.getJarSFFiles().getSfFileInfo(includeFile);
                                    if (sfInfo != null) {
                                        ArrayList<String> componentsToShow = new ArrayList<String>();
                                        getComponentsToShow(componentsToShow,sfInfo);
                                        Iterator<String> iter2 = componentsToShow.iterator();
                                        while (iter2.hasNext()) {
                                            String val = iter2.next();
                                            if (filter != null) {
                                                if (val.startsWith(filter)) {
                                                    ts.add(val.substring(filter.length()));
                                                }
                                            } else {
                                                ts.add(val);
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Iterator<String> iter = ts.iterator();
                            while (iter.hasNext()) {
                                String val = iter.next();
                                completionResultSet.addItem(new IncludeCompletionItem(val,startOffset, caretOffset));
                            }
                        }
                        
                        
                        
                        completionResultSet.setAnchorOffset(startOffset);
                        completionResultSet.finish();
                    }
                }
                
                Operation oper = new Operation();
                try {
                    NbDocument.runAtomicAsUser(bDoc, oper);
                    oper.finishWork();
                } catch (BadLocationException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                
            }
        },jTextComponent);
    }
    
    public int getAutoQueryTypes(JTextComponent jTextComponent, String string) {
        
        
        StyledDocument bDoc = (StyledDocument)jTextComponent.getDocument();
        int caretPosition = jTextComponent.getCaretPosition();
        int lineStartOffset=0;
        String line="";
 
        // find the start of this line
        try {
            lineStartOffset = getRowFirstNonWhite(bDoc, caretPosition);
        } catch (BadLocationException ex) {
            lineStartOffset = 0;
            ex.printStackTrace();
        }
        
        // acquire the line
        try {
            line = new String(jTextComponent.getText(lineStartOffset, caretPosition - lineStartOffset));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        
        // if nothing in the line return
        if (line.length() == 0 ) {
            return 0;
        }
        
        // check to see if the include has been quoted. two quotes represent this condition
        if (lineStartOffset > -1 && caretPosition>lineStartOffset) {
            int t2 = line.indexOf("\"");
            if (t2 >= 0) {
                int t3 = line.indexOf("\"",t2+1);
                if (t3>=t2) {
                    return 0;
                }
            }
            
            // finally see if the line contains the appropriate include syntax
            if (line.indexOf(includeStringMatch)>=0) {
                return COMPLETION_QUERY_TYPE;
            } else if (line.indexOf(extendsStringMatch)>=0) {
                int idx = line.indexOf(extendsStringMatch) + extendsStringMatch.length();
                while (idx < line.length() && Character.isWhitespace(line.charAt(idx))) {
                    idx++;
                }
                if (idx < line.length()) {
                    // found first character of extends
                    if (line.endsWith(" ") || line.endsWith("{") || line.endsWith(";")) {
                        return 0;
                    } else {
                        return COMPLETION_QUERY_TYPE;
                    }
                } else {
                    return COMPLETION_QUERY_TYPE;
                }
            }
        }
        
        // return 0 if include syntax is no tfound
        return 0;
    }
    
    static int getRowFirstNonWhite(StyledDocument doc, int offset) throws BadLocationException {
        Element lineElement = doc.getParagraphElement(offset);
        int start = lineElement.getStartOffset();
        while (start + 1 < lineElement.getEndOffset()) {
            try {
                if (doc.getText(start,1).charAt(0) != ' ') {
                    break;
                }
            } catch (BadLocationException ex) {
                throw (BadLocationException) new BadLocationException(
                        "calling getText(" + start + ", " + (start +1) +
                        ") on doc length: " + doc.getLength(), start
                        ).initCause(ex);
            }
            start++;
        }
        return start;
    }
    
    static int indexOfWhite(char[] line) {
        int i = line.length;
        while (--i > -1) {
            final char c = line[i];
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return -1;
    }
    
    private String findInclude(int idx, String value) {
        // check for include which identifies an include statement
        String res = new String();
        if (idx>=0) {
            idx++;
            // find first double quote prior whitespace
            boolean stop = false;
            while (!stop && idx < value.length()) {
                if (value.charAt(idx) != '\"') {
                    idx++;
                } else {
                    stop = true;
                }
            }
            int firstQuote = idx+1;
            idx++;
            // find trailing double quote
            stop = false;
            while (!stop && idx<value.length()) {
                if (value.charAt(idx)=='\"') {
                    stop=true;
                } else {
                    idx++;
                }
            }
            res = value.substring(firstQuote,idx);
        }
        return res;
    }
    
    private void getComponentsToShow(ArrayList<String>componentsToShow,SfFileInfo sfInfo) {
        ArrayList<String> myDependents = sfInfo.getDependentIncludes();
        Iterator<String> list = myDependents.iterator();
        while (list.hasNext()) {
            String includeDependentName = list.next();
            if (includeDependentName.charAt(0)=='/') {
                includeDependentName = includeDependentName.substring(1);
            }
            SfFileInfo nextSfInfo = rjf.getJarSFFiles().getSfFileInfo(includeDependentName);
            if (nextSfInfo != null) {
                getComponentsToShow(componentsToShow,nextSfInfo);
            }
        }
        
        ArrayList<String> myComponents = sfInfo.getComponents();
        list = myComponents.iterator();
        while (list.hasNext()) {
            componentsToShow.add(list.next());
        }
        
    }
}
