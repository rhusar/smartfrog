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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.rmi.RemoteException;

/**
 *
 * List utility functions
 *
 */

public final class ListUtils {
    public static final String EMPTY_LIST_ELEMENT = "Empty list element";
    public static final String ERROR_WRONG_WIDTH = "Wrong number of list elements in sublist ";
    public static final String ERROR_NOT_A_LIST = "not a list: ";
    private static final String ERROR_WRONG_SIZE = " is the wrong size";


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
    public static Vector<String> join(List<?> source, String prefix, String joiner, String suffix,
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
            List<?> subvector = (List<?>) element;
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
        Iterator<?> uris = source.iterator();
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
    public static Properties convertToProperties(List<?> tupleList) throws SmartFrogResolutionException {
        Properties properties = new Properties();
        if (tupleList != null) {
            for (Object element : tupleList) {
                if (!(element instanceof Vector)) {
                    throw new SmartFrogResolutionException(ERROR_NOT_A_LIST + element);
                }
                Vector<?> entry = (Vector<?>) element;
                if (entry.size() != 2) {
                    throw new SmartFrogResolutionException(ERROR_WRONG_SIZE + entry);
                }
                String name = entry.get(0).toString();
                String value = entry.get(1).toString();
                properties.setProperty(name, value);
            }
        }
        return properties;
    }

    /**
     * Extract a string list; everything is
     * converted to strings in the process
     *
     * @param component component to resolve against
     * @param ref       a reference
     * @param required  whether the element is required or not
     *
     * @return the list, or null if none was provided
     *
     * @throws SmartFrogResolutionException if the resolution fails.
     * @throws RemoteException network problems
     */
    public static Vector<String> resolveStringList(Prim component,
                                                                Reference ref,
                                                                boolean required)
            throws SmartFrogResolutionException, RemoteException {
        Vector<?> vector = null;
        vector = component.sfResolve(ref, vector, required);
        if (vector == null) {
            return null;
        }
        Vector<String> result = new Vector<String>(vector.size());
        for (Object element : vector) {
            result.add(element.toString());
        }
        return result;
    }

    /**
     * Extract a string tuple list; verify the depth is 2.
     * Everything is converted to strings in the process
     * @param component component to resolve against
     * @param ref a reference
     * @param width width; use a negative number for any
     * @param required whether the element is required or not
     * @return the tuple list, or null if none was provided
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple, or the resolution
     * otherwise fails.
     * @throws RemoteException network problems
     */
    @SuppressWarnings("unchecked")
    public static Vector<Vector<?>> resolveNTupleList(Prim component, Reference ref, int width, boolean required)
            throws SmartFrogResolutionException, RemoteException {
        Vector tupleList = null;
        tupleList = component.sfResolve(ref, tupleList, required);
        if (tupleList == null) {
            return null;
        }
        int count = 0;
        for (Object element : tupleList) {
            if (!(element instanceof Vector)) {
                throw new SmartFrogResolutionException(
                        ref, null,
                        "Element [" + count + "] is " + ERROR_NOT_A_LIST + element,
                        element,
                        null,
                        component);
            }
            Vector<?> entry = (Vector<?>) element;
            if (width>=0 && entry.size() != width) {
                throw new SmartFrogResolutionException(
                        ref, null,
                        "Element [" + count + "] is " + ERROR_WRONG_SIZE +
                                " (expected " + width + " but got " + entry.size() + ") "
                                + entry,
                        entry,
                        null,
                        component);
            }
        }
        return tupleList;
    }


    /**
     * Extract a string tuple list; verify the depth is 2. Everything is converted to strings in the process
     *
     * @param component component to resolve against
     * @param ref       a reference
     * @param width width of the n-tuple; use a negative number for 'any'
     * @param required  whether the element is required or not
     * @return the tuple list, or null if none was provided
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple, or the resolution otherwise
     *                                      fails.
     * @throws RemoteException              network problems
     */
    public static Vector<Vector<String>> resolveStringNTupleList(Prim component, Reference ref, int width, boolean required)
            throws SmartFrogResolutionException, RemoteException {
        Vector<Vector<?>> tupleList = resolveNTupleList(component, ref, width, required);
        if (tupleList == null) {
            return null;
        }
        Vector<Vector<String>> result = new Vector<Vector<String>>(tupleList.size());
        for (Vector<?> tuple : tupleList) {
            Vector<String> row=new Vector<String>(tuple.size());
            for(Object field:tuple) {
                row.add(field.toString());
            }
            result.add(row);
        }
        return result;
    }

    /**
     * Turn a table into a string that can be logged
     * @param table table to print out
     * @return a string form
     */
    public static String tuplesToString(Vector<Vector<String>> table) {
        StringBuilder builder=new StringBuilder();
        builder.append("[\n");
        for(Vector<String> row:table) {
            builder.append(" [");
            boolean first=true;
            for(String column:row) {
                if(first) {
                    first=false;
                } else {
                    builder.append(',');
                }
                builder.append('\"');
                builder.append(column);
                builder.append('\"');
            }
            builder.append("]\n");
        }
        builder.append("]\n");
        return builder.toString();
    }

    /**
     * Extract a string tuple list; verify the depth is 2. Everything is converted to strings in the process
     *
     * @param component component to resolve against
     * @param ref       a reference
     * @param required  whether the element is required or not
     * @return the tuple list, or null if none was provided
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple
     * @throws RemoteException network problems
     */
    public static Vector<Vector<String>> resolveStringTupleList(Prim component, Reference ref, boolean required)
            throws SmartFrogResolutionException, RemoteException {
        return resolveStringNTupleList(component, ref, 2,required);
    }

    /**
     * Resolve a list to a properties instance
     * @param component component to resolve against
     * @param ref       a reference
     * @param required  whether the element is required or not
     * @return the newly resolved properties
     * @throws SmartFrogResolutionException if one of the list entries is not a tuple
     * @throws RemoteException network problems
     */
    public static Properties resolveProperties(Prim component, Reference ref, boolean required)
            throws SmartFrogResolutionException, RemoteException {
        Vector<Vector<String>> tuples = resolveStringNTupleList(component, ref, 2, required);
        return convertToProperties(tuples);
    }


    /**
     * Convert the properties to to a string tuple list
     *
     * @param properties the properties to work on
     * @return the properties as a list of [name,value] tuples, sorted on name
     */
    @SuppressWarnings("unchecked")
    public static Vector<Vector<String>> propertiesToTuples(Properties properties) {
        Vector<Vector<String>> propertyTupleList = new Vector<Vector<String>>(properties.size());
        List keyList = new ArrayList(properties.keySet());
        Collections.sort(keyList);
        for (Object keyO : keyList) {
            String key = keyO.toString();
            String value = properties.getProperty(key);
            Vector<String> tuple = tuple(key, value);
            propertyTupleList.add(tuple);
        }
        return propertyTupleList;
    }

    /**
     * Turn a pair of entries into a string tuple
     * @param left left entry of the pair
     * @param right right entry of the pair
     * @return a vector of the two values
     */
    public static Vector<String> tuple(String left, String right) {
        Vector<String> tuple = new Vector<String>(2);
        tuple.add(left);
        tuple.add(right);
        return tuple;
    }

    public static String left(Vector<String> tuple) {
        return tuple.get(0);
    }

    public static String right(Vector<String> tuple) {
        return tuple.get(1);
    }

    /**
     * flatten a string list, validating type as we go. recurses as much as we
     * need to. At its most efficient if no flattening is needed.
     *
     * @param src source list
     * @param listName name of the list, for reporting errors
     * @return a flatter list
     * @throws SmartFrogInitException if there is an element that is not of the right type
     */
    public static List<String> flattenStringList(List<?> src, String listName)
            throws SmartFrogInitException {
        if (src == null) {
            return new ArrayList<String>(0);
        }
        List<String> dest = new ArrayList<String>(src.size());
        for(Object element:src) {
            if (element instanceof List) {
                List<String> l2 = flattenStringList((List<?>) element, listName);
                for (String s:l2) {
                    dest.add(s);
                }
            } else if (!(element instanceof String)) {
                throw new SmartFrogInitException("An element in "
                        +
                        listName +
                        " is not string or a list: " +
                        element.toString() + " class=" + element.getClass());
            } else {
                dest.add((String) element);
            }
        }
        return dest;
    }
}
