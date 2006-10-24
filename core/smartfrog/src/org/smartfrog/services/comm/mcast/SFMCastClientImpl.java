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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * sfMultiCastClient provides an implementation of a simple Multicast Client that
 * listens in 'mcastAddress:mcastPort' for multicast messages.
 *
 * All the messages received are added as attributes in its "servers" component description.
 *
 * It also publishes the content of the 'servers' component description as a vertor named
 * serversv.
 *
 * 'servers' and 'serversv' can be use to discover new available services, etc
 *
 */

public class SFMCastClientImpl extends PrimImpl implements Prim, SFMCastClient,
        Runnable {
    /*
      Date format
     */
    static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS z yyyy/MM/dd");

    /** Muilticast socket. */
    MulticastSocket sock;

    /** SF cached Attribute - inetaddress. */
    InetAddress address=null;
    /** SF cached Attribute - port. */
    int port = 64206;
    //String message = "Hola Mundo!";
    /** ComponentDescription object. */
    ComponentDescription cd = null;

    /** Thread object. */
    Thread action = null;

    /** Shows debug messages. */
    private boolean debug = true;
    /** String name for component used for debug messages. */
    private String myName = "SFMCastClientImpl";

    /**
     * Constructs the multicastimpl object.
     *
     *@throws RemoteException  If any network or RMI error.
     */
    public SFMCastClientImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     * Deploys the component and read attributes defined in component
     * description.
     *@throws  SmartFrogException  If any error during deployment
     *@throws RemoteException  If any network or RMI error.
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
            super.sfDeploy();

            myName = this.sfCompleteNameSafe().toString();
            readSFAttributes();
            // Configure MCast
            try {
              sock = new MulticastSocket(port);
              sock.joinGroup(address);
            }
            catch (IOException e) {
              if (sfLog().isErrorEnabled()) sfLog().error("Can't create multicast addrss: " + e,e);
              throw SmartFrogException.forward (e);
            }
    }

    /**
     *Starts client thread
     *@throws  SmartFrogException  If any error during component startup
     *@throws RemoteException  If any network or RMI error.
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        super.sfStart();
        action = new Thread(this);
        action.start();
    }

    /**
     *  Tedrminates the component. The superclass implementation of
     *  sfTerminateWith is called after the component specific termination
     *  code to implement useful termination behaviour of the component
     *@param  t  Termination record
     */
    public synchronized void sfTerminateWith(TerminationRecord t) {
        if (sfLog().isErrorEnabled()) sfLog().info("Terminating for reason: " + t.toString());
        if (action != null) {
            try {
              action.interrupt();
            } catch (Exception ex){}
        }
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods

    // Read Attributes from description

    /**
     * Reads optional and mandatory attributes
     *
     *@throws  SmartFrogException  If any error during reading attributes
     *@throws RemoteException  If any network or RMI error.
     */
    private void readSFAttributes() throws SmartFrogException, RemoteException {
        debug = sfResolve(ATR_DEBUG, debug, false);
        //
        // Mandatory attributes.
        try {
            address = sfResolve(ATR_MCASTADDRESS, address, true);
            cd = sfResolve (ATR_SERVERS, cd, true);
            //True to Get exception thown!
        } catch (SmartFrogResolutionException e) {
            if (sfLog().isErrorEnabled()) sfLog().error("Failed to read mandatory attribute "+ ", Error:"+ e.toString(),e);
            throw e;
        }
        port = sfResolve(ATR_MCASTPORT, port, false);
    }

    // Main component action methods

    /**
     *  Main processing method (implements 'Runnable').
     */
    public void run() {
        // MCast Client
        byte buf[] = new byte[1500];
        ByteArrayInputStream b_in = new ByteArrayInputStream(buf);
        int packetCount = 0;
        DatagramPacket rcvPacket = new DatagramPacket(buf, buf.length);
        if (sfLog().isInfoEnabled()) sfLog().info("Ready to receive... add:"+this.address+" port:"+port);
        while (true) {
          try {
            try {
               cd = sfResolve (ATR_SERVERS, cd, true);
            } catch (SmartFrogResolutionException sfex){
            }
            StringBuffer str = new StringBuffer();
            sock.receive(rcvPacket);
            packetCount++;
            str.append("packet: " + packetCount);
            str.append(", "+rcvPacket.getAddress() + ":" + rcvPacket.getPort());
            ObjectInputStream o_in = new ObjectInputStream(b_in);
            Object o = o_in.readObject();
            str.append(", object: " + o.getClass().toString() + ", "+
                o.toString());
            if (sfLog().isInfoEnabled()) sfLog().info(str.toString());
            //send replay
            //DatagramPacket sendReply = new DatagramPacket(rcvPacket.getData()
        //,rcvPacket.getLength(), rcvPacket.getAddress(), rcvPacket.
        //getPort());
            //sock.send(sendReply);
            //log("run","replaySent to " + packetCount);
            // Add packet to data set
            try {
              if (o instanceof InetAddress) {
                cd.sfReplaceAttribute( ( (InetAddress) o).getCanonicalHostName(),
                                      o);
              } else {
                cd.sfReplaceAttribute(o.toString(), o);
              }
              //this.sfReplaceAttribute(this.ATR_SERVERS,cd);
            } catch (SmartFrogRuntimeException ex1) {
               //Logger.log(ex1);
               if (sfLog().isErrorEnabled()){
                 sfLog().error(ex1);
               }
            }
            try {
              Vector v = new Vector();
              for (Enumeration h = cd.sfContext().keys();
                  h.hasMoreElements(); ) {
                    v.add(h.nextElement());
              }
              this.sfReplaceAttribute(ATR_SERVERS+"v", v);
            } catch (Exception ex){
                if (sfLog().isErrorEnabled()) sfLog().error(" Error: "+this.sfCompleteNameSafe()+" "+ ex.getMessage(),ex);
            }


          }
          catch (IOException e) {
            if (sfLog().isErrorEnabled()) sfLog().error("Problems receiving packet", e);
         } catch (ClassNotFoundException ex){
            if (sfLog().isErrorEnabled()) sfLog().error("Problems getting object from received packet", ex);
         }
          rcvPacket.setLength(buf.length);
          b_in.reset(); // reset so next read is from start of byte[] again
        }
    }

}


