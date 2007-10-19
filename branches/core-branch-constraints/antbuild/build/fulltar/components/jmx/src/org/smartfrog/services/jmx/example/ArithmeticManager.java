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

import java.rmi.*;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.componentdescription.*;
import org.smartfrog.sfcore.parser.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 *  Description of the Class
 *
 *          sfJMX
 *   JMX-based Management Framework for SmartFrog Applications
 *       Hewlett Packard
 *
 *@version        1.0
 */
public class ArithmeticManager extends CompoundImpl implements ArithmeticManagerMBean, Remote {

    /**
     *  Description of the Field
     */
    public SleepyGeneratorMBean leftGenerator = null;
    /**
     *  Description of the Field
     */
    public SleepyGeneratorMBean rightGenerator = null;
    /**
     *  Description of the Field
     */
    public StorePrinterMBean printer = null;


    /**
     *  Constructor for the OperationManager object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public ArithmeticManager() throws RemoteException { }


    /**
     *  Management methods *
     *
     *@return                The leftValue value
     *@exception  Exception  Description of the Exception
     */
    public Integer getLeftValue() throws Exception {
        return (Integer) sfResolve("leftValue");
    }


    /**
     *  Gets the rightValue attribute of the OperationManager object
     *
     *@return                The rightValue value
     *@exception  Exception  Description of the Exception
     */
    public Integer getRightValue() throws Exception {
        return (Integer) sfResolve("rightValue");
    }


    /**
     *  Gets the result attribute of the OperationManager object
     *
     *@return                The result value
     *@exception  Exception  Description of the Exception
     */
    public Integer getResult() throws Exception {
        return (Integer) sfResolve("result");
    }


    /**
     *  Sets the leftValue attribute of the OperationManager object
     *
     *@param  value          The new leftValue value
     *@exception  Exception  Description of the Exception
     */
    public void setLeftValue(Integer value) throws Exception {
        Object leftObject = sfResolveHere("leftValue");//sfResolveId
        // Get the value stored in this Context
        // Check if it is a Reference. If so, we need to get the container of the referenced attribute
        if (leftObject instanceof Reference) {
            sfReplaceReferencedAttribute(this, (Reference) leftObject, value);
        } else {
            sfReplaceAttribute("leftValue", value);
        }
    }


    /**
     *  Sets the rightValue attribute of the OperationManager object
     *
     *@param  value          The new rightValue value
     *@exception  Exception  Description of the Exception
     */
    public void setRightValue(Integer value) throws Exception {
        Object leftObject = sfResolveHere("rightValue");//sfResolveId
        // Get the value stored in this Context
        // Check if it is a Reference. If so, we need to get the container of the referenced attribute
        if (leftObject instanceof Reference) {
            sfReplaceReferencedAttribute(this, (Reference) leftObject, value);
        } else {
            sfReplaceAttribute("rightValue", value);
        }
    }


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sendLeftValue() throws Exception {
        leftGenerator.wakeUp();
    }


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sendRightValue() throws Exception {
        rightGenerator.wakeUp();
    }


    /**
     *  Description of the Method
     *
     *@exception  RemoteException  Description of the Exception
     *@exception  Exception        Description of the Exception
     */
    public void sendBothValues() throws Exception {
        printer.disableNextEvaluation();
        leftGenerator.wakeUp();
        rightGenerator.wakeUp();
    }


    /**
     *  Lifecycle methods *
     *
     *@exception  Exception  Description of the Exception
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        leftGenerator = (SleepyGeneratorMBean) sfResolve("leftGenerator");
        rightGenerator = (SleepyGeneratorMBean) sfResolve("rightGenerator");
        printer = (StorePrinterMBean) sfResolve("printer");
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
            referencedObject = ((Prim) container).sfResolveHere(attribute); //sfResolveId
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

}
