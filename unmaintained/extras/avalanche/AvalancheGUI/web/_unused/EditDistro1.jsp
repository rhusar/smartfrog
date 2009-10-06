<!-- /**
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
*/
-->
<%@ page language="java" %>


<%@	page import="org.smartfrog.avalanche.settings.xdefault.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.modules.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="java.util.*"%>

<%@ include file="../InitBeans.jsp" %>

<%
  	AvalancheRepository repository = avalancheFactory.getModuleRepository();
  	String errMsg = null; 
  	
  	String moduleId = request.getParameter("moduleId");
  	String version =  request.getParameter("version");
  	String distroId =  request.getParameter("distroId");
  	
  	ModuleType module = null ;
  	VersionType mv = null ;
  	DistributionType distro = null ;

  	SystemSettings sett = avalancheFactory.getSettings();
  	SfConfigsType configs = sett.getSFConfigs();  
  	
  	if( null == sett ){
  		errMsg = "Error connecting to settings database" ;
  		throw new Exception ( "Error connecting to settings database" );
  	}
  	SettingsType defSettings = sett.getDefaultSettings();  

  	if( null != moduleId || version != null || distroId == null ){
  		module = repository.getModule(moduleId.trim());
  		if( null == module ){
  			errMsg = "No Module exists with Module Id : " + moduleId ;
  		}
  		
  		VersionType[] versions = module.getVersionArray();
 		boolean exists = false;
  		for( int i=0;i<versions.length;i++){
  			if( version.equals(versions[i].getNumber()) ){
  				exists = true ;
  				mv = versions[i];
  				break;
  			}
  		}

  		if( null == mv ){
			errMsg = "No version : " + version + " exists for Module Id : " + moduleId ;
  		}
  		
		DistributionType []distros = mv.getDistributionArray();
		boolean distroFlag = false;
  		for( int i=0;i<distros.length;i++){
  			if( distroId.equals(distros[i].getId()) ){
  				distroFlag = true ;
  				distro = distros[i];
  				break;
  			}
  		}
  		if( null == distro ){
			errMsg = "No distrribution Id : " + distroId + 
					" ,  version : " + version + " exists for Module Id : " + moduleId ;
  		}
		
  	}else{
  			errMsg = "Invalid arguments Module Id : " + moduleId + "Version Id : " + version;
  	}
  	
  	String addAction = request.getParameter("addAction");
  	String engine = request.getParameter("engine");
  	String configFile = request.getParameter("config");
  	

  	if( null != addAction && "true".equals(addAction)){
  		String id = request.getParameter("action");
  		
  		ActionType act = distro.addNewAction();
  		act.setName(id);
  		act.setEngine(engine);
  		act.setConfiguration(configFile);
  		
  		java.util.Enumeration enum = request.getParameterNames();
  		while(enum.hasMoreElements()){
  			String param = (String)enum.nextElement();
  			if( param.startsWith("key") ){
  				String keyValue = request.getParameter(param);
  				if( null != keyValue && !keyValue.trim().equals("") ){
	  				String idx = param.substring(3, param.length());
	  				String value = request.getParameter("value" + idx);
	  				ActionType.Argument arg = act.addNewArgument();
	  				arg.setName(keyValue);
	  				arg.setValue(value);
	  			}
  			}
  		}
  		if( act == null ){
  			errMsg = "Failed Adding action ";
  		}
  		
  		repository.setModule(module);
	}
	
	// cache sf configs for javascripting
	SfDescriptionType []descs = configs.getSfDescriptionArray();

	SettingsType.Action []allActions = defSettings.getActionArray();
	HashMap actionMap = new HashMap();
	
	for ( int i=0;i<allActions.length;i++){
		String actName = allActions[i].getName() ;
		
		for( int j=0;j<descs.length;j++){
			String action = descs[j].getAction();
			List aurls = (List)actionMap.get(action) ; 
			if( null == aurls ){
				aurls = new ArrayList(20);
				actionMap.put(action, aurls);
			}
			aurls.add(descs[j]);
		}
	}
%>


<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<html>
<head>
<link rel="stylesheet" type="text/css" href="styles.csscss"/>
<script type="text/javascript" src="utils.js.js"></script>

<script type="text/javascript">
    function toggle(divId)
    {
        var state = document.getElementById(divId).style.visibility ;
        if (state == "hidden")
        {
            document.getElementById(divId).style.visibility = "visible";
        } else {
            document.getElementById(divId).style.visibility = "hidden";
        }
    }

    function addRow(table, key, value)
    {
        var len = table.rows.length ;

        var newRow = document.createElement("tr");
        var idx = "" + (len + 1) ;

        var col1 = document.createElement("td");
        var d2 = document.createElement("input");
        d2.setAttribute('type', 'text');
        d2.setAttribute('name', 'key' + idx);
        d2.setAttribute('value', key);
        col1.appendChild(d2);

        var col2 = document.createElement("td");
        var d3 = document.createElement("input");
        d3.setAttribute('type', 'text');
        d3.setAttribute('name', 'value' + idx);
        d3.setAttribute('value', value);
        col2.appendChild(d3);

        newRow.appendChild(col1);
        newRow.appendChild(col2);
        table.getElementsByTagName("tbody")[0].appendChild(newRow);
    }

    function addRowOpt(table, keys, value)
    {
        var len = table.rows.length ;

        var newRow = document.createElement("tr");
        var idx = "" + (len + 1) ;

        var col1 = document.createElement("td");
        var d2 = document.createElement("select");

        for (var i = 0; i < keys.length; i++) {
            var opt = document.createElement("option");
            opt.appendChild(document.createTextNode(keys[i]));
            d2.appendChild(opt)

        }
        col1.appendChild(d2);

        var col2 = document.createElement("td");
        var d3 = document.createElement("input");
        d3.setAttribute('type', 'text');
        d3.setAttribute('name', 'value' + idx);
        d3.setAttribute('value', value);
        col2.appendChild(d3);

        newRow.appendChild(col1);
        newRow.appendChild(col2);
        table.getElementsByTagName("tbody")[0].appendChild(newRow);
    }

    function setConfigs() {
        var sel = document.getElementById('actions');

        var stext = sel.options[sel.selectedIndex].text
        var i = 0;

        var cfgSel = document.getElementById('configFile');
        cfgSel.options.length = 0;
    <%
        Set keys = actionMap.keySet();
        Iterator itor = keys.iterator();
        while(itor.hasNext()){
            String key = (String)itor.next();
            List vals = (List) actionMap.get(key);
    %>
		if( stext == <%=key%> ){
<%
		for( int j =0;j<vals.size();j++){
			SfDescriptionType t = (SfDescriptionType)vals.get(j);
%>
				cfgSel.options[j] = new Option('<%=t.getUrl()%>', '<%=t.getTitle()%>');
<%
		}
	}
%>
 }

</script> 

</head>
<body bgcolor="#FFFFFF">
<script>
setNextSubtitle("Edit Distro1 Page");
</script>


<center>
<big>Module : <%=moduleId%> </big><br>
<big>Version : <%=version%> </big>
<big>Distribution : <%=distroId%> </big>

<br><br>
<%
	if( null != errMsg ){
%>

<font color="red">
	<%=errMsg%>
</font><br>
<%
}
%>

<%
if( null != module && null != mv){
%>

<table cellspacing="2" cellpadding="4" border="1" style="border-collapse: collapse" bordercolor="#00FFFF">
<tr bgcolor="#00FFFF"> <th> Module ID </th> <th> Version </th> <th> Platform </th> <th>Actions</th>
</tr>
<tr>
	<td> <%=module.getId()%> <br> 
	Vendor : <%=module.getVendor()%><br>
	Description : <%=module.getDescription()%>
	</td>	<td></td>	<td></td> <td></td>
</tr>
<tr>
	<td> </td>
	<td>
		<%=version%>  
	</td>
	<td></td> <td></td>
</tr>  
<%
				PlatformSelectorType selector = distro.getPlatformSelector();
				String os = selector.getOs();
				String plaf = selector.getPlatform();
				String arch = selector.getArch();
				
%>
<tr>
	<td></td><td></td>
	<td> 
		<%=distroId%></a><br>
			(<%=os%>, <%=plaf%>, <%=arch%>)
	</td>
	<td>
<%
			 ActionType []actions = distro.getActionArray();
			 for( int a=0;a<actions.length;a++){
%>
<a href="Action.jsp?moduleId=<%=moduleId%>&&version=<%=version%>&&distroId=<%=distroId%>&&action=<%=actions[a].getName()%>">
				<%=actions[a].getName()%></a><br>
<%
			 }
%>		
	</td>	
</table>
<br>

<a href="#" onClick="javascript:toggle('newAction')"> Add a new Action </a> <br><br>
<div id="newAction" style="visibility:hidden">
<form method="post" action="EditDistro.jsp?addAction=true&&moduleId=<%=moduleId%>&&version=<%=version%>&&distroId=<%=distroId%>">
<table>
<tr>
	<td> Action Name </td>
	<td> 
	<select name="action" onchange="setConfigs()" id="actions">
<%
			SettingsType.Action []actionOpts = defSettings.getActionArray();
			for( int i = 0;i<actionOpts.length;i++){
%>
				<option><%=actionOpts[i].getName()%></option>
<%
			}
%>	
	</td>
</tr>
<tr>
	<td> Engine </td>
	<td>
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
	<td> Configuration File </td>
	<td> 
		<select name="config" id="configFile">
<%
		for( int i=0;i<descs.length;i++){
%>	
		<option><%=descs[i].getUrl()%></option>
<%
		}
%>		
		</select>
	</td>
</tr>	
</table>

<br>
<input type="submit" value="Add" name="AddAction">
</div>
</form>
</div>
<%
  	}
%>

</center>

</body>
</html>
