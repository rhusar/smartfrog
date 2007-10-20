<%-- /**
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
*/ --%>
<%@ page language="java" %>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>
<%@ include file="header.inc.jsp" %>

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
	session.setAttribute("message", "Invalid TITLE \"" + title + "\"");
	// dispatch back 
	RequestDispatcher dispatcher = 
		request.getRequestDispatcher("SFActions.jsp"); 
	dispatcher.forward(request, response);
	return;
    }
    // else continue
    SfDescriptionType.Argument []args = desc.getArgumentArray();
    
    String configFile = desc.getUrl();
    boolean state = true ;
    
    String []classpaths = desc.getClassPathArray();
	
%>

<script language="javascript" type="text/javascript">
function toggle(divId) {
    var state = document.getElementById(divId).style.display ;
    if ( state == "none" ) {
	document.getElementById(divId).style.display = "";
    }else{
	document.getElementById(divId).style.display = "none";
    }
}  

function submit(formId, target){
    var form = document.getElementById(formId);
    form.action = target ;
    form.submit();
}

function addRow1(table, name) {
    var len = table.rows.length ;

    var newRow = document.createElement("tr");
    var idx = "" + (len ) ;

    var col1 = document.createElement("td");
    col1.setAttribute("class", "medkum");
    var d2 = document.createElement("input");
    d2.setAttribute('type', 'text');
    d2.setAttribute('name', name );
    d2.setAttribute('size', "40" );

    col1.appendChild(d2);

    var col2 = document.createElement("td");
    col2.setAttribute("class", "medium");

    var a = document.createElement("input");
    a.setAttribute("type", "button");
    a.setAttribute("value", "Remove");
    a.setAttribute("class", "default");
    a.setAttribute("onclick", "deleteRow(this.parentNode.parentNode.rowIndex)");

    col2.appendChild(a);

    newRow.appendChild(col2);
    newRow.appendChild(col1);
    table.getElementsByTagName("tbody")[0].appendChild(newRow);
}

function deleteRow(rowIdx){
    var table = document.getElementById('jarTable');
    table.deleteRow(rowIdx);
}

setNextSubtitle("Library Dependencies Page");

</script>

<br>
<div align="center">
<center>
<!-- This is the page menu -->
<div align="center" style="width: 95%;">
  <script language="javascript" type="text/javascript">
    oneVoiceWritePageMenu("SFActionLibs","header",
                            "Configure Attributes",
  	                        "javascript:setLocation('SFActionArgs.jsp?title=<%=title%>')"
    );
  </script>
</div>

<%@ include file="message.inc.jsp" %>

<!-- Actual Body starts here -->
<form action="SaveSFAction.jsp?pageAction=setActionLibs&&title=<%=title%>" method="post">

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="jarTable">
  <thead>
    <tr class="captionRow">
	<th colspan="99"> JAR Dependencies</th>
    </tr>	
  </thead>
<tbody>
<%
	String rowClass = "";
	if( null != classpaths ){
	  for( int i=0;i<classpaths.length;i++){
	    rowClass = rowClass == "" ? "class='altRowColor'" : "";
%>
<tr <%=rowClass %>>
	<td class="medium" style="width:20px;"><input type="button" value="Remove" class="default" onclick="deleteRow(this.parentNode.parentNode.rowIndex)"></td>
	<td class="medium">
		<input type="text" name="classpath" value="<%=classpaths[i]%>" size="40"></input>
	</td>
</tr>		
<%
		}
	}
%>
</tbody>
</table>
<br/>
<div style="width:95%;">
<div class="buttonSet">
  <div class="bWrapperUp"><div><div>
    <input class="hpButton" type="submit" name="save" value="Save Changes">
  </div></div></div>
  <div class="bWrapperUp"><div><div>
    <input class="hpButton" type="button" value="Add a Dependency"
      onclick="addRow1(document.getElementById('jarTable'),'classpath')"></input>
  </div></div></div>
</div>
<div class="clearFloats"></div>
</div>
</form>
<br/><br/>
</center>
</div>

<%@ include file="footer.inc.jsp"%>
