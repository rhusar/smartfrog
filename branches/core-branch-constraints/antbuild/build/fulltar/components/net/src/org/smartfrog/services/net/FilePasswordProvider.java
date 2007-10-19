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

package org.smartfrog.services.net;

import java.rmi.RemoteException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * Implementation of PasswordProvider which reads the password from a text 
 * file.
 * @author Ashish Awasthi
 */ 
public class FilePasswordProvider extends PrimImpl implements Prim, 
                                                    PasswordProvider     {

    private String passwordFile = "";
    private String password = "";
    // attribute name for password file
    private final String PASSWORD_FILE="passwordFile";
    
    /**
     * Constructs FilePasswordProvider object.
     *
     * @throws RemoteException in case of network/rmi error
     */
    public FilePasswordProvider() throws RemoteException {
    }
    
    /**
     * Reads SmartFrog attributes and deploys FilePasswordProvider component.
     *
     * @throws SmartFrogException in case of error in deploying or reading the 
     * attributes
     * @throws RemoteException in case of network/emi error
     */ 
    public synchronized void sfDeploy() throws SmartFrogException, 
                                                            RemoteException {
        //read SmartFrog Attributes
        super.sfDeploy();
        passwordFile = sfResolve(PASSWORD_FILE, passwordFile, true);
    }
    /**
     * Reads password from password file.
     * @return password
     */
    public String getPassword() throws SmartFrogException, RemoteException {
        BufferedReader passwdReader = null;
        String passwd = null;
        try {
            passwdReader = new BufferedReader(new FileReader(passwordFile));
            if (passwdReader != null) {
                passwd = passwdReader.readLine();
            }
            if ((passwd != null) && (!passwd.equals("")) ) {
                passwd = passwd.trim();
            } 
        }catch (IOException ioex) {
                throw new SmartFrogException(ioex);
        }finally {
            try {
                if (passwdReader != null) {
                    passwdReader.close();
                }
            }catch (IOException iox) {
                //ignore
            }
        }
        return passwd;        
    }
}
