/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.persistence.storage;


/**
 * LocalTransactionTarget is an object that can be handled by a local transaction.
 * The Transaction class cannot interact with remote references and it is 
 * possible for smartfrog components to be come remote due to the resolution process.
 * If a recoverable component passes "this" to a transaction it may be passed as a 
 * remote reference. If it passes an instance LocalTransactionTarget it will not. 
 * We can ensure that the target attribute of LocalTransactionTarget is a local
 * pointer to the component by construction.  
 */
public class LocalTransactionTarget extends Object implements TransactionTarget {
    
    TransactionTarget target;
    
    public LocalTransactionTarget(TransactionTarget rc) { 
        target = rc; 
    }

    public void lock(Transaction xact) throws TransactionException {
        target.lock(xact);
    }

    public void unlock(Transaction xact) {
        target.unlock(xact);
    }
    
    public void terminatePending() {
        target.terminatePending();
    }
}


