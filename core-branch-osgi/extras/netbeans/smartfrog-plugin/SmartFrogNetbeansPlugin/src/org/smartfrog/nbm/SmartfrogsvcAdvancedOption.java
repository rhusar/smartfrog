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

package org.smartfrog.nbm;

import org.netbeans.spi.options.AdvancedOption;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;
import java.util.prefs.*;

public final class SmartfrogsvcAdvancedOption extends AdvancedOption {
    
    public static final String SFHOMEVAR = "SFHOME";
    public static final String SFUSERHOMEVAR = "SFUSERHOME";
    public static final String SFRESTRICTINCLUDE = "SFRESTRICTINCLUDE";
    public static final String SFQUIETTIME = "SFQUIETTIME";
    
    public String getDisplayName() {
        return NbBundle.getMessage(SmartfrogsvcAdvancedOption.class, "AdvancedOption_DisplayName");
    }
    
    public String getTooltip() {
        return NbBundle.getMessage(SmartfrogsvcAdvancedOption.class, "AdvancedOption_Tooltip");
    }
    
    public OptionsPanelController create() {
        return new SmartfrogsvcOptionsPanelController();
    }
    
    public static String getSFHome() {
        return Preferences.userNodeForPackage(SmartfrogsvcPanel.class).get(SmartfrogsvcAdvancedOption.SFHOMEVAR,"");
    }
    
    public static String getSFUserHome() {
        return Preferences.userNodeForPackage(SmartfrogsvcPanel.class).get(SmartfrogsvcAdvancedOption.SFUSERHOMEVAR,"");
    }
    
    public static boolean getRestrictInclude() {
        return Preferences.userNodeForPackage(SmartfrogsvcPanel.class).getBoolean(SmartfrogsvcAdvancedOption.SFRESTRICTINCLUDE,true);
    }
    
    public static int getQuietTime() {
        String myTime = Preferences.userNodeForPackage(SmartfrogsvcPanel.class).get(SmartfrogsvcAdvancedOption.SFQUIETTIME,"1");
        int res = 1;
        try {
            res = Integer.parseInt(myTime);
        } catch (NumberFormatException nfe) {
            res = 1;
        }
        return res;
    }
    
}
