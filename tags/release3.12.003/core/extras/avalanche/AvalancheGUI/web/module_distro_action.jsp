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
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>
<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>

<%
  	String errMsg = null; 
  	ModulesManager manager = factory.getModulesManager();
	SettingsManager settingsMgr = factory.getSettingsManager();
	SettingsType defSettings = settingsMgr.getDefaultSettings();
  	
  	if( null == manager ){
  		errMsg = "Error connecting to modules database" ;
  		throw new Exception ( "Error connecting to modules database" );
  	}
  	
  	String moduleId = request.getParameter("moduleId");
  	String versionNumber = request.getParameter("version");
  	String distroId = request.getParameter("distroId");
  	SfConfigsType configs = settingsMgr.getSFConfigs();  
  	  	
	ModuleType m = manager.getModule(moduleId);
	VersionType versions[] = m.getVersionArray();
	VersionType version = null ;
	DistributionType distro = null ;
	for( int j=0;j<versions.length;j++){
		if( versionNumber.equals(versions[j].getNumber()) ){
			version = versions[j];
			break;
		}
	}

	DistributionType []distros = version.getDistributionArray();
	for( int i=0;i<distros.length;i++){
		String id = distros[i].getId();
		if( distroId.equals(id) ){
			distro = distros[i];
			break; 
		}
	}

  	ActionType []actions = distro.getActionArray();

	SfDescriptionType []descs = configs.getSfDescriptionArray();
	String keys = "new Array(" ;
	String values = "new Array(" ;
	String titles = "new Array(" ;

	SettingsType.Action []allActions = defSettings.getActionArray();
	
	for ( int i=0;i<allActions.length;i++){
		String actName = allActions[i].getName() ;
		
		String cfgArr = "new Array(" ;
		String cfgTitle = "new Array(" ;
		boolean flag = false ; 
		for( int j=0;j<descs.length;j++){
			String action = descs[j].getAction();
			
			if( action.equals(actName) ){
				// append in array
				if( flag) {
					cfgArr += ",'" + descs[j].getUrl() +"'";
					cfgTitle += ",'" + descs[j].getTitle() +"'";
				}else{
					flag = true ;
					cfgArr += "'" + descs[j].getUrl() + "'";
					cfgTitle += "'" + descs[j].getTitle() + "'";
				}
			}
		}

		if( i != allActions.length -1 ){
			keys += "'" + actName+ "'," ;
			values += cfgArr + "),";
			titles += cfgTitle + "),";
			
		}else{
			keys += "'" + allActions[i].getName() + "')" ;
			values += cfgArr + "))";
			titles += cfgTitle + "))";
		}
	}
  	
%>

<script language="JavaScript" type="text/javascript">
    <!--
    function toggle(divId)
    {
        var state = document.getElementById(divId).style.display ;
        if (state == "none")
        {
            document.getElementById(divId).style.display = "block";
        } else {
            document.getElementById(divId).style.display = "none";
        }
    }

    function submit(target) {
        document.moduleListFrm.action = target;
        document.moduleListFrm.submit();
    }

    function setConfigs(keys, titles, values) {
        var sel = document.getElementById('actions');

        var stext = sel.options[sel.selectedIndex].text
        var i = 0;

        for (i = 0; i < keys.length; i++) {
            if (keys[i] == stext) {
                break;
            }
        }

        var opts = values[i];
        var titles = titles[i];
        var cfgSel = document.getElementById('configFile');

        cfgSel.options.length = 0;

        for (var j = 0; j < opts.length; j++) {
            cfgSel.options[j] = new Option(titles[j], titles[j]);
        }
    }

    setNextSubtitle("Distribution Actions Page");
    -->
</script>


<form id='moduleListFrm' name='moduleListFrm' method='post'>

<div align="center" style="width: 95%;">
<script language="JavaScript" type="text/javascript">
<!--
oneVoiceWritePageMenu("DistroActions","",
                "Add",
                "javascript:toggle('newAction')",
                "Delete",
                "javascript:submit('module_save.jsp?pageAction=delAction&moduleId=<%=moduleId%>&version=<%=versionNumber%>&&distroId=<%=distroId %>')"
)
-->
</script>
</div>

<%@ include file="message.inc.jsp" %>
<!-- Actual Body starts here -->
<br/>
<center>
<br/><br/>
<table id="actionListTbl" style="border-collapse: collapse;" 
		bordercolor="#800080" border="1">
    <tbody>
	<tr> 
	    <th class="data" colspan="99">
Module: <a href="module_view.jsp?moduleId=<%=moduleId %>"><%=moduleId %></a> &gt;&gt; <a href="module_version_view.jsp?moduleId=<%=moduleId %>&&version=<%=versionNumber %>"><%=versionNumber %></a>
	    </th>
	</tr> 
	<tr> 
	    <th class="data">Select</th> 
	    <th class="data">Name </th>
	    <th class="data">Action Type</th>
	</tr>
<%
	for( int i=0;i<actions.length;i++){
		String actionName = actions[i].getName();
%>			
	<tr>
	    <td class="data" align="center">
		<input type="checkbox" name="selectedAction" value="<%=actions[i].getConfiguration()%>">
	    </td>
	    <td class="data">
		<a href="module_distro_action_args.jsp?moduleId=<%=moduleId%>&version=<%=versionNumber%>&distroId=<%=distroId%>&&title=<%=actions[i].getConfiguration()%>"><%=actions[i].getConfiguration() %></a>
	    </td>
	    <td class="data">
		<%=actionName %>
	    </td>
	</tr>
<%
    }
%>
	
</tbody>
</table>
</center>
</form>

<center>
<div id="newAction" style="display:none">

<form method="post" action="module_save.jsp?pageAction=addAction&&moduleId=<%=moduleId%>&&version=<%=version.getNumber()%>&&distroId=<%=distroId %>">

<table style="border-collapse: collapse;" bordercolor="#800080" border="1">
<tr>
    <th class="data" align="right">Action Name:</th>
    <td class="data"> 
	<select name="actionTitle" id="actions"
		onchange="setConfigs(<%=keys%>, <%=titles%>, <%=values%>)">
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
<tr>
    <th class="data" align="right">Engine:</th>
    <td class="data">
	<select name="engine"> 
<%
	SettingsType.DeploymentEngine []engines = defSettings.getDeploymentEngineArray();
	for( int i=0;i<engines.length;i++){
%>	
	    <option><%=engines[i].getName()%></option>
<%
	}
%>
	</select>
    </td>
</tr>	
<tr>
    <th class="data" align="right">Configuration File:</th>
    <td class="data"> 
	<select name="configFile" id="configFile">
<%
	for( int i=0;i<descs.length;i++){
%>	
	    <option value="<%=descs[i].getTitle()%>"><%=descs[i].getTitle()%></option>
<%
	}
%>		
	</select>
    </td>
</tr>	
</table><br/>
    <input type="submit" value="Add this Action" name="AddDistro"
	    class="default">

</form>
</div>

<%@ include file="footer.inc.jsp" %>
