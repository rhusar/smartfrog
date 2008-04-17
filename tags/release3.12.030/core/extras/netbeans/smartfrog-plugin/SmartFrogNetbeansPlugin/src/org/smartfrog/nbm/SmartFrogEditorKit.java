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
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
import org.netbeans.modules.editor.NbEditorKit;

public class SmartFrogEditorKit extends NbEditorKit {
    private Document doc = null;
    public static final String MIME_TYPE = "text/x-smartfrog"; // NOI18N
    
    /**
     * Creates a new instance of SmartFrogEditorKit
     */
    public SmartFrogEditorKit() {
    }
    
    /**
     * Create a syntax object suitable for highlighting Manifest file syntax
     */
    public Syntax createSyntax(Document doc) {
        this.doc = doc;
        return new SmartFrogSyntax();
    }
    
    protected void executeDeinstallActions(JEditorPane jEditorPane) {
        if (doc instanceof StyledDocument) {
            SFChangeListener sfc = SFChangeListener.getInstance((StyledDocument)doc);
            sfc.stop();
            doc.removeDocumentListener(sfc);
        }
    }
    
    protected void executeInstallActions(JEditorPane jEditorPane) {
        if (doc instanceof StyledDocument) {
            SFChangeListener sfc = SFChangeListener.getInstance((StyledDocument)doc);
            doc.addDocumentListener(sfc);
        }
    }
    
    
    /**
     * Retrieves the content type for this editor kit
     */
    public String getContentType() {
        return MIME_TYPE;
    }
    
    
}
