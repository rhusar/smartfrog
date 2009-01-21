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

package org.smartfrog.examples.orchdws.gui.graphpanel;

/**
 * Ingerface defining the attribute names that are to be set as part of a Grpah
 * component.
 */
public interface Graph {
    public final String LOGTO = "logTo";
    public final String DATADISPLAYED = "dataDisplayed";
    public final String DISPLAY = "display";
    public final String MINX = "minX";
    public final String MAXX = "maxX";
    public final String STEPX = "stepX";
    public final String MINY = "minY";
    public final String MAXY = "maxY";
    public final String GRIDSTEPX = "gridStepX";
    public final String GRIDSTEPY = "gridStepY";
    public final String XOFFSET = "xOffset";
    public final String YOFFSET = "yOffset";
    public final String POSITIONDISPLAY = "positionDisplay";
    public final String FRAMETITLE = "frameTitle";
    public final String HISTOGRAM = "histogram";
    public final String ADJUST = "adjust";
    public final String KEYSALLOWED = "keysAllowed";
    public final String PENCILCOLOUR = "pencilColour";
    public final String PANELWIDTH = "panelWidth";
    public final String PANELHEIGHT = "panelHeight";
    public final String POLLINGPERIOD = "pollingPeriod";
    public final String DATASOURCE = "dataSource";
    public final String GRAPHPENCILWIDTH = "graphPencilWidth";
}
