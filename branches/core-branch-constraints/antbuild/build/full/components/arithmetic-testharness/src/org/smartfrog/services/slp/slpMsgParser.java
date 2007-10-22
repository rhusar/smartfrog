package org.smartfrog.services.slp;
/**
 * SLPv2 message parser (protocol stack)
 * Use separate get-methods to obtain each field after parsing
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class slpMsgParser {

    da daf;
    Database database;
    int    version, func_id, packet_len, slp_flag, ext_offset, xid,
           ecode, lifetime;
    String ltag, scope, attr, pred, spi, tag, type, prlist, url;
    String attrList, typeList, urlList;
    int    daBootTS; 		// DA boot timestamp
    int    meshFwdID;		// in MeshFwd ext.
    long   versionTS;		// version TS from the SA for the update
    long   arrivalTS;		// arrival TS at the DA for the update
    String acceptDA;		// the accept DA for the update
    long   acceptTS;		// the accept TS for the update

    slpMsgParser(da daf) {  // for DA
	this.daf = daf;
	database = daf.getDatabase();
    }

    slpMsgParser() {	    // for UA/SA
    }

/**
 * Parse a specific SLP header.
 * SLP common message header, not including language tag
 * <pre>
 *+--------------+-----------------+----------------------------------+
 *|  Version     |   Function-ID   |            Length                |
 *+--------------+-+-+-+-----------+---------------+------------------+
 *| Length cont. |O|F|R|       Reserved            | Next Ext. Offset |
 *+--------------+-+-+-+-----------+---------------+------------------+
 *|     Next Ext. Offset  Cont.    |              XID                 |
 *+--------------------------------+----------------------------------+
 * </pre>
 */
    public void Header(byte[] buf) {  // parse header
	int[] ia = { 0 };
	version    = Util.parseInt(buf, ia, 1);		// index=0
	func_id    = Util.parseInt(buf, ia, 1);		// index=1
   	packet_len = Util.parseInt(buf, ia, 3);		// index=2
	slp_flag   = Util.parseInt(buf, ia, 2);		// index=5
	ext_offset = Util.parseInt(buf, ia, 3);		// index=7
	xid        = Util.parseInt(buf, ia, 2);		// index=10
    }

    public void LangTag(byte[] buf, int ia[]) {  // parse language tag
	ltag = Util.parseString(buf, ia);
    }

    public int getPacketLen() {
	return packet_len;
    }

    public int getFuncID() {
	return func_id;
    }

    public int getFlag() {
	return slp_flag;
    }

    public int getXID() {
	return xid;
    }

    public String getLtag() {
	return ltag;
    }

    public String getURL() {
	return url;
    }

    public String getScope() {
	return scope;
    }

    public String getAttr() {
	return attr;
    }

    public String getAttrList() {
	return attrList;
    }

    public String getTypeList() {
	return typeList;
    }

    public String getUrlList() {
	return urlList;
    }

    public int getEcode() {
	return ecode;
    }

    public int getDaBootTS() {
	return daBootTS;
    }

    public int getMeshFwdID() {
	return meshFwdID;
    }

    public long getVersionTS() {
	return versionTS;
    }

    public String getAcceptDA() {
	return acceptDA;
    }

    public long getAcceptTS() {
	return acceptTS;
    }

/**
 * Parse a specific SLP header.
 * parse URL entry, to get the lifetime and URL string
 * <pre>
 *+---------------+---------------------------------+----------------+
 *|    Reserved   |          Lifetime               |   URL length   |
 *+---------------+---------------------------------+----------------+
 *| URL len cont. |          URL (variable length)                   \
 *+---------------+--------------------------------------------------+
 *| # of URL auths|          Auth. blocks (if any)                   \
 *+------------------------------------------------------------------+
 * </pre>
 */
    public String parseURL(byte[] buf, int[] ia) {
	ia[0] += 1; 				// skip one byte for reserved
	lifetime = Util.parseInt(buf, ia, 2);   // lifetime
	url      = Util.parseString(buf, ia);	// URL
	if (Util.parseInt(buf, ia, 1) != 0) {
	    System.err.println("URL authentication blocks are present");
	}
	return url;
    }

/**
 * Parse a specific SLP header.
 * service request #1
 * <pre>
 *+----------------------------+---------------------------+
 *| length of (PRList)         |   (PRList) string         \
 *+----------------------------+---------------------------+
 *| length of (service-type)   | (service-type) string     \
 *+----------------------------+---------------------------+
 *| length of (scope-list)     | (scope-list) string       \
 *+----------------------------+---------------------------+
 *| length of predicate string | service request predicate \
 *+----------------------------+---------------------------+
 *| length of (SLP SPI) string |   (SLP SPI) string        \
 *+----------------------------+---------------------------+
 * </pre>
 */
    public byte[] SrvRqst(byte[] buf, int[] ia) {
	prlist = Util.parseString(buf, ia);		// PRList
	type   = Util.parseString(buf, ia);		// service type
	scope  = Util.parseString(buf, ia);		// scope list
	pred   = Util.parseString(buf, ia);		// predicate
	spi    = Util.parseString(buf, ia);		// SLP SPI string
	if (type.equalsIgnoreCase(Const.DAAdvert_Rqst)) {
	    return null;
        }
	return database.getMatchedURL(type, scope, pred, ltag);
    }

/**
 * Parse a specific SLP header.
 * service reply (reply for service request) #2
 * <pre>
 *+----------------------------+---------------------------+
 *|       Error Code           |    URL entry count        |
 *+----------------------------+---------------------------+
 *|   URl entry 1             ...      URL entry N         \
 *+----------------------------+---------------------------+
 * </pre>
 */
    public void SrvReply(byte[] buf, int[] ia) {
	ecode = Util.parseInt(buf, ia, 2);
	int n = Util.parseInt(buf, ia, 2);
	StringBuffer tl = new StringBuffer();
	for (int i=0; i<n; i++) {
	    if (tl.length() > 0) tl.append(",");
	    tl.append(parseURL(buf, ia));	// URL only, no lifetime
	}
	urlList = tl.toString();
    }

/**
 * Parse a specific SLP header.
 * service registration #3
 * <pre>
 *+----------------------------------------------------------------+
 *|                          <URL-Entry>                           \
 *+---------------------------------+------------------------------+
 *|  length of service type string  |     <service-type>           \
 *+---------------------------------+------------------------------+
 *|  length of <scope-list>         |     <scope-list>             \
 *+---------------------------------+------------------------------+
 *|  length of attr-list string     |     <attr-list>              \
 *+----------------+----------------+------------------------------+
 *| # of AttrAuths | (if present) Attribute Authentication Blocks  \
 *+----------------+-----------------------------------------------+
 * </pre>
 * Need to set error code (ecode)
 * Extensions have been parsed, so versionTS/acceptDA/acceptTS are known
 */
    public void SrvReg(byte[] buf, int[] ia) {
	parseURL(buf, ia);			// URL
	type  = Util.parseString(buf, ia);	// service type
	scope = Util.parseString(buf, ia);	// scope list
	attr  = Util.parseString(buf, ia);	// attribute list
	if (lifetime == 0) {
	    ecode = Const.INVALID_REGISTRATION;
	} else if (!Util.shareString(daf.getScope(), scope, ",")) {
	    ecode = Const.SCOPE_NOT_SUPPORTED;
	} else {
	    if (acceptDA.equalsIgnoreCase(daf.getURL())) {
		arrivalTS = acceptTS;
	    } else {
		arrivalTS = System.currentTimeMillis();
	    }
	    ecode = database.addEntry(false, ltag, type, url, lifetime, scope,
		    attr, slp_flag, versionTS, arrivalTS, acceptDA, acceptTS);
	}
    }

/**
 * Parse a specific SLP header.
 * service De-registration #4
 * <pre>
 *+-----------------------------+---------------------------+
 *|  Length of (scope-list)     |        (scope-list)       \
 *+-----------------------------+---------------------------+
 *|                         (URL-entry)                     \
 *+-----------------------------+---------------------------+
 *|  Length of (tag-list)       |        (tag-list)         \
 *+-----------------------------+---------------------------+
 * </pre>
 * Need to set error code, 0 is for OK
 */
    public void SrvDeReg(byte[] buf, int[] ia) {
	scope = Util.parseString(buf, ia);		// scope list
	parseURL(buf, ia);				// URL
	tag   = Util.parseString(buf, ia);		// tag list
	if (Util.shareString(daf.getScope(), scope, ",")) {
	    ecode = database.rmEntry(ltag, url, scope, tag, versionTS,
				     acceptDA, acceptTS);
	} else {
	    ecode = Const.SCOPE_NOT_SUPPORTED;
	}
    }

/**
 * Parse a specific SLP header.
 * service ack (reply for SrvReg & SrvDeReg) #5
 * <pre>
 *+-----------------------------+
 *|        Error Code           |
 *+-----------------------------+
 * </pre>
 * return the error code
 */
    public void SrvAck(byte[] buf, int[] ia) {
	ecode = Util.parseInt(buf, ia, 2);
    }

/**
 * Parse a specific SLP header.
 * attribute request #6
 * <pre>
 *+-------------------------------+----------------------------+
 *|  length of PRList             |   (PRList) string          \
 *+-------------------------------+----------------------------+
 *|  length of URL                |         URL                \
 *+-------------------------------+----------------------------+
 *|  length of (scope-list)       |   (scope-list) string      \
 *+-------------------------------+----------------------------+
 *|  length of (tag-list) string  |   (tag-list) string        \
 *+-------------------------------+----------------------------+
 *|  length of (SLP SPI) string   |   (SLP SPI) string         \
 *+-------------------------------+----------------------------+
 * </pre>
 */
    public void AttrRqst(byte[] buf, int[] ia) {
	prlist = Util.parseString(buf, ia);		// PRList
	url    = Util.parseString(buf, ia);		// URL or service type
	scope  = Util.parseString(buf, ia);		// scope list
	tag    = Util.parseString(buf, ia);		// tag list
	spi    = Util.parseString(buf, ia);		// SLP SPI string
	if (!Util.shareString(daf.getScope(), scope, ",")) {
	    ecode = Const.SCOPE_NOT_SUPPORTED;
	    attrList = "";
	} else {
	    ecode = Const.OK;
	    attrList = database.getAttrList(url, scope, tag, ltag);
	}
    }

/**
 * Parse a specific SLP header.
 * attribute reply (reply for attribute request) #7
 * <pre>
 *+-----------------------------+---------------------------------+
 *|         Error Code          |   length of (attr-list)         |
 *+-----------------------------+---------------------------------+
 *|                          (attr-list)                          \
 *+----------------+----------------------------------------------+
 *| # of AttrAuths |  Attribute authentication block (if present) \
 *+----------------+----------------------------------------------+
 * <pre>
 */
    public void AttrReply(byte[] buf, int[] ia) {
	ecode = Util.parseInt(buf, ia, 2);
	attrList = Util.parseString(buf, ia);
    }

/**
 * Parse a specific SLP header.
 * directory agent advertisement #8.
 *<pre>
 *+-------------------------------+--------------------------------+
 *|     Error Code                |   DA Stateless Boot Timestamp  |
 *+-------------------------------+--------------------------------+
 *| DA Stateless Boot Time cont.  |      length of URL             |
 *+-------------------------------+--------------------------------+
 *|                              URL                               \
 *+-------------------------------+--------------------------------+
 *|   length of (scope-list)      |      (scope-list)              \
 *+-------------------------------+--------------------------------+
 *|   length of (attr-list)       |      (attr-list)               \
 *+-------------------------------+--------------------------------+
 *|   length of SLP (SPI)         |      SLP (SPI) string          \
 *+---------------+---------------+--------------------------------+
 *| # Auth Blocks |       Authentication blocl (if any)            \
 *+---------------+---------------+--------------------------------+
 * </pre>
 */
    public void DAAdvert(byte[] buf, int[] ia) {
	ecode = Util.parseInt(buf, ia, 2);
	daBootTS = Util.parseInt(buf, ia, 4);		// boot timestamp
	url   = Util.parseString(buf, ia); 		// URL
	scope = Util.parseString(buf, ia);              // scope-list
	attr  = Util.parseString(buf, ia);              // attr-list
    }

/**
 * Parse a specific SLP header.
 * service type request #9.
 * <pre>
 *+-------------------------------+-----------------------------+
 *|     length of PRList          |     (PRList) string         |
 *+-------------------------------+-----------------------------+
 *| length of Naming Authority    |  (Naming Authority String)  |
 *+-------------------------------+-----------------------------+
 *| length of (scope-list)        |    (scope-list) string      |
 *+-------------------------------+-----------------------------+
 * </pre>
 */
    public void SrvTypeRqst(byte[] buf, int[] ia) {
	prlist = Util.parseString(buf, ia);		// PRList
	String na = Util.parseString(buf, ia);		// Naming authority
	scope  = Util.parseString(buf, ia);		// scope list
	if (!Util.shareString(daf.getScope(), scope, ",")) {
	    ecode = Const.SCOPE_NOT_SUPPORTED;
	    typeList = "";
	} else {
	    ecode = Const.OK;
	    typeList = database.getServiceTypeList(na, scope);
	}
    }

/**
 * Parse a specific SLP header.
 * service type reply (reply for service type request) #10.
 *<pre>
 *+-------------------------------+-------------------------------+
 *|      Error Code               |    length of (srvType-list)   |
 *+-------------------------------+-------------------------------+
 *|                       (srvType-list)                          |
 *+---------------------------------------------------------------+
 * </pre>
 */
    public void SrvTypeReply(byte[] buf, int[] ia) {
	ecode = Util.parseInt(buf, ia, 2);
	typeList = Util.parseString(buf, ia);
    }

/**
 * Parse a specific SLP header.
 * DataRqst message #12.
 *<pre>
 *+---------------------------------------------------------------+
 *|                          Accept ID                            \
 *+---------------------------------------------------------------+
 * </pre>
 */
    public void DataRqst(byte[] buf, int[] ia) {
	acceptTS = Util.parseLong(buf, ia);
	acceptDA = Util.parseString(buf, ia);
    }

/**
 * Parse a specific SLP header.
 * SLP extension parser: MeshFwdExt.
 * 
 *    (1) initialize (turn off previous value)
 *    (2) if MeshFwdExt,
 *           get Fwd-ID & versionTS
 *           if Fwd-ID == Const.RqstFwd, change it to Const.Fwded
 *           if Fwd-ID == Const.Fwded, get acceptDA & acceptTS
 * <pre>
 *+--------------------------------+----------------------------------+
 *|  MeshFwd Extension ID = 0x0006 |  Next Extension Offset (NEO)     |
 *+--------------+-----------------+----------------------------------+
 *|  NEO Contd.  |     Fwd-ID      |         Version Timestamp        |
 *+--------------+-----------------+----------------------------------+
 *|                     Version Timestamp, contd.                     |
 *+--------------------------------+----------------------------------+
 *|    Version Timestamp, contd.   |           Accept ID              \
 *+--------------------------------+----------------------------------+
 * </pre>
 */
    public void Extension(byte[] buf, String fromPeer) { // buf: whole message
        meshFwdID = -1;   		  // no MeshFwdExt
	acceptDA  = daf.getURL();
	acceptTS  = System.currentTimeMillis();
	versionTS  = acceptTS;
	int[] ia = { ext_offset };	  // initial extension offset
	while (ia[0] != Const.EndOfExt) { // while has more extensions
	    int extID = Util.parseInt(buf, ia, 2);	// extension ID
	    int nextExt = Util.parseInt(buf, ia, 3);	// next extension
	    if (extID == Const.MeshFwdExt) { 	// mesh-forwarding extension
	        int idAddr = ia[0]; 	  // may need to MeshFwdID
		meshFwdID = Util.parseInt(buf, ia, 1);
		versionTS  = Util.parseLong(buf, ia);
		if (meshFwdID == Const.Fwded) {
		    acceptTS = Util.parseLong(buf, ia);
		    acceptDA = Util.parseString(buf, ia);
	    	    daf.setSummary(acceptDA, acceptTS, fromPeer);
		} else if (meshFwdID == Const.RqstFwd) {
	    	    Util.writeInt(buf, idAddr, Const.Fwded, 1);
	    	    Util.writeLong(buf, idAddr+9, acceptTS);
		}
	    }
	    ia[0] = nextExt;
	}
	if (meshFwdID != Const.Fwded) {		// accepted by local host
	    daf.setSummary(acceptDA, acceptTS, acceptDA);
	}
    }
}
