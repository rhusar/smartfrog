package org.smartfrog.services.slp;
/**
 * This class implements the data entry management in SLPv2 database
 * each ltag+URL is mapped to a unique Entry.
 *
 * (c) Columbia University, 2001, All Rights Reserved.
 * Author: Weibin Zhao
 */

import java.io.*;
import java.util.*;

class Entry {
    String ltag;    // language tag
    String url;        // service URL
    String type;    // service type
    String na;        // naming authority
    int lifetime;    // service lifetime (duration)
    String scope;    // service scope
    TreeMap attr;    // service attribute
    long versionTS;     // version TS from the SA for an update
    long arrivalTS;     // arrival TS at the DA for an update
    String acceptDA;    // the accept DA for the update
    long acceptTS;    // the accept TS for the update
    boolean deleted;    // deletion flag

    Entry() {
        this.attr = new TreeMap();
        deleted = false;
    }

    public int update(boolean deleted, String ltag, String url, String type,
                      int lifetime, String scope, String attr, int reg_flag,
                      long versionTS, long arrivalTS, String acceptDA, long acceptTS) {
        if (versionTS <= this.versionTS) {
            return Const.OK;        // ignore old update
        }
        if ((reg_flag & Const.fresh_flag) == 0 &&     // incremental SrvReg
                !type.equalsIgnoreCase(this.type)) {
            return Const.INVALID_UPDATE;
        }
        if ((reg_flag & Const.fresh_flag) == 0 &&     // incremental SrvReg
                !scope.equalsIgnoreCase(this.scope)) {
            return Const.SCOPE_NOT_SUPPORTED;
        }
        this.deleted = deleted;
        this.url = url;
        this.ltag = ltag;
        this.type = type;
        this.na = Util.url2na(url);
        this.lifetime = lifetime;
        this.scope = scope;
        this.versionTS = versionTS;
        this.arrivalTS = arrivalTS;
        this.acceptDA = acceptDA;
        this.acceptTS = acceptTS;
	if (attr == null || attr.equals("")) {
            return Const.OK;
        }
        if ((reg_flag & Const.fresh_flag) != 0) { // full SrvReg
            this.attr.clear();              // reset all attributes
        }
        updateAttr(attr);
        return Const.OK;
    }

    public void deletion(String tag, long versionTS,
                         String acceptDA, long acceptTS) {
        if (versionTS < this.versionTS) {
            return;        // ignore old deletion (it is "<" not "<=")
        }
        this.versionTS = versionTS;
        this.acceptDA = acceptDA;
        this.acceptTS = acceptTS;
	if (tag.equals("")) {
            deleted = true;
        } else {
            rmAttr(tag);
        }
    }

    public boolean getDeleted() {
        return deleted;
    }

    //-----------------------------------------------
    // display Entry at stdout or print it to a file
    //-----------------------------------------------
    public void prtEntry(da daf, BufferedWriter o) {
        StringBuffer buf = new StringBuffer();
        buf.append(deleted + " " + ltag + " " + type + " " + url + " " +
                lifetime + " " + scope + " " + versionTS + " " + arrivalTS +
                " " + acceptDA + " " + acceptTS + " ");
        Iterator keys = attr.keySet().iterator();
        while (keys.hasNext()) {
            String k = (String) keys.next();
            String v = (String) attr.get(k);
            if (buf.length() > 0) buf.append(",");
            buf.append("(" + k + "=" + v + ")");
        }
        if (o == null) {
            daf.append(buf.toString());
        } else { // save data to file
            buf.append("\n");
            try {
                o.write(buf.toString(), 0, buf.length());
            } catch (Exception e) {
                if (ServiceLocationManager.displayMSLPTrace) e.printStackTrace();
            }
        }
    }

    public String getURL() {
        return url;
    }

    public String getLtag() {
        return ltag;
    }

    public int getLifetime() {
        return lifetime;
    }

    public String getType() {
        return type;
    }

    public String getNA() {
        return na;
    }

    public String getScope() {
        return scope;
    }

    public long getVersionTS() {
        return versionTS;
    }

    public long getArrivalTS() {
        return arrivalTS;
    }

    public String getAcceptDA() {
        return acceptDA;
    }

    public long getAcceptTS() {
        return acceptTS;
    }

    public String getAttr(String tag) {
        Vector taglist = new Vector(5);
        if (!tag.equals("")) {
            StringTokenizer st = new StringTokenizer(tag, ",");
            while (st.hasMoreTokens()) {
                taglist.addElement(st.nextToken());
            }
        }
        StringBuffer al = new StringBuffer();
        Iterator keys = attr.keySet().iterator();
        while (keys.hasNext()) {
            String k = (String) keys.next();
            String v = (String) attr.get(k);
            if (tag.equals("") || taglist.contains(k)) {
                if (al.length() > 0) al.append(",");
                al.append("(" + k + "=" + v + ")");
            }
        }
        return al.toString();
    }

    public void rmAttr(String tag) {
        StringTokenizer st = new StringTokenizer(tag, ",");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            attr.remove(s);
        }
    }

    //-------------------------------------------------
    // parse attribute list, and update "attr" TreeMap
    // format: (attr1=value1),(attr2=value2)
    //-------------------------------------------------
    private void updateAttr(String attr) {
        String pair, name, value;
        StringTokenizer st = new StringTokenizer(attr, ",");
        while (st.hasMoreTokens()) {
            pair = removeParentheses(st.nextToken());
            StringTokenizer st1 = new StringTokenizer(pair, "=");
            if (st1.countTokens() == 2) {
                name = st1.nextToken();
                value = st1.nextToken();
                this.attr.put(name, value);
            }
        }
    }

    //--------------------------------------------
    // remove parentheses at both ends, if it has
    //--------------------------------------------
    private String removeParentheses(String s) {
        s = s.trim();         // remove whitespace at both end
        if (s.charAt(0) == '(') s = s.substring(1);
        if (s.charAt(s.length() - 1) == ')') s = s.substring(0, s.length() - 1);
        return s;
    }

    //-----------------------------------------------------------------
    // called by SrvRqst, does Entry matches (type, scope, pred, ltag)
    // predicate is in the form: (attr=val) or
    //                           (&(attr1=val1)(attr2=val2))
    // complex form of predicate will be supported later
    //-----------------------------------------------------------------
    public boolean match(String type, String scope, String pred, String ltag) {
        if (deleted) return false;
        if (!this.type.equalsIgnoreCase(type)) return false;
        if (!this.scope.equalsIgnoreCase(scope)) return false;
        if (!this.ltag.equalsIgnoreCase(ltag)) return false;
        if (pred.equals("")) return true;

        pred = removeParentheses(pred);

        // remove logic-and
        pred = pred.trim();
        if (pred.charAt(0) == '&') pred = pred.substring(1);

        // tokenize each condition
        StringTokenizer st = new StringTokenizer(pred, ")");
        int count = st.countTokens();
        String c[] = new String[count];
        for (int i = 0; i < count; i++) {
            c[i] = removeParentheses(st.nextToken());
        }

        // parse each condition
        for (int i = 0; i < count; i++) {
            st = new StringTokenizer(c[i], "=");  // assume is "="
            int op = Const.equal;
            if (st.countTokens() == 1) {
                st = new StringTokenizer(c[i], "<"); // assume is "<"
                op = Const.less;
            }
            if (st.countTokens() == 1) {
                st = new StringTokenizer(c[i], ">"); // assume is ">"
                op = Const.greater;
            }
            if (st.countTokens() == 1) return false; // error predicate
            String name = st.nextToken();
            char ch = name.charAt(name.length() - 1);
            if (ch == '<' || ch == '>') {
                name = name.substring(0, name.length() - 1);
                if (ch == '<') {
                    op = Const.lequal;
                } else {
                    op = Const.gequal;
                }
            }
            String value = st.nextToken();
            if (!attr.containsKey(name)) return false;
            String val = (String) attr.get(name);
            switch (op) {
                case Const.equal:
                    if (!value.equalsIgnoreCase(val)) return false;
                    break;
                case Const.less:
                    if (Float.parseFloat(val) >= Float.parseFloat(value)) {
                        return false;
                    }
                    break;
                case Const.greater:
                    if (Float.parseFloat(val) <= Float.parseFloat(value)) {
                        return false;
                    }
                    break;
                case Const.lequal:
                    if (Float.parseFloat(val) > Float.parseFloat(value)) {
                        return false;
                    }
                    break;
                case Const.gequal:
                    if (Float.parseFloat(val) < Float.parseFloat(value)) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}
