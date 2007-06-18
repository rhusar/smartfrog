/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.nbm.documents;

import org.smartfrog.nbm.SmartFrogSvcUtil;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.openide.text.NbDocument;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.parser.Phases;
import org.smartfrog.sfcore.parser.SFParser;

/**
 * SFChangeListener is responsible for determining that a smartfrog document needs to
 * be reparsed and any error annotations placed in the edit window. The run loop waits one
 * second and then checks to see if a document needs updating and if a userconfigurable
 * timeout (quiet time wait) has beene exceeded. A document will only be reparsed if
 * the user has stopped editing the document.
 */
public class SFChangeListener implements DocumentListener, Runnable {
    
    private static int WAITTIME = 1000; // one second thread sleep time during processing
    private boolean needsProcessing = true;
    private boolean stop = false;
    private long timeOfLastEvent=0;
    private StyledDocument myDoc = null;
    private String myTitle = null;
    private ArrayList<CompileErrorAnnotation> prevList = new ArrayList<CompileErrorAnnotation>();
    private static TreeMap<String,SFChangeListener> tm = new TreeMap<String,SFChangeListener>();
    
    
    /**
     * SFChangeListener stores the set of available instances, one per StyledDocument, in a treemap. getInstance()
     * will lookup appropraite SFChangeListener from the map based on the provided StyledDocument. The title attribute of
     * the StyledDocument is used as the key into this map.
     * @return SFChangeListener
     * @param doc The StyledDocument whose listener is desired
     */
    public static SFChangeListener getInstance(StyledDocument doc) {
        String myTitle = (String)doc.getProperty(doc.TitleProperty);
        if (myTitle != null) {
            if (tm.containsKey(myTitle)) {
                return tm.get(myTitle);
            } else {
                SFChangeListener sfc = new SFChangeListener(doc,myTitle);
                tm.put(myTitle,sfc);
                return sfc;
            }
        } else {
            return null;
        }
    }
    
    /** Creates a new instance of SFChangeListener */
    private SFChangeListener(StyledDocument doc, String myTitle) {
        myDoc = doc;
        this.myTitle = myTitle;
        Thread t = new Thread(this);
        t.start();
    }
    
    private void setDirtyStatus() {
        if (myDoc!=null) {
            SFUpToDateStatusProvider statP = SFUpToDateStatusProvider.getInstance(myDoc);
            statP.setStatus(UpToDateStatus.UP_TO_DATE_DIRTY);
        }
    }
    
    private void setOkStatus() {
        if (myDoc!=null) {
            SFUpToDateStatusProvider statP = SFUpToDateStatusProvider.getInstance(myDoc);
            statP.setStatus(UpToDateStatus.UP_TO_DATE_OK);
        }
    }
    
    private void setProcessingStatus() {
        if (myDoc!=null) {
            SFUpToDateStatusProvider statP = SFUpToDateStatusProvider.getInstance(myDoc);
            statP.setStatus(UpToDateStatus.UP_TO_DATE_PROCESSING);
        }
    }
    
    /**
     * Processes the insertUpdate event
     * @param e DocumentEvent
     */
    public void insertUpdate(DocumentEvent e) {
        processEvent(e);
    }
    
    /**
     * Processes the removeUpdate event
     * @param e DocumentEvent
     */
    public void removeUpdate(DocumentEvent e) {
        processEvent(e);
    }
    
    /**
     * Processes the changedUpdate event
     * @param e DocumentEvent
     */
    public void changedUpdate(DocumentEvent e) {
        // no action. It appears that adding an annotation causes the event to be fired
    }
    
    private void processEvent(DocumentEvent e) {
        needsProcessing = true;
        timeOfLastEvent = System.currentTimeMillis();
        setDirtyStatus();
    }
    
    public void stop() {
        stop = true;
        tm.remove(myTitle);
        myTitle = null;
        myDoc = null;
    }
    
    public void run() {
        try {
            while (!stop) {
                int delayTime = SmartFrogSvcUtil.getSFQuietTime()*1000;
                Thread.sleep(WAITTIME); // default is one second delay but can be adjusted
                if (needsProcessing && (System.currentTimeMillis() - timeOfLastEvent > delayTime ) ) {
                    setProcessingStatus();
                    needsProcessing = false;
                    String txt;
                    try {
                        txt = myDoc.getText(0, myDoc.getLength());
                    } catch (BadLocationException ex) {
                        txt = "";
                        ex.printStackTrace();
                    }
                    ByteArrayInputStream bs=null;
                    try {
                        bs = new ByteArrayInputStream(txt.getBytes("utf8"));
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                    
                    if (bs != null) {
                        try {
                            Iterator<CompileErrorAnnotation> iter = prevList.iterator();
                            while (iter.hasNext()) {
                                CompileErrorAnnotation cea = iter.next();
                                NbDocument.removeAnnotation(myDoc,cea);
                            }
                            
                            String furls = SmartFrogSvcUtil.getUrlCodebase();
                            Phases phases = new SFParser().sfParse(bs,furls);
                            Vector thePhases = phases.sfGetPhases();
                            for (Enumeration e = thePhases.elements(); e.hasMoreElements();) {
                                phases = phases.sfResolvePhase((String) e.nextElement());
                                
                            }
                            setOkStatus();
                        }
                        
                        catch (SmartFrogParseException ex) {
                            String fs = ex.getMessage();
                            int idx = fs.indexOf("line");
                            if (idx >= 0) {
                                idx += 5; // offsetpase "line "
                                int idx2 = idx + 1;
                                while (idx2 < fs.length() && !Character.isWhitespace(fs.charAt(idx2))) {
                                    idx2++;
                                }
                                idx2--; // move back off the trailing "," character
                                if (idx2 < fs.length()) {
                                    String lineNumber = fs.substring(idx,idx2);
                                    try {
                                        int ln = Integer.parseInt(lineNumber);
                                        int offset = NbDocument.findLineOffset(myDoc,ln-1);
                                        Position pos = NbDocument.createPosition(myDoc,offset,null);
                                        CompileErrorAnnotation annotation = new CompileErrorAnnotation(ex.getMessage());
                                        NbDocument.addAnnotation(myDoc,pos,-1, annotation);
                                        prevList.add(annotation);
                                        //NbDocument.markError(myDoc,ln);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    
                                }
                            } else { // no line number in exception error message. first check for include exception. If none, place onto last line
                                idx = fs.indexOf("Parsing include file");
                                if (idx > 0) {
                                    idx = idx + "Parsing include file".length() + 1;
                                    int idx2 = idx;
                                    while (!Character.isWhitespace(fs.charAt(idx2)) && idx2 < fs.length()) {
                                        idx2++;
                                    }
                                    String incFile = fs.substring(idx,idx2);
                                    try {
                                        int idx3 = myDoc.getText(0,myDoc.getLength()).indexOf(incFile);
                                        Position pos = NbDocument.createPosition(myDoc,idx3,null);
                                        CompileErrorAnnotation annotation = new CompileErrorAnnotation(ex.getMessage());
                                        NbDocument.addAnnotation(myDoc,pos,-1, annotation);
                                        prevList.add(annotation);
                                        //NbDocument.markError(myDoc,ln);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    int len = myDoc.getLength();
                                    Position pos;
                                    try {
                                        pos = NbDocument.createPosition(myDoc, len, null);
                                        CompileErrorAnnotation annotation = new CompileErrorAnnotation(ex.getMessage());
                                        NbDocument.addAnnotation(myDoc,pos,-1, annotation);
                                        prevList.add(annotation);
                                    } catch (BadLocationException ex2) {
                                        ex2.printStackTrace();
                                    }
                                }
                            }
                            setOkStatus();
                            ex.printStackTrace();
                        } catch (SmartFrogException fe) {
                            int len = myDoc.getLength();
                            Position pos;
                            try {
                                pos = NbDocument.createPosition(myDoc, len, null);
                                CompileErrorAnnotation annotation = new CompileErrorAnnotation(fe.toString());
                                NbDocument.addAnnotation(myDoc,pos,-1, annotation);
                                prevList.add(annotation);
                            } catch (BadLocationException ex2) {
                                ex2.printStackTrace();
                            }
                            setOkStatus();
                        }
                    }
                }
            }
            
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
    }
    
}
