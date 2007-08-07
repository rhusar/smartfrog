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
<%@ include file="header.inc.jsp" %>
<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>

<% 
  	String errMsg = null; 
        ModulesManager manager = factory.getModulesManager();
  	
  	if( null == manager ){
  		errMsg = "Error connecting to modules database" ;
  		throw new Exception ( "Error connecting to modules database" );
  	}
  	
  	String []modules = manager.listModules();
%>
	
<%--
	SettingsManager settingsMgr = factory.getSettingsManager();
	SettingsType defSettings = settingsMgr.getDefaultSettings();
	
	defSettings.addOs("Windows");
	defSettings.addOs("Linux");

	defSettings.addPlatform("Intel");

	defSettings.addArch("IA64");
	defSettings.addArch("x86");

	SettingsType.AccessMode accessMode = defSettings.addNewAccessMode();
	accessMode.setName("ssh");

	SettingsType.DataTransferMode dataTransferMode = defSettings.addNewDataTransferMode();
	dataTransferMode.setName("scp");

	defSettings.addSystemProperty("JAVA_HOME");
	defSettings.addSystemProperty("CATALINA_HOME");
	defSettings.addSystemProperty("AVALANCHE_HOME");

	SettingsType.DeploymentEngine deploymentEngine = defSettings.addNewDeploymentEngine();
	deploymentEngine.setName("SMARTFROG");

	SettingsType.Action action1 = defSettings.addNewAction();
	action1.setName("INSTALL");
	SettingsType.Action action2 = defSettings.addNewAction();
	action2.setName("UNINSTALL");
	SettingsType.Action action3 = defSettings.addNewAction();
	action3.setName("START");
	SettingsType.Action action4 = defSettings.addNewAction();
	action4.setName("STOP");

	settingsMgr.setDefaultSettings(defSettings);
--%>

<script language="JavaScript" type="text/javascript">
   <!--
    function toggle(divId)
    {
        var state = document.getElementById(divId).style.display ;
        if (state == "none")
        {
            document.getElementById(divId).style.display = "";
        } else {
            document.getElementById(divId).style.display = "none";
        }
    }

    function deleteModule() {
        var selectors = document.getElementsByName("selectedModule");
        var selectedModules = new Array();

        for (var i = 0; i < selectors.length; i++)
        {
            if (selectors[i].checked)
                selectedModules.push(selectors[i]);
        }

        var count = selectedModules.length;
        if (count == 0)
        {
            alert("You must select one or more modules for this action.");
            return;
        }

        var alertMsg = "This action will delete ";
        if (count == 1)
            alertMsg += "one modules."
        else
            alertMsg += count + " modules."

        alertMsg += " Are you sure you want to continue?";

        if (confirm(alertMsg)) {
            document.hostListFrm.action = "module_save.jsp?pageAction=delMod";
            document.hostListFrm.submit();
        }
    }

    function submit(target) {
        document.moduleListFrm.action = target;
        document.moduleListFrm.submit();
    }

    setNextSubtitle("List Modules Page")
    -->
</script>

<form id="moduleListFrm" name="moduleListFrm" method="post" action="module_save.jsp">

    <!-- This is the page menu -->
    <br/>

    <div align="center">
        <center>

            <div align="center" style="width: 95%;">
                <script>
                    oneVoiceWritePageMenu("ModulesList", "header");
                </script>
            </div>

            <%@ include file="Message.jsp" %>

<!-- Actual Body starts here -->
<table border="0" cellpadding="0" cellspacing="0" class="dataTable tableHasCheckboxes" id="moduleListTable">
    <thead>
	<tr class="captionRow"> 
	    <th class="checkboxCell"><input type="checkbox" tableid="moduleListTable"></th>
	    <th style="width:120px">Module</th>
	    <th>View</th>
	    <th>Description</th>
	    <th>Versions</th>
	</tr>
    </thead>
    <tbody>
<%
    String rowClass = "";
    if (modules.length == 0) {
    for( int i=0;i<modules.length;i++ ){
      ModuleType m = null;
      String description = null;
      try{
	    m = manager.getModule(modules[i]);
	    description = m.getDescription();
      }catch(NullPointerException e){
      // ugly patch for the xindice bug
	    description = "Error !!"; 
      }
      rowClass = rowClass == "" ? "class='altRowColor'" : "";
		    
%>  		
	<tr <%=rowClass %>>
	  <td class="checkboxCell">
	    <input type="checkbox" rowselector="yes"
		name="selectedModule" value="<%=modules[i]%>">
	    </input>
          </td>
	  <td><%=modules[i]%></td>
	  <td align="center">
	    <a href="module_view.jsp?moduleId=<%=modules[i] %>">
		[View]
	    </a>
	  </td>
	  <td><%=description %></td>
	  <td>
<%
	    if( null != m ){
		VersionType versions[] = m.getVersionArray();
		for( int j=0;j<versions.length;j++){
%>			
	    <%=versions[j].getNumber() %><br/>
<%
		}
%>
	  </td>
	</tr>
<%
	    }
    }} else { %>
        <tr><td colspan="5">Currently there are no modules ready. Click "Add a module" to add a module.</td></tr>
        <% } %>
    </tbody>
</table>
            <br/>

            <div align="center" style="width: 95%;">
                <script type="text/javascript" language="JavaScript">
                    oneVoiceWritePageMenu("ModulesList", "footer",
                            "Delete selected modules",
                            "javascript:deleteModule()",
                            "Add a module",
                            "javascript:toggle('addModuleDiv')"
                            );
                </script>

            </div>
        </center>
    </div>
</form>

<br>

<div id="addModuleDiv" style="display:none;">
    <center>

        <form name="addModFrm" method="post" action="module_save.jsp?pageAction=addMod">
            <table id="hostListTable" border="0" cellpadding="0" cellspacing="0" class="dataTable">
                <caption>New Module</caption>
                <tbody>
                    <tr>
                        <td class="medium" align="right">Module ID:</td>
                        <td class="editableFieldCell">
                            <input type="text" name="moduleId" size="30" id="moduleId"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="medium" align="right">Vendor:</td>
                        <td class="editableFieldCell">
                            <input type="text" name="vendor" size="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="medium" align="right">Description:</td>
                        <td class="editableFieldCell">
                            <textarea rows="5" name="description" cols="30"></textarea>
                        </td>
                    </tr>
                </tbody>
            </table>
            <br/>

            <div align="center" style="width: 95%;">
                <div class="buttonSet">
                    <div class="bWrapperUp">
                        <div>
                            <div>
                                <input type="submit" name="save" value="Save Module" class="hpButton"/>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="clearFloats"></div>
            </div>

        </form>
    </center>
</div>

<%@ include file="footer.inc.jsp"%>
