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

package org.smartfrog.services.jmx.example;

import java.util.*;
import java.rmi.*;
import org.smartfrog.examples.arithnet.Printer;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.reference.*;
import javax.management.*;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class StorePrinter extends Printer implements StorePrinterMBean, StorePrinter_StubMBean, NotificationBroadcaster {
    private NotificationBroadcasterSupport broadcaster = null;
    private long sequence = 0;
    boolean evaluate = true;


    /**
     *  Constructor for the StorePrinter object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public StorePrinter() throws RemoteException {
        super();
    }


    /**
     *  MBean methods
     *
     *@return                      The result value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Integer getResult() throws  Exception {
        return (Integer) sfResolve("result");
    }


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void disableNextEvaluation() throws Exception {
        synchronized (this) {
            evaluate = false;
        }
    }


    /**
     *  Gets the evaluationEnabled attribute of the StorePrinter object
     *
     *@return                      The evaluationEnabled value
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public Boolean isEvaluationEnabled() throws Exception {
        return new Boolean(evaluate);
    }


    /**
     *  Description of the Method
     *
     *@param  from   Description of the Parameter
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    public int evaluate(String from, int value) {
        try {
            synchronized (this) {
                if (!evaluate) {
                    evaluate = true;
                    return value;
                }
            }

            // Get the value stored in this Context
            Object result = sfResolveHere("result"); //sfResolveId

            // Check if it is a Reference. If so, we need to get the container of the referenced attribute
            Integer newValue = new Integer(value);
            if (result instanceof Reference) {
                sfReplaceReferencedAttribute(this, (Reference) result, newValue);
            } else {
                sfReplaceAttribute("result", newValue);
            }

            if (broadcaster != null) {
                broadcaster.sendNotification(new AttributeChangeNotification(
                        this,
                        sequence++,
                        (new Date()).getTime(),
                        "New arithmetic operation performed",
                        "Result",
                        "java.lang.Integer",
                        result,
                        newValue));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.evaluate(from, value);
    }


    /**
     *  Private methods *
     *
     *@param  resolver       Description of the Parameter
     *@param  ref            Description of the Parameter
     *@param  value          Description of the Parameter
     *@exception  Exception  Description of the Exception
     */
    private void sfReplaceReferencedAttribute(RemoteReferenceResolver resolver, Reference ref, Object value) throws Exception {
        // Get the reference to parent that contains pointed attribute
        Reference reference = (Reference) ref.copy();
        ReferencePart attribRefPart = reference.lastElement();
        // ReferencePart that corresponds to the attribute
        reference.removeElement(attribRefPart);
        // Remove the attribute ReferencePart. Now it points to the parent

        // Resolve the reference and substitute the container by the parent that owns the pointed attribute
        Object container = resolver.sfResolve(reference);
        String attribute = attribRefPart.toString();
        Object referencedObject = null;
        if (container instanceof Prim) {
            referencedObject = ((Prim) container).sfResolveHere(attribute); //sfREsolveId
            // If it is still a reference we call this method recursively, otherwise we replace the attribute
            if (referencedObject instanceof Reference) {
                sfReplaceReferencedAttribute((RemoteReferenceResolver) container,
                        (Reference) referencedObject,
                        value);
            } else {
                ((Prim) container).sfReplaceAttribute(attribute, value);
            }
        } else if (container instanceof ComponentDescription) {
            // If the container is a ComponentDescription, it cannot resolve a LAZY reference
            ((ComponentDescription) container).sfContext().put(attribute, value);
        }
    }


    /**
     *  Inteface NotificationBroadcaster
     *
     *@param  listener  The feature to be added to the NotificationListener
     *      attribute
     *@param  filter    The feature to be added to the NotificationListener
     *      attribute
     *@param  handback  The feature to be added to the NotificationListener
     *      attribute
     */

    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, java.lang.Object handback) {
        if (broadcaster == null) {
            broadcaster = new NotificationBroadcasterSupport();
        }
        broadcaster.addNotificationListener(listener, filter, handback);
    }


    /**
     *  Gets the notificationInfo attribute of the StorePrinter object
     *
     *@return    The notificationInfo value
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]
                {
                new MBeanNotificationInfo(new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                "RESULT",
                "The result of an arithmetic has arrived to the printer")
                };
    }


    /**
     *  Description of the Method
     *
     *@param  listener                       Description of the Parameter
     *@exception  ListenerNotFoundException  Description of the Exception
     */
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        if (broadcaster == null) {
            throw new ListenerNotFoundException("No notification listeners registered");
        } else {
            broadcaster.removeNotificationListener(listener);
        }
    }

}
