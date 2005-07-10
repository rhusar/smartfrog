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

package org.smartfrog.projects.alpine.http;

import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.core.MessageContext;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.SoapMessageParser;
import org.smartfrog.projects.alpine.xmlutils.ResourceLoader;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.faults.ServerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.IOException;

import sun.net.www.protocol.http.HttpURLConnection;
import nu.xom.Serializer;

/**
 * This is a helper class used by the servlet.
 * It is decoupled for reuse and ease of testing; you can test this logic without posting
 * requests to ourselves. 
 */
public class HttpBinder {
    
    private static final boolean IS_PARSER_VALIDATING=false;
    private EndpointContext endpointContext;
    public static final String ERROR_NO_SOAPACTION = "No SOAPAction";
    public static final String ERROR_UNSUPPORTED_CONTENT = "Unsupported content type: ";
    public static final String CONTENT_TYPE_TEXT_XML = "text/xml";

    public HttpBinder(EndpointContext endpointContext) {
        this.endpointContext = endpointContext;
    }
    
    /**
     * Bind a message context to the incoming message. 
     * The request message of the cont
     * @param request
     * @return the incoming message, which is also bound to the request in the message context
     */ 
    
    public MessageDocument parseIncomingPost(MessageContext messageContext, HttpServletRequest request) throws Exception {
        validateContentType(request);
        ResourceLoader loader = new ResourceLoader(this.getClass());
        SoapMessageParser parser = new SoapMessageParser(loader, IS_PARSER_VALIDATING);
        MessageDocument message = parser.parseStream(request.getInputStream());
        bindHeadersToDocument(message,request);
        messageContext.setRequest(message);
        return message;
    }
    
    /**
     * Churn out a response to the channel. If the message is a fault, we generate
     * a 500 response, if not, a 200
     * @param messageContext
     * @param response
     */ 
    public void outputResponse(MessageContext messageContext, HttpServletResponse response) throws IOException {
        MessageDocument message= messageContext.getResponse();
        int responseCode=message.isFault()?
                HttpServletResponse.SC_OK:HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        response.setStatus(responseCode);
        response.setContentType(CONTENT_TYPE_TEXT_XML);
        response.setContentType("UTF-8");
        //PrintWriter writer = response.getWriter();
        ServletOutputStream out=response.getOutputStream();
        Serializer serializer=new Serializer(out);
        serializer.write(message);
        serializer.flush();
        out.flush();
    }
    
    /**
     * Get the headers from a request to a document
     * @param message
     * @param request
     */ 
    public void bindHeadersToDocument(MessageDocument message,
                                     HttpServletRequest request) {
        Enumeration headers=request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = (String) headers.nextElement();
            String value = request.getHeader(name);
            message.putMimeHeader(name,value);
        }
    }

    /**
     * Copy the headers from a document to a request 
     *
     * @param message
     * @param response
     */
    public void copyHeadersToResponse(MessageDocument message,
                                      HttpServletResponse response) {
        for(String name:message.getMimeHeaders().keySet()) {
            String value=message.getMimeHeader(name);
            response.setHeader(name,value);
        }
    }

    
    public void validateContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        if(!CONTENT_TYPE_TEXT_XML.equals(contentType)) {
            throw new ServerException(ERROR_UNSUPPORTED_CONTENT+contentType);
        }
    }
    
    public void validateSoapAction(HttpServletRequest request) {
        String value = request.getHeader(HttpConstants.HEADER_SOAP_ACTION);
        if (value==null) {
            throw new ServerException(ERROR_NO_SOAPACTION);
        }
    }
    
    

}
