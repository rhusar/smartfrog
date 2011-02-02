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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;
import org.openide.ErrorManager;
import org.openide.util.Utilities;


public class IncludeCompletionItem implements CompletionItem {
    
    private static Color fieldColor = Color.decode("0x0000B2");
    private static ImageIcon fieldIcon = null;
    private ImageIcon _icon;
    private int _type;
    private int _carretOffset;
    private int _dotOffset;
    private String _text;
    
    /** Creates a new instance of IncludeCompletionItem */
    public IncludeCompletionItem(String text, int dotOffset, int carretOffset) {
        _text = text;
        _dotOffset = dotOffset;
        _carretOffset = carretOffset;
        
        if (fieldIcon == null) {
            fieldIcon = new ImageIcon(Utilities.loadImage("org/smartfrog/nbm/frog16x16.gif"));
        }
        
        _icon = fieldIcon;
    }
    
    public void processKeyEvent(KeyEvent keyEvent) {
    //    char test = keyEvent.getKeyChar();
    }
    
    public int getPreferredWidth(Graphics graphics, Font font) {
        return CompletionUtilities.getPreferredWidth(_text,null, graphics, font);
    }
    
    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(_icon, _text, null, g, defaultFont,
                (selected ? Color.white : fieldColor), width, height, selected);
    }
    
    public CompletionTask createDocumentationTask() {
        return null;
    }
    
    public CompletionTask createToolTipTask() {
        return null;
    }
    
    public boolean instantSubstitution(JTextComponent jTextComponent) {
        return false;
    }
    
    public int getSortPriority() {
        return 0;
    }
    
    public CharSequence getSortText() {
        return _text;
    }
    
    public CharSequence getInsertPrefix() {
        return _text;
    }
    
    private void doSubstitute(final JTextComponent component, final String toAdd, final int backOffset) {
        
        final StyledDocument doc = (StyledDocument)component.getDocument();  
        class AtomicChange implements Runnable {
            
            public void run() {
                
                int caretOffset = component.getCaretPosition();
                String value = _text;
                if (toAdd != null) {
                    value += toAdd;
                }
                
                try {
                    doc.insertString(caretOffset, value, null);
                    component.setCaretPosition(component.getCaretPosition());
                } catch (BadLocationException e) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
                }
                
            }
            
        }
        Thread tr = new Thread(new AtomicChange());
        tr.run();
    }
    
    public void defaultAction(JTextComponent jTextComponent) {
        Completion.get().hideAll();
        doSubstitute(jTextComponent, null, 0);
    }
    
}
