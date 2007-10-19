package org.smartfrog.services.slp;
/**
 * Maintain the Summary & DAAdvert information for peers
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.util.*;

class Summary {
    String  url;		// url of this DA
    String  scope;		// supporting scope
    int     bts;		// boot TS
    String  attr;		// attribute
    long    ats;		// accept TS
    String  slist[];		// scope list
    long    tlist[];		// ats list
    int     nscope;		// number of scopes

    Summary(String url, String scope, int bts, String attr, long ats) {
	this.url   = url;
	this.scope = scope;
	this.bts   = bts;
	this.attr  = attr;
	this.ats   = ats;
	StringTokenizer st = new StringTokenizer(scope, ",");
	nscope = st.countTokens();
	slist  = new String[nscope];
	tlist  = new long[nscope];
	for (int i=0; i<nscope; i++) {
            slist[i] = st.nextToken();
	    tlist[i] = ats;
	}
    }

    public String getURL() {	 // URL
	return url;
    }

    public String getScope() {	 // scope
	return scope;
    }

    public int getBootTS() {	 // boot TS
	return bts;
    }

    public String getAttr() {	 // attribute
	return attr;
    }

    public long getAcceptTS() {	 // accept TS
	return ats;
    }

    public void setAcceptTS(long ts) {	 // accept TS
	if (ts > ats) {
	    ats = ts;
	    for (int i=0; i<nscope; i++) tlist[i] = ts;
	}
    }

    public void setAcceptTS(long ts, String cs) { // ATS for some scopes
        // update ATS for each scope
	StringTokenizer st = new StringTokenizer(cs, ",");
	while (st.hasMoreTokens()) {
	    String s = st.nextToken();
	    int j = 0;
	    for (int i=0; i<nscope; i++)
		if (s.equalsIgnoreCase(slist[i])) { j = i; break; }
	    if (ts > tlist[j]) tlist[j] = ts;
	}
	// update ATS as a whole (min)
	long t = tlist[0];
	for (int i=0; i<nscope; i++)
	    if (tlist[i] < t) t = tlist[i];
	if (t > ats) ats = t;
    }
}
