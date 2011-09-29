
package org.smartfrog.authoringtool;


import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.gems.designer.GemsEditor;
import org.gems.designer.GemsPlugin;

/**
 * @author Jules White
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DSMLEditor extends GemsEditor {

    static {
        SmartfrogPlugin.getDefault().getPreferenceStore().setDefault(
           PALETTE_SIZE, DEFAULT_PALETTE_SIZE);
    }
    /**
     * 
     */
    public DSMLEditor() {
        super();
        
    }
    
    protected String getModelID() {
   	 	return SmartfrogProvider.MODEL_ID;
	}
    
    protected PaletteRoot getPaletteRoot() {
        if( root == null ){
            root = SmartfrogProvider.getInstance().getPaletteProvider().createPalette();
        }
        return root;
    }
    
    protected FlyoutPreferences getPalettePreferences() {
        return new FlyoutPreferences() {
            public int getDockLocation() {
                return SmartfrogPlugin.getDefault().getPreferenceStore()
                      .getInt(PALETTE_DOCK_LOCATION);
                
            }
            public int getPaletteState() {
                return SmartfrogPlugin.getDefault().getPreferenceStore().getInt(PALETTE_STATE);
            }
            public int getPaletteWidth() {
                return DEFAULT_PALETTE_SIZE;
            }
            public void setDockLocation(int location) {
              SmartfrogPlugin.getDefault().getPreferenceStore()
                      .setValue(PALETTE_DOCK_LOCATION, location);
            }
            public void setPaletteState(int state) {
              SmartfrogPlugin.getDefault().getPreferenceStore()
                      .setValue(PALETTE_STATE, state);
            }
            public void setPaletteWidth(int width) {
              SmartfrogPlugin.getDefault().getPreferenceStore()
                      .setValue(PALETTE_SIZE, width);
            }
        };
    }
    
     protected ClassLoader getClassLoaderForSerialization() {
        return getClass().getClassLoader();
    }
    

}
