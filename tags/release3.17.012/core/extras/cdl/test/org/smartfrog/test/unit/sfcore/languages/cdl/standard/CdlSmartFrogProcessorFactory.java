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
package org.smartfrog.test.unit.sfcore.languages.cdl.standard;

import org.ggf.cddlm.cdl.test.CDLProcessor;
import org.ggf.cddlm.cdl.test.CDLProcessorFactory;
import org.ggf.cddlm.cdl.test.CDLException;
import org.xml.sax.SAXException;

/**
 * created 25-Nov-2005 15:08:50
 */

public class CdlSmartFrogProcessorFactory implements CDLProcessorFactory {

    /**
     * Returns a CDL processor which is ready to use for testing CDL document
     * resolution.
     *
     * @return an instance of {@link CdlSmartFrogProcessor}
     *
     */
    public CDLProcessor getProcessor() {
        try {
            return new CdlSmartFrogProcessor();
        } catch (SAXException e) {
            throw new CDLException(e);
        }
    }
}
