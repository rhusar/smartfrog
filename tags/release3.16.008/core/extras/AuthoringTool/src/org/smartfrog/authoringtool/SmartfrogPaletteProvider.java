
package org.smartfrog.authoringtool;


import org.eclipse.gef.palette.PaletteRoot;
import org.gems.designer.AbstractPaletteProvider;
import org.gems.designer.ModelProvider;
import org.gems.designer.PaletteProvider;

/**
 * @author Jules White
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SmartfrogPaletteProvider extends AbstractPaletteProvider implements PaletteProvider{
    public PaletteRoot createPalette() {
        return super.createPalette();
    }
    public ModelProvider getModelProvider() {
        return SmartfrogProvider.getInstance();
    }
}
