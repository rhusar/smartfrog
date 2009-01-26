/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.avalanche.client.monitor.xmpp;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @deprecated Unused.
 */
public class AvalancheEventProvider implements IQProvider {

	public IQ parseIQ(XmlPullParser parser) throws Exception {
		AvalancheEventIQ event = null ;
		StringBuffer sb = new StringBuffer();
	      try {
	          int evt = parser.getEventType();
	          // get the content
	          while (true) {
	              switch (evt) {
	                  case XmlPullParser.TEXT:
	                      sb.append(parser.getText());
	                      break;
	                  case XmlPullParser.START_TAG:
	                      sb.append('<' + parser.getName() + '>');
	                      break;
	                  case XmlPullParser.END_TAG:
	                      sb.append("</" + parser.getName() + '>');
	                      break;
	                  default:
	              }

	              if (evt == XmlPullParser.END_TAG && "avalancheEvent".equals(parser.getName())) break;

	              evt = parser.next();
	          }
	      } catch (XmlPullParserException e) {
	          e.printStackTrace();
	      } catch (IOException e) {
	          e.printStackTrace();
	      }

	      String xmlText = sb.toString();
	      event = new AvalancheEventIQ();
	      try {
	          DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	          DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	          Document document = documentBuilder.parse(new ByteArrayInputStream(xmlText.getBytes()));

	          event = toEvent(document); 

	      } catch (Exception e) {
	          e.printStackTrace(System.err);
	      }
	      return event;
	}

	/**
	 * Format for XML is 
	 * 
	 * 
	 * @param doc
	 * @return
	 */
	private AvalancheEventIQ toEvent(Document doc){
		AvalancheEventIQ event = new AvalancheEventIQ();
		
		Element root = doc.getDocumentElement(); 
		
		NodeList childNodes = root.getChildNodes();
		int size = childNodes.getLength();
		for( int i=0;i<size;i++){
			Node n = childNodes.item(i);
			if( n.getNodeName().equals("moduleId")){
				event.setModuleId(n.getFirstChild().getNodeValue());
			}else if( n.getNodeName().equals("instanceName")){
				event.setInstanceName(n.getFirstChild().getNodeValue());
			}else if( n.getNodeName().equals("host")){
				event.setHost(n.getFirstChild().getNodeValue());
			}else if( n.getNodeName().equals("moduleState")){
				event.setModuleState(n.getFirstChild().getNodeValue());
			}else if( n.getNodeName().equals("messageType")){
				event.setMessageType(Integer.parseInt(n.getFirstChild().getNodeValue()));
			}else if( n.getNodeName().equals("msg")){
				event.setMsg(n.getFirstChild().getNodeValue());
			}else if( n.getNodeName().equals("lastAction")){
				event.setLastAction(n.getFirstChild().getNodeValue());
			}else if( n.getNodeName().equals("timestamp")){
				event.setTimestamp(n.getFirstChild().getNodeValue());
			}

		}
		
		return event;
	}

}
