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
import java.util.Map;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

public class SmartFrogSettingsInitializer 
        extends Settings.AbstractInitializer {
    
    public static final String NAME = 
            "smartfrog-settings-initializer"; // NOI18N
    
    /**
     * Constructor
     */
    public SmartFrogSettingsInitializer() {
        super(NAME);
    }
    
    /**
     * Update map filled with the settings.
     * @param kitClass kit class for which the settings are being updated.
     *   It is always non-null value.
     * @param settingsMap map holding [setting-name, setting-value] pairs.
     *   The map can be empty if this is the first initializer
     *   that updates it or if no previous initializers updated it.
     */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {
        if (kitClass == BaseKit.class) {
            new SmartFrogTokenColoringInitializer().
                    updateSettingsMap(kitClass, settingsMap);
        }
        
        if (kitClass == SmartFrogEditorKit.class) {
            SettingsUtil.updateListSetting(
                    settingsMap,
                    SettingsNames.TOKEN_CONTEXT_LIST,
                    new TokenContext[] 
            { SmartFrogTokenContext.context }
            );
        }
    }
    
    /**
     * Class for adding syntax coloring to the editor
     */
    /** Properties token coloring initializer. */
    private static class SmartFrogTokenColoringInitializer 
            extends SettingsUtil.TokenColoringInitializer {
        
        /** Bold font. */
        private static final Font boldFont = 
                SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        /** Italic font. */
        private static final Font italicFont = 
                SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
        
        /** Key coloring. */
        
        /* possible tokens 
         * INCLUDE
         * INCLUDEVALUE
         * ATTRIBUTENAME
         * ATTRIBUTEVALUE 
         * EXTENDS
         * BASE 
         * OPEN_BRACKET
         * CLOSE_BRACKET 
         * SEMI_COLON
         * END_OF_LINE
         * COMMENT
         */
        
        private static final Coloring includeColoring = 
                new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE, 
                Color.green, null);
        
        private static final Coloring includeValueColoring = 
                new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE, 
                Color.green, null);
       
        private static final Coloring attributeNameColoring = 
                new Coloring(boldFont, Color.magenta, null);
        
        private static final Coloring attributeValueColoring = 
                new Coloring(null, Color.DARK_GRAY, null);
       
        private static final Coloring extendsColoring =
                new Coloring(boldFont, Color.BLUE, null);
        
        private static final Coloring lazyColoring =
                new Coloring(boldFont, Color.BLUE, null);
        
        private static final Coloring baseColoring = 
                new Coloring(boldFont, Color.DARK_GRAY, null);
        
        private static final Coloring commentColoring = 
                new Coloring(null, Color.LIGHT_GRAY, null);
        
        private static final Coloring semicolonColoring =
                new Coloring(null, Color.GREEN, null);
        
         private static final Coloring bracketColoring =
                new Coloring(boldFont, Color.GREEN, null);
        
        private static final Coloring emptyColoring = 
                new Coloring(null, null, null);
        
        private static final Coloring keywordColoring =
                new Coloring(boldFont,Color.BLUE, null);
        
        private static final Coloring stringColoring =
                new Coloring(boldFont, Color.MAGENTA, null);
        
        private static final Coloring operatorColoring =
                new Coloring(boldFont, Color.BLUE, null);
        
        private static final Coloring tbdColoring =
                new Coloring(boldFont, Color.RED, null);
        
        /** Constructs PropertiesTokenColoringInitializer. */
        public SmartFrogTokenColoringInitializer() {
            super(SmartFrogTokenContext.context);
        }
        
        
        /** Gets token coloring. */
        public Object getTokenColoring(TokenContextPath tokenContextPath,
                TokenCategory tokenIDOrCategory, boolean printingSet) {
            
            if(!printingSet ) {
                int tokenID = tokenIDOrCategory.getNumericID();
                if(tokenID == SmartFrogTokenContext.INCLUDE_ID) {
                    return includeColoring;
                } else if(tokenID == SmartFrogTokenContext.IVALUE_ID) {
                    return includeValueColoring;
                } else if(tokenID == SmartFrogTokenContext.ATTRIBUTE_NAME_ID) {
                    return attributeNameColoring;
                } else if(tokenID == SmartFrogTokenContext.ATTRIBUTE_VALUE_ID) {
                    return attributeValueColoring;
                } else if(tokenID == SmartFrogTokenContext.EXTENDS_ID) {
                    return extendsColoring;
                } else if(tokenID == SmartFrogTokenContext.BASE_PROTOTYPE_ID) {
                    return baseColoring;
                } else if(tokenID == SmartFrogTokenContext.END_OF_LINE_ID) {
                    return emptyColoring;
                } else if(tokenID == SmartFrogTokenContext.OPEN_BRACKET_ID) {
                    return bracketColoring;
                } else if(tokenID == SmartFrogTokenContext.CLOSE_BRACKET_ID) {
                    return bracketColoring;
                } else if(tokenID == SmartFrogTokenContext.SEMI_COLON_ID) {
                    return semicolonColoring;
                } else if (tokenID == SmartFrogTokenContext.COMMENT_ID) {
                    return commentColoring;
                } else if (tokenID == SmartFrogTokenContext.LAZY_ID) {
                    return lazyColoring;
                } else if (tokenID == SmartFrogTokenContext.KEYWORD_ID) {
                    return keywordColoring;
                } else if (tokenID == SmartFrogTokenContext.STRING_ID) {
                    return stringColoring;
                } else if (tokenID == SmartFrogTokenContext.OPERATOR_ID) {
                    return operatorColoring;
                } else if (tokenID == SmartFrogTokenContext.TBD_ID) {
                    return tbdColoring;
                }
            } else { // printing set
                return SettingsUtil.defaultPrintColoringEvaluator;
            }
            return null;
        }
        
    } // End of class ManifestTokenColoringInitializer.
    
}