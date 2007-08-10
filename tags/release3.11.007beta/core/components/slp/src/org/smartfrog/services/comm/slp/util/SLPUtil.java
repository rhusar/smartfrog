/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp.util;

import org.smartfrog.services.comm.slp.ServiceLocationAttribute;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Vector;

/** This class implements various utility methods used in the library. */
public class SLPUtil {
    /** A string of the reserved characters in scope lists */
    private static String scopeReserved = "(),\\!<=>~;*+";

    /**
     * Scans a string and replaces any reserved characters with their escaped value.
     *
     * @param str The string to scan.
     * @param res A string of reserved characters. I null, scopeReserved is used.
     * @return A string with any reserved characters escaped.
     */
    public static String escapeString(String str, String res) {
        String reserved;
        if (res == null) {
            reserved = scopeReserved;
        } else {
            reserved = res;
        }
        // start escaping...
        StringBuffer sb = new StringBuffer(str);
        //System.out.println("Escaping - " + sb);

        for (int i = 0; i < sb.length(); i++) {
            //System.out.println("i= " + i);
            char c = sb.charAt(i);
            int index = reserved.indexOf(c);
            if (index != -1) {
                //System.out.println("Found reserved: " + c);
                // check that we are not escaping an escape-sequence...
                boolean needToEscape = true;
                if (c == '\\') {
                    String number = sb.substring(i + 1, i + 3);
                    needToEscape = false;
                    try {
                        //System.out.println("Converting - " + number + " - To int");
                        char ch = (char) Integer.parseInt(number, 16);
                        //System.out.println("Found: " + ch);
                        if (reserved.indexOf(ch) == -1) {
                            needToEscape = true;
                        }
                    }
                    catch (NumberFormatException e) {
                        needToEscape = true;
                    }
                }
                if (needToEscape) {
                    String escVal = "\\" + Integer.toHexString((int) c);
                    //System.out.println("Replacing " + c + " with " + escVal);
                    //System.out.println(Integer.toHexString((int)c));
                    sb.replace(i, i + 1, escVal);
                    //System.out.println("String: " + sb);
                }
                i += 4; // skip escape value
            }
        }

        return sb.toString();
    }

    /**
     * Converts a Vector to a String-list.
     *
     * e.g. [1,2] => "1, 2"
     *
     * @param v Vector
     * @return String   String-list "1, 2"
     */
    public static String vectorToString(Vector v) {
        String s = v.toString();
        s = s.substring(1, s.length() - 1); // remove [ and ]
        return s;
    }

    /**
     * Extracts the attributes from the given String.
     *
     * @param attrString The string containing the attributes.
     * @return A vector of ServiceAttribute objects
     */
    public static Vector parseAttributes(String attrString) throws ServiceLocationException {
        Vector attributes = new Vector();
        String attr = "";
        String rest = attrString;
        int i;
        while (!rest.equals("")) {
            i = rest.indexOf(")");
            if (i != -1) attr = rest.substring(1, i);
            i = rest.indexOf("(", i);
            if (i != -1) {
                rest = rest.substring(i + 1);
            } else {
                rest = "";
            }

            // create the current attribute.
            String[] a = attr.split("=");
            if (a.length != 2) {
                //System.out.println("ERROR: Something strange happened when parsing the attribute");
                throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
            }
            String id = a[0];
            String vals = a[1];
            Vector values = new Vector();
            String[] v = vals.split(",");
            for (int k = 0; k < v.length; k++) {
                values.add(v[k]);
            }
            ServiceLocationAttribute srvAttr = new ServiceLocationAttribute(id, values);
            attributes.add(srvAttr);
        }
        return attributes;
    }

    /**
     * Converts a string-list into a Vector
     *
     * @param str The String to parse.
     * @throws ServiceLocationException if there is an error parsing the String.
     */
    public static Vector stringToVector(String str) throws ServiceLocationException {
        Vector toReturn = new Vector();
        if (!str.equals("")) {
            String s[] = str.split(",");
            for (int i = 0; i < s.length; i++) {
                toReturn.add(s[i]);
            }
        }

        return toReturn;
    }

    /**
     * Parses the given string and returns a Vector of attribute names (tags)
     *
     * @param tagStr The String to parse.
     * @return A Vector of the listed tags.
     * @throws ServiceLocationExceptionif there is a parse error.
     */
    public static Vector parseTags(String tagStr) throws ServiceLocationException {
        Vector toReturn = new Vector();
        if (!tagStr.equals("")) {
            String s[] = tagStr.split(",");
            for (int i = 0; i < s.length; i++) {
                toReturn.add(s[i]);
            }
        }
        return toReturn;
    }

    public static String createAttributeString(Vector attributes) {
        String attributeStr = "";
        Iterator iter = attributes.iterator();
        while (iter.hasNext()) {
            if (!attributeStr.equals("")) attributeStr += ",";
            ServiceLocationAttribute attr = (ServiceLocationAttribute) iter.next();
            // add id
            attributeStr += "(" + ServiceLocationAttribute.escapeId(attr.getId());
            // add values
            String valStr = "";
            for (Iterator valIter = attr.getValues().iterator(); valIter.hasNext();) {
                if (!valStr.equals("")) valStr += ",";
                valStr += ServiceLocationAttribute.escapeValue(valIter.next());
            }
            attributeStr += "=" + valStr + ")";
        }

        return attributeStr;
    }

    /** Finds the scopes that are in both s1 and s2. */
    public static Vector findCommonScopes(Vector s1, Vector s2) {
        Vector toReturn = new Vector();
        for (Iterator i = s1.iterator(); i.hasNext();) {
            Object o = i.next();
            if (s2.contains(o)) {
                toReturn.add(o);
            }
        }
        return toReturn;
    }

    // create a Datagram packet
    public static DatagramPacket createDatagram(SLPMessageHeader msg, InetAddress addr, int port) {
        // create stream
        SLPOutputStream sos = new SLPOutputStream(new ByteArrayOutputStream());
        // write message to stream
        try {
            msg.toOutputStream(sos);
        } catch (ServiceLocationException s) {
            return null;
        }
        //System.out.println("Create Datagram:\n" + msg.toString());
        //System.out.println("Real length: " + sos.getByteArray().length);
        return new DatagramPacket(sos.getByteArray(), msg.getLength(), addr, port);
    }

    /*
        Check that at least one of the scopes in s2 exists in s1.
        @param s1 The scopes we support
        @param s2 The scopes we want.
        @return true if at least one of the wanted scopes are supported.
    */
    public static boolean supportScopes(Vector s1, Vector s2) {
        Iterator iter = s2.iterator();
        while (iter.hasNext()) {
            if (s1.contains(iter.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Write message to log file.
     *
     * @param message  The message to write
     * @param filename The name of the logfile. If empty, message is written to stdout.
     */
    public static synchronized void writeLogFile(String message, String filename) {
        if (!filename.equals("")) {
            try {
                PrintWriter out = new PrintWriter(new FileOutputStream("filename", true));
                out.println(message);
            } catch (Exception e) {
                System.err.println("SLP -> Could not write to logfile");
            }
        } else {
            System.out.println(message);
        }
    }

    public static Vector mergeVectors(Vector v1, Vector v2) {
        Vector toReturn = new Vector();
        Iterator i1 = v1.iterator();
        Iterator i2 = v2.iterator();
        String s1 = i1.hasNext() ? (String) i1.next() : null;
        String s2 = i2.hasNext() ? (String) i2.next() : null;

        while (s1 != null && s2 != null) {
            int comp = s1.compareToIgnoreCase(s2);
            if (comp < 0) {
                toReturn.add(s1);
                s1 = i1.hasNext() ? (String) i1.next() : null;
            } else if (comp > 0) {
                toReturn.add(s2);
                s2 = i2.hasNext() ? (String) i2.next() : null;
            } else {
                toReturn.add(s1);
                s1 = i1.hasNext() ? (String) i1.next() : null;
                s2 = i2.hasNext() ? (String) i2.next() : null;
            }
        }
        // add remaining
        if (s1 != null) {
            toReturn.add(s1);
        }
        if (s2 != null) {
            toReturn.add(s2);
        }
        while (i1.hasNext()) {
            toReturn.add(i1.next());
        }
        while (i2.hasNext()) {
            toReturn.add(i2.next());
        }

        return toReturn;
    }
}
