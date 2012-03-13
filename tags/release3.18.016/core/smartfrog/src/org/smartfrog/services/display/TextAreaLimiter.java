/** (C) Copyright Hewlett-Packard Development Company, LP

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

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * Observes the size of a Document of a JTextArea and removes lines
 * from the start of the JTextArea as the document grows in length.
 * 
 * The document is allowed to grow to a number of lines that is 10% greater than 
 * a specified desired number, at which point excess lines are removed from the 
 * start of the document to achieve the desired number of lines. 
 *
 */
public class TextAreaLimiter implements DocumentListener, Runnable {
    
    /**
     * The observed text area.
     */
    private final JTextArea jtArea;
    
    /**
     * Desired number of displayed lines.
     */
    private int numLines;
    
    /**
     * Restricts the number of display lines to a sensible minimum value. 
     */
    private static final int MIN_NUM_LINES = 10;
    
    /**
     * Construct an instance that will observe the JTextArea.
     * 
     * @param area JTextArea that will be observed.
     * @param numLines Desired number of lines that text area will be limited to displaying.
     */
    public TextAreaLimiter(final JTextArea area, final int numLines) {
        this.jtArea = area;
        this.numLines = numLines;
        if (this.numLines < MIN_NUM_LINES) {
            this.numLines = MIN_NUM_LINES;
        }
        area.getDocument().addDocumentListener(this);
    }

    /**
     * Implements Runnable interface.
     */
    public void run() {
        int lineCount = jtArea.getLineCount();
        int excess = lineCount - numLines;
        if(excess > numLines/10) {
            try {
                int start = jtArea.getLineStartOffset(0);
                int end = jtArea.getLineEndOffset(excess);
                jtArea.replaceRange(null, start, end);
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    }        

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        // no action
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(this);
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        // no action
    }

    /**
     * Detach this instance from observing the text area.
     */
    public void detach() {
        jtArea.getDocument().removeDocumentListener(this);
    }
    
}
