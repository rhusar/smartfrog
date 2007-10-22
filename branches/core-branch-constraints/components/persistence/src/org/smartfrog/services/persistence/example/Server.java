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

package org.smartfrog.services.persistence.example;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.services.persistence.recoverablecomponent.RComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class Server extends RComponentImpl implements RComponent {

    ServerSocket server;
    Socket incoming, outcoming;
    DataInputStream inStream;
    DataOutputStream outStream = null;
    int initialvalue;

    private class GetIncome extends Thread {
        public void run() {
            boolean connected;
            do {
                connected = true;
                try {
                    System.out.println((String) sfResolve("name") +
                                       " Creating reading socket");
                    incoming = server.accept();
                    inStream = new DataInputStream(incoming.getInputStream());
                } catch (Exception exc) {
                    connected = false;
                }
            } while (!connected); while (true) {
                try {
                    int v = inStream.readInt() + 1;

                    if (v == 5) {
                        System.exit(0);
                    }

                    sfReplaceAttribute("token", new Integer(v));

                    System.out.println((String) sfResolve("name") + "---" +
                                       (Integer) sfResolve("token"));
                    sleep(5000);
                    while (true) {
                        synchronized (server) {
                            if (outStream != null) {
                                break;
                            }
                        }
                    }
                    outStream.writeInt(((Integer) sfResolve("token")).intValue());
                    outStream.flush();

                    sfReplaceAttribute("token", new Integer(0));
                } catch (Exception exc) {}
            }
        }
    };

    private class GetOutcome extends Thread {
        public void run() {
            try {
                System.out.println((String) sfResolve("name") +
                                   " will create writting socket");
                boolean connected;
                do {
                    connected = true;
                    try {
                        outcoming = new Socket((String) sfResolve("neighbor"),
                                               ((Integer) sfResolve(
                                "neighborPort")).intValue());
                    } catch (Exception exc) {
                        connected = false;
                    }
                } while (!connected);
                System.out.println((String) sfResolve("name") +
                                   " created writting socket");
                synchronized (server) {
                    outStream = new DataOutputStream(new BufferedOutputStream(
                            outcoming.getOutputStream()));
                }
                if (initialvalue > 0) {
                    System.out.println("I am sending " + initialvalue);
                    outStream.writeInt(initialvalue);
                    outStream.flush();
                    sfReplaceAttribute("token", new Integer(0));
                }
            } catch (Exception exc) {}
        }
    };


    public Server() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }


    private void execute() throws SmartFrogException {
        try {
            initialvalue = ((Integer) sfResolve("token")).intValue();
            Integer portnumber = (Integer) sfResolve("portNumber");
            System.out.println(portnumber);
            server = new ServerSocket(portnumber.intValue(), 100);
            System.out.println((String) sfResolve("name") +
                               " created Server Socket");
        } catch (Exception exc) {
            throw new SmartFrogException("Impossible to resume execution.", exc);
        }
        GetIncome i = new GetIncome();
        i.start();
        GetOutcome j = new GetOutcome();
        j.start();
    }

    public synchronized void sfDeploy() throws RemoteException,
            SmartFrogException {
        System.out.println("It is going to be deployed.");
        super.sfDeploy();
    }

    public synchronized void sfStart() throws RemoteException,
            SmartFrogException {
        super.sfStart();
        execute();
    }

    public synchronized void sfRecover() throws SmartFrogException,
            RemoteException {
        super.sfRecover();
        execute();
    }

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
    }
}
