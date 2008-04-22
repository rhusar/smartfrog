/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.restlet.client;

import org.restlet.data.MediaType;
import org.restlet.resource.Representation;

/**
 *
 */
public class RepresentationHelper {

    private Representation data;

    /**
     * create an instance
     * @param data the incoming data
     */
    public RepresentationHelper(Representation data) {
        this.data = data;
    }

    /**
     * Is this any text type?
     * @return true if we think it is a text, XML or XHTML
     */
    public boolean isTextType() {
        if(data==null) {
            return false;
        }
        MediaType mt=data.getMediaType();
        return isTextType(mt);
    }

    /**
     * query media type
     * @return true iff this is  XML
     */
    public boolean isXMLType() {
        if(data==null) {
            return false;
        }
        return isXMLType(data.getMediaType());
    }

    /**
     * query media type
     * @param mt media type
     * @return true iff this is text or XML
     */
    public static boolean isTextType(MediaType mt) {
        if(mt==null) {
            return false;
        }
        String maintype = mt.getMainType();
        return "text".equals(maintype)
                || mt.equals(MediaType.TEXT_ALL, true)
                || isXMLType(mt);
    }

    /**
     * query media type
     * @param mt media type
     * @return true iff this is  XML
     */
    public static boolean isXMLType(MediaType mt) {
        return mt!=null && (mt.equals(MediaType.APPLICATION_XML, true)
                || mt.equals(MediaType.APPLICATION_ATOM_XML, true)
                || mt.equals(MediaType.APPLICATION_XHTML_XML, true)
                || mt.equals(MediaType.APPLICATION_RDF_XML, true)
                || mt.equals(MediaType.TEXT_XML, true))
                ;
    }

}
