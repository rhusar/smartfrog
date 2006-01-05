/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.references;

/**
 * This is a normal step down the tree.
 */
public class StepDown extends Step {

    private String prefix;
    private String localname;

    public StepDown() {
    }

    public StepDown(String prefix, String localname) {
        this.prefix = prefix;
        this.localname = localname;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLocalname() {
        return localname;
    }

    public void setLocalname(String localname) {
        this.localname = localname;
    }

    /**
     * Returns a string representation of the object.
     * @return [prefix:]localname
     */
    public String toString() {
        StringBuffer result=new StringBuffer();
        if(prefix!=null && prefix.length()>0) {
            result.append(prefix);
            result.append(':');
        }
        result.append(localname);
        return result.toString();
    }
}
