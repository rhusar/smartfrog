/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.junit.listeners;

import org.smartfrog.services.junit.TestInfo;

import java.rmi.RemoteException;
import java.io.File;

/**
 * This class listens to tests on a single host.
 * The XML Listener forwards stuff to it.
 * Date: 12-Jun-2004
 * Time: 00:28:44
 */
public class OneHostXMLListener implements XmlListener {

    /**
     * file we save to
     */
    File directory;

    /** name of host */
    String hostname;


    /**
     * Listen to stuff coming from a single host.
     * @param hostname
     * @param basedir directory to create a new subdir from.
     */
    public OneHostXMLListener(String hostname, File basedir) {
        assert hostname!=null;
        assert basedir!=null;
        //create our new directory
        directory=new File(basedir,hostname);
        directory.mkdirs();
        this.hostname = hostname;
    }

    /**
     * An error occurred.
     */
    public void addError(TestInfo test) throws RemoteException {
    }

    /**
     * A failure occurred.
     */
    public void addFailure(TestInfo test) throws RemoteException {
    }

    /**
     * A test ended.
     */
    public void endTest(TestInfo test) throws RemoteException {
    }

    /**
     * A test started.
     */
    public void startTest(TestInfo test) throws RemoteException {
    }

    /**
     * equality test does hostname and dir
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !(o instanceof OneHostXMLListener) ) {
            return false;
        }

        final OneHostXMLListener oneHostXMLListener = (OneHostXMLListener) o;

        if ( !hostname.equals(oneHostXMLListener.hostname) ) {
            return false;
        }
        if ( !directory.equals(oneHostXMLListener.directory) ) {
            return false;
        }

        return true;
    }

    /**
     * hashcode is hashcode of hostname. Lets us search using the hashcode
     * as the 1ary key.
     * @return
     */
    public int hashCode() {
        return hostname.hashCode();
    }
}
