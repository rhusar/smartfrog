package org.smartfrog.services.slp;
/**
 * SLPv2 DA Database Management
 *
 * We implemented the database function within Java2. We didn't use
 * a standard DBMS for simplicity and efficiency. The whole database
 * is organized as red-black tree (provided by Java)
 *
 *  (1)	the key is ltag+URL, assume each service has a unique URL,
 *      and the same service can be registered using different
 *      languaage (ltag)
 *  (2) the value at each node is an Entry class, which keeps all the
 *      information about the service.
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.io.*;
import java.util.*;

public class Database {

    da daf;
    TreeMap table;
    slpMsgComposer composer;
    ByteArrayOutputStream b;
    DataOutputStream d;

    Database(da daf) {
	this.daf = daf;
	table = new TreeMap();
	composer = new slpMsgComposer();
	b = new ByteArrayOutputStream();
	d = new DataOutputStream(b);
    }

    //------------------------------------------------
    // save database to either the stdout or the file
    //------------------------------------------------
    public synchronized void saveDatabase(BufferedWriter o) {
	Iterator values = table.values().iterator();
	while (values.hasNext()) {
	    Entry e = (Entry) values.next();
	    e.prtEntry(daf, o);
	}
    }

    //------------------------------------------
    // load data form file to internal database
    //------------------------------------------
    public synchronized void loadDatabase(String dbase) {
	String line, ltag, type, url, scope, acceptDA, attr = "";
	int lifetime;
	long versionTS, arrivalTS, acceptTS;
	boolean deleted = false;
	try {
	    BufferedReader in =
		new BufferedReader(new FileReader(dbase));
	    while ((line = in.readLine()) != null) {
		StringTokenizer st = new StringTokenizer(line);
		deleted = Boolean.valueOf(st.nextToken()).booleanValue();
		ltag = st.nextToken();
		type = st.nextToken();
		url  = st.nextToken();
		lifetime = Integer.parseInt(st.nextToken());
		scope = st.nextToken();
		versionTS = Long.parseLong(st.nextToken());
		arrivalTS = Long.parseLong(st.nextToken());
		acceptDA = st.nextToken();
		acceptTS = Long.parseLong(st.nextToken());
		if (st.hasMoreTokens()) attr = st.nextToken();
		addEntry(deleted, ltag, type, url, lifetime, scope, attr,
			 Const.fresh_flag, versionTS, arrivalTS,
			 acceptDA, acceptTS);
	    }
	    in.close();
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
    }

    //-------------------------------------------
    // check lifetime and remove expired entries
    //-------------------------------------------
    public synchronized void rmExpiredEntry() {
	long currtime = System.currentTimeMillis();
        Iterator keys = table.keySet().iterator();
	while (keys.hasNext()) {
	    String k = (String) keys.next();
	    Entry  e = (Entry)  table.get(k);
	    if (currtime > (e.getArrivalTS() + e.getLifetime()*1000)) {
	    	keys.remove();
	    }
	}
    }

    //---------------------------------------------------------
    // for "SrvReg"
    // add a new entry of service registration to the database
    // or replace/update its previous registration
    //---------------------------------------------------------
    public synchronized int addEntry(boolean deleted, String ltag, String type,
	String url, int lifetime, String scope, String attr, int reg_flag,
	long versionTS, long arrivalTS, String acceptDA, long acceptTS) {
	if (!table.containsKey(ltag+url)) {
	    if ((reg_flag & Const.fresh_flag) == 0) { // incermental SrvReg
		return Const.INVALID_UPDATE;
	    }
	    table.put(ltag+url, new Entry());
	}
	Entry e = (Entry) table.get(ltag+url);
	return e.update(deleted, ltag, url, type, lifetime, scope, attr,
			reg_flag, versionTS, arrivalTS, acceptDA, acceptTS);
    }

    //---------------------------------------------------
    // for "SrvDeReg"
    // remove the entry with the key: ltag+url (tag=="")
    // or delete some attributes of this entry (tag!="")
    //---------------------------------------------------
    public synchronized int rmEntry(String ltag, String url, String scope,
		String tag, long versionTS, String acceptDA, long acceptTS) {
	if (table.containsKey(ltag+url)) {
	    Entry e = (Entry) table.get(ltag+url);
	    if (!scope.equalsIgnoreCase(e.getScope())) {
		return Const.SCOPE_NOT_SUPPORTED;
	    }
	    e.deletion(tag, versionTS, acceptDA, acceptTS);
	}
	return Const.OK;
    }

    //----------------------------------------------------------------------
    // for "SrvTypeRqst"
    // get the list of service types for specified scope & naming authority
    //----------------------------------------------------------------------
    public synchronized String getServiceTypeList(String na, String scope) {
	Vector typelist = new Vector(5);
	Iterator values = table.values().iterator();
	while (values.hasNext()) {
            Entry e = (Entry) values.next();
	    if (!e.getDeleted() &&			 // nor deleted
		scope.equalsIgnoreCase(e.getScope()) &&  // match scope
		(na.equals("*") ||			 // NA wildcard
		 na.equalsIgnoreCase(e.getNA())) &&	 // match NA
		!typelist.contains(e.getType())) {
		typelist.addElement(e.getType());
	    }
	}
	StringBuffer tl = new StringBuffer();
	for (int i=0; i<typelist.size(); i++) {
            String s = (String)typelist.elementAt(i);
	    if (tl.length() > 0) tl.append(",");
	    tl.append(s);
	}
	return tl.toString();
    }

    //-----------------------------------------------------------
    // for "SrvRqst"
    // find the matched URLs with (type, scope, predicate, ltag)
    // return: error code (short)
    //         number of matched URLs (short)
    //         URL blocks (decided bt previous #URL)
    //-----------------------------------------------------------
    public synchronized byte[] getMatchedURL(String type, String scope,
		String pred, String ltag) {
	byte[] buf = null;
	int ecode = Const.OK;
	if (!Util.shareString(daf.getScope(), scope, ",")) {
	    ecode = Const.SCOPE_NOT_SUPPORTED;
	}
	b.reset();
	try {
	    int count = 0;
	    d.writeShort(ecode);	// error code
	    d.writeShort(count);	// URL count, place holder
	    if (ecode == Const.OK) {	// no error, find matched URLs
	        Iterator values = table.values().iterator();
	        while (values.hasNext()) {
                    Entry e = (Entry) values.next();
                    if (e.match(type, scope, pred, ltag)) {
       	    	        count++;
              	        d.writeByte(0);
               	        d.writeShort(e.getLifetime());
               	        d.writeShort(e.getURL().length());
              	        d.writeBytes(e.getURL());
               	        d.writeByte(0);
                    }
                }
	    }
            buf = b.toByteArray();
	    if (count > 0) Util.writeInt(buf, 2, count, 2); // update count
	} catch (Exception e) {
	    if( ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
	}
        return buf;
    }

    //------------------------------------------------------
    // for "AttrRqst"
    // Return: attrbute list for the URL/"service type"
    //         format: (attr1=value1),(attr2=value2)
    // if tag != "", return ONLY those attributes in tag
    //------------------------------------------------------
    public synchronized String getAttrList(String url, String scope,
				String tag, String ltag) {
        if (table.containsKey(ltag+url)) {
	    Entry e = (Entry) table.get(ltag+url);
	    if (!e.getDeleted() &&
                scope.equalsIgnoreCase(e.getScope())) {
		return e.getAttr(tag);
	    } else {
		return "";
	    }
	} else {	// Not URL, try servive type
	    return typeAttrList(url, scope, tag, ltag);
	}
    }

    private synchronized String typeAttrList(String type, String scope,
				String tag, String ltag) {
        StringBuffer attrList = new StringBuffer();
        Iterator values = table.values().iterator();
        while (values.hasNext()) {
	    Entry e = (Entry) values.next();
	    if (!e.getDeleted() &&
                type.equalsIgnoreCase(e.getType()) &&
		scope.equalsIgnoreCase(e.getScope()) &&
		ltag.equalsIgnoreCase(e.getLtag())) {
		String s = e.getAttr(tag);
		if (attrList.length() > 0) attrList.append(",");
            	attrList.append(s);
	    }
        }
        return attrList.toString();
    }

    //------------------------------------------------------------------
    // send newer updates to the peer through "tcp" peering connection
    // compare each entry's updating ID with IDs in (adalist, atslist)
    //------------------------------------------------------------------
    public synchronized void antiEntropy(slpTcpHandler tcp, String scope,
					 String rda, long rts) {
	TreeMap tmp = new TreeMap();
	Iterator values = table.values().iterator();
	while (values.hasNext()) {
	    Entry e = (Entry) values.next();
	    String ada = e.getAcceptDA();
	    long   ats = e.getAcceptTS();
	    if (ada.equalsIgnoreCase(rda) && ats > rts && // for newer updates
	        Util.shareString(scope, e.getScope(), ",")) {
		tmp.put(new Long(ats), e.getLtag().concat(e.getURL()));
	    }
	}
      	long ctime = System.currentTimeMillis()/1000;
	values = tmp.values().iterator();
	while (values.hasNext()) {
	    String k = (String) values.next();
	    Entry e = (Entry) table.get(k);
	    int ltime = (int)(e.getArrivalTS()/1000 + e.getLifetime() - ctime);
	    byte[] buf = composer.SrvReg(0, Const.fresh_flag, e.getLtag(),
		 			 e.getURL(), ltime, e.getType(),
					 e.getScope(), e.getAttr(""));
	    buf = composer.MeshFwdExt(buf, Const.Fwded, e.getVersionTS(),
				      e.getAcceptDA(), e.getAcceptTS());
	    tcp.send(buf, buf.length);
	    if (e.getDeleted()) {
		buf = composer.SrvDeReg(0, e.getLtag(), e.getScope(),
				        e.getURL(), ltime, "");
		buf = composer.MeshFwdExt(buf, Const.Fwded, e.getVersionTS(),
					  e.getAcceptDA(), e.getAcceptTS());
		tcp.send(buf, buf.length);
	    }
	}
    }
}
