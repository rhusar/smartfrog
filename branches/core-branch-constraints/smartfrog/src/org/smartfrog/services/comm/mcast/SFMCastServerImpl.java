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

package org.smartfrog.services.comm.mcast;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * sfMultiCastServer provides an implementation of a simple Multicast Server that
 * sends multicast messages to 'mcastAddress:mcastPort' every 5 secs.
 *
 * Options: " onlyInRoot": terminates if it is not deployed in a rootProcessCompound
 *          " message": message to advertise.
 *
 */

public class SFMCastServerImpl extends PrimImpl implements Prim, SFMCastServer,
        Runnable {
    /** Multicast Socket. */
    private MulticastSocket sock = null;

    /** SF cached Attribute - inetaddress. */
    InetAddress address = null;
    /** SF cached Attribute - port. */
    int port = 64206;
    /** Message. */
    Object message = "Hola Mundo!";
    /** Thread object. */
    Thread action = null;

    /** Shows debug messages. */
    private boolean debug = true;
    /** String name for component used for debug messages. */
    private String myName = "SFMCastServerImpl";
    /** Flag indicating if server is started in root process or not. */
    boolean onlyInRoot = true;

    /**
     *Constructs the multicast server object.
     *
     *@throws  RemoteException If network or RMI error
     */
    public SFMCastServerImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *  Reads attributes and configures thread. The superclass implementation
     *  of sfDeploy is called before the  component specific initialization
     *  code  and configuring thread to maintain correct behaviour of
     *  initial deployment and starting the heartbeat monitoring of this
     *  component.
     *@throws  SmartFrogException If unable to deploy the component
     *@throws  RemoteException If network or RMI error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
        super.sfDeploy();
        TerminationRecord termR;
                String processName = sfResolve(SmartFrogCoreKeys.SF_PROCESS, "", false);
                if ((onlyInRoot)&& (processName.equals(SmartFrogCoreKeys.SF_ROOT_PROCESS))) {
                    termR = new TerminationRecord(TerminationRecord.NORMAL,
                                                  "Not deployed in rootProcess",
                                                  this.sfCompleteName());

                    TerminatorThread terminator = new TerminatorThread(this,
                    termR);
                    terminator.start();
                    return;
                }

        myName = this.sfCompleteNameSafe().toString();
        readSFAttributes();
        //creates thread
        this.action = new Thread(this);
        //creates multicast socket
        try {
            sock = new MulticastSocket(port);
            sock.joinGroup(address);
            log("sfDeploy","Created mcast socket:" + address.toString());
        }
        catch (Exception e) {
            error("sfDeploy","Can't create multicast address: " +e.toString());
            throw SmartFrogException.forward(e);
        }



    }

    /**
     *  Starts the thread.The superclass implementation of sfStart is called
     *  before the component specific code (starting thread) to maintain
     *  correct behaviour of starting the active threads for components
     * @throws  SmartFrogException  If any error
     * @throws RemoteException in case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        super.sfStart();

        if (sock != null) {
          try {
            action.start();
          }
          catch (Exception e) {
            exception("Problems starting thread. " , e);
          }
          //this.receiveblock();
        }
    }

    /**
     *  sfTerminate
     *  The superclass implementation of sfTerminateWith is called after the
     *  component specific termination code to implement useful termination
     *  behaviour of the component
     *@param  t  termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        log("sfTerminateWith", " Terminating for reason: " + t.toString());

        if (action != null) {
            action.interrupt();
        }

        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    // Read Attributes from description

    /**
     *  Reads optional and mandatory attributes.
     *
     *@throws  SmartFrogException if error in reading attributes
     *@throws  RemoteException if RMI or network error
     */
    private void readSFAttributes() throws SmartFrogException, RemoteException {
      debug = sfResolve(ATR_DEBUG, debug, false);
      //
      // Mandatory attributes.
      try {
          address = sfResolve(ATR_MCASTADDRESS, address, true);
          //True to Get exception thown!
      } catch (SmartFrogResolutionException e) {
          error("readSFAttributes","Failed to read mandatory attribute "+
                 ", Error:"+ e.toString());
          throw e;
      }
      port = sfResolve(ATR_MCASTPORT, port, false);
      message = sfResolve(ATR_MESSAGE, false);
      onlyInRoot = sfResolve(ATR_ONLYINROOT, onlyInRoot, false);
    }

    // Main component action methods

    /**
     *  Main processing method (implements 'Runnable').
     */
    public void run() {
        //byte[] msg = {1, 2, 3};
        try {
            ByteArrayOutputStream b_out = new ByteArrayOutputStream();
            ObjectOutputStream o_out = new ObjectOutputStream(b_out);
            o_out.writeObject(this.message);
            byte[] msg = b_out.toByteArray();

            while (true) {
                DatagramPacket packet = new DatagramPacket(msg, msg.length,
                    address,
                    port);
                sock.send(packet);
                log("run", "Sent advert, going to sleep ...");
                Thread.sleep(5 * 1000);
                //Refresh content for message
                message = sfResolve(ATR_MESSAGE, false);
            }
        }
        catch (Exception e) {
            exception("Error sending advert", e);
        }
    }

    // Utility methods
    /**
     * Logs error mesasge at the standard err stream.
     * @param method Name of the method
     * @param message Error Message
     */
    private void error(String method, String message) {
        if (debug) {
            StringBuffer msg = new StringBuffer();
            msg.append (myName);
            msg.append (".");
            msg.append (method);
            msg.append ( " [" );
            msg.append ((new SimpleDateFormat("HH:mm:ss.SSS z, yyyy/MM/dd").
                    format(new Date())));
            msg.append ("]> ");
            msg.append (message);
            System.err.println(msg.toString());
        }
    }
    /**
     * Logs mesasge at the standard out stream.
     * @param method Name of the method
     * @param message Log message
     */
    private void log(String method, String message) {
        if (debug) {
            StringBuffer msg = new StringBuffer();
            msg.append (myName);
            msg.append (".");
            msg.append (method);
            msg.append ( " [" );
            msg.append ((new SimpleDateFormat("HH:mm:ss.SSS z, yyyy/MM/dd").
                    format(new Date())));
            msg.append ("]> ");
            msg.append (message);
            System.out.println(msg.toString());
        }
    }
    /**
     * Logs exception with stack trace at the standard err stream.
     * @param method Name of the method
     * @param exception The exception object
     */
    private void exception(String method, Throwable exception) {
        if (debug) {
            StringBuffer msg = new StringBuffer();
            msg.append (myName);
            msg.append (".");
            msg.append ( "Exception");
            msg.append ( " [" );
            msg.append ((new SimpleDateFormat("HH:mm:ss.SSS z, yyyy/MM/dd").
                    format(new Date())));
            msg.append ("]> ");
            msg.append (message);
            msg.append("\n StackTrace: ");
            msg.append(exception.getStackTrace().toString());
            System.err.println(msg.toString());
        }
    }
    /**
     * Receives data in a block and increments the data packet count.
     */
    public void receiveblock() {
      byte buf[] = new byte[1500];
      int packetCount = 0;

      while (true) {
        DatagramPacket rcvPacket = new DatagramPacket(buf, buf.length);
        try {
          sock.receive(rcvPacket);
          packetCount++;
          //System.out.println("packet: " + packetCount);
          log ("receiveblock",rcvPacket.getAddress() + ":" +
              rcvPacket.getPort());
        }
        catch (IOException e) {
          exception("Problems receiving packet", e);
        }
      }
    }


/*


     class mcastServer implements Runnable {

       public mcastServer() {

       }



       public static void main(String[] args) {

       }
     }

*/
}
