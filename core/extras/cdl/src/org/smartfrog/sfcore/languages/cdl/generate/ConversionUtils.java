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
package org.smartfrog.sfcore.languages.cdl.generate;

import javax.xml.namespace.QName;

/**
 * utils to help with the conversion to smartfrog components
 * created 24-Jan-2006 15:47:55
 */

public class ConversionUtils {

    private ConversionUtils() {
    }

    public static String createValidName(QName source) {
        String name;
        final String uri = source.getNamespaceURI();
        final String local = source.getLocalPart();
        if(uri.length()>0) {
            name=escapeNameString(source+"."+local);
        } else {
            name= escapeNameString(local);
        }
        return name;
    }

    private static final String HEAD ="abcdefghijlkmnopqurstuvwxyz";
    private static final String TAIL = HEAD+"ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890.";

    /**
     * Convert to a string a-zA-Z0-9_, where 0-9 is forbidden
     * @param source
     * @return
     */
    public  static String escapeNameString(CharSequence source) {
        final int len = source.length();
        StringBuffer buffer=new StringBuffer(len);
        for(int i=0;i<len;i++) {
            final char ch = source.charAt(i);
            int found;
            if(i==0) {
                found=HEAD.indexOf(ch);
            } else {
                found = TAIL.indexOf(ch);
            }
            if(found>=0) {
                buffer.append(ch);
            } else {
                buffer.append(escape(ch));
            }
        }
        return buffer.toString();
    }

    private static String escape(char ch) {
        StringBuffer buffer=new StringBuffer(4);
        buffer.append('_');
        final String hex = Integer.toHexString(ch);
        buffer.append(hex);
        buffer.append('_');
        return buffer.toString();
    }
}
