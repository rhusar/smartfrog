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
package org.smartfrog.services.xunit.listeners.html;

import org.smartfrog.services.xunit.listeners.xml.XmlListenerFactory;


/**
 * created 08-Jun-2006 11:33:36
 */


public interface HtmlTestListenerFactory extends XmlListenerFactory {


    /**
     * page title
     * {@value}
     */
    String ATTR_TITLE = "title";

    String ATTR_CSS_RESOURCE = "cssResource";

    String ATTR_CSS_URL = "cssURL";

    String ATTR_CSS_DATA = "cssData";
}
