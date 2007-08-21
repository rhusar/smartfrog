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
<%@ include file="header.inc.jsp" %>
<%@ page import="org.smartfrog.avalanche.server.engines.sf.*"%>
<%@ page import="org.smartfrog.avalanche.server.*"%>
<%@ page import="org.smartfrog.avalanche.settings.sfConfig.*"%>
<%@ page import="java.util.*"%>

<%
    String errMsg = null; 
    SettingsManager settingsMgr = factory.getSettingsManager();
    SfConfigsType configs = settingsMgr.getSFConfigs();  

    SfDescriptionType desc = null;
    SfDescriptionType[] descs = configs.getSfDescriptionArray();
    
    String title = request.getParameter("title");
    
    for( int i=0;i<descs.length;i++){
	    if( descs[i].getTitle().equals(title) ){
		    desc = descs[i] ;
		    break;
	    }
    }
    
    if( null == desc ){
	session.setAttribute("message",
		 "Invalid tiTLE \"" + title + "\"");
	// dispatch back 
	javax.servlet.RequestDispatcher dispatcher = 
		request.getRequestDispatcher("SFActions.jsp"); 
	dispatcher.forward(request, response);
	return;
    }
    // else continue
    SfDescriptionType.Argument []args = desc.getArgumentArray();
    
    String configFile = desc.getUrl();
    boolean state = true ;
    Map  m  = null;
    try{
	    m  = SFAdapter.getSFAttributes(configFile);
    }catch(Throwable t ){
	state = false ; 
	session.setAttribute("message",
		 "Failed to load URL \"" + configFile + "\"");
    }
    if((! state) || (m == null)) {
	// Dispatch back 
	javax.servlet.RequestDispatcher dispatcher =
		 request.getRequestDispatcher("SFActions.jsp"); 
	dispatcher.forward(request, response);
	return;
    }

    Set keys = m.keySet();	
%>

<script language="javascript" type="text/javascript">
function submit(formId, target){
    var form = document.getElementById(formId);
    form.action = target ;
    form.submit();
}
setNextSubtitle("Action Arguments Page");
</script>
<br>

<div align="center">
<center>

<!-- This is the page menu -->
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("SFActionArgs","header",
      "Library Dependencies",
  	"javascript:setLocation('SFActionLibs.jsp?title=<%=title%>')"
//      "Configure Attributes",
  	//"nowhere"
    );
  </script>
</div>
<%@ include file="message.inc.jsp" %>

<!-- Actual Body starts here -->
<form action="SaveSFAction.jsp?pageAction=setActionArgs&&title=<%=title%>"
	method="post">

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="actionArgsTable">
  <thead>
    <tr class="captionRow">
      <th>Attribute Name</th>
      <th>Description</th>
      <th>Default Value</th>
      <th>Can<br>Override</th>
    </tr>	
  </thead>
  <tbody>
<%
	int i = 0;
	String rowClass = "";
	Iterator itor = keys.iterator();
	while(itor.hasNext()){

	    rowClass = rowClass == "" ? "class='altRowColor'" : "";

	    String attrName = (String)itor.next();

	    SfDescriptionType.Argument arg = null ;
	    String value = null ;
	    for( int j=0;j<args.length;j++){
		    String argName = args[j].getName();
		    if( null!= argName && argName.equals(attrName)){
			    arg = args[j]; 
			    value = arg.getValue();
			    break;
		    }
	    }
	    if ( null == value ) {
		    value = (String)m.get(attrName);
	    }

String inputType = "text";
	    // If the attribute name or its description includes the 
	    // word "password, the input field must be protected:
	    //String inputType = attrName.matches(".*((?i)password).*") ?
		//"password" : "text";
	    //if (arg != null)
	        //if (arg.getDescription().matches(".*((?i)password).*"))
		    //inputType = "password";
%>
      <tr <%=rowClass %>>
	<td class="medium">
	    <input type="text" name="argument.name.<%=i%>"
		value="<%=attrName %>" readonly size="40">
	</td>
	<td class="medium">	
	    <input type="text" size="30" 
		name="argument.description.<%=i%>" 
		value="<%=(null ==arg)?"": arg.getDescription() %>">
	</td>
	<td class="medium">	
	    <input type="<%= inputType %>" size="30"
		name="argument.defaultValue.<%=i%>"
		value="<%=(null ==value)?"": value %>">
	</td>
	<td class="medium" align="center">	
<%
	if( null == arg) {
%>			
	    <input type="checkbox" name="selectedArg" value="<%=attrName%>">
<%
	}else{
%>		
	    <input type="checkbox" name="selectedArg" value="<%=attrName%>"
		 checked>
<%
	}
%>
	</td>
	
</tr>		
	
<%
	    i++;
	}
%>
</tbody>
</table>
<br/>

<div style="width:95%;">
<div class="buttonSet">
  <div class="bWrapperUp"><div><div>
    <input type="submit" name="save" value="Save Changes" class="hpButton"></input>
  </div></div></div>
</div>
<div class="clearFloats"></div>
</div>
</form>
<br/>
</center>
</div>

<%@ include file="footer.inc.jsp"%>
