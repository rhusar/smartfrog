<% /*
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
<%@ include file="header.inc.jsp"%>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.core.host.*"%>

<%@ include file="init_hostmanager.inc.jsp"%>
<%  SettingsManager settingsMgr = factory.getSettingsManager();
  	SettingsType defSettings = settingsMgr.getDefaultSettings();


    String os = null;
    String plaf = null;
    String arch = null;

    HostType host = null;
    String hostId = request.getParameter("hostId");

    if (hostId != null) {
        hostId = hostId.trim().toLowerCase();
        if (!hostId.equals("")) {
            host = manager.getHost(hostId);
            if (null != host) {
                PlatformSelectorType ps = host.getPlatformSelector();
                if (null != ps) {
                    os = ps.getOs();
                    plaf = ps.getPlatform();
                    arch = ps.getArch();
                }
            }
        }
    }

    String site = "host_save.jsp?action=bs&next=";
%>

<script type="text/javascript" language="JavaScript">
    <!--
    setNextSubtitle("Host Basic Settings Page");
    -->
</script>

<br/>

<form id="addHostFrm" name="addHostFrm" method="post" action="">
<div align="center">
<center>

<%@ include file="host_setup_menu.inc.jsp" %>

<br/>
<table id="hostListTable" class="dataTable" 
    style="width: 400px; border-collapse: collapse;">
    <caption>Basic Settings</caption>
    <tbody>
	<tr> 
	    <td class="medium" align="right">Host:</td> 
	    <td class="medium"> 
	<% if (host == null) { %>
		    <input type="text" name="hostId" size="30" id="hostId" />
	<% } else { %>
		    <input type="text" name="hostId" size="30" id="hostId" disabled="true" value="<%= host.getId() %>" class="default" />
	<% } %>
	    </td>
	</tr>  			

	<tr> 
		<td class="medium" align="right">Operating System:</td> 
		<td class="medium">  
		<select name="os">
	    <%  String oses[] = defSettings.getOsArray();
            for (String ose : oses) { %>
            <option<%=((os!=null)&&os.equals(ose))?" selected":""%>><%=ose%></option>
        <% } %>
		</select>
		</td>
	</tr>  			
	<tr>
	    <td class="medium" align="right">Platform:</td>
	    <td class="medium">  
		<select name="platform">
		<% String plafs[] = defSettings.getPlatformArray();
           for (String plaf1 : plafs) { %>
           <option<%=((plaf != null) && plaf.equals(plaf1))?" selected":""%>><%=plaf1%></option>
        <% } %>
		</select>
		</td>
	</tr>  	
	<tr>		
	    <td class="medium" align="right">Architecture:</td>
	    <td class="medium">  
		<select name="arch">
	    <% String archs[] = defSettings.getArchArray();
           for (String arch1 : archs) { %>
            <option<%= arch1.equals(arch)?" selected":""%>><%=arch1%></option>
        <% } %>
		</select>
		</td>
	</tr>  	
    </tbody>
</table>

<br/>
<div align="center" style="width: 95%;">
    <script language="JavaScript" type="text/javascript">
        <!--
        oneVoiceWritePageMenu(  "HostSetup", "footer",
                                "Save Changes", "javascript:document.addHostFrm.action='<%= site %>am<%= hostIdent %>'; document.addHostFrm.submit();");
        -->
    </script>
</div>

</center>
</div>
</form>



<%@ include file="footer.inc.jsp"%>