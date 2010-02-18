/* (C) Copyright 2007-2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

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
                    while (iNext == XmlPullParser.START_TAG) {
                        // get the name of the element
                        String strTmp = xmlPullParser.getName();

                        // get the text
                        ext.getPropertyBag().put(strTmp, getText(xmlPullParser, strTmp));

                        // peek the next tag
                        iNext = xmlPullParser.nextTag();
                    }

                    // check the closing of the property bag
                    if (iNext == XmlPullParser.END_TAG)
                        if (xmlPullParser.getName().equals(strName)) {
                            // peek the next token
                            iNext = xmlPullParser.next();
                            continue;
                        }

                    // error
                    throw new XmlPullParserException("Unexpected closing tag found. \"</propertyBag>\" expected.");
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
