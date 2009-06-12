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
package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.utils.ListUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

/**
 * utility operations for this package
 * created Sep 30, 2004 3:25:12 PM
 */

public final class RunJavaUtils {
    private RunJavaUtils() {
    }

    /**
     * recursive flattening of the incoming collection.
     * self-referential objects will loop forever.
     * @param in something to iterate over; null pointers return null results
     * @return a vector containing everything in the collection, apart from
     * collection objects, which are themselves flattened.
     */
    public static Vector recursivelyFlatten(Collection in) {
        if(in==null) {
            return null;
        }
        Vector flat=new Vector(in.size());
        for (Object o : in) {
            if (o instanceof Collection) {
                Collection c = (Collection) o;
                Vector v = recursivelyFlatten(c);
                flat.addAll(v);
            } else {
                flat.add(o);
            }
        }
        return flat;
    }

    /**
     * eliminate all duplicate entries from a vector.
     * uses a hash table, O(n*(O(hashtable add)+O(hashtable lookup))
     * @param source source collection
     * @return Vector
     */
    public static Vector mergeDuplicates(Collection source) {
        HashMap map=new HashMap(source.size());
        Vector dest=new Vector(source.size());
        for (Object o : source) {
            if (map.get(o) == null) {
                map.put(o, o);
                dest.add(o);
            } else {
                //duplicate item; remove it

            }
        }
        return dest;
    }

    /**
     * turn a vector of strings into a space separated list
     * @param source source vector
     * @return String string list.
     */
    public static String makeSpaceSeparatedString(Vector source) {
        //the classpath; space separated values
        return ListUtils.stringify(source, "", " ", " ");
    }

    /**
     * convert a classname to a resource
     * @param classname class name to turn into a resource
     * @return resource the classname as a resource
     */
    public static String makeResource(String classname) {
        assert (classname!=null && classname.length()>0);
        //add seven,
        StringBuffer buffer=new StringBuffer(classname.length()+"/.class".length());
        buffer.append('/');
        for(int i=0;i<classname.length();i++) {
            char c=classname.charAt(i);
            if(c=='.') {
                c='/';
            }
            buffer.append(c);
        }
        buffer.append(".class");
        return buffer.toString();
    }



}
