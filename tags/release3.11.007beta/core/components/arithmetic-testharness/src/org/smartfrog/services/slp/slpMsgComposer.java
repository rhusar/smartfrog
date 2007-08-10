
package org.smartfrog.services.slp;
/**
 * Compose various SLPv2 messages (protocol stack).
 * Return the message in a byte array
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *  Class to compose SLP messages
 */
public class slpMsgComposer {

    DataOutputStream      d;
    ByteArrayOutputStream b;

    slpMsgComposer() {
	b = new ByteArrayOutputStream();
	d = new DataOutputStream(b);
    }

/**
 * Construct a common SLP message header.
 * Compose SLP common message header, flags may be set, there
 * may exist extensions, language tag is NOT included here
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
    private void Header(int type, int len, int flag, int xid) {
	try {
	    b.reset();
	    d.writeByte(Const.version); // SLP version
	    d.writeByte(type);		// Function type
	    d.writeByte(0);		// len
	    d.writeShort(len); 		// length
	    d.writeShort(flag); 	// flag bits
	    d.writeByte(0);		// next ext. offset
	    d.writeShort(0);		// next ext. offset
	    d.writeShort(xid);          // XID
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

/**
 * Put a string in the byte[], precede with its length.
 * The string could be empty, with a length of 0.
 * If the string is null, then no action is taken.
 */
    private void putString(String s) {
	if (s == null) return;
	try {
	    d.writeShort(s.length());
	    if (s.length() > 0) {
		d.writeBytes(s);
	    }
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

/**
 * put an integer as a byte (normally zero) in the byte[]
 */
    private void putByte(int z) {
	try {
	    d.writeByte(z);
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

/**
 * put an integer as a short (normally as error code) in the byte[]
 */
    private void putShort(int z) {
	try {
	    d.writeShort(z);
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

/**
 * put an integer as an integer in the byte[]
 */
    private void putInt(int z) {
	try {
	    d.writeInt(z);
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

    private void putLong(long z) {
	try {
	    d.writeLong(z);
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

/**
 * Construct a specific SLP message.
 * put URL entry in the byte[], assume "# of URL auths" is zero
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
    private void putURL(String url, int lifetime) {
	try {
	    d.writeByte(0);                     // reserved
            d.writeShort(lifetime);             // lifetime
            d.writeShort(url.length());         // len of URL
            d.writeBytes(url);			// URL string
            d.writeByte(0);                     // # of authenticate
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

/**
 * calculate string length, precede with a short integer length field
 */
    private int strlen(String s) {
	return (2 + s.length());
    }

/**
 * calculate URL-entry length, assume "# of url auths" is zero
 */
    private int urllen(String url) {
	return (6 + url.length());
    }

/**
 * Construct a specific SLP message.
 * service request #1
 * <pre>
 *+----------------------------+---------------------------+
 *| Length of [PRList]         |   [PRList] string         \
 *+----------------------------+---------------------------+
 *| Length of [service-type]   | [service-type] string     \
 *+----------------------------+---------------------------+
 *| Length of [scope-list]     | [scope-list] string       \
 *+----------------------------+---------------------------+
 *| Length of predicate string | service request predicate \
 *+----------------------------+---------------------------+
 *| Length of [SLP SPI] string |   [SLP SPI] string        \
 *+----------------------------+---------------------------+
 * <pre>
 */
    public byte[] SrvRqst(int xid, int flag, String ltag, String pr,
		String type, String scope, String pred, String spi) {
	int len = Const.header_len + strlen(ltag) + strlen(pr) +
		strlen(type) + strlen(scope) + strlen(pred) + strlen(spi);
	Header(Const.SrvRqst, len, flag, xid);
	putString(ltag);		// language tag
	putString(pr);			// PRList
	putString(type);		// service type
	putString(scope);		// scope list
	putString(pred);		// predicate
	putString(spi);			// SPI
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * service reply (reply for service request) #2.
 * <pre>
 *+----------------------------+---------------------------+
 *|       Error Code           |    URL entry count        |
 *+----------------------------+---------------------------+
 *|   [URl entry 1]           ...     [URL entry N]        \
 *+----------------------------+---------------------------+
 * </pre>
 */
    public byte[] SrvReply(int xid, String ltag, byte[] buf) {
	int len = Const.header_len + strlen(ltag) + buf.length;
	Header(Const.SrvRply, len, Const.normal_flag, xid);
	putString(ltag);			// language tag
	try {
	    d.write(buf, 0, buf.length);	// ErrCode + #URL + each entry
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * service registration #3.
 * <pre>
 *+----------------------------------------------------------------+
 *|                          [URL-Entry]                           \
 *+---------------------------------+------------------------------+
 *|  Length of service type string  |     [service-type]           \
 *+---------------------------------+------------------------------+
 *|  Length of [scope-list]         |     [scope-list]             \
 *+---------------------------------+------------------------------+
 *|  Length of attr-list string     |     [attr-list]              \
 *+----------------+----------------+------------------------------+
 *| # of AttrAuths | (if present) Attribute Authentication Blocks  \
 *+----------------+-----------------------------------------------+
 * </pre>
 */
    public byte[] SrvReg(int xid, int flag, String ltag, String url,
		int lifetime, String type, String scope, String attr) {
	int len = Const.header_len + strlen(ltag) + urllen(url) +
                  strlen(type) + strlen(scope) + strlen(attr) + 1;
	Header(Const.SrvReg, len, flag, xid);
	putString(ltag);			 // language tag
	putURL(url, lifetime);
	putString(type);			 // service type
	putString(scope);			 // scope list
	putString(attr);			 // attr list
	putByte(0);				 // num of attrAuths
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * service De-registration #4
 * <pre>
 *+-----------------------------+---------------------------+
 *|  Length of [scope-list]     |        [scope-list]       \
 *+-----------------------------+---------------------------+
 *|                         [URL-entry]                     \
 *+-----------------------------+---------------------------+
 *|  Length of [tag-list]       |        [tag-list]         \
 *+-----------------------------+---------------------------+
 * </pre>
 */
    public byte[] SrvDeReg(int xid, String ltag, String scope, String url,
		int ltime, String tag) {
	int len = Const.header_len + strlen(ltag) + strlen(scope) +
			urllen(url) + strlen(tag);
	Header(Const.SrvDeReg, len, Const.normal_flag, xid);
	putString(ltag);			 // language tag
	putString(scope);			 // scope list
	putURL(url, ltime);			 // URL
	putString(tag);				 // tag list
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * service ack (reply for SrvReg & SrvDeReg) #5
 * <pre>
 *+-----------------------------+
 *|        Error Code           |
 *+-----------------------------+
 * </pre>
 */
    public byte[] SrvAck(int xid, String ltag, int errcode) {
	int len = Const.header_len + strlen(ltag) + 2;
	Header(Const.SrvAck, len, Const.normal_flag, xid);
	putString(ltag);			// language tag
	putShort(errcode);			// ErrCode
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * attribute request #6.
 <pre>
 *+-------------------------------+----------------------------+
 *|  Length of PRList             |   [PRList] string          |
 *+-------------------------------+----------------------------+
 *|  Length of URL                |         URL                |
 *+-------------------------------+----------------------------+
 *|  Length of [scope-list]       |   [scope-list] string      |
 *+-------------------------------+----------------------------+
 *|  Length of [tag-list] string  |   [tag-list] string        |
 *+-------------------------------+----------------------------+
 *|  Length of [SLP SPI] string   |   [SLP SPI] string         |
 *+-------------------------------+----------------------------+
 </pre>
 */
    public byte[] AttrRqst(int xid, String ltag, String pr, String url,
			   String scope, String tag, String spi) {
	int len = Const.header_len + strlen(ltag) + strlen(pr) +
		strlen(url) + strlen(scope) + strlen(tag) + strlen(spi);
	Header(Const.AttrRqst, len, Const.normal_flag, xid);
	putString(ltag);
	putString(pr);
	putString(url);
	putString(scope);
	putString(tag);
	putString(spi);
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * attribute reply (reply for attribute request) #7.
 <pre>
 *+-----------------------------+---------------------------------+
 *|         Error Code          |   Length of [attr-list]         |
 *+-----------------------------+---------------------------------+
 *|                          [attr-list]                          \
 *+----------------+----------------------------------------------+
 *| # of AttrAuths |  Attribute authentication block (if present) \
 *+----------------+----------------------------------------------+
 </pre>
 */
    public byte[] AttrReply(int xid, String ltag, int ecode, String buf) {
	int len = Const.header_len + strlen(ltag) + 3 + strlen(buf);
	Header(Const.AttrRply, len, Const.normal_flag, xid);
	putString(ltag);		// language tag
	putShort(ecode);		// ErrCode
	putString(buf);			// attr-list
	putByte(0);			// # of AttrAuths
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * directory agent advertisement #8.
 * <pre>
 *+-------------------------------+--------------------------------+
 *|     Error Code                |   DA Stateless Boot Timestamp  |
 *+-------------------------------+--------------------------------+
 *| DA Stateless Boot Time cont.  |      Length of URL             |
 *+-------------------------------+--------------------------------+
 *|                              URL                               \
 *+-------------------------------+--------------------------------+
 *|   Length of <scope-list>      |      <scope-list>              \
 *+-------------------------------+--------------------------------+
 *|   Length of <attr-list>       |      <attr-list>               \
 *+-------------------------------+--------------------------------+
 *|   Length of SLP <SPI>         |      SLP <SPI> string          \
 *+---------------+---------------+--------------------------------+
 *| # Auth Blocks |       Authentication blocl (if any)            \
 *+---------------+---------------+--------------------------------+
 * </pre>
 */
    public byte[] DAAdvert(int xid, int flag, String ltag, int ts,
		String url, String scope, String attr, String spi) {
	int len = Const.header_len + 7 + strlen(ltag) + strlen(url) +
		strlen(scope) + strlen(attr) + strlen(spi);
	Header(Const.DAAdvert, len, flag, xid);
	putString(ltag);		// language tag
	putShort(0);			// ErrCode
	putInt(ts);			// boot timestamp
	putString(url);			// URL
	putString(scope);		// scope list
	putString(attr);		// attribute list
	putString(spi);			// SLP SPI
	putByte(0);			// # Auth blocks
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * service type request #9
 * <pre>
 *+-------------------------------+-----------------------------+
 *|     Length of PRList          |     [PRList] string         |
 *+-------------------------------+-----------------------------+
 *| Length of Naming Authority    |  [Naming Authority String]  |
 *+-------------------------------+-----------------------------+
 *| Length of [scope-list]        |    [scope-list] string      |
 *+-------------------------------+-----------------------------+
 * </pre>
 */
    public byte[] SrvTypeRqst(int xid, String ltag, String pr, String na,
				String scope) {
	int len = Const.header_len + strlen(ltag) + strlen(pr) +
			strlen(na) + strlen(scope);
	Header(Const.SrvTypeRqst, len, Const.normal_flag, xid);
	putString(ltag);		// language tag
	putString(pr);			// PRList
	if (na.equals("-1")) {		// Naming Authority
	    putShort(0xFFFF);		// -1 for all
	} else {
	    putString(na);
	}
	putString(scope);		// scope list
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * service type reply (reply for service type request).
 * <pre>
 *+-------------------------------+-------------------------------+
 *|          Error Code           |    Length of [srvType-list]   |
 *+-------------------------------+-------------------------------+
 *|                       [srvType-list]                          \
 *+---------------------------------------------------------------+
 </pre>
 */
    public byte[] SrvTypeReply(int xid, String ltag, int ecode, String buf) {
	int len = Const.header_len + strlen(ltag) + 2 + strlen(buf);
	Header(Const.SrvTypeRply, len, Const.normal_flag, xid);
	putString(ltag);		// language tag
	putShort(ecode);		// ErrCode
	putString(buf);			// srvtype-list
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * DataRqst message #12.
 * <pre>
 *+---------------------------------------------------------------+
 *|                         Accept ID                             \
 *+---------------------------------------------------------------+
 * </pre>
 */
    public byte[] DataRqst(String ltag, String ada, long ats) {
	int len = Const.header_len + strlen(ltag) + 8 + strlen(ada);
	Header(Const.DataRqst, len, Const.normal_flag, 0);
	putString(ltag);                // language tag
	putLong(ats);			// ATS
	putString(ada); 		// ADA
	return b.toByteArray();
    }

/**
 * Construct a specific SLP message.
 * append MeshFwd extension and adjust original message.
 * <pre>
 *+--------------------------------+----------------------------------+
 *|  MeshFwd Extension ID = 0x0006 |  Next Extension Offset (NEO)     |
 *+--------------+-----------------+----------------------------------+
 *|  NEO Contd.  |     Fwd-ID      |         Version Timestamp        |
 *+--------------+-----------------+----------------------------------+
 *|                     Version Timestamp, contd.                     |
 *+--------------------------------+----------------------------------+
 *|    Version Timestamp, contd.   |            Accept ID             \
 *+--------------------------------+----------------------------------+
 * </pre>
 */
    public byte[] MeshFwdExt(byte[] buf, int id, long versionTS,
			     String ada, long ats) {
	if (ada == null) {
	    System.out.println("Null acceptDA!");
	    return buf;
	}
	int len = Util.parseInt(buf, 2, 3);
	int alen = 14 + 10 + ada.length();
	adjustMesg(buf, alen);
	try {
	    b.reset();
	    d.write(buf, 0, len);
            d.writeShort(Const.MeshFwdExt);     // mesh-forwarding extension
            d.writeShort(0);         		// next ext. offset
            d.writeByte(0);                     // next ext. offset cont.
            d.writeByte(id);                    // Fwd-ID
            d.writeLong(versionTS);             // version timestamp
            d.writeLong(ats);                   // accept TS
            d.writeShort(ada.length());     	// length of accept DA URL
            d.writeBytes(ada);              	// accept DA URL
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
	return b.toByteArray();
    }

/**
 * adjust source message for the adding extension.
 * need to change:
 *   (1) packet length (add new length)
 *   (2) last extension's NEO links to new one
 */
    private void adjustMesg(byte[] buf, int alen) {
	int plen = Util.parseInt(buf, 2, 3);
	int nextExt = Util.parseInt(buf, 7, 3);
	int lastExtAddr = 7;
	while (nextExt != Const.EndOfExt) {
	    lastExtAddr = nextExt+2;
	    nextExt = Util.parseInt(buf, lastExtAddr, 3);
	}
	Util.writeInt(buf, lastExtAddr, plen, 3); // new ext. starting point
	Util.writeInt(buf, 2, plen+alen, 3);      // adjust message length
    }
}
