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

package org.smartfrog.examples.dynamicwebserver.gui.progresspanel;

import java.awt.GridLayout;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.smartfrog.examples.dynamicwebserver.logging.LogWrapper;
import org.smartfrog.examples.dynamicwebserver.logging.Logger;
import org.smartfrog.services.display.SFDisplay;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventBus;
import org.smartfrog.sfcore.workflow.eventbus.EventRegistration;
import org.smartfrog.sfcore.workflow.eventbus.EventSink;


/**
 * DoNothing is a test routine for SmartFlow systems. It does nothing for a
 * period of time then terminates according to the attributes given at
 * deployment. Attributes are documented in the file doNothing.sf
 *
 * @author julgui
 *
 * @deprecated 12 October 2002
 */
public class ProgressBarsImpl extends SFDisplay implements Prim, ProgressBars,
    EventRegistration, EventSink, EventBus {
    String name;
    int time;
    String terminationType;
    String message;
    boolean printEvents;
    private JPanel masterPanel = null;
    private JScrollPane scrollPane = new JScrollPane();
    Hashtable hash = new Hashtable();
    LogWrapper logger;

    //EventPrimImpl
    Vector receiveFrom = new Vector();
    Vector sendTo = new Vector();
    GridLayout gridLayout1 = new GridLayout(0, 1);

    /**
     * The require default constructor.
     *
     * @exception RemoteException In case of network/rmi error
     */
    public ProgressBarsImpl() throws RemoteException {
        super();
    }

    /**
     * Print the receipt of any event for debugging
     *
     * @param eventObj event
     */
    public void handleEvent(Object eventObj) {
        String event = "";
        if (eventObj==null) {
            event = "null";
        }  else {
            event = eventObj.toString();
        }
        if (printEvents) {
            System.out.println(name + " received event " + event);
        }

        if (display != null) {
            display.append(name + " received event " + event + "\n");
        } else {
            return;
        }

        String key = (String) getEventSender(event);
        int total = getEventTotalItems(event);
        int item = getEventItem(event);
        String msg = (String) getEventMsg(event);

        if (key.equals("")) {
            return;
        }

        if (total <= 0) {
            return;
        }

        if (item < 1) {
            return;
        }

        if (hash.containsKey(key)) {
            ProgressPanel panel = ((ProgressPanel) (hash.get(key)));
            panel.updateBall(item, 1, msg);

            if (item >= 0) {
                panel.updateBall(item - 1, 2, msg);
            }
        } else {
            ProgressPanel panel = new ProgressPanel(key);
            panel.createBalls(total);
            panel.updateBall(item, 1, msg);
            this.masterPanel.add(panel);
            this.masterPanel.updateUI();

            try {
                hash.put(key, panel);
            } catch (Exception ex) {
                if (sfLog().isErrorEnabled()) sfLog().error (ex);
            }
        }

        //System.out.println("Event hadled: "+ event);
    }

    // Parsing MSG

    /**
     * Gets the eventSender attribute of the SFProgressBars object
     *
     * @param msgO message
     *
     * @return The eventSender value
     */
    private Object getEventSender(Object msgO) {
      String msg = "";
      if (msgO instanceof String){
         msg = (String) msgO;
      } else return "";

        try {
            String sender = msg.substring(0, msg.indexOf(':'));

            return sender;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Gets the eventTotalItems attribute of the SFProgressBars object
     *
     * @param msgO message
     *
     * @return The eventTotalItems value
     */
    private int getEventTotalItems(Object msgO) {
        String msg = "";
        if (msgO instanceof String){
           msg = (String) msgO;
        } else return -1;

        try {
            String item = msg.substring(msg.indexOf(':') + 1, msg.length());
            item = item.substring(item.indexOf(':') + 1, item.length());
            item = item.substring(0, item.indexOf(':'));

            return Integer.parseInt(item);
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * Gets the eventItem attribute of the SFProgressBars object
     *
     * @param msgO message
     *
     * @return The eventItem value
     */
    private int getEventItem(Object msgO) {

      String msg = "";
      if (msgO instanceof String){
         msg = (String) msgO;
      } else return -1;


        try {
            String item = msg.substring(msg.indexOf(':') + 1, msg.length());
            item = item.substring(0, item.indexOf(':'));

            //System.out.println("Item:"+item);
            return Integer.parseInt(item);
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()) sfLog().ignore(ex);
            return -1;
        }
    }

    /**
     * Gets the eventMsg attribute of the SFProgressBars object
     *
     * @param msgO message
     *
     * @return The eventMsg value
     */
    private Object getEventMsg(Object msgO) {
        String msg = msgO.toString();
        try {
            msg = msg.substring(msg.lastIndexOf(':') + 1, msg.length());
            //System.out.println("msg:"+msg);
            return msg;
        } catch (Exception ex) {
            if (sfLog().isIgnoreEnabled()) sfLog().ignore (ex);
            if (msgO!=null){
                return msgO.toString();
            } else {
                return "null";
            }
        }
    }

    // -- End Parsing Msg

    /**
     * The lifecycle initialisation hook - reads time attribute and
     * terminationType attribute. If they don't exists, delay 10 seconds and
     * terminate normally.
     *
     * @exception SmartFrogException In cas of error while deploying
     * @exception RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        name = sfCompleteName().toString();
        logger = new LogWrapper((Logger) sfResolve(LOGTO, false));

        // For Event Bus

        /*
         *  find local registrations and register them
         */
        ComponentDescription sends = (ComponentDescription) sfResolve(SEND);
        Context scxt = sends.sfContext();

        for (Enumeration e = scxt.keys(); e.hasMoreElements();) {
            Object k = e.nextElement();
            Reference l = (Reference) scxt.get(k);
            EventSink s = (EventSink) sfResolve(l);
            sendTo.addElement(s);
        }

        /*
         *  find own registrations, and register remotely
         */
        ComponentDescription regs = (ComponentDescription) sfResolve(RECEIVE);
        Context rcxt = regs.sfContext();

        for (Enumeration e = rcxt.keys(); e.hasMoreElements();) {
            Object k = e.nextElement();
            Reference l = (Reference) rcxt.get(k);
            EventRegistration s = (EventRegistration) sfResolve(l);
            receiveFrom.addElement(s);
            s.register((EventSink) this);
        }

        // -----------
        time = sfResolve(TIME, 10000, false);
        terminationType = sfResolve(TERMINATIONTYPE, "normal", false);
        printEvents = sfResolve(PRINTEVENTS, true, false);

        message = sfResolve(MESSAGE, "", false);

        if (message.equals("")) {
            message = null;
        } else {
            message = name + message;
        }

        //creating panelf
        if (display == null) {
            return;
        }

        try {
            this.masterPanel = new JPanel();
            masterPanel.setLayout(gridLayout1);
            masterPanel.setVisible(true);
            masterPanel.setEnabled(true);
            scrollPane.getViewport().add(masterPanel, null);
            display.tabPane.add(scrollPane, "Progress ...",0);
            display.tabPane.setSelectedIndex(0);
        } catch (Exception ex) {
            throw new SmartFrogException("Failure sfDeploy SFDeployDisplay!",
                ex, this);
        }
    }

    /**
     * This lifecycle start hook - kick of the timer and terminate when it
     * fires.
     *
     * @exception SmartFrogException In case of error while starting
     * @exception RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        if (message != null) {
            System.out.println(message);
        }

        Runnable terminator = new Runnable() {
                public void run() {
                    try {
                        if (time > 0) {
                            Thread.sleep(time);
                        }
                    } catch (Exception ex) {
                    }

                    sfTerminate(new TerminationRecord(terminationType, name,
                            null));
                }
            };

        if (!terminationType.equals("none")) {
            new Thread(terminator).start();
        }
    }

    /**
     * Implementation of sfTerminateWith which deregisters from all current
     * registrations
     *
     * @param status TerminationRecord object
     * @param comp Prim compoenent
     */
    public synchronized void sfTerminateWith(TerminationRecord status, Prim comp) {
        /*
         *  unregister from all remote registrations
         */
        for (Enumeration e = receiveFrom.elements(); e.hasMoreElements();) {
            EventRegistration s = (EventRegistration) e.nextElement();

            try {
                s.deregister(this);
            } catch (RemoteException ex) {
            }
        }

        super.sfTerminatedWith(status, comp);
    }

    //EventMethods.

    /**
     * Implementation of the EventRegistration interface. Register an EventSink
     * for forwarding of events.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink
     */
    public synchronized void register(EventSink sink) {
        if (!sendTo.contains(sink)) {
            sendTo.addElement(sink);
        }
    }

    /**
     * Implementation of the EventRegistration interface. Deregister an
     * EventSink for forwarding of events.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink
     */
    public synchronized void deregister(EventSink sink) {
        if (sendTo.contains(sink)) {
            sendTo.removeElement(sink);
        }
    }

    /**
     * Implementation of the EventSink interface. Handle the event locally then
     * forward to all registered EventSinks
     *
     * @param event java.lang.Object
     */
    public synchronized void event(Object event) {
        handleEvent(event);
        sendEvent(event);
    }

    /**
     * Default implementation of the EventBus sendEvent method to forward all
     * events to registered EventSinks. Errors are ignored.
     *
     * @param event java.lang.Object
     */
    public synchronized void sendEvent(Object event) {
        try {
            for (Enumeration e = sendTo.elements(); e.hasMoreElements();) {
                EventSink s = (EventSink) e.nextElement();

                try {
                    s.event(event);
                } catch (RemoteException ex) {
                }
            }
        } catch (Exception exc) {
            if (sfLog().isErrorEnabled()) sfLog().error (exc);
        }
    }
}
