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

package org.smartfrog.examples.dynamicwebserver.stresstest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Vector;

import org.smartfrog.examples.dynamicwebserver.gui.graphpanel.DataSource;
import org.smartfrog.examples.dynamicwebserver.logging.LogWrapper;
import org.smartfrog.examples.dynamicwebserver.logging.Logger;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * Description of the Class
 */
public class StressTesterImpl extends PrimImpl implements Prim, Runnable,
    StressTester, DataSource {
    String name;
    String host = "localhost";

    //String[] hosts = null;
    Vector hosts = null;
    int port = 80;
    String page;

    // factor * frecuency = sleep time.
    int frequency;
    int factor = 10;
    int timeSleep;
    int hits = 1;

    //Data value to represent in a graph
    int data = 0;

    // When frequency is set to this value then sleep = frequency * frequency * factor -> Extremely slow ...
    int maxValue = 100;

    // Management for load Generator
    StressControlGui controlGui = null;
    private String requestLine;

    //notify the thread to terminate
    boolean terminated = false;
    boolean sleeping = false;
    Thread downloader = null;

    // Connection to remote host!
    Socket client;
    boolean connected = false;
    int[] lastHits = null;

    // Random number generator for randomized sleep...
    Random random = new Random();
    LogWrapper logger;

    /**
     * Constructor for the StressTesterImpl object
     *
     * @exception RemoteException Description of the Exception
     */
    public StressTesterImpl() throws RemoteException {
        lastHits = new int[1];
        lastHits[0] = 0;
    }

    /**
     * Reads SF description.
     *
     * @exception SmartFrogResolutionException Description of the Exception
     * @throws RemoteException DOCUMENT ME!
     */
    private void readSFAttributes()
        throws SmartFrogResolutionException, RemoteException {
        //
        // Mandatory attributes.
        //
        page = sfResolve(PAGE, page, true);
        this.frequency = sfResolve(FREQUENCY, frequency, true);
        timeSleep = (this.frequency * factor);

        //
        // Optional attributes.
        //
        host = sfResolve(HOST, host, false);
        hosts = sfResolve(HOSTS, hosts, false);
        port = sfResolve(PORT, port, false);
        factor = sfResolve(FACTOR, factor, false);
        controlGui = (StressControlGui) sfResolve(CONTROLGUI, false);

        hits = sfResolve(NUMHITS, hits, false);

        if (hits >= 1) {
            lastHits = new int[hits];
        }

        if (hosts == null) {
            hosts = new Vector();

            if (host != null) {
                hosts.add(host);
            }
        }

        logger = new LogWrapper((Logger) sfResolve(LOGTO, false));
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     */
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("name: " + name);
        str.append(", host: " + host);
        str.append(", hosts: " + hosts);
        str.append(", port: " + port);
        str.append(", page: " + page);
        str.append(", frequency: " + frequency);
        str.append(", factor: " + factor);
        str.append(", controlGui: " + controlGui);
        str.append(", hits: " + hits);

        return (str.toString());
    }

    /**
     * Description of the Method
     */
    public void stop() {
        terminated = true;
    }

    /**
     * Sets the hits attribute of the StressTesterImpl object
     *
     * @param s The new hits value
     */
    public void setHits(int s) {
        hits = s;
    }

    /**
     * Standard sfDeploy()
     *
     * @exception SmartFrogException Description of the Exception
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        name = sfCompleteName().toString();
        readSFAttributes();

        if (controlGui != null) {
            boolean done = controlGui.registerStressClient(this);

            if (done) {
                logger.log(name, "registered to controlGui");
            } else {
                logger.log(name, "registered to controlGui. FAILED");
            }
        }

        requestLine = "GET " + page + " HTTP/1.0";
    }

    /**
     * Standard sfStart()
     *
     * @exception SmartFrogException Description of the Exception
     * @throws RemoteException DOCUMENT ME!
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        downloader = new Thread(this);
        downloader.start();
    }

    /**
     * Standard sfTerminateWith()
     *
     * @param tr Description of the Parameter
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        //terminate the thread nicely if needed
        //... could interrupt its sleep, but not necessary
        //in general, since the thread initiates the termination, this will be irrelevant
        //but do so in case termination is through error or management action
        terminated = true;

        if (controlGui != null) {
            try {
                controlGui.deregisterStressClient(this);
            } catch (Exception ex) {
            }
        }

        try {
            downloader.interrupt();
        } catch (Exception ex) {
        }

        downloader = null;
        super.sfTerminateWith(tr);
    }

    /**
     * get the hosts that should be tested
     *
     * @return DOCUMENT ME!
     */
    protected Vector getHosts() {
        return (Vector) hosts.clone();
    }

    /**
     * Main processing method for the StressTesterImpl object
     */
    public void run() {
        long beforeTime = 0;
        long afterTime = 0;

        Socket clientSocket;

        int hitPosition = 0;
        int total = 0;
        int x = 0;
        int sizeHitList = lastHits.length;

        boolean firstDataValue = true;
        String newHost = "";
        Vector newHosts = null;

        while (!terminated) {
            try {
                // sleep until a proper timeout
                // changes in times result in new sleep
                sleeping = true;

                while (sleeping) {
                    try {
                        if (this.frequency < maxValue) {
                            int targetSleep = (this.frequency * factor);
                            logger.log(name,
                                "freq " + frequency + " factor " + factor);

                            if (targetSleep <= 0) {
                                timeSleep = 0;
                            } else {
                                int rnd = random.nextInt(targetSleep);
                                logger.log(name,
                                    "sleep numbers are " + rnd + " " +
                                    targetSleep + " " + afterTime + " " +
                                    beforeTime);
                                timeSleep = (rnd + (targetSleep / 2)) -
                                    (int) (afterTime - beforeTime);

                                if (timeSleep < 0) {
                                    timeSleep = 0;
                                } else if (timeSleep > (2 * targetSleep)) {
                                    logger.log(name,
                                        "emergency correction - attempted sleep " +
                                        timeSleep);
                                    timeSleep = targetSleep;
                                }
                            }
                        } else {
                            timeSleep = (1000 * 60 * 20);
                        }

                        logger.log(name, "sleeping for " + timeSleep);

                        if ((timeSleep) > 0) {
                            Thread.sleep(timeSleep);
                        }

                        sleeping = false;
                        logger.log(name, "awake again...");
                    } catch (InterruptedException ie) {
                        logger.err(name,
                            "interrupted sleep - returning to sleep ," +
                            ie.getMessage());
                    } catch (Exception ex) {
                        logger.err(name,
                            "Setting time to sleep: " + ex.getMessage());
                    }
                }

                newHosts = getHosts();

                boolean succeeded = false;

                while (!terminated && !succeeded && (newHosts.size() > 0)) {
                    // try them all in random order until one works...
                    int hostId = random.nextInt(newHosts.size());

                    // should be 0 to size-1
                    newHost = (String) newHosts.elementAt(hostId);
                    newHosts.removeElementAt(hostId);
                    logger.log(name, "trying server " + newHost);

                    beforeTime = System.currentTimeMillis();
                    clientSocket = connect(newHost, this.port);

                    if (clientSocket != null) {
                        logger.log(name, "connected to server " + newHost);
                        succeeded = true;
                        afterTime = handleConnection(newHost, clientSocket);

                        if (afterTime >= beforeTime) {
                            int newTime = ((int) (afterTime - beforeTime));

                            if (firstDataValue) {
                                firstDataValue = false;

                                for (x = 0; x < sizeHitList; x++) {
                                    lastHits[x] = newTime;
                                }
                            }

                            if (newTime == 0) {
                                break;
                            }

                            lastHits[hitPosition] = newTime;
                            total = 0;

                            for (x = 0; x < sizeHitList; x++) {
                                total = total + lastHits[x];
                            }

                            this.data = total / sizeHitList;
                            hitPosition++;

                            if (hitPosition >= sizeHitList) {
                                hitPosition = 0;
                            }
                        } else {
                            logger.log(name,
                                "potential problem coming up - after is early than before!");
                        }

                        logger.log(name,
                            "> Access to Host: " + newHost + ":" + port + ", " +
                            this.requestLine + ", " + ", elapseTime" + ":" +
                            this.data + ", toSleep:" + timeSleep);
                    }
                }
            } catch (Throwable ex) {
                logger.log(name,
                    "In while(!terminated) loop: " + newHost + ", " +
                    ex.getMessage());
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param targetHost Description of the Parameter
     * @param uriSocket Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private long handleConnection(String targetHost, Socket uriSocket) {
        try {
            if (uriSocket == null) {
                return 0;
            }

            PrintWriter out = getWriter(uriSocket);
            BufferedReader in = getReader(uriSocket);
            out.println(requestLine + "\n");
            out.print("\n");

            String line = in.readLine();
            logger.log(name, "   Response: " + line + "\n");

            while ((line = in.readLine()) != null) {
            }

            return System.currentTimeMillis();
        } catch (Exception e) {
            logger.err(name, "Error: " + e.getMessage());
            connected = false;

            return 0;
        }
    }

    /**
     * Description of the Method
     *
     * @param targetHost Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private boolean checkHost(String targetHost) {
        try {
            InetAddress.getByName(targetHost);

            return (true);
        } catch (java.net.UnknownHostException uhe) {
            logger.err(name, "Bogus host: " + targetHost);

            return (false);
        }
    }

    /**
     * Establishes the connection, then passes the socket to handleConnection.
     *
     * @param targetHost Description of the Parameter
     * @param targetPort Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public Socket connect(String targetHost, int targetPort) {
        try {
            //logger.log(name, "Connecting to:"+host+":"+port);
            if (client != null) {
                client.close();
            }

            client = new Socket(targetHost, targetPort);
            connected = true;

            return client;

            //handleConnection(client);
        } catch (java.net.UnknownHostException uhe) {
            logger.err(name, "Unknown host: " + targetHost + ", " + uhe.getMessage());
        } catch (IOException ioe) {
            logger.err(name,
                targetHost + ":" + targetPort + ", IOException: " + ioe.getMessage());
        }

        connected = false;

        return null;
    }

    /**
     * The hostname of the server we're contacting.
     *
     * @return The host value
     */
    public String getHost() {
        return (host);
    }

    /**
     * The port connection will be made on.
     *
     * @return The port value
     */
    public int getPort() {
        return (port);
    }

    /**
     * Make a BufferedReader to get incoming data.
     *
     * @param s Description of the Parameter
     *
     * @return The reader value
     *
     * @exception IOException Description of the Exception
     */
    public static BufferedReader getReader(Socket s) throws IOException {
        return (new BufferedReader(new InputStreamReader(s.getInputStream())));
    }

    /**
     * Make a PrintWriter to send outgoing data. This PrintWriter will
     * automatically flush stream when println is called.
     *
     * @param s Description of the Parameter
     *
     * @return The writer value
     *
     * @exception IOException Description of the Exception
     */
    public static PrintWriter getWriter(Socket s) throws IOException {
        // 2nd argument of true means autoflush
        return (new PrintWriter(s.getOutputStream(), true));
    }

    /**
     * Sets the page attribute of the StressTesterImpl object
     *
     * @param newPage The new page value
     *
     * @exception RemoteException Description of the Exception
     */
    public void setPage(String newPage) throws RemoteException {
        this.requestLine = "GET " + newPage + " HTTP/1.0";
    }

    /**
     * Sets the frequency attribute of the StressTesterImpl object
     *
     * @param newFrequency The new frequency value
     *
     * @exception RemoteException Description of the Exception
     */
    public void setFrequency(int newFrequency) throws RemoteException {
        this.frequency = newFrequency;

        //this.timeSleep = (this.frequency * factor);
        logger.log(name, " frequency updated: " + this.frequency);

        if (sleeping) {
            downloader.interrupt();
        }
    }

    /**
     * Gets the data attribute of the StressTesterImpl object
     *
     * @return The data value
     */
    public int getData() {
        //logger.log(name, "> "+this.getNameID()+", getData = "+ this.data);
        return this.data;
    }
}
