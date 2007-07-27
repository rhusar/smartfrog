/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.messages.SLPSrvReqMessage;
import org.smartfrog.services.comm.slp.network.SLPTcpClient;
import org.smartfrog.services.comm.slp.network.SlpUdpCallback;
import org.smartfrog.services.comm.slp.network.SlpUdpClient;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * This class is used to send messages that should wait for a reply. These are: - SrvReq, SrvReq, SrvTypeReq, SrvDeReg.
 * Objects of this class are not meant to be shared by multiple SLP agents. Doing that may result in strange results...
 */
class SLPMessageSender implements SlpUdpCallback {
    private SlpUdpClient communicator = null;
    private ServiceLocationEnumeration results = null;
    private int mtu, config_retry;
    private Timer timer = null;

    private short stupidException = 0;

    // the request...
    private int currentTimeout = 0;
    private SLPMessageHeader theRequest = null;
    private DatagramPacket thePacket = null;
    private volatile boolean amWaiting = true;

    private Vector tcpTargets = new Vector();

    private class resendTask extends TimerTask {
        public void run() {
            timerResendCallback();
        }
    }

    private class timeoutTask extends TimerTask {
        public void run() {
            timerTimeoutCallback();
        }
    }

    private SLPAgent owner = null;

    private Object wtSync = new Object();

    /**
     * Creates a new SLPMessageSender.
     *
     * @param owner      The SLP Agent using the message sender.
     * @param mtu        The mtu for SLP messages.
     * @param retryDelay The initial timeout before retransmitting the message.
     * @param res        The object that is to hold the result of the request.
     */
    SLPMessageSender(SLPAgent owner, int mtu, int retryDelay, ServiceLocationEnumeration res) {
        this.owner = owner;
        this.mtu = mtu;
        config_retry = retryDelay;
        results = res;
    }

    /**
     * Sends an SLP message and wait for replies. The SlpUdpclient used by the message sender can be shared by other
     * message sender objects (in multiple threads).
     *
     * @param msg    The message to send.
     * @param addr   The address to send the message to.
     * @param port   The port to send the message to.
     * @param toWait The maximum time to wait for replies.
     * @comm The SlpUdpClient used to send the message.
     */
    protected void
    sendSLPMessage(SLPMessageHeader msg, String addr, int port, int toWait,
                   SlpUdpClient comm) throws ServiceLocationException {

        stupidException = 0;
        timer = null;
        communicator = comm;
        tcpTargets.clear();
        owner.logMessage("Sending Message:", msg);
        try {
            InetAddress toAddress = InetAddress.getByName(addr);
            DatagramPacket p = SLPUtil.createDatagram(msg, toAddress, port);
            theRequest = msg;
            thePacket = p;
            currentTimeout = config_retry;

            // timing..
            timer = new Timer();
            timer.schedule(new resendTask(), currentTimeout);
            timer.schedule(new timeoutTask(), toWait);

            boolean sentOK = false;
            while (!sentOK) {
                sentOK = communicator.send(p, msg.getXID(), this);
                if (!sentOK) {
                    msg.setXID(msg.nextXID());
                    p = SLPUtil.createDatagram(msg, toAddress, port);
                    thePacket = p;
                }
            }
        } catch (ServiceLocationException e) {
            weAreDone();
            throw e;
        } catch (UnknownHostException uke) {
            weAreDone();
            throw new ServiceLocationException(ServiceLocationException.UNKNOWN_HOST, "Unknown Host: " + addr,
                    uke);
        }

        waitForReply();

        // now, we are done with the service discovery.
        // did we get any overflows ?
        if (!tcpTargets.isEmpty()) {
            SLPTcpClient tcp = new SLPTcpClient(owner);
            for (Iterator tcpIter = tcpTargets.iterator(); tcpIter.hasNext();) {
                TcpHostEntry host = (TcpHostEntry) tcpIter.next();
                owner.logDebug("TCP: " + host.address.getHostAddress() + " - " + host.port);
                ((SLPSrvReqMessage) msg).clearResponders();
                msg.clearFlags();
                try {
                    tcp.sendSlpMessage(msg, host.address.getHostAddress(), host.port, results);
                } catch (IOException e) {
                    throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                            "could not send message",
                            e);
                }
            }

        }

        //communicator.close();
        communicator = null;
        if (stupidException != 0) {
            throw new ServiceLocationException(stupidException);
        }
    }

    /** Called when the mesage is sent, and we need to wait for replies. */
    protected void waitForReply() {
        synchronized (wtSync) {
            while (amWaiting) {
                try {
                    wtSync.wait();
                } catch (Exception e) {
                }
            }
            amWaiting = true;
        }
    }

    /** Tells the thread to stop waiting for replies. */
    protected void stopWaiting() {
        synchronized (wtSync) {
            amWaiting = false;
            wtSync.notifyAll();
        }
    }


    public synchronized boolean udpTimeout() {
        // timeout - should not happen.
        // WRITE LOG if requested in config...
        //return 0;
        return true;
    }

    /** Called when an error is found by the listener thread. If we get here, something is wrong with the system. */
    public synchronized boolean udpError(Exception e) {
        // We stop waiting.
        weAreDone();
        stupidException = ServiceLocationException.INTERNAL_SYSTEM_ERROR;
        owner.logError("Internal System Error: ", e);
        return true;
    }

    public synchronized boolean udpReceived(DatagramPacket packet) {
        // received a reply.
        // read version and function, call SLPAgent.handleReplyMessage(...)
        // create input stream. Check version.
        int version = 0;
        int function = 0;
        SLPInputStream sis = new SLPInputStream((InputStream) (new ByteArrayInputStream(packet.getData())));
        try {
            version = sis.readByte();
            function = sis.readByte();
        } catch (IOException ioe) {
            function = version = 0; // this packet will not be handled.
        }
        // check that version == 2. We only support version 2 of the SLP.
        if (version != 2) {
            function = 0; // packet will be ignored
            owner.logError("Wrong SLP Version number: " + version, null);
        }

        boolean complete = true;
        try {
            complete = owner.handleReplyMessage(function, sis, results);
        } catch (ServiceLocationException e) {
            // log error.
            owner.logError("Error during message handling", e);
        }
        if (!complete) {
            // need to try again with TCP...
            owner.logDebug("Received Message was truncated");
            tcpTargets.add(new TcpHostEntry(packet.getAddress(),
                    packet.getPort()));
        }

        // if the current request was multicast, and we haven't waited too long,
        // we continue listening for more replies.
        if (((theRequest.getFlags() & SLPMessageHeader.FLAG_MCAST) != 0x0)) {
            // add responder to responder list, and create new datagram.
            SLPSrvReqMessage msg = (SLPSrvReqMessage) theRequest;
            msg.addResponder(packet.getAddress().getHostAddress());
            thePacket = SLPUtil.createDatagram(msg,
                    thePacket.getAddress(),
                    thePacket.getPort());
            // We don't retransmit the request here, but wait for a timeout.
            //return 0;
            return true;
        }
        // if we get here, we are done with this request.
        weAreDone();
        //return 0;
        return true;
    }

    /** Called by the timer when it is time to resend the message. */
    private synchronized void timerResendCallback() {
        try {
            owner.logMessage("ReSending Message:", theRequest);
            communicator.send(thePacket);
        } catch (Exception e) {
        }
        currentTimeout *= 2;
        timer.schedule(new resendTask(), currentTimeout);
    }

    /** Called by the timer when the maximum time to wait for replies have elapsed. */
    private synchronized void timerTimeoutCallback() {
        if ((theRequest.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {

            // set an error to indicate that the DA is down...
            stupidException = ServiceLocationException.DA_NOT_AVAILABLE;
        }

        weAreDone();
    }

    /** Called when we are done waiting for replies. This stops the timer, and tells the thread to stop waiting. */
    private void weAreDone() {
        try {
            communicator.removeCallback(theRequest.getXID());
        } catch (ServiceLocationException ex) {
            owner.logError("ERROR: SlpMessageSender.weAreDone() - Failed to remove callback !", null);
        }
        if (timer != null) timer.cancel();
        timer = null;
        theRequest = null;
        thePacket = null;
        stopWaiting();
    }
}
