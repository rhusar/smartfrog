/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.xmpp;

import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.packet.PacketExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XMPPEventExtensionProvider implements PacketExtensionProvider {
    public PacketExtension parseExtension(XmlPullParser xmlPullParser) throws Exception {
        // create the packet extension
        XMPPEventExtension ext = new XMPPEventExtension();

        // get the next token id
        int iNext = xmlPullParser.next();
        while (iNext != XmlPullParser.END_TAG) {
            if (iNext == XmlPullParser.START_TAG) {
                // it's a opening tag, get the name
                String strName = xmlPullParser.getName();

                // store the values
                if (strName.equals("moduleId")) {
                    ext.setModuleId(getText(xmlPullParser, strName));
                } else if (strName.equals("instanceName")) {
                    ext.setInstanceName(getText(xmlPullParser, strName));
                } else if (strName.equals("host")) {
                    ext.setHost(getText(xmlPullParser, strName));
                } else if (strName.equals("moduleState")) {
                    ext.setModuleState(getText(xmlPullParser, strName));
                } else if (strName.equals("messageType")) {
                    ext.setMessageType(Integer.parseInt(getText(xmlPullParser, strName)));
                } else if (strName.equals("msg")) {
                    ext.setMsg(getText(xmlPullParser, strName));
                } else if (strName.equals("lastAction")) {
                    ext.setLastAction(getText(xmlPullParser, strName));
                } else if (strName.equals("timestamp")) {
                    ext.setTimestamp(getText(xmlPullParser, strName));
                } else if (strName.equals("propertyBag")) {
                    // extra parsing of the property bag needed
                    iNext = xmlPullParser.next();
                    if (iNext == XmlPullParser.START_TAG) {
                        iNext = xmlPullParser.next();
                        while (iNext != XmlPullParser.END_TAG) {
                            // get the name of the element
                            String strTmp = xmlPullParser.getName();

                            // put it into the property bag
                            ext.getPropertyBag().put(strTmp, getText(xmlPullParser, strTmp));

                            // peek the next token
                            iNext = xmlPullParser.next();
                        }
                    } else if (iNext == XmlPullParser.END_TAG) {
                        if (!xmlPullParser.getName().equals(strName)) {
                            // closing tag found but it wasn't "</propertyBag>
                            throw new XmlPullParserException("Wrong closing tag found. \"</propertyBag>\" expected but \"</" + xmlPullParser.getName() + ">\" found.");
                        }
                    } else throw new XmlPullParserException("Error while parsing \"propertyBag\". Unexpected token.");
                } else throw new XmlPullParserException("Unexpected token found: " + strName);

                // peek the next token
                iNext = xmlPullParser.next();
            }
        }

        return ext;
    }

    /**
     * Gets the text content of a element and checks for the closing tag.
     * @param inParser
     * @param inName
     * @return
     * @throws Exception
     */
    private String getText(XmlPullParser inParser, String inName) throws Exception
    {
        // the result string
        String strResult = "";

        // is the next element a text element
        int iNext = inParser.next();
        if (iNext == XmlPullParser.TEXT) {
            // get the content
            strResult = inParser.getText();

            // get the next element
            iNext = inParser.next();
        }

        // check the closing tag
        if (iNext == XmlPullParser.END_TAG)
            if (inParser.getName().equals(inName))
                return strResult;

        throw new XmlPullParserException("Wrong closing tag found. \"</" + inName + ">\" expected but \"</" + strResult + ">\" found.");
    }
}
