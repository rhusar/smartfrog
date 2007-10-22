/**
	(C) Copyright 2006 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.rest.servlets;

import nu.xom.*;
import org.smartfrog.services.rest.exceptions.RestException;
import org.smartfrog.services.rest.XmlConstants;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Utility class used to parse information from the incoming XML
 * packets associated with HTTP PUT and POST requests within the
 * SmartFrog REST system. Deals with the issues of character
 * encoding and value escaping.
 *
 * @author Derek Mortimer
 * @version 1.1
 */
public class ParsedResourceRequest
{
	/**
	 * Construct a parsed resource request without strict validation.
	 *
	 * @param restRequest Object containing all of the data associated with this request.
	 *
	 * @throws UnsupportedEncodingException If the specified character encoding does not exist on the system.
	 * @throws RestException If an internal exception is generated.
	 * @throws IOException If a problem occurs while trying to read the XML stream.
	 * @throws ValidityException If the contents of the XML document are deemed to be invalid.
	 * @throws ParsingException If the contents of the XML document could not be parsed.
	 * @throws SAXException If the underlying SAX processor causes an exception.
	 */
	public ParsedResourceRequest(HttpRestRequest restRequest)
			throws UnsupportedEncodingException, RestException, IOException,
				   ValidityException, ParsingException, SAXException
	{
		this(restRequest, false);
	}

	/**
	 * Construct a parsed resource request with the option to enable strict validation.
	 *
	 * @param restRequest Object containing all of the data associated with this request.
	 * @param strictValidation A boolean deciding whether or not to force strict validation of the incoming XML.
	 *
	 * @throws UnsupportedEncodingException If the specified character encoding does not exist on the system.
	 * @throws RestException If an internal exception is generated.
	 * @throws IOException If a problem occurs while trying to read the XML stream.
	 * @throws ValidityException If the contents of the XML document are deemed to be invalid.
	 * @throws ParsingException If the contents of the XML document could not be parsed.
	 * @throws SAXException If the underlying SAX processor causes an exception.
	 */
	public ParsedResourceRequest(HttpRestRequest restRequest, boolean strictValidation)
			throws UnsupportedEncodingException, RestException, IOException,
				   ValidityException, ParsingException, SAXException
	{
		this.restRequest = restRequest;

		if (restRequest.getContents().length == 0)
			throw new RestException("Empty request entity body provided");

		if (restRequest.getCharacterEncoding() != null)
			xml = new String(restRequest.getContents(), restRequest.getCharacterEncoding());
		else
			xml = new String(restRequest.getContents());

		Builder parser;
		if (strictValidation)
		{
			if ((restRequest.getContentType() == null) || (!(restRequest.getContentType().equals(
                    XmlConstants.APPLICATION_XML))))
				throw new RestException("Missing or Invalid Content Type Specified!\n"+
                        " Expected: "+ XmlConstants.APPLICATION_XML+" got "+ restRequest.getContentType());

			XMLReader xerces = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
			xerces.setFeature("http://apache.org/xml/features/validation/schema", true);
			parser = new Builder(xerces, true);
		}
		else
		{
			parser = new Builder();
		}

		document = parser.build(xml, null);

		// at this point the document is expected to have been validated so we can safely pull out the values
		Element root = document.getRootElement();
		targetType = root.getAttribute("type").getValue();
		parserLanguage = root.getAttribute("language").getValue();
		payload = root.getChild(0).getValue();
	}

	/**
	 * Returns the fully parsed XML as a XOM {@link Document} object.
	 *
	 * @return A <code>Document</code> object containing the parsed structure of the incoming XML request.
	 */
	public Document getDocument()
	{
		return document;
	}

	/**
	 * Returns the language associated with the payload of this XML request
	 *
	 * @return A string containing the named language for the parser to use when processing the payload of this request.
	 */
	public String getParserLanguage()
	{
		return parserLanguage;
	}

	/**
	 * Returns the raw string contents of the incoming XML request.
	 *
	 * @return A String containing the textual contents of the entire parsed XML document.
	 */
	public String getXML()
	{
		return xml;
	}

	/**
	 * Returns the type of SmartFrog Resource (that is, attribute, reference,
	 * description or component) targetted by this request.
	 *
	 * @return A string containing the type of SmartFrog resource targetted.
	 */
	public String getTargetType()
	{
		return targetType;
	}

	/**
	 * Returns the textual SmartFrog Language included in this XML request to be parsed directly into an object.
	 *
	 * @return A string containing valid SF-Language to be parsed using an sfParseXxx method.
	 */
	public String getPayload()
	{
		return payload;
	}

	private final String targetType;
	private final String payload;

	private final String xml;
	private final String parserLanguage;
	private final Document document;
	private final HttpRestRequest restRequest;
}
