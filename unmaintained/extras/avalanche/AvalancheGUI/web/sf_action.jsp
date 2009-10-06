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
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>

<%
  	String errMsg = null; 
  	ModulesManager manager = factory.getModulesManager();
  	SettingsManager settingsMgr = factory.getSettingsManager();
  	SettingsType defSettings = settingsMgr.getDefaultSettings();
  	SfConfigsType configs = settingsMgr.getSFConfigs();  
  	if( null == manager ){
  		errMsg = "Error connecting to manager database" ;
  		throw new Exception ( "Error connecting to manager database" );
  	}

%>

<script language="JavaScript" type="text/javascript">
    <!--
function toggle(divId) {
    var state = document.getElementById(divId).style.display ;
    if ( state == "none" )
    {
        document.getElementById(divId).style.display = "block";
    }else{
        document.getElementById(divId).style.display = "none";
    }
}  

function sub(formId, target){
    var form = document.getElementById(formId);
    form.action = target ;
    form.submit();
}

setNextSubtitle("Supported Actions Page");
    -->
</script>

<form name="actionListFrm" id='actionListFrm' method="post">
<br/>
<div align="center">
<center>
<!-- This is the page menu -->
<div align="center" style="width: 95%;">
    <script>
      oneVoiceWritePageMenu("SFActions","header");
    </script>
</div>

<%@ include file="message.inc.jsp" %>

<!-- Actual Body starts here -->
<table  border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="actionListTable">
  <thead>
    <tr class="captionRow">
      <th class="checkboxCell"><input type="checkbox" tableid="actionListTable"></th>
      <th>Title</th>
      <th>Action Type</th>
    </tr>
  </thead>
  <tbody>
<%
    String rowClass = "";
    SfDescriptionType []descs = configs.getSfDescriptionArray();
    for( int i=0;i<descs.length;i++){
      rowClass = rowClass == "" ? "class='altRowColor'" : "";
%>
    <tr <%=rowClass %>>
      <td class="checkboxCell"><input type="checkbox" rowselector="yes"
	name="selectedAction" value="<%=descs[i].getTitle()%>"></input>
      </td>
      <td>
	<a href="sf_action_args.jsp?title=<%=descs[i].getTitle()%>">
	  <%=descs[i].getTitle()%>
	</a>
      </td>
      <td> <%=descs[i].getAction()%></td>
    </tr>
<%
	}
%>
  </tbody>
</table>
    <br/>

    <div align="center" style="width: 95%;">
        <script>
            oneVoiceWritePageMenu("SFActions", "footer",
                    "Delete Selected Actions", "javascript:sub('actionListFrm','sf_action_save.jsp?pageAction=delAction')",
                    "Add an Action", "javascript:toggle('newActionDiv')"
                    );
        </script>
    </div>

</center>
</div>
</form>


<div id='newActionDiv' style="display:none">
    <center>
        <form id='newActionFrm' name='newActionFrm' method='post' action="sf_action_save.jsp?pageAction=addAction">
            <table id="newActionListTable" border="0" cellpadding="0" cellspacing="0" class="dataTable">
                <caption>New Action</caption>
                <tbody>
                    <tr>
                        <td class="medium" align="right">Title:</td>
                        <td class="editaleFieldCell">
                            <input type="text" name="title" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td class="medium" align="right">URL:</td>
                        <td class="editableFieldcell">
                            <input type="text" name="url" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td class="medium" align="right">Action:</td>
                        <td class="editableFieldCell">
                            <select name="sfAction">
                                <%
	  SettingsType.Action []actionOpts = defSettings.getActionArray();
	  for( int i = 0;i<actionOpts.length;i++){
%>
	    <option><%=actionOpts[i].getName()%></option>
<%
	  }
%>	
	  </select>
	</td>
      </tr>
    </tbody>
  </table>
<br/>
<div align="center" style="width: 95%;">
<div class="buttonSet">
  <div class="bWrapperUp"><div><div>
    <input class="hpButton" type="submit" name="Save" value="Save Action"></input>
  </div></div></div>
</div>
<div class="clearFloats"></div>
</div>
</form>
</center>
</div>

<%@ include file="footer.inc.jsp" %>
