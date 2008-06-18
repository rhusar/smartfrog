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

<%@	page import="org.smartfrog.avalanche.core.module.*"%>
<%@	page import="org.smartfrog.avalanche.server.*"%>
<%@	page import="org.smartfrog.avalanche.settings.sfConfig.*"%>
<%@ page import="org.smartfrog.avalanche.shared.ActiveProfileUpdater"%>
<%@ page import="org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType"%>
<%@ page import="java.util.ArrayList"%>
<%@ include file="header.inc.jsp"%>
<%
    String errMsg = null; 
    ModulesManager manager = factory.getModulesManager();

    HostManager hostManager = factory.getHostManager();
    SettingsManager settingsMgr = factory.getSettingsManager();

    SfConfigsType configs = settingsMgr.getSFConfigs();  
	    
    if( null == manager ){
	errMsg = "Error connecting to hosts database" ;
	throw new Exception ( "Error connecting to hosts database" );
    }
    String moduleId = request.getParameter("moduleId");
    if( null == moduleId){
	errMsg = "Null ModuleId" ;
	throw new Exception ( errMsg );
    }

    String version  = request.getParameter("version");
    String distroId = request.getParameter("distroId");
    String actionTitle = request.getParameter("action");
    ModuleType module = manager.getModule(moduleId);

    VersionType mv = null;
    DistributionType distro = null;
    ActionType action = null;
    SfDescriptionType sfDesc = null; 

    String []hostlist = hostManager.listHosts();
	String []hosts= new String[0];
	ArrayList listHolder = new ArrayList();
	ActiveProfileUpdater updater = new ActiveProfileUpdater();
	 ActiveProfileType type = null;
	for(int p=0;p<hostlist.length;p++){
		boolean active = false;
		int q =0;
		 type = updater.getActiveProfile(hostlist[p]);
		 active = type.getHostState().equals("Available");
		 if(active)
			listHolder.add(hostlist[p]);
	}
    hosts = (String[])listHolder.toArray(hosts);

    VersionType[] versions = module.getVersionArray();
    for( int i=0;i<versions.length;i++){
	if( version.equals(versions[i].getNumber()) ){
	    mv = versions[i];
	    break;
	}
    }

    if( null == mv ){
	errMsg = "No version : " + version + " exists for Module Id : "
	     + moduleId ;
    }
	    
    DistributionType []distros = mv.getDistributionArray();
    for( int i=0;i<distros.length;i++){
	if( distroId.equals(distros[i].getId()) ){
	    distro = distros[i];
	    break;
	}
    }
    
    if( null == distro ){
	errMsg = "No distrribution Id : " + distroId + " ,  version : " 
	    + version + " exists for Module Id : " + moduleId ;
    }else{
	ActionType []actions = distro.getActionArray();
	for( int i=0;i<actions.length;i++){
	    if( actionTitle.equals(actions[i].getConfiguration())){
		action=actions[i];
		break;
	    }
	}
    }		
    if( null != action ){
	SfDescriptionType[] descs = configs.getSfDescriptionArray();
	for( int i=0;i<descs.length;i++){
	    if( descs[i].getTitle().equals(action.getConfiguration()) ){
		    sfDesc = descs[i];
	    }
	}
    }
%>

<script language="JavaScript" type="text/javascript">

function submit(target){
	document.moduleListFrm.action = target ;
	document.moduleListFrm.submit();
}

function transfer(srcId, destId, all){
    var src  = document.getElementById(srcId);
    var dest = document.getElementById(destId);
    var removeList = new Array();

    for (var i = 0; i < src.options.length; i++){
	if (all || src.options[i].selected){
	    var opt = document.createElement('option');
	    opt.appendChild(document.createTextNode(src.options[i].text));
	    removeList.push(i);
	    dest.appendChild(opt);
	}
    }

    // Must remove items in reverse position order, to
    // preserve the position numbers as we remove them:
    removeList = removeList.sort();
    for (i = removeList.length; i > 0; i--){
	src.remove(removeList[i-1]);
    }
}

function selectAll(src)
{
    if ( src.options.length == 0 ){
	alert("You must select one or more target nodes before " +
		"executing this action.");
	return false;
    }
    for (var i = 0; i < src.options.length; i++){
	    src.options[i].selected = true ;
    }
    return true;
}

function copy(src, dest)
{
    if ( src.options.length == 0 ){
	alert("You must select one or more target nodes before " +
		"executing this action.");
	return false;
    }
    for (var i = 0; i < src.options.length; i++){
	    var opt = document.createElement('option');
	    opt.appendChild(document.createTextNode(src.options[i].text));
	    dest.appendChild(opt);
    }
    selectAll(dest);
}

function expandCollapse(item) 
{
  if (item == "None") {
    document.getElementById("None").style.display = "";
  } else {
    if (document.getElementById("Expand").style.display == "none") {
      document.getElementById("Collapse").style.display = "none";
      document.getElementById("Expand").style.display = "";
    } else {
      document.getElementById("Expand").style.display = "none";
      document.getElementById("Collapse").style.display = "";
    }
  }
}

function showDefAttr()
{
  var obj = document.getElementsByTagName("tr");
  for (var i = 0; i < obj.length; i++){
    if (obj[i].id == "DefAttr") {
      if (obj[i].style.display != "none")
      {
        hideDefAttr(obj[i]);
      } else {
         obj[i].style.display = "";
      }
    }
  }

  var obj = document.getElementById("allAttributeSet");
  if (obj != "") {
    if (obj.style.display != "none") {
	obj.style.display = "none";
    } else {
	obj.style.display = "";
    }
  }

  expandCollapse("true");
 /* 
  //var obj = document.getElementById("Expand");
  //if (obj.src == "onevoice/images/icon_tray_expand_u.gif"){
    //obj.src="onevoice/images/icon_tray_expand_d.gif";
    // obj.title="Show only basic attributes";
  //} else {
  //  obj.src="onevoice/images/icon_tray_expand_u.gif";
    //obj.title="Show all editable attributes";
//  }

//  var obj = document.getElementById("MoreOrLess");
  //if (obj.value == "More >>>"){
    //obj.value="<<< Less";
    //obj.title="Show only basic attributes";
  //} else {
    //obj.value="More >>>";
    //obj.title="Show all editable attributes";
  //}
*/
}

function hideDefAttr( obj ) {
  obj.style.display = "none";
}

setNextSubtitle("Select Host Page");

</script>


<br/>
<div align="center">
<center>
<!-- This is the page menu -->
<div align="center" style="width: 95%;">
  <script>
    oneVoiceWritePageMenu("SelectHost","header");
  </script>
</div>

<%@ include file="message.inc.jsp" %>
<% if (hosts.length==0){%>
	<p><b><font size="3">No Active nodes available.</font></b></p>
	<%}%>
<!-- Actual Body starts here -->
<table  border="0" cellpadding="0" cellspacing="0" class="dataTable" id="moduleTable">
  <caption>Action Information</caption>
  <tbody>
    <tr> 
      <td style="width: 15%;font-weight: bold;" align="right" class="medium">Module ID</td>
      <td class="medium"><%=moduleId %></td>
    </tr>
    <tr>
      <td style="width: 15%;font-weight: bold;" align="right" class="medium">Description</td>
      <td class="medium"><%=module.getDescription() %></td>
    </tr>	
    <tr>
      <td style="width: 15%;font-weight: bold;" align="right" class="medium">Action</td>
      <td class="medium"><%=actionTitle %></td>
    </tr>	
  </tbody>
</table>

<form method="post"  id="hostTransferList"
	action="module_distro_action_exec.jsp?title=<%=actionTitle%>&&engine=<%=action.getEngine()%>&&moduleId=<%=moduleId%>&&version=<%=version%>&&distroId=<%=distroId%>"
    onsubmit="javascript:selectAll(document.getElementById('selectedHosts'))">

<table border="0" cellpadding="0" cellspacing="0" class="dataTable" id="argumentTable">
<!--  <caption>Action Parameters</caption> -->
<!-- <div class="subTitleIcon"><input type="image" id="MoreOrLess" 
  src="onevoice/images/icon_tray_contract_u.gif" width="11" height="11" 
  border="0" alt=""
  onclick="javascript:showDefAttr()"></input></div>Action Parameters</caption>-->
<caption style="display:none;" id="Expand">
    <div class="subTitle">
        <div class="subTitleIcon"><a href="javascript:showDefAttr()"><img
                src="onevoice/images/icon_tray_expand_d.gif" title="Show all editable attributes" width="11"
                height="11" border="0" alt=""></a></div>
        Action Parameters
    </div>
</caption>
<caption style="display:none;" id="Collapse">
    <div class="subTitle">
        <div class="subTitleIcon"><a href="javascript:showDefAttr()"><img
                src="onevoice/images/icon_tray_contract_u.gif" title="Show only basic attributes" width="11"
                height="11" border="0" alt=""></a></div>
        Action Parameters
    </div>
</caption>
<caption style="display:none;" id="None">
    <div class="subTitle">
        <div class="subTitleIcon"></div>
        Action Paramters
    </div>
</caption>
<thead>
    <tr class="captionRow">
        <th class="medium" style="display:none;">Argument</th>
        <th class="medium" style="width:45%;">Description</th>
        <th class="medium" style="width:55%;">Value</th>
    </tr>
</thead>
<tbody>

    <%
	SfDescriptionType.Argument []args = sfDesc.getArgumentArray();
	String rowClass = "";
		
	boolean moreOrLess = false;
	boolean allAttrSet = false;
	for( int i=0;i<args.length;i++){
		rowClass = rowClass == "" ? "class='altRowColor'" : "";
		String name = args[i].getName();
		String value = args[i].getValue();
		String description = args[i].getDescription();

		// If the description includes the word "password, the
		// input field must be protected:
		String inputType = description.matches(".*((?i)password).*") ?
		    "password" : "text";

		// first see if we overwrote this value in module.
		ActionType.Argument []actionArgs = action.getArgumentArray();
		if( null != actionArgs ){
			for( int j =0;j<actionArgs.length;j++){
				if(actionArgs[j].getName().equals(name)){
					value = actionArgs[j].getValue();
				}
			}
		}
		String displayType = value.equals("") ? "" : "none";
                String divID = displayType.equals("") ? "" : "DefAttr" ;

                // This check will determine if we need the button to display
                // or hide attributes. The idea being that if all attributes
                // have no defaults, then there is no reason to hid them. If
                // we find at least one attribute with some default value,
                // then display the button to view them.
                if (displayType.equals("none")) {
                        moreOrLess = true;
                }
		if (displayType.equals("")) {
			allAttrSet = true;
		}
%>
	<tr <%=rowClass %> id='<%=divID %>' style='display: <%=displayType %>;'>
	    <td class="medium" style="display:none;">
		<input type="text" name="action.argument.name<%=i %>"
			 value="<%=name%>" readonly></input>
	    </td>
	    <td class="medium"><%=description %></td>
	    <td class="medium" width=45%>
		<input type='<%= inputType %>'
			 size="40"
			 name='action.argument.value<%=i%>'
			 value='<%=value%>'>
		</input>
	    </td>
	</tr>
<%
	}
	if (!allAttrSet) {
%>
    <tr id='allAttributeSet'>
        <td class="medium" colspan="99" align="center">
            [There are no required paramaters for this module.
            Click <img src="onevoice/images/icon_tray_expand_d.gif" width="11" height="11" border="0"
                       style="background-color:#716B66;" alt=""> to display all parameters.]
        </td>
    </tr>
    <%
	}
	if (moreOrLess) {
%>
	<script>
	  expandCollapse("true");
	</script>

<!-- 
	This is code for a button at the botton of the Action Parameters table
     	It was replaced by a expand/collapse icon on the caption line
	<tr style="background-color:#968F89;">
	<th  colspan="99" align="right">
 	  <div class="buttonSet">
            <div class="bWrapperUp"><div><div>
              <input type="button" class="hpButton" id="MoreOrLess" value="More >>>"
                title="Show all editable attributes"
                onclick="javascript:showDefAttr()" >
              </input>
	    </div></div></div>
	  </div>
	</th>
	</tr>
-->
<%
        }
        else {
%>
	<script>expandCollapse("None");</script>
<%
	}
%>
</tbody>
</table>

<table class="dataTable" border="0" cellpadding="0" cellspacing="0">
  <caption>Select target nodes for this action</caption>
  <tr>
    <td width="40%">
      Available Targets
      <span class="sp7">&nbsp;</span>
    </td>
    <td></td>
    <td width="40%">
      Selected Targets
      <span class="sp7">&nbsp;</span>
    </td>
  </tr>

  <tr valign="middle">
    <td style="width:50%;">
      <select size="10" style="width:100%;" id="allHosts" name="allHosts" multiple
	title="Select and click &quot;&gt;&gt;&gt;&quot; to transfer">
<%
	for( int i=0;i<hosts.length;i++){
%>
   	  <option><%=hosts[i]%></option>
<% 
	}
%>
      </select>
    </td>
    <td><br>
      <div class="verticalButtonSet">
        <div class="bWrapperUp"><div><div>
	  <input type="button" class="hpButtonSmall" value=" > " 
	    title="Select highlighted targets" 
	    onclick="javascript:transfer('allHosts','selectedHosts',0)">
	  </input>
	</div></div></div>
	
        <div class="bWrapperUp"><div><div>
	  <input type="button" class="hpButtonSmall" value=" < " 
	    title="Unselect highlighted targets"
	    onclick="javascript:transfer('selectedHosts','allHosts',0)">
	  </input>
	</div></div></div>
	<br><br>

        <div class="bWrapperUp"><div><div>
	  <input type="button" class="hpButtonSmall" value=">>>" 
	    title="Select all available targets"
	    onclick="javascript:transfer('allHosts','selectedHosts',1)">
	  </input>
	</div></div></div>

        <div class="bWrapperUp"><div><div>
	  <input type="button" class="hpButtonSmall" value="<<<" 
	    title="Unselect all targets"
	    onclick="javascript:transfer('selectedHosts','allHosts',1)">
	  </input>
	</div></div></div>
      </div>
    </td>
    <td style="width:50%;">
      <select style="width:100%;" size="10" id='selectedHosts' 
	name="selectedHosts" 
	multiple title="Select and click &quot;&lt;&lt;&lt;&quot; to remove">
      </select>
    </td>
  </tr>
</table>


<div style="width:95%;">
<div class="buttonSet">
  <div class="bWrapperUp" style="margin-top:10px;"><div><div>
    <input class="hpButton" type="reset" value="Reset form"></input>
  </div></div></div>
  <div class="bWrapperUp" style="margin-top:10px;"><div><div>
    <input class="hpButton" type="submit" name="submit" 
	value="Execute this action on the selected target nodes"></input>
  </div></div></div>
</div>
</div>
</form >

<form method="post"  id="hostTransferList"
	action="module_distro_action_schedule.jsp?title=<%=actionTitle%>&&engine=<%=action.getEngine()%>&&moduleId=<%=moduleId%>&&version=<%=version%>&&distroId=<%=distroId%>"
	onsubmit="javascript:copy(document.getElementById('selectedHosts'), document.getElementById('selectedHosts2'))">
      <select style="display:none" size="0" id='selectedHosts2' 
	name="selectedHosts2" 
	multiple>
      </select>

<div style="width:95%;">
<div class="buttonSet">
  <div class="bWrapperUp" style="margin-top:10px;"><div><div>
    <input class="hpButton"type="submit" name="submit" 
	value="Using Scheduler Execute this action on target nodes from the selected target nodes"></input>
  </div></div></div>
    Number of target nodes: <input type="text" name="number" size="10" id="hostId" />
</div>
</div>
</form>

</center>
</div>

<%@ include file="footer.inc.jsp"%>
