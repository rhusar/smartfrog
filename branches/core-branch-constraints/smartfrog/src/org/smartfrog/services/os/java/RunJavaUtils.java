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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * utility operations for this package
 * created Sep 30, 2004 3:25:12 PM
 */

public class RunJavaUtils {
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
        Iterator flattener=in.iterator();
        while (flattener.hasNext()) {
            Object o = (Object) flattener.next();
            if(o instanceof Collection) {
                Collection c=(Collection)o;
                Vector v=recursivelyFlatten(c);
                flat.addAll(v);
            } else {
                flat.add(o);
            }
        }
        return flat;
    }

    /**
     * split a list of string values into separate elements of a vector.
     * dumb simple parsing
     * @param uriList
     * @return the string cracked; may be empty for no interesting content
     */
    public static Vector crack(final String uriList) {
        Vector list=new Vector();
        if(uriList==null) {
            return list;
        }
        StringBuffer buffer;
        buffer=new StringBuffer(uriList.length());
        for(int index=0;index<uriList.length();index++) {
            char c=uriList.charAt(index);
            if(!Character.isWhitespace(c)) {
                //common case, just another character
                buffer.append(c);
            } else {
                //end of line, extra spaces at beginning or end, etc.
                if(buffer.length()==0) {
                    //nothing in the buffer yet, this is leading
                    //whitespace, so ignore it
                    continue;
                }
                //make a uri from the buffer
                String uri=buffer.toString();
                //append to the list
                list.add(uri);
                buffer = new StringBuffer(uriList.length());
            }
        }
        //and the end of the run, if we have a non zero list, add that
        if(buffer.length()!=0) {
            list.add(buffer.toString());
        }
        return list;
    }

    /**
     * eliminate all duplicate entries from a vector.
     * uses a hash table, O(n*(O(hashtable add)+O(hashtable lookup))
     * @param source
     * @return Vector
     */
    public static Vector mergeDuplicates(Collection source) {
        HashMap map=new HashMap(source.size());
        Vector dest=new Vector(source.size());
        Iterator it=source.iterator();
        while (it.hasNext()) {
            Object o = (Object) it.next();
            if(map.get(o)==null) {
                map.put(o,o);
                dest.add(o);
            } else {
                //duplicate item; remove it

            }
        }
        return dest;
    }

    /**
     * turn a vector of strings into a space separated list
     * @param source
     * @return String
     */
    public static String makeSpaceSeparatedString(Vector source) {
        //the classpath; space separated values
        StringBuffer buffer = new StringBuffer();
        Iterator uris = source.iterator();
        while (uris.hasNext()) {
            String uri = (String) uris.next();
            buffer.append(uri);
            if(uris.hasNext()) {
                buffer.append(' ');
            }
        }
        return buffer.toString();
    }

    /**
     * convert a classname to a resource
     * @param classname
     * @return resource
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
