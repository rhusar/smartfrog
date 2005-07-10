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
import org.smartfrog.projects.alpine.om.soap11.Fault;
import org.smartfrog.projects.alpine.om.soap11.Body;
import org.smartfrog.projects.alpine.interfaces.SoapFaultSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * This servlet handles SOAP Posted stuff
 */
public class SoapPostServlet extends ServletBase {

    
    
    /**
     * this is the wrong place for state
     */ 
    private EndpointContext context=new EndpointContext();

    public synchronized void setContext(EndpointContext context) {
        this.context = context;
    }

    public EndpointContext getContext() {
        return context;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        //todo
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doPost(httpServletRequest, httpServletResponse);
        EndpointContext endpointContext = getContext();
        MessageContext messageContext = endpointContext.createMessageContext();
        MessageDocument requestMessage = null;
        MessageDocument responseMessage=null;
        HttpBinder binder = new HttpBinder(getContext());
        try {
            requestMessage = binder.parseIncomingPost(messageContext,httpServletRequest);
            //TODO: dispatch
            responseMessage = messageContext.getResponse();
        } catch (Exception e) {
            if(e instanceof SoapFaultSource) {
                SoapFaultSource source=(SoapFaultSource) e;
                Fault fault = source.GenerateSoapFault();
                responseMessage = messageContext.getResponse();
                Body body = responseMessage.getEnvelope().getBody();
                body.removeChildren();
                body.appendChild(fault);
            }
        }
        binder.outputResponse(messageContext, httpServletResponse);
    }
}
