<% /**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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
*/ %>
<%@ page language="java" contentType="text/html" %>
<%
//=================================================================
// This "page" is intended to be included by other pages
// to display a message banner.
//=================================================================
%>

<%
    String session_message = (String)session.getAttribute("error_msg");
    if(null != session_message && !session_message.trim().equals("")) {
%>

<center>
  <div id="errmsg" class="data" display="block"
        style="width:90%; background:#FFAAAA; font-size: 10pt; padding: 4px;
	    margin: 4px; border: red solid 1px;">
      <%= "<b>Error:</b> " + session_message %>
  </div>
<center>

<%
	session.removeAttribute("error_msg");
    }
    
    session_message = (String)session.getAttribute("success_msg");
    if(null != session_message && !session_message.trim().equals("")) {
%>

<center>
  <div id="msg" class="data" display="block"
        style="width:90%; background:#AAFFAA; font-size: 10pt; padding: 4px;
	    margin: 4px; border: red solid 1px;">
      <%= "<b>Error:</b> " + session_message %>
  </div>
<center>

<%
	session.removeAttribute("success_msg");
    }
%>
