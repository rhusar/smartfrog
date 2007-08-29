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

package org.smartfrog.nbm.options;

import java.util.MissingResourceException;
import org.netbeans.modules.editor.options.BaseOptions;
import org.smartfrog.nbm.SmartFrogEditorKit;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

public class SmartFrogOptions extends BaseOptions {
    
    public static String SMARTFROG = "SmartFrog"; // NOI18N
    
    /** Name of property. */
    private static final String HELP_ID = "editing.editor.sf"; // NOI18N
    
    //no manifest specific options at this time
    static final String[] SF_PROP_NAMES = new String[] {};
    
    public SmartFrogOptions() {
        super(SmartFrogEditorKit.class, SMARTFROG);
    }
    
    /**
     * Gets the help ID
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(HELP_ID);
    }
    
    /**
     * Look up a resource bundle message, if it is not found locally defer to
     * the super implementation
     */
    protected String getString(String key) {
        try {
            return NbBundle.getMessage(SmartFrogOptions.class, key);
        } catch (MissingResourceException e) {
            return super.getString(key);
        }
    }
    
}