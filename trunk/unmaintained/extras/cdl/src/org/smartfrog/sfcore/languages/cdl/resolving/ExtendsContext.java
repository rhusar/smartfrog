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
package org.smartfrog.sfcore.languages.cdl.resolving;

import org.smartfrog.sfcore.languages.cdl.faults.CdlRecursiveExtendsException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlRuntimeException;
import org.smartfrog.sfcore.languages.cdl.faults.CdlResolutionException;
import org.smartfrog.sfcore.languages.cdl.utils.ClassLogger;
import org.smartfrog.sfcore.logging.Log;

import javax.xml.namespace.QName;
import java.util.Stack;

/**
 * This contains the context for extending something; essentially the stack of
 * which items are being extended created 13-Jun-2005 13:23:06
 */

public class ExtendsContext {

    private Stack<QName> extendsStack = new Stack<QName>();

    public static final String ERROR_WRONG_EXIT = "runtime exception: we are not exiting what we entered";
    public static final String ERROR_RECURSING = "Recursive extension. " +
            "Already extending: ";
    public static final String ERROR_NULL_QNAME = "Entered a null element";

    /**
     * search the stack for a particular element
     *
     * @param element
     * @return
     */
    public boolean lookup(QName element) {
        return extendsStack.contains(element);
    }

    /**
     * get depth of extension
     *
     * @return
     */
    public int depth() {
        return extendsStack.size();
    }

    /**
     * Enter an element
     *
     * @param element
     */
    public void enter(QName element) throws CdlResolutionException {
        if(element==null) {
            throw new CdlResolutionException(ERROR_NULL_QNAME);
        }
        if (lookup(element)) {
            //bail out early on a match
            throw new CdlRecursiveExtendsException(ERROR_RECURSING + element);
        }
        extendsStack.push(element);
    }


    /**
     * @param element
     * @throws java.util.EmptyStackException on empty exit
     * @throws CdlRuntimeException           if we are exiting wrongly
     */
    public void exit(QName element) {
        QName end = extendsStack.pop();
        if (!end.equals(element)) {
            throw new CdlRuntimeException(ERROR_WRONG_EXIT);
        }
    }

}
