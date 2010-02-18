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

import nu.xom.Element;
import nu.xom.Document;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class seeks to encapsulate all of the necessary information
 * about an HTTP response to be delievered to a user. This utility
 * class is required due to the unserialisable nature of the HttpServletResponse
 * class in the Enterprise Java API (it contains an output stream).
 * These HTTP rest objects are designed to be safe for network transmission
 * within a SmartFrog system.
 *
 * @author Derek Mortimer
 * @version 1.0
 */
public class HttpRestResponse
{
	/**
	 * Returns the contents of the response to be sent to the client.
	 *
	 * @return A byte array whose raw contents are to be sent to the client under the specified content type and
	 * character encoding if applicable.
	 */
	public byte[] getContents()
	{
		return contents;
	}

	public Document getDocument()
	{
		return doc;
	}

	public String getStringContents()
	{
		return stringcontents;
	}

	/**
	 * Returns the MIME content type associated with the contents of this response.
	 *
	 * @return A string containing the MIME content type associated with this response.
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Returns the HTTP status code associated with this response.
	 *
	 * @return An integer containing the HTTP status code associated with this response.
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * Returns the HTTP status message associated with this response.
	 *
	 * @return A string containing the HTTP status message associated with this repsonse.
	 */
	public String getStatusMessage()
	{
		return statusMessage;
	}

	/**
	 * Returns the character encoding scheme associated with the contents of this response.
	 *
	 * @return A string containing the character encoding scheme associated with the contents of this response.
	 */
	public String getCharacterEncoding()
	{
		return characterEncoding;
	}

	/**
	 * Returns the length, in bytes, of the contents to be served with this response.
	 *
	 * @return An integer containing the length of the contents, in bytes, to be served with this response.
	 */
	public int getContentLength()
	{
		return contentLength;
	}

	/**
	 * Returns the locale associated with this response
	 *
	 * @return A Locale object referencing the locale associated with this response.
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * Sets the raw contents of the response to be served to the client.
	 *
	 * @param contents A byte array whose raw contents are that of the response to be transmitted.
	 */
	public void setContents(byte[] contents)
	{
		this.contents = contents;
	}

	/**
	 * Sets the raw contents of the response to be served to the client.
	 *
	 * @param contents A byte array whose raw contents are that of the response to be transmitted.
	 */
	public void setStringContents(String stringcontents)
	{
		this.stringcontents = stringcontents;
	}

	/**
	 * Sets the raw contents of the response to be served to the client.
	 *
	 * @param contents A byte array whose raw contents are that of the response to be transmitted.
	 */
	public void setDocument(Document doc)
	{
		this.doc = doc;
	}
	/**
	 * Sets the MIME content type associated with the contents of this response.
	 *
	 * @param contentType A string containing the MIME content type.
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * Sets the HTTP status code associated with the contents of this response.
	 *
	 * @param status An integer containing the HTTP status code.
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}

	/**
	 * Sets the HTTP status message associated with the contents of this response.
	 *
	 * @param statusMessage A string containing the HTTP status message.
	 */
	public void setStatusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
	}

	/**
	 * Sets the character encoding associated with the (presumed) textual contents of this response.
	 *
	 * @param characterEncoding A string naming the character encoding scheme to be used.
	 */
	public void setCharacterEncoding(String characterEncoding)
	{
		this.characterEncoding = characterEncoding;
	}

	/**
	 * Sets the content length header associated with this response.
	 *
	 * @param contentLength An integer containing the length of the contents associated with this response.
	 */
	public void setContentLength(int contentLength)
	{
		this.contentLength = contentLength;
	}

	/**
	 * Sets the locale associated with this response.
	 *
	 * @param locale An instance of Locale to be associated with this response.
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Ascertains whether this request contains the named header.
	 *
	 * @param name The textual name of the header to be verified
	 * @return <code>true</code> if the header exists within this request, <code>false</code> otherwise.
	 */
	public boolean containsHeader(String name)
	{
		return headers.containsKey(name);
	}

	/**
	 * Adds a header name/value pair to this response. Add allows multiple values
	 * to be defined for a single named header.
	 *
	 * @param name The textual name of the header to be added
	 * @param value The value to be associated with the provided name.
	 */
	public void addHeader(String name, String value)
	{
		if (containsHeader(name))
		{
			Object oldValue = headers.get(name);

			if (oldValue instanceof ArrayList)
			{
				((ArrayList) oldValue).add(value);
			}
			else
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add((String) oldValue);
				list.add((String) value);

				oldValue = list;
			}

			headers.put(name, oldValue);
		}
		else
		{
			headers.put(name, value);
		}
	}

	/**
	 * Adds a header name/value pair to this response. Add allows multiple values
	 * to be defined for a single named header.
	 *
	 * @param name The textual name of the header to be added
	 * @param value The integer value to be parsed and associated with the provided name.
	 */
	public void addIntHeader(String name, int value)
	{
		addHeader(name, String.valueOf(value));
	}

	/**
	 * Adds a header name/value pair to this response. Add allows multiple values
	 * to be defined for a single named header.
	 *
	 * @param name The textual name of the header to be added
	 * @param date The date value to be parsed and associated with the provided name.
	 */
	public void addDateHeader(String name, long date)
	{
		SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		String value = f.format(new Date(date));

		addHeader(name, value);
	}

	/**
	 * Sets a header name/value pair to this response and overwrites any existing
	 * headers with the same name.
	 *
	 * @param name The textual name of the header to be added
	 * @param value The value to be associated with the provided name.
	 */
	public void setHeader(String name, String value)
	{
		headers.put(name, value);
	}

	/**
	 * Sets a header name/value pair to this response and overwrites any existing
	 * headers with the same name.
	 *
	 * @param name The textual name of the header to be added
	 * @param value The integer value to be parsed and associated with the provided name.
	 */
	public void setIntHeader(String name, int value)
	{
		setHeader(name, String.valueOf(value));
	}

	/**
	 * Sets a header name/value pair to this response and overwrites any existing
	 * headers with the same name.
	 *
	 * @param name The textual name of the header to be added
	 * @param date The date value to be parsed and associated with the provided name.
	 */
	public void setDateHeader(String name, long date)
	{
		SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		String value = f.format(new Date(date));

		setHeader(name, value);
	}

	// utility function to check the range of the
	// assigned http status response code.
	private boolean statusRangeCheck(int lower, int upper)
	{
		return (status != 0) && (status >= lower) && (status < upper);
	}

	/**
	 * Resets the contents, headers, status code and status message associated with this response.
	 */
	public void reset()
	{
		contents = new byte[0];
		headers = new HashMap<String, Object>();

		status = 0;
		statusMessage = "";
	}

	/**
	 * Wipes the value of the contents but retains the headers, status code and status message.
	 */
	public void resetBuffer()
	{
		contents = new byte[0];
	}

	/**
	 * Copies all of the information contained within this object into a J2EE {@link HttpServletResponse} object
	 * so that it may be used appropriately for serving the repsonse to a client.
	 *
	 * @param servletResponse The response to be updated with the contained information.
	 */
	public void update(HttpServletResponse servletResponse)
	{
		if (status > 0)
			servletResponse.setStatus(status);

		if (contentLength > 0)
			servletResponse.setContentLength(contentLength);

		if (contentType != null)
			servletResponse.setContentType(contentType);

        if (characterEncoding != null)
			servletResponse.setCharacterEncoding(characterEncoding);

		if (locale != null)
			servletResponse.setLocale(locale);

		for (String key: headers.keySet())
		{
			Object val = headers.get(key);

			if (val instanceof ArrayList)
			{
				ArrayList list = (ArrayList) val;

                for (Object aList : list) {
                    servletResponse.addHeader(key, (String) aList);
                }
			}
			else
			{
				servletResponse.addHeader(key, (String) val);
			}
		}
	}

	/**
	 * Utility method used to generate <response ...> xml packets for
	 * transmission to the client.
	 *
	 * @param code A machine-friendly code representing the result of the requested action
	 * @param message A user-friendly message representing the result of the requested action.
	 *
	 * @return A string containing the XML representation of the final response
	 */
	public static String generateResponseXML(String code, String message)
	{
		Element root = new Element("response");
		Element codeNode = new Element("code");
		Element messageNode = new Element("message");

		codeNode.appendChild(code);
		messageNode.appendChild(message);

		root.appendChild(codeNode);
		root.appendChild(messageNode);

		return new Document(root).toXML();
	}


	private byte[] contents;
	private Document doc;
	private String stringcontents;
	private int status;
	private int contentLength;

	private String statusMessage;
	private String contentType;
	private String characterEncoding;

	private Locale locale;

	private Map<String,Object> headers = new HashMap<String, Object>();
}
