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

package org.smartfrog.services.persistence.storage;

import java.util.HashSet;
import java.util.Set;

/**
 * TagString is a utility with methods to encode Tags as
 * strings and reconstruct them from a string. These string
 * encoded tags are much smaller when serialised then the
 * java serialization would be and are much faster to serialise/deserialise.
 */
public class TagsString {
    
    private static final int VARCHAR_SIZE = 254;
    
    public static String toString(Set set) {
        if( set.isEmpty() ) {
            return "";
        }
        StringBuffer strBuff = new StringBuffer(VARCHAR_SIZE);
        for( Object obj : set ) {
            strBuff.append((String)obj).append(",");
        }
        return strBuff.toString();
    }
    
    public static Set fromString(String str) {
        if( str.length() == 0 ) {
            return new HashSet();
        }
        String[] tags = str.split(",");
        Set set = new HashSet();
        for( String t : tags ) {
            set.add(t);
        }
        return set;
    }
    
    public static void main(String args[]) {
        System.out.println("Input: " + args[0]);
        System.out.println("Tags:");
        Set tset = fromString(args[0]);
        for(Object obj : tset) {
            System.out.println("   '" + obj.toString() + "'");
        }
        System.out.println("Reverted to string: " + toString(tset));
    }

}
