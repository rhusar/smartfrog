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
import org.smartfrog.projects.alpine.core.AlpineContext;
import org.smartfrog.projects.alpine.core.ContextConstants;
import org.smartfrog.projects.alpine.om.soap11.MessageDocument;
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.faults.FaultBridge;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;


/**
 * This servlet handles SOAP Posted stuff
 */
public class SoapPostServlet extends ServletBase {
    public static final String ERROR_NO_HANDLER = "No handler class defined for the endpoint";
    public static final String TEXT_HTML = "text/html";

    /**
     * get the alpine context from the servlet context
     * ; create it if needed
     * @return
     */
    public synchronized AlpineContext getAlpineContext() {
        return AlpineContext.getAlpineContext();
    }


    public EndpointContext getContext(HttpServletRequest request) {
        AlpineContext ctx=getAlpineContext();
        return ctx.getEndpoints().lookup(request);
    }

    protected void report404(HttpServletResponse response) throws ServletException {
        try {
            PrintWriter writer = beginHttpResponse(response, HttpServletResponse.SC_NOT_FOUND);
            writer.println(makeHtmlText("Not Found"));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private String makeHtmlText(String message) {
        return "<html><head><title>"+message+"</title></head><body>"+message+"</body></html>";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EndpointContext context=getContext(request);
        if(context==null) {
            report404(response);
        } else {
            PrintWriter writer = beginHttpResponse(response, HttpServletResponse.SC_OK);
            String text=(String) context.get(ContextConstants.ATTR_GET_MESSAGE);
            if(text==null) {
                text=makeHtmlText("Alpine Endpoint");
            }
            writer.println(text);
            writer.flush();
            writer.close();
        }
    }

    protected PrintWriter beginHttpResponse(HttpServletResponse response, int code) throws IOException {
        response.setStatus(code);
        response.setContentType(TEXT_HTML);
        PrintWriter writer = response.getWriter();
        return writer;
    }


    /**
     * Post handles SOAP requests
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EndpointContext endpointContext = getContext(request);
        if(endpointContext==null) {
            //nothing here; keep going
            report404(response);
            return;
        }
        MessageContext messageContext = endpointContext.createMessageContext();
        MessageDocument requestMessage = null;
        MessageDocument responseMessage=null;
        HttpBinder binder = new HttpBinder(endpointContext);
        try {
            requestMessage = binder.parseIncomingPost(messageContext,request);

            //get the handler list
            List<String> handlers=(List<String>) endpointContext.get(ContextConstants.ATTR_HANDLERS);
            if(handlers==null) {
                throw new AlpineRuntimeException(ERROR_NO_HANDLER);
            }
            //instantiate all of them before starting dispatch
            List<MessageHandler> instances=new ArrayList<MessageHandler>(handlers.size());
            for(String classname:handlers) {
                MessageHandler handler = createMessageHandler(messageContext, classname);
                instances.add(handler);
            }
            //now go and dispatch them
            for (MessageHandler handler : instances) {
                //dispatch
                handler.processMessage(messageContext, endpointContext);
            }
            responseMessage = messageContext.getResponse();
        } catch (Throwable thrown) {
            FaultBridge bridge=FaultBridge.getFaultBridge(messageContext);
            Fault fault=bridge.extractFaultFromThrowable(thrown);
            //we have the fault; patch it in
            responseMessage = messageContext.getResponse();
            Body body = responseMessage.getEnvelope().getBody();
            body.removeChildren();
            body.appendChild(fault);
        }
        responseMessage = messageContext.getResponse();
        response.setStatus(responseMessage.isFault()?
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR : HttpServletResponse.SC_OK);
        binder.outputResponse(messageContext, response);
    }

    /**
     * Override point: create a new handler
     * @param messageContext
     * @param classname
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    MessageHandler createMessageHandler(MessageContext messageContext,String classname) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        Class<MessageHandler> aClass = (Class<MessageHandler>) Class.forName(classname);
        MessageHandler handler = aClass.newInstance();
        return handler;
    }
}
