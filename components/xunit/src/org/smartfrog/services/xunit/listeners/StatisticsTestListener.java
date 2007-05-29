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
package org.smartfrog.services.xunit.listeners;

import org.smartfrog.services.xunit.base.TestListenerFactory;
import org.smartfrog.services.xunit.serial.Statistics;

import java.rmi.RemoteException;

/**
 * The stats listener collects statistics, and can publish them to a web page
 * created 28-Oct-2005 17:47:25
 */


public interface StatisticsTestListener extends TestListenerFactory {

    int getTestsStarted() throws RemoteException ;

    int getTestsRun() throws RemoteException ;

    int getFailures() throws RemoteException ;

    int getErrors() throws RemoteException ;

    int getUnsuccessfulTests() throws RemoteException ;

    Statistics getStatistics() throws RemoteException;
}
