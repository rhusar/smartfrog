package org.smartfrog.services.slp;
/**
 * Some useful utilities:             
 *   (1) parseInt     (2) parseLong    (3) wrireInt  (4) writeLong
 *   (5) parseString  (6) shareString  (7) url2dname (8) url2na
 *   (9) sameHost
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.util.*;
import java.net.*;

public class Util {

    //-------------------------------------------------------------------
    // get an integer/short/byte from a byte array,  starting from index 
    // ia[0], which moves forward to skip the parsed part when return
    //-------------------------------------------------------------------
    public static int parseInt(byte buf[], int[] ia, int len) {
	int value = 0;
	int index = ia[0];
	ia[0] += len;
	for (int i=index; i<ia[0]; i++) {
	    value <<=  8;
	    value += buf[i] & 0xff;
	}
	return value;
    }

    //-------------------------------------------------------------------
    // get an integer/short/byte from a byte array, starting from "index" 
    //-------------------------------------------------------------------
    public static int parseInt(byte buf[], int index, int len) {
	int value = 0;
	for (int i=index; i<index+len; i++) {
	    value <<=  8;
	    value += buf[i] & 0xff;
	}
	return value;
    }

    //----------------------------------------------------------------
    // get a long integer from a byte array,  starting from index 
    // ia[0], which moves forward to skip the parsed part when return
    //----------------------------------------------------------------
    public static long parseLong(byte buf[], int[] ia) {
	long value = 0;
	int index = ia[0];
	ia[0] += 8;
	for (int i=index; i<ia[0]; i++) {
	    value <<=  8;
	    value += buf[i] & 0xff;
	}
	return value;
    }

    //------------------------------------------------------------------
    // write an integer "value" to a byte array (starting from "index"), 
    // high byte first, it can occupy 4/3/2/1 byte(s)
    //------------------------------------------------------------------
    public static void writeInt(byte buf[], int index, int value, int len) {
	for (int i=len; i>0; i--) {
	    int j = (i - 1) * 8;
	    buf[index++] = (byte) ((value >>> j) & 0xff);
	}
    }

    //---------------------------------------------------------------
    // write a long "value" to a byte array (starting from "index")
    //---------------------------------------------------------------
    public static void writeLong(byte buf[], int index, long value) {
	for (int i=56; i>=0; i=i-8) {
	    buf[index++] = (byte) ((value >>> i) & 0xff);
	}
    }

    //-----------------------------------------------------------------
    // get a String from a byte array, starting from index ia[0],
    // the first two bytes is a short int, specifies the string length.
    // if the string length=0, then return an empty string, NOT null
    // ia[0] moves forward to skip the parsed part of when return
    //-----------------------------------------------------------------
    public static String parseString(byte[] buf, int[] ia) {
	String str = "";  	// default is return empty string 
	int len = parseInt(buf, ia, 2);
	if (len > 0 && len != 0xFFFF) {
	    str = new String(buf, ia[0], len);
	    ia[0] += len;
	}
	if (len == 0xFFFF) return "*";	// return wildcard (*) if len == -1
	return str;
    }

    //------------------------------------------------------------
    // test whether "s1" and "s2" share substring, each substring
    // is deliminated by "delim"
    //------------------------------------------------------------
    public static boolean shareString(String s1, String s2, String delim) {
        Vector v1 = str2vec(s1, delim);
        Vector v2 = str2vec(s2, delim);
	for (int i=0; i<v1.size(); i++) {
	    String s = (String)v1.elementAt(i);
	    if (v2.contains(s)) return true;
	}
	return false;
    }

    public static String commonString(String s1, String s2, String delim) {
        Vector v1 = str2vec(s1, delim);
        Vector v2 = str2vec(s2, delim);
	StringBuffer buf = new StringBuffer();
	for (int i=0; i<v1.size(); i++) {
	    String s = (String)v1.elementAt(i);
	    if (v2.contains(s)) {
		if (buf.length() > 0) buf.append(delim);
		buf.append(s);
	    }
	}
	return buf.toString();
    }

    private static Vector str2vec(String str, String delim) {
        Vector v = new Vector(5);
	StringTokenizer st = new StringTokenizer(str, delim);
	while (st.hasMoreTokens()) {
	    String s = st.nextToken();
	    v.addElement(s);
	}
	return v;
    }

    //------------------------------------------------------
    // extract the absolute domain name (dname) from an URL
    // convert it to lower case 
    //------------------------------------------------------
    public static String url2dname(String url) {
	int start = url.indexOf("://");
	if (start == -1) {
	    start = 0;
	} else {
	    start += 3;
	}
	String dname = url.substring(start);
	int end = dname.indexOf("/");
	if (end != -1) {
	    dname = dname.substring(0, end);
	}
	return dname.toLowerCase();
    }

    //-----------------------------------------------
    // extract the naming authority (na) from an URL
    // return empty if no NA is present in the URL
    //-----------------------------------------------
    public static String url2na(String url) {
	int end = url.indexOf("://");
	if (end == -1) {	// no naming authority
	    return "";
	} 
	String na = url.substring(0, end);
	int start = na.indexOf(".");
	if (start == -1) {	// no naming authority
	    return "";
	} 
	na = na.substring(start+1);
	end = na.indexOf(":");
	if (end != -1) {	
	    na = na.substring(0, end);
	} 
	return na;
    }

    //---------------------------------------------------------
    // decide whether two host names (h1, h2) refer to same IP
    //---------------------------------------------------------
    public static boolean sameHost(String h1, String h2) {
	try {
	    String a1 = InetAddress.getByName(h1).getHostAddress();
	    String a2 = InetAddress.getByName(h2).getHostAddress();
	    if (a1.equals(a2)) return true;
	} catch (Exception e) {
	    System.out.println(e);
	}
	return false;
    }
}
