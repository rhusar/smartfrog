/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.languages.cdl.process.PropertyListOperation;
import org.smartfrog.sfcore.languages.cdl.process.ProcessingPhase;
import org.smartfrog.sfcore.languages.cdl.process.DepthFirstOperationPhase;
import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.languages.cdl.faults.CdlException;
import org.smartfrog.sfcore.languages.cdl.resolving.ResolveEnum;

import java.io.IOException;

import nu.xom.Node;

import javax.xml.namespace.QName;

/**
 * Operation to verify that extends is complete
 * This is just a development option.
 */

public class VerifyExtendsComplete implements PropertyListOperation {

    /**
     * Apply an operation to a node.
     *
     * @param target
     * @throws org.smartfrog.sfcore.languages.cdl.faults.CdlException
     *
     */
    public void apply(PropertyList target) throws CdlException, IOException {
        QName extendsName = target.getExtendsName();
        if(extendsName!=null) {
            throw new CdlException("Still thinks it is extensible: "+target.getDescription());
        }
    }

    public static ProcessingPhase createPhase() {
        return new DepthFirstOperationPhase(new VerifyExtendsComplete(), null,true,true);
    }
}
