/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.locator.test;


import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.smartfrog.services.anubis.locator.AnubisListener;
import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisProvider;
import org.smartfrog.services.anubis.locator.AnubisValue;
import org.smartfrog.services.anubis.locator.Locator;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

public class Test
    extends PrimImpl
    implements Prim {


    class Listener extends AnubisListener {
        public Listener(String name) { super(name); }
        public void display() {
            String str = "Listener " + getName() + " has values:";
            for(Iterator iter = values().iterator();
                iter.hasNext();
                str += " " + iter.next().toString() );
            driver.println(str);
        }
        public void newValue(AnubisValue v) {
            display();
            if( v.getValue().equals("throw") ) {
                // this expression intentionally leads to an exception
                int x = ((Integer)null).intValue();
            } else if ( v.getValue().equals("wait") ) {
                // this expression intentially leads to a pause
                try { wait(1000); }
                catch(Exception ex) {}
            }
        }
        public void removeValue(AnubisValue v)   {
            driver.println("Removing value " + v.toString());
            display();
        }
    }

    class Provider extends AnubisProvider {
        Provider(String name) { super(name); }
    }

    private Map providers;
    private Map listeners;
    private AnubisLocator locator;
    private Driver        driver;
    private Reference     componentReference;

    public Test() throws RemoteException {
        locator = null;
        driver = null;
        providers = new HashMap();
        listeners = new HashMap();
    }

    public void terminate() {
        sfTerminate(new TerminationRecord("normal",
                "Termination from within Anubis Locator test console",
                componentReference) );
    }

    /**
     * Implementation of Prim interface.
     *
     * @throws Exception
     */
    public void sfDeploy() throws SmartFrogException, RemoteException  {
        super.sfDeploy();
        try {
//            locator = (AnubisLocator)sfResolve("locator");
            Object obj = sfResolve("locator");
//            System.out.println("Lookup on loctor interface returned: " + obj);
            locator = (AnubisLocator)obj;
            componentReference = sfCompleteName();
            driver = new Driver(this, "TestDriver: " + componentReference);
            driver.setVisible(true);
        } catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }


    /**
     * Implementation of Prim interface.
     *
     * @throws Exception
     */
    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }


    /**
     * Implementation of Prim interface.
     *
     * @param tr
     */
    public void sfTerminateWith(TerminationRecord tr) {
        try {
            if( driver != null )
                driver.setVisible(false);
            removeLocal();
            removeGlobal();
            super.sfTerminateWith(tr);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String retrieveName(String str) {
        String trimmed = str.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return (spaceIndex == -1) ? trimmed : trimmed.substring(0, spaceIndex);
    }

    public String retrieveValue(String str) {
        String trimmed = str.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return (spaceIndex == -1) ? "dummy" : trimmed.substring(spaceIndex).trim();
    }


    public void addProvider(String str) {

        String name = retrieveName(str);
        String value = retrieveValue(str);

        if( name.equals("") ) {
            driver.println("Need a name");
            return;
        }

        Provider provider = (Provider)providers.get(name);
        if( provider == null ) {
            provider = new Provider(name);
            provider.setValue(value);
            providers.put(name, provider);
            locator.registerProvider(provider);
            driver.println("Registered provider for " + name + " with value " + value);
        } else {
            provider.setValue(value);
            driver.println("Set value of provider " + name + " to " + value);
        }
    }


    public void removeProvider(String str) {

        String name = retrieveName(str);

        if( name.equals("") ) {
            driver.println("Need a name");
            return;
        }

        if( providers.containsKey(name) ) {
            Provider p = (Provider)providers.remove(name);
            locator.deregisterProvider(p);
            driver.println("Deregistered provider for " + name);
        } else
            driver.println("Can't deregister a provider that does not exist");
    }

    public void rapidStates(String str) {

        StringTokenizer tokens = new StringTokenizer(str);

        if( tokens.countTokens() < 2 ) {
            driver.println("Error - Usage: <name> <sequence of states>");
            return;
        }

        String name = tokens.nextToken();
        if( !providers.containsKey(name) ) {
            driver.println("Error - unknown provider name");
            return;
        }

        Provider provider = (Provider)providers.get(name);
        while( tokens.hasMoreTokens() )
            provider.setValue(tokens.nextToken());
    }


   public void nonBlockAddListener(String str) {

       String name = retrieveName(str);

       if( name.equals("") ) {
           driver.println("Need a name");
           return;
       }

       Listener l = new Listener(name);
       if( listeners.containsKey(name) ) {
           ((Set)listeners.get(name)).add(l);
       } else {
           Set s = new HashSet();
           s.add(l);
           listeners.put(name, s);
       }

       locator.registerListener(l);
   }



    public void removeListener(String str) {

        String name = retrieveName(str);

        if( name.equals("") ) {
            driver.println("Need a name");
            return;
        }

        if( listeners.containsKey(name) ) {

            Set s = (Set)listeners.get(name);
            Listener l = (Listener)s.iterator().next();
            s.remove(l);
            if( s.isEmpty() )
                listeners.remove(name);

            locator.deregisterListener(l);
            driver.println("Deregistered listener for " + name + " -- " + s.size() + " left");

        } else {
            driver.println("There are no listeners for " + name);
        }
    }



    public void showGlobal() {
        ((Locator)locator).global.showDebugFrame();
    }
    public void removeGlobal() {
        if( locator!=null && ((Locator)locator).local!=null )
            ((Locator)locator).global.removeDebugFrame();
    }
    public void showLocal() {
        ((Locator)locator).local.showDebugFrame();
    }
    public void removeLocal() {
        if( locator!=null && ((Locator)locator).local!=null )
            ((Locator)locator).local.removeDebugFrame();
    }

}
