
/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.tools.eclipse.ui.editor.sf2;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import java.util.*;


/**
 * Manager for colors used in the smartFrog editor to be reused
 */
public class DescriptionColorProvider
{
    public static final RGB KEYWORDS_1_COLOR = new RGB(0, 0, 128);
    public static final RGB KEYWORDS_2_COLOR = new RGB(128, 0, 64);
    public static final RGB KEYWORDS_3_COLOR = new RGB(0, 128, 64);
    public static final RGB LITERAL2_COLOR = new RGB(64, 0, 128);
    public static final RGB SF_COLOR = new RGB(64, 0, 128);
    public static final RGB LABELS_COLOR = new RGB(128, 0, 0);
    public static final RGB BACKGROUND_COLOR = new RGB(255, 255, 255);
    public static final RGB SINGLE_LINE_COMMENT = new RGB(0, 128, 64);
    public static final RGB STRING = new RGB(128, 0, 255);

    public static final RGB MULTI_LINE_COMMENT = new RGB(128, 0, 0);
    public static final RGB DEFAULT = new RGB(0, 0, 0);

    protected Map mColorTable = new HashMap(11);
    private static DescriptionColorProvider INSTANCE;

    public static DescriptionColorProvider getInstance()
    {
        if (null == INSTANCE) {
            INSTANCE = new DescriptionColorProvider();
        }

        return INSTANCE;
    }

    /**
     * Release all of the color resources held onto by the receiver.
     */
    private void dispose()
    {
        Iterator e = mColorTable.values().iterator();

        while (e.hasNext()) {
            ( (Color)e.next() ).dispose();
        }
    }

    /**
     * Return the Color that is stored in the Color table as rgb.
     */
    public Color getColor(RGB rgb)
    {
        Color color = (Color)mColorTable.get(rgb);

        if (color == null) {
            color = new Color(Display.getCurrent(), rgb);
            mColorTable.put(rgb, color);
        }

        return color;
    }
}
