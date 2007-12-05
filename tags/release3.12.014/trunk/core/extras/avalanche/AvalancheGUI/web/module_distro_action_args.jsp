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

<%
  	String errMsg = null; 
  	ModulesManager manager = factory.getModulesManager();

  	SettingsManager settingsMgr = factory.getSettingsManager();
  	SfConfigsType configs = settingsMgr.getSFConfigs();  

  	String moduleId = request.getParameter("moduleId");
  	if( null == moduleId){
	    session.setAttribute("message", "Module-id is null");
	    RequestDispatcher dispatcher = 
		request.getRequestDispatcher("module_list.jsp");  		
	    dispatcher.forward(request, response);
	    return;
  	}

  	String version  = request.getParameter("version");
  	String distroId = request.getParameter("distroId");
  	String actionName = request.getParameter("title");
  	
  	ModuleType module = manager.getModule(moduleId);
  	if( null == module ) {
	    session.setAttribute("message",
	    	"Module \"" + moduleId + "\" not found");
	    RequestDispatcher dispatcher = 
	    	request.getRequestDispatcher("module_list.jsp");
	    dispatcher.forward(request, response);
	    return;
  	}

  	VersionType mv = null;
  	DistributionType distro = null;
  	ActionType action = null;
  	SfDescriptionType sfDesc = null; 

	VersionType[] versions = module.getVersionArray();
	if( null == versions ) {
	    session.setAttribute("message",
	    	"No versions in module \"" + moduleId + "\"");
	    RequestDispatcher dispatcher = 
	    	request.getRequestDispatcher("module_list.jsp");
	    dispatcher.forward(request, response);
	    return;
	}
	
	for( int i=0;i<versions.length;i++){
		if( version.equals(versions[i].getNumber()) ){
			mv = versions[i];
			break;
		}
	}

	if( null == mv ){
	    errMsg = "No version \"" + version + 
	    	"\" exists for Module-id \"" + moduleId + "\"";
	    session.setAttribute("message", errMsg);
	    RequestDispatcher dispatcher =
	    	request.getRequestDispatcher("module_list.jsp");
	    dispatcher.forward(request, response);
	    return;
  	}
  		
	DistributionType []distros = mv.getDistributionArray();
  	for( int i=0;i<distros.length;i++){
  		if( distroId.equals(distros[i].getId()) ){
  			distro = distros[i];
  			break;
  		}
  	}
  	
	if( null == distro ){
		errMsg = "No distribution-id \"" + distroId + 
			"\",  version \"" + version +
			"\" exists for Module-id \"" + moduleId + "\"";
  		session.setAttribute("message", errMsg);
		RequestDispatcher dispatcher = 
			request.getRequestDispatcher("module_list.jsp");
		dispatcher.forward(request, response);
		return;
	}else{
		ActionType []actions = distro.getActionArray();
  		for( int i=0;i<actions.length;i++){
			System.out.println("Comparing : " + actionName +
				"  , " + actions[i].getName());
  			if( actionName.equals(actions[i].getConfiguration())){
				System.out.println("got action also ");
  				action=actions[i];
  				break;
  			}
  		}
	}		
	 
  	if( null != action ){
		SfDescriptionType[] descs = configs.getSfDescriptionArray();
		for( int i=0;i<descs.length;i++){
			if( descs[i].getTitle().equals(action.getConfiguration()) ){
				System.out.println("Found action : " + actionName);
				sfDesc = descs[i];
			}
		}
	}else{
	    session.setAttribute("message", "Action \"" + actionName +
	    		"\" not found");
	    RequestDispatcher dispatcher = 
	    		request.getRequestDispatcher("module_list.jsp");
	    dispatcher.forward(request, response);
	    return;
	}
%>

<script language="JavaScript" type="text/javascript">
    <!--

    function submit(target) {
        document.moduleListFrm.action = target;
        document.moduleListFrm.submit();
    }

    setNextSubtitle("Module Action Attributes Page");

    writePageMenu("ModuleActionArgs",
            "Install",
            "#"
            );
     -->
</script>

<%@ include file="message.inc.jsp" %>
<!-- Actual Body starts here -->
<br/>
<center>
 
<form method="post" 
    action="module_save.jsp?pageAction=saveActionArgs&&actionTitle=<%=action.getConfiguration()%>&&engine=<%=action.getEngine()%>&&moduleId=<%=moduleId%>&&version=<%=version%>&&distroId=<%=distroId%>">

<table id='argumentTable' style="border-collapse: collapse;" 
		bordercolor="#800080" border="1">
<tbody>
<tr>
    <th class="data">Attribute Description</th>
    <th class="data">Value</th>
</tr>

<%
    SfDescriptionType.Argument []args = sfDesc.getArgumentArray();
		
    for( int i=0;i<args.length;i++){
	String name = args[i].getName();
	String value = args[i].getValue();

	// If the name includes the word "password, the
	// input field must be protected:
	String inputType = name.matches(".*((?i)password).*") ?
	    "password" : "text";
		
	// See if we overwrote this value in module.
	ActionType.Argument []actionArgs = action.getArgumentArray();
	if( null != actionArgs ){
	    for( int j =0;j<actionArgs.length;j++){
		if(actionArgs[j].getName().equals(name)){
		    value = actionArgs[j].getValue();
		}
	    }
	}
%>
    <tr>
	<td class="data"><%=args[i].getDescription() %></td>
	<td class="data">
	    <input type='<%= inputType %>' name='action.argument.<%=name%>' 
	    		value='<%=value%>' size="40">
    </tr>
<%
    }
%>
</tbody>
</table>
<br/>
<input type="submit" name="submit" value="Save Changes" class="btn">
</form>
 
<%@ include file="footer.inc.jsp"%>
