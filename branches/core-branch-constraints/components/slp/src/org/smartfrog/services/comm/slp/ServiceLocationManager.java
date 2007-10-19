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

package org.smartfrog.services.comm.slp;

import org.smartfrog.services.comm.slp.agents.ServiceAgent;
import org.smartfrog.services.comm.slp.agents.UserAgent;
import org.smartfrog.services.comm.slp.util.SLPDefaults;

import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

/**
 * The ServiceLocationManager manages the access to the SLP framework. Use this class to obtain a Locator or Advertiser
 * for a given locale.
 */
public class ServiceLocationManager {
    /** A vector of Locator objects. This vector holds the Locators that have been created through this SLM. */
    private static Vector Locators = new Vector();
    /** A vector of Advertiser objects. This vector holds the Advertisers that have been created throug this SLM. */
    private static Vector Advertisers = new Vector();
    /**
     * Properties for new Agents. The properties defined by this object will be used when creating new agents. Changing
     * this has no effect on running agents.
     */
    private static Properties properties = null;
    /** The minimum refresh interval to use when refreshing services with the DAs. */
    private static int min_refresh_interval = 0;

    /**
     * Returns the minimum refresh interval advertised by all running DAs. This method is NOT implemented. (always
     * returns 0)
     */
    public static int getRefreshInterval() throws ServiceLocationException {
        return min_refresh_interval;
    }

    /**
     * Returns all scopes supported by the running agents (that we know about). Advertisers and Locators into one
     * vector.
     */
    public static synchronized Vector findScopes() throws ServiceLocationException {
        Vector foundScopes = new Vector();

        // find all known scopes...
        for (Iterator it = Locators.iterator(); it.hasNext();) {
            getScopesFrom(((Locator) it.next()).getScopes().iterator(), foundScopes);
        }
        for (Iterator it = Advertisers.iterator(); it.hasNext();) {
            getScopesFrom(((Advertiser) it.next()).getScopes().iterator(), foundScopes);
        }

        // return the scope list
        if (foundScopes.isEmpty()) {
            foundScopes.add(SLPDefaults.DEF_CONFIG_SCOPE_LIST);
        }

        return foundScopes;
    }

    /**
     * This method returns a Locator object to use. If a Locator object with the given properties allready exists, the
     * existing locator will be returned. If no locator with the given properties exists, a new will be created. This
     * method will throw a ServiceLocationException is the creation of a new Locator fails.
     *
     * @param locale The language locale for the Locator
     * @return A locator that can be used for issuing service requests.
     */
    public static synchronized Locator getLocator(Locale locale) throws ServiceLocationException {
        /*
         Search through list of locators. If a locator supporting the
         given properties exists: Return that locator.
         Else: Return a new Locator for the given properties
        */
        if (properties == null) {
            properties = new Properties(SLPDefaults.getDefaultProperties());
        }
        properties.setProperty("net.slp.locale", locale.getLanguage());

        Iterator iter = Locators.iterator();
        UserAgent a;
        while (iter.hasNext()) {
            //System.out.println("SLM -> Has old locator");
            a = (UserAgent) iter.next();
            //System.out.println("SLM -> checking properties");
            if (a.getProperties().equals(properties)) {
                //System.out.println("SML -> Returning old locator");
                return a;
            }
        }

        //System.out.println("SLM -> Returning new locator");

        a = new UserAgent(properties);
        Locators.add(a);
        //updateScopeList();
        return a;
    }

    /**
     * This method returns an Advertiser object to use. If an Advertiser object with the given properties allready
     * exists, the existing advertiser will be returned. If no advertiser with the given properties exists, a new will
     * be created. This method will throw a ServiceLocationException is the creation of a new Advertiser fails.
     *
     * @param locale The language locale for the Advertiser
     * @return An advertiser that can be used for issuing service requests.
     */
    public static synchronized Advertiser getAdvertiser(Locale locale) throws ServiceLocationException {
        /*
         Search through list of advertisers. If an advertiser supporting the
         given properties exists: Return that advertisor.
         Else: Return a new advertiser for the given properties
        */
        if (properties == null) {
            properties = new Properties(SLPDefaults.getDefaultProperties());
        }
        properties.setProperty("net.slp.locale", locale.getLanguage());

        Iterator iter = Advertisers.iterator();
        ServiceAgent a;
        while (iter.hasNext()) {
            a = (ServiceAgent) iter.next();
            if (a.getProperties().equals(properties)) return a;
        }

        a = new ServiceAgent(properties);
        Advertisers.add(a);
        //updateScopeList();
        return a;
    }

    /**
     * Sets the properties to use for new locators and advertisers. This has no effect on allready running objects.
     *
     * @param p The new properties
     */
    public static void setProperties(Properties p) {
        properties = p;
    }

    private static void getScopesFrom(Iterator iter, Vector result) {
        while (iter.hasNext()) {
            String s = (String) iter.next();
            if (!result.contains(s)) {
                result.add(s);
            }
        }
    }
}
