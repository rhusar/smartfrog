/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
import java.util.Properties;
import java.rmi.RemoteException;

/**
 *
 * List utility functions
 *
 */

public final class ListUtils {
    public static final String EMPTY_LIST_ELEMENT = "Empty list element";
    public static final String ERROR_WRONG_WIDTH = "Wrong number of list elements in sublist ";
    public static final String ERROR_NOT_A_LIST = "Not a list: ";
    private static final String ERROR_WRONG_SIZE = "Property entry of the wrong size";


    private ListUtils() {
    }

    /**
     * take a vector of name value pairs like [["a",true],["b",3]] and create a list like
     * ["-Da=true","-Db=3"] with configurable prefix, joiner and suffix strings.
     * </p>
     * the return type is a vector
     *
     * @param source list source. All elements must contain a list, itself two elements long.
     * @param prefix optional prefix for every element
     * @param joiner optional string to use between each pair
     * @param suffix optional suffix to use at the end
     * @param skipEmptyElements should empty elements be skipped, or reported as an error
     * @return a merged array or null if an empty list came in.
     * @throws SmartFrogInitException if a list is the wrong width, type or missing
     */
    public static Vector<String> join(List source, String prefix, String joiner, String suffix,
        boolean skipEmptyElements) throws SmartFrogInitException {
        if (source == null) {
            return null;
        }
        if (prefix == null) {
            prefix = "";
        }
        if (joiner == null) {
            joiner = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        Vector<String> results = new Vector<String>(source.size());
        for (Object element : source) {
            if (!(element instanceof List)) {
                throw new SmartFrogInitException(ERROR_NOT_A_LIST + element);
            }
            List subvector = (List) element;
            int subsize = subvector.size();
            if (subsize == 0) {
                if(skipEmptyElements) {
                    //empty sublist; skip
                    continue;
                } else {
                    throw new SmartFrogInitException(EMPTY_LIST_ELEMENT);
                }

            }
            if (subsize != 2) {
                throw new SmartFrogInitException(ERROR_WRONG_WIDTH + subvector);
            }
            String key = subvector.get(0).toString();
            //take any value and stringify it -we dont care about its underlying type
            String value = subvector.get(1).toString();
            String entry = prefix + key + joiner + value + suffix;
            results.add(entry);
        }
        return results;
    }

    /**
     * split a list of string values into separate elements of a vector.
     * dumb simple parsing
     * @param uriList string list separated by whitespace
     * @return the string cracked; may be empty for no interesting content
     */
    public static Vector<String> crack(final String uriList) {
        Vector<String> list=new Vector<String>();
        if (uriList == null) {
            return list;
        }
        StringBuffer buffer;
        int length = uriList.length();
        buffer = new StringBuffer(length);
        for (int index = 0; index < length; index++) {
            char c = uriList.charAt(index);
            if (!Character.isWhitespace(c)) {
                //common case, just another character
                buffer.append(c);
            } else {
                //end of line, extra spaces at beginning or end, etc.
                if (buffer.length() == 0) {
                    //nothing in the buffer yet, this is leading
                    //whitespace, so ignore it
                    continue;
                }
                //make a uri from the buffer
                String uri = buffer.toString();
                //append to the list
                list.add(uri);
                buffer = new StringBuffer(length);
            }
        }
        //and the end of the run, if we have a non zero list, add that
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }
        return list;
    }

    /**
     *
     * turn a vector of strings into a space separated list
     * @param source source vector
     * @param prefix prefix to insert
     * @param separator separator string
     * @param ending ending string
     * @return String string list.
     */
    public static String stringify(Vector source, String prefix, String separator, String ending) {
        StringBuilder buffer = new StringBuilder();
        Iterator uris = source.iterator();
        while (uris.hasNext()) {
            buffer.append(prefix);
            String uri = uris.next().toString();
            buffer.append(uri);
            if(uris.hasNext()) {
                buffer.append(separator);
            } else {
                buffer.append(ending);
            }
        }
        return buffer.toString();
    }

    /**
     * Convert a property list into a Java properties class
     * @param tupleList a list of tuples (must not be null)
     * @return a properties object containing name,value pairs.
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple
     */
    public static Properties convertToProperties(List tupleList) throws SmartFrogResolutionException {
        Properties properties=new Properties();
        if(tupleList!=null) {
            for (Object element : tupleList) {
                if(!(element instanceof Vector)) {
                    throw new SmartFrogResolutionException(ERROR_NOT_A_LIST +element);
                }
                Vector entry = (Vector) element;
                if (entry.size() != 2) {
                    throw new SmartFrogResolutionException(ERROR_WRONG_SIZE + entry);
                }
                String name = entry.get(0).toString();
                String value = entry.get(1).toString();
                properties.setProperty(name,value);
            }
        }
        return properties;
    }

    /**
     * Extract a string tuple list; verify the depth is 2.
     * Everything is converted to strings in the process
     * @param component component to resolve against
     * @param ref a reference
     * @param required whether the element is required or not
     * @return the tuple list, or null if none was provided
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple, or the resolution
     * otherwise fails.
     * @throws RemoteException network problems
     */
    public static Vector<Vector<String>> resolveStringTupleList(Prim component, Reference ref,boolean required)
            throws SmartFrogResolutionException, RemoteException {
        Vector tupleList=null;
        tupleList=component.sfResolve(ref, tupleList,required);
        if(tupleList==null) {
            return null;
        }
        Vector<Vector<String>> result=new Vector<Vector<String>>(tupleList.size());
        for (Object element : tupleList) {
            if (!(element instanceof Vector)) {
                throw new SmartFrogResolutionException(ERROR_NOT_A_LIST + element);
            }
            Vector entry = (Vector) element;
            if (entry.size() != 2) {
                throw new SmartFrogResolutionException(ERROR_WRONG_SIZE + entry);
            }
            Vector<String> newEntry=new Vector<String>(2);
            String name = entry.get(0).toString();
            String value = entry.get(1).toString();
            newEntry.add(name);
            newEntry.add(value);
            result.add(newEntry);
        }
        return result;
    }
}
