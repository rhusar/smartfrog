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

package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.ServiceLocationAttribute;
import org.smartfrog.services.comm.slp.ServiceType;
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.util.ParseTree;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Vector;

/** Implements a simple database of services. This is currently implemented by a linked list. */
class SLPDatabase {
    /** The linked list holding the entries. */
    LinkedList database;

    /** Creates a new database */
    public SLPDatabase() {
        database = new LinkedList();
    }

    /**
     * Adds an entry to the database.
     *
     * @param entry The entry to add.
     */
    public synchronized void addEntry(SLPDatabaseEntry entry) {
        database.add(entry);
    }

    /**
     * Removes an entry from the database.
     *
     * @param entry The entry to remove.
     */
    public synchronized SLPDatabaseEntry removeEntry(SLPDatabaseEntry entry) {
        database.remove(entry);
        return entry;
    }

    /**
     * Removes the entries for a given ServiceURL from the database. The URL is removed in all languages.
     *
     * @param url The ServiceURL of the entries to remove
     * @return A Vector containing the removed entries.
     */
    public synchronized boolean removeEntry(ServiceURL url, Vector scopes) {
        boolean ok = false;
        boolean equal = false;
        Iterator iter = database.iterator();
        while (iter.hasNext()) {
            equal = false;
            SLPDatabaseEntry e = (SLPDatabaseEntry) iter.next();
            if (e.getURL().equals(url)) {
                if (scopes == null) {
                    equal = true;
                } else {
                    Vector s = SLPUtil.findCommonScopes(e.getScopes(), scopes);
                    equal = (s.size() == scopes.size());
                }
                if (equal) {
                    iter.remove();
                    ok = true;
                }
            }
        }
        return ok;
    }

    /**
     * Removes the entry for the given URL in the locale lang.
     *
     * @param url  The url of the services to remove.
     * @param lang The locale of the service to remove.
     */
    public synchronized void removeEntry(ServiceURL url, Locale lang) {
        Iterator iter = database.iterator();
        while (iter.hasNext()) {
            SLPDatabaseEntry e = (SLPDatabaseEntry) iter.next();
            if (e.getURL().equals(url) && e.getLocale().equals(lang)) {
                iter.remove();
            }
        }
    }

    /**
     * Does a search through the database.
     *
     * @param type      The service type of the entry to find.
     * @param predicate The predicate to match. (may be empty).
     * @param lang      The language of the services.
     * @return A vector containing all found entries.
     */
    public synchronized Vector findEntries(ServiceType type, String predicate, Locale lang) {
        // go through list and return matching entries.
        Vector toReturn = new Vector();
        Iterator iter = database.iterator();
        boolean ignoreLang = predicate.equals("");
        boolean hasWrongLanguage = false;

        while (iter.hasNext()) {
            SLPDatabaseEntry e = (SLPDatabaseEntry) iter.next();
            if (serviceTypeMatch(e, type)) {
                // we have found an entry with the correct service type
                // check that its lifetime has not expired...
                int newLifetime = e.getRemainingLifetime();
                if (newLifetime > 0 || e.getLifetime() == ServiceURL.LIFETIME_PERMANENT) {
                    // found a valid entry.
                    // checking locale.
                    if (ignoreLang || e.getLocale().equals(lang)) {
                        // the language is correct.
                        // checking attributes.
                        if (attributeMatch(e.getAttributes(), predicate)) {
                            // We have a match
                            ServiceURL u = new ServiceURL(e.getURL().toString(), newLifetime);
                            toReturn.add(u);
                        }//match
                    }// language
                    else {
                        hasWrongLanguage = true;
                    }
                }// lifetime
                else {
                    iter.remove(); // remove expired entry
                }
            }// service type

        }//while

        if (toReturn.isEmpty() && hasWrongLanguage) toReturn = null;

        return toReturn;
    }

    /**
     * Returns a Vector containing all Service Types that match the given naming authority. If na is null: all service
     * types are returned. If na is an empty string: all servicetypes with the default (IANA) naming authority are
     * returned.
     *
     * @param na The name of the naming authority.
     * @return A vector of service types.
     */
    public synchronized Vector findServiceTypes(String na) {
        Vector toReturn = new Vector();
        Iterator dbIter = database.iterator();
        while (dbIter.hasNext()) {
            SLPDatabaseEntry e = (SLPDatabaseEntry) dbIter.next();
            ServiceType st = e.getURL().getServiceType();
            if (na == null) {
                toReturn.add(st);
            } else if (na.equals("") && st.isNADefault()) {
                toReturn.add(st);
            } else if (na.equals(st.getNamingAuthority())) {
                toReturn.add(st);
            }
        }
        return toReturn;
    }


    public synchronized Vector findServiceAttributes(ServiceType type, Locale loc, Vector tags) {
        Vector toReturn = new Vector();
        // go through database looking for services of the given type.
        for (Iterator it = database.iterator(); it.hasNext();) {
            SLPDatabaseEntry e = (SLPDatabaseEntry) it.next();
            // check service type
            if (serviceTypeMatch(e, type)) {
                // check locale
                if (e.getLocale().equals(loc)) {
                    // append attributes to result.
                    appendAttributes(e.getAttributes(), tags, toReturn);
                }
            }
        }
        return toReturn;
    }

    public synchronized Vector findServiceAttributes(ServiceURL url, Locale loc, Vector tags) {
        Vector toReturn = new Vector();
        // find the correct service
        for (Iterator it = database.iterator(); it.hasNext();) {
            SLPDatabaseEntry e = (SLPDatabaseEntry) it.next();
            // check url.
            if (e.getURL().equals(url)) {
                // check locale
                if (e.getLocale().equals(loc)) {
                    // append attributes to result.
                    appendAttributes(e.getAttributes(), tags, toReturn);
                    break; // can only have one service for a given url.
                }
            }
        }
        return toReturn;
    }

    /** Returns a list representation of the stored entries */
    public synchronized LinkedList getAllServices() {
        return (LinkedList) database.clone();
    }

    /** Returns a linked list containing all permanent registrations. */
    public synchronized LinkedList getPermanentServices() {
        LinkedList toReturn = new LinkedList();
        Iterator iter = database.iterator();
        SLPDatabaseEntry e;
        while (iter.hasNext()) {
            e = (SLPDatabaseEntry) iter.next();
            if (e.getURL().getLifetime() == ServiceURL.LIFETIME_PERMANENT) {
                toReturn.add(e);
            }
        }
        return toReturn;
    }

    /**
     * Tries to find attributes that matches the given predicate.
     *
     * @param attributes The attributes of the entry to check.
     * @param predicate  The predicate to match.
     */
    private boolean attributeMatch(Vector attributes, String predicate) {
        // check if the set of attributes matches the predicate
        if (predicate.equals("")) {
            return true; // no predicate given.
        }

        /*
        1) Build parse tree
        2) Go through parse tree and evaluate result.
        3) Return the result of the root node...
        */
        ParseTree parseTree = new ParseTree();
        parseTree.buildTree(predicate);

        //parseTree.print("");
        //System.out.println("attr: "+attributes.toString());
        return parseTree.evaluate(attributes);
    }

    /**
     * Checks if a given service type matches that in the database entry.
     *
     * @param e    The database entry to check.
     * @param type The service type we want.
     * @return true if there is a match.
     */
    private boolean serviceTypeMatch(SLPDatabaseEntry e, ServiceType type) {
        boolean areEqual = false;
        ServiceType eType = e.getType();
        if (eType.equals(type)) {
            areEqual = true;
        } else if (eType.getPrincipleTypeName().equalsIgnoreCase(type.getPrincipleTypeName())) {
            areEqual = true;
        }

        return areEqual;
    }

    private void appendAttributes(Vector attributes, Vector tags, Vector result) {
        // loop through attributes...
        for (Iterator it = attributes.iterator(); it.hasNext();) {
            ServiceLocationAttribute attr = (ServiceLocationAttribute) it.next();
            // check that ID is in tags.
            if (tags.isEmpty() || tags.contains(attr.getId())) {
                // if the result does not contain the attribute, we add it as is.
                // if the attribute is included in the result, we add any new values
                // that we may have here.
                boolean isDone = false;
                for (Iterator resIter = result.iterator(); resIter.hasNext();) {
                    ServiceLocationAttribute resAttr = (ServiceLocationAttribute) resIter.next();
                    // if id equals the one we are looking at, add any new values.
                    if (resAttr.getId().equalsIgnoreCase(attr.getId())) {
                        // merge attribute values.
                        Vector newValues = SLPUtil.mergeVectors(attr.getValues(), resAttr.getValues());
                        resAttr.setValues(newValues);
                        isDone = true;
                        break;
                    }
                }
                if (!isDone) {
                    // new attribute.
                    result.add(attr);
                }
            }
        }
    }
}
