/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.shared;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class XMLHelper {

    public static void addRSSChannelInformation (Document XMLDocument, Node channelRoot, String title, String link, String description) {
        // Title node
        Node entry = XMLDocument.createElement("title");
        entry.appendChild(XMLDocument.createTextNode(title));
        channelRoot.appendChild(entry);

        // Link node
        entry = XMLDocument.createElement("link");
        entry.appendChild(XMLDocument.createTextNode(link));
        channelRoot.appendChild(entry);

        // Description node
        entry = XMLDocument.createElement("description");
        entry.appendChild(XMLDocument.createTextNode(description));
        channelRoot.appendChild(entry);

        // lastBuildDate node
        entry = XMLDocument.createElement("lastBuildDate");
        entry.appendChild(XMLDocument.createTextNode(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date())));
        channelRoot.appendChild(entry);

        // language node
        entry = XMLDocument.createElement("language");
        entry.appendChild(XMLDocument.createTextNode("en-gb"));
        channelRoot.appendChild(entry);
    }

    public static void addRSSItem(Document XMLDocument, Node channelRoot, String title, String link, String guid, String pubDate, String text) {
        // Create <item> node
        Node entry = XMLDocument.createElement("item");

        // Set title node beneath <item>
        Node subentry = XMLDocument.createElement("title");
        subentry.appendChild(XMLDocument.createTextNode(title));
        entry.appendChild(subentry);

        // Set link node beneath <item>
        subentry = XMLDocument.createElement("link");
        subentry.appendChild(XMLDocument.createTextNode(link));
        entry.appendChild(subentry);

        // Set guid node beneath <item>
        subentry = XMLDocument.createElement("guid");
        subentry.appendChild(XMLDocument.createTextNode(guid));
        entry.appendChild(subentry);

        // Set pubDate beneath <item>
        subentry = XMLDocument.createElement("pubDate");
        subentry.appendChild(XMLDocument.createTextNode(pubDate));
        entry.appendChild(subentry);

        // Set last message node beneath <host>
        subentry = XMLDocument.createElement("description");
        subentry.appendChild(XMLDocument.createTextNode(text));
        entry.appendChild(subentry);

        channelRoot.appendChild(entry);
    }

    public static String XMLToString(Document XMLDocument) {
        String xmlString = null;
        try {
            // Convert DOM to XML string
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.transform(new DOMSource(XMLDocument), result);
            xmlString = sw.toString();
            sw.close();
        } catch (TransformerConfigurationException tcex) {

        } catch (TransformerException tex) {

        } catch (IOException iex) {

        }
        return xmlString;
    }
}
