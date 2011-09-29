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

package org.smartfrog.nbm.documents;

import java.util.TreeMap;
import javax.swing.text.Document;
import org.netbeans.spi.editor.errorstripe.UpToDateStatus;
import org.netbeans.spi.editor.errorstripe.UpToDateStatusProvider;

public class SFUpToDateStatusProvider extends UpToDateStatusProvider {
    private Document myDoc = null;
    private UpToDateStatus myStatus = UpToDateStatus.UP_TO_DATE_OK;
    
    private static TreeMap<String,SFUpToDateStatusProvider> tm = new TreeMap<String,SFUpToDateStatusProvider>();
    
    /** Creates a new instance of SFUpToDateStatusProvider */
    private SFUpToDateStatusProvider(Document doc) {
        myDoc = doc;
    }

    /**
     * 
     * @return 
     */
    public UpToDateStatus getUpToDate() {
        return myStatus;
    }
    
    /**
     * 
     * @param u 
     */
    public void setStatus(UpToDateStatus u) {
        myStatus = u;
    }
    
    /**
     * 
     * @param doc 
     * @return 
     */
    public static SFUpToDateStatusProvider getInstance(Document doc) {
        String title = (String) doc.getProperty(Document.TitleProperty);
        if (tm.containsKey(title)) {
            return tm.get(title);
        } else {
            SFUpToDateStatusProvider p = new SFUpToDateStatusProvider(doc);
            tm.put(title,p);
            return p;
        }
    }
    
}
