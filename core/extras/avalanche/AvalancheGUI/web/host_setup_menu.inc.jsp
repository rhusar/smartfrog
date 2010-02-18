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
<%@ page language="java" %>

<div align="center" style="width:95%;">
  <script type="text/javascript" language="JavaScript">
    <!--

   <% String hostIdent = request.getParameter("hostId");
      if (hostIdent == null) hostIdent = ""; else hostIdent = "&hostId=" + hostIdent; %>

    oneVoiceWritePageMenu("HostSetup","header",
      "Host Properties",
  	  "javascript:document.addHostFrm.action='<%= site %>env<%= hostIdent %>'; document.addHostFrm.submit();",
      "Transfer Modes",
  	  "javascript:document.addHostFrm.action='<%= site %>tm<%= hostIdent %>'; document.addHostFrm.submit();",
      "Access Modes",
  	  "javascript:document.addHostFrm.action='<%= site %>am<%= hostIdent %>'; document.addHostFrm.submit();",
      "Basic Settings",
  	  "javascript:document.addHostFrm.action='<%= site %>bs<%= hostIdent %>'; document.addHostFrm.submit();");
     -->
  </script>
</div>

<%@ include file="message.inc.jsp"%>