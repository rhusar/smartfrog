/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Form;
import org.restlet.resource.Representation;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class RestletOperationException extends SmartFrogException {

    private int status;
    private String text;
    private Map<String,String> headers;

    /**
     * Constructs a SmartFrogException with specified message.
     *
     * @param message exception message
     */
    public RestletOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a SmartFrogException with specified message and cause.
     *
     * @param message exception message
     * @param cause   exception causing this exception
     */
    public RestletOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes the
     * exception context with component details.
     *
     * @param message  exception message
     * @param sfObject The Component that has encountered the exception
     */
    public RestletOperationException(String message, Prim sfObject) {
        super(message, sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes the
     * exception context with component details.
     *
     * @param message  message
     * @param cause    exception causing this exception
     * @param sfObject The Component that has encountered the exception
     */
    public RestletOperationException(String message,
                                     Throwable cause, Prim sfObject) {
        super(message, cause, sfObject);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes
     * the exception context with component details.
     *
     * @param url the URL at the end of the wire
     * @param message  message
     * @param response the response in question
     * @param sfObject The Component that has encountered the exception
     */
    public RestletOperationException(String url,String message,
                                     Response response, Prim sfObject) {
        super(url+" : "+message, sfObject);
        build(response);
    }

    /**
     * Constructs a SmartFrogException with specified message. Also initializes
     * the exception context with component details.
     *
     * @param request request that caused the problem
     * @param message  message
     * @param response the response in question
     * @param sfObject The Component that has encountered the exception
     */
    public RestletOperationException(Request request, String message,
                                     Response response, Prim sfObject) {
        super(request.getMethod().getName()
                +" on "+
                request.getResourceRef().toString()
                        + " : " + message,
                sfObject);
        build(response);
    }

    /**
     * Constructs a SmartFrogException with specified cause. Also initializes the
     * exception context with component details.
     *
     * @param cause    cause of the exception
     * @param sfObject The Component that has encountered the exception
     */
    public RestletOperationException(Throwable cause, Prim sfObject) {
        super(cause, sfObject);
    }

    /**
     * Build an exception from a response. Subclasses could do clever things
     * here.
     * @param response response text
     */
    protected void build(Response response) {
        status=response.getStatus().getCode();
        Representation data = response.getEntity();
        RepresentationHelper rh=new RepresentationHelper(data);
        if(rh.isTextType())
        try {
            text = data.getText();
        } catch (IOException e) {
            text=null;
        }
        Form form=(Form)response.getAttributes().get("org.restlet.http.headers");
        headers = new HashMap<String, String>(form.size());
        for(String header: form.getNames()) {
            headers.put(header,form.getFirstValue(header));
        }
    }

    public int getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }

    /**
     * Gets a string representation of the exception.
     *
     * @param nm Message separator (ex. "\n");
     * @return String this object to String.
     */
    public String toString(String nm) {
        StringBuilder builder=new StringBuilder(super.toString(nm));
        builder.append(nm);
        builder.append("Headers:");
        builder.append(nm);
        if(headers!=null) {
            for(String header:headers.keySet()) {
                        builder.append(header);
                        builder.append(":");
                        builder.append(headers.get(header));
                        builder.append(nm);
                    }
        }
        builder.append(text);
        builder.append(nm);
        return builder.toString();
    }
}
