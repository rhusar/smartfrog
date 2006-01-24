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
package org.smartfrog.services.cddlm.cdl.components;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import javax.xml.namespace.QName;

/**
 * This is an extended component description that is used when turning a CDL graph into a smartfrog graph
 * created 24-Jan-2006 13:34:37
 */

public class CdlComponentDescriptionImpl extends SFComponentDescriptionImpl implements CdlComponentDescription {

    /** node name */
    private QName qname;

    public CdlComponentDescriptionImpl(QName name, SFComponentDescription parent) throws SmartFrogRuntimeException {
        this(name,parent, new ContextImpl(),false);
    }

    public CdlComponentDescriptionImpl(QName name, SFComponentDescription parent, Context cxt, boolean eager)
            throws SmartFrogRuntimeException {
        super(null,(SFComponentDescription) parent, cxt, eager);
        if(parent!=null) {
            parent.sfReplaceAttribute(name,this);
        }
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName qname) {
        this.qname = qname;
    }


}
