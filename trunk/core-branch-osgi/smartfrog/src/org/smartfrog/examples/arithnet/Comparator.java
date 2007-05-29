/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.examples.arithnet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the comparator component.
 */ 
public class Comparator extends NetElemImpl implements Remote {
    /** Reference used to look up the equals count. */
    protected static final Reference refCount = new Reference("count");
    Vector candidateCache = new Vector(100);
    Vector targetCache = new Vector();

    // the number of times we want the component to match target and candidate
    int equalsCount = 1;
    int actualCount = 0;
    
    /**
     * Constructs Comparator object.
     * @throws RemoteException if any network and RMI error
     */
    public Comparator() throws java.rmi.RemoteException {
    }
    
    /**
     * Searches matches for a specific target.
     *
     * @param currentTarget the target 
     */
    public void searchCache(Integer currentTarget) {
        int index = candidateCache.indexOf(currentTarget); // returns the first index matching

        if (index != -1) {
            // we have a match. Remove all candidates previous to the one matching the target
            for (int i = 0; i <= index; i++) {
                candidateCache.removeElementAt(0);
            }

            // and remove the current target.
            targetCache.removeElementAt(0);
            System.out.println(" Match : candidate = target = " +
                currentTarget.intValue());

            if (++actualCount == equalsCount) {
                System.out.println("Found necessary matches " + name);
                sfTerminate(TerminationRecord.normal(nameRef));
            }
        }
    }

    /**
     * Checks if target and candidate are equal, and stop the test if they 
     * have been for 'equalsCount' times.
     * @param from String identifier target or candidate
     * @param value the integer value
     * @return -1 if target cache is empty else evaluates value in the target 
     * cache
     */
    protected int evaluate(String from, int value) {
        if (from.equals("target")) {
            targetCache.addElement(new Integer(value));
        } else {
            candidateCache.addElement(new Integer(value));
        }

        System.out.println(" Target    : " + targetCache);
        System.out.println(" Candidate : " + candidateCache);

        // compare only if the first target has arrived. Always compare afterwards.
        if (targetCache.size() != 0) {
            Integer currentTarget = (Integer) targetCache.firstElement();
            searchCache(currentTarget);

            return value - currentTarget.intValue(); // irrelevant for comparison anyway
        } else {
            return -1;
        }
    }

    /**
     * Deploys the component. 
     * @throws SmartFrogException if unable to deploy the component and read
     * attributes
     * @throws RemoteException if network or RMI error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        // read optional attribute
        equalsCount = sfResolve(refCount, equalsCount, false);
    }
}
