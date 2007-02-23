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
package org.smartfrog.projects.alpine.faults;

import nu.xom.Element;
import org.smartfrog.projects.alpine.om.soap11.SoapConstants;

/**
 * created 22-Mar-2006 12:01:49
 */

public class MustUnderstandFault extends SoapException {

    private Element header;
    /**
     * the text in the messge
     * {@value}
     */
    public static final String NOT_UNDERSTOOD = "A mustUnderstand header was not understood by the endpoint :";

    public MustUnderstandFault(String soapNamespace,
                               String faultActor,
                               Element header) {
        super(soapNamespace, SoapConstants.FAULTCODE_MUST_UNDERSTAND,
                faultActor,
                NOT_UNDERSTOOD +header.getQualifiedName(),
                null);
        this.header=header;
    }


    /**
     * The header
     * @return the specific header
     */
    public Element getHeader() {
        return header;
    }

}
