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

package org.smartfrog.nbm.info;

import java.util.ArrayList;

public class SfFileInfo {
    
    private ArrayList<String> dependentIncludes;
    private ArrayList<String> components;
    private String name;
    
    /** Creates a new instance of SfFileInfo */
    public SfFileInfo(String name) {
        this.name = name;
        dependentIncludes = new ArrayList<String>();
        components = new ArrayList<String>();
    }
    
    public void addDependentInclude(String s) {
        dependentIncludes.add(s);
    }
    
    public void removeDepdenentInclude(String s) {
        dependentIncludes.remove(s);
    }
    
    public ArrayList<String> getDependentIncludes() {
        return dependentIncludes;
    }
    
    public void addComponent(String s) {
        components.add(s);
    }
    
    public void removeComponent(String s) {
        components.remove(s);
    }
    
    public ArrayList<String> getComponents() {
        return components;
    }
    
    public String getName() {
        return name;
    }
    
}
