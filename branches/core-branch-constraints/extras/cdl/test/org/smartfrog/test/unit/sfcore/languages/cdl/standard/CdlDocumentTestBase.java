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

import org.ggf.cddlm.cdl.test.AbstractCdlTestBase;

/**
 * created 25-Nov-2005 15:06:06
 */

public abstract class CdlDocumentTestBase extends AbstractCdlTestBase {

    /**
     * Override point: the name of the default factory
     *
     * @return the name of the default factory, or null for none known
     */
    protected String getDefaultFactoryClassname() {
        return CdlSmartFrogProcessorFactory.class.getName();
    }

    /**
     * implementations should return the name of the resource dir
     * @return
     */
    abstract protected String getTestFileResourceDir();

    protected void setUp() throws Exception {
        super.setUp();
        String dir=getTestFileResourceDir();
        setPatterns(createPatternsFromResourceDir(dir));
    }
}
